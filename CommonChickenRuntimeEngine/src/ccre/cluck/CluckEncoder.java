package ccre.cluck;

import ccre.chan.BooleanInput;
import ccre.chan.BooleanInputProducer;
import ccre.chan.BooleanOutput;
import ccre.chan.BooleanStatus;
import ccre.chan.FloatInput;
import ccre.chan.FloatInputProducer;
import ccre.chan.FloatOutput;
import ccre.chan.FloatStatus;
import ccre.event.Event;
import ccre.event.EventConsumer;
import ccre.event.EventSource;
import ccre.holders.StringHolder;
import ccre.log.LogLevel;
import ccre.log.Logger;
import ccre.log.LoggingTarget;
import ccre.util.CArrayList;
import ccre.util.CHashMap;
import ccre.workarounds.ThrowablePrinter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A CluckEncoder provides a number of methods for publishing and subscribing to
 * various dynamic objects, such as channels, events, loggers, and holders.
 *
 * @author skeggsc
 */
public class CluckEncoder {

    /**
     * The list of every object published by this encoder. This is used when
     * another encoder searches for published objects.
     */
    protected CArrayList<String> provided = new CArrayList<String>();
    /**
     * The CluckNode that this encoder uses for all communications.
     */
    protected final CluckNode server;
    /**
     * The cache of objects that have been subscribed to already, to prevent
     * duplication.
     */
    protected CHashMap<String, Object> cache = new CHashMap<String, Object>();

    /**
     * Create a new encoder that operates on the specified server.
     *
     * @param server the server to operate on.
     */
    public CluckEncoder(final CluckNode server) {
        this.server = server;
        server.subscribe("ENCODER-LIST", new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!"ENCODER-LIST".equals(channel)) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (String s : provided) {
                    sb.append(s).append('\n');
                }
                // Also published when an entry is added
                CluckEncoder.this.server.publish("ENCODER-LIST-RESPONSE", sb.toString().getBytes());
            }
        });
    }

    /**
     * An AvailableListener is notified whenever a new object is provided on the
     * network that it did not previously know about.
     *
     * @see
     * #searchAndSubscribePublished(ccre.cluck.CluckEncoder.AvailableListener)
     */
    public static abstract class AvailableListener implements CluckChannelListener {

        /**
         * The list of everything that has been recorded as existing so far.
         * This is automatically added to, and updateAdded and updatedReceived
         * are automatically called when that happens.
         *
         * @see #clearRecord()
         */
        protected CArrayList<String> totality = new CArrayList<String>();

        /**
         * Clear all the known objects in existence. Can be used when you want
         * to relist everything on the network, in conjunction with
         * searchPublished()
         *
         * @see CluckEncoder#searchPublished()
         */
        public void clearRecord() {
            totality.clear();
        }

        /**
         * Called whenever a new package of provided objects is received. May be
         * called when there was no change to listing. updateAdded is called for
         * each new listing, and may be a better choice to implement.
         */
        protected abstract void updatedReceived();

        /**
         * Called whenever a new object is detected on the network. This will
         * not be recalled for the same channel unless clearRecord() is called.
         *
         * @param channel The channel of the objects that has just been
         * discovered.
         */
        protected abstract void updateAdded(String channel);

        /**
         * Implementation detail - this is how the AvailableListener detects
         * various new objects.
         *
         * @param channel the channel of the received message.
         * @param data the received data.
         */
        public synchronized void receive(String channel, byte[] data) {
            if (!channel.equals("ENCODER-LIST-RESPONSE")) {
                return;
            }
            String dataAsString = new String(data);
            int startAt = 0;
            // Splitting the string without using String.split, because that doesn't work in Squawk
            while (startAt < dataAsString.length()) {
                int nextNewline = dataAsString.indexOf('\n', startAt);
                if (nextNewline == -1) {
                    nextNewline = dataAsString.length();
                }
                String receivedObject = dataAsString.substring(startAt, nextNewline);
                if (receivedObject.length() != 0 && !totality.contains(receivedObject)) {
                    totality.add(receivedObject);
                    updateAdded(receivedObject);
                }
                startAt = nextNewline + 1;
            }
            updatedReceived();
        }
    }

    /**
     * Register a AvailableListener to listen for new objects on the network,
     * and ask all other encoders to resend their lists of what objects they
     * provide.
     *
     * @param listener The AvailableListener to subscribe to responses.
     */
    public void searchAndSubscribePublished(AvailableListener listener) {
        server.subscribe("ENCODER-LIST-RESPONSE", listener);
        server.publish("ENCODER-LIST", null);
    }

    /**
     * Tell all encoders on the network to resend their lists of objects. These
     * will be picked up by any AvailableListeners on the network. Use in
     * combination with clearRecord() to relist everything.
     *
     * @see AvailableListener#clearRecord()
     */
    public void searchPublished() {
        server.publish("ENCODER-LIST", null);
    }

    /**
     * Unsubscribe an AvailableListener from listening to objects on the
     * network. It will no longer be notified of the existence of any objects.
     *
     * @param listener the listener to remove.
     */
    public void unsubscribePublished(AvailableListener listener) {
        server.unsubscribe("ENCODER-LIST-RESPONSE", listener);
    }

    /**
     * Publish an EventConsumer so that other encoders can subscribe to it. They
     * will be able to fire this EventConsumer remotely.
     *
     * @param name the name to put this object under.
     * @param cons the EventConsumer to publish.
     * @see #subscribeEventConsumer(java.lang.String)
     */
    public void publishEventConsumer(String name, final EventConsumer cons) {
        final String chan = "EC:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        server.subscribe(chan, new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                cons.eventFired();
            }
        });
    }

    /**
     * Subscribe to an EventConsumer from the network. This can be fired, which
     * will fire the original published EventConsumer.
     *
     * @param name the name to look for the object under.
     * @return the EventConsumer that has been received.
     * @see #publishEventConsumer(java.lang.String, ccre.event.EventConsumer)
     */
    public EventConsumer subscribeEventConsumer(String name) {
        final String chan = "EC:" + name;
        EventConsumer out = (EventConsumer) cache.get(chan);
        if (out != null) {
            return out;
        }
        out = new EventConsumer() {
            public void eventFired() {
                server.publish(chan, null);
            }
        };
        cache.put(chan, out);
        return out;
    }

    /**
     * Publish an EventSource so that other encoders can subscribe to it. They
     * will be able to listen on this EventSource remotely.
     *
     * @param name the name to put this object under.
     * @param source the EventSource to publish.
     * @see #subscribeEventSource(java.lang.String)
     */
    public void publishEventSource(String name, final EventSource source) {
        final String chan = "ES:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        source.addListener(new EventConsumer() {
            public void eventFired() {
                server.publish(chan, null);
            }
        });
    }

    /**
     * Subscribe to an EventSource from the network. This can be listened to,
     * which will be notified when the original published EventSource fires.
     *
     * @param name the name to look for the object under.
     * @return the EventSource that has been received.
     * @see #publishEventSource(java.lang.String, ccre.event.EventSource)
     */
    public EventSource subscribeEventSource(String name) {
        final String chan = "ES:" + name;
        EventSource out = (EventSource) cache.get(chan);
        if (out != null) {
            return out;
        }
        final Event created = new Event();
        server.subscribe(chan, new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                created.produce();
            }
        });
        cache.put(chan, created);
        return created;
    }

    /**
     * Publish a LoggingTarget so that other encoders can subscribe to it. They
     * will be able to log events to this logging target remotely.
     *
     * @param name the name to put this object under.
     * @param minimum The minimum LogLevel to require for anything transmitted
     * to the target.
     * @param target the LoggingTarget to publish.
     * @see #subscribeLoggingTarget(ccre.log.LogLevel, java.lang.String)
     */
    public void publishLoggingTarget(String name, final LogLevel minimum, final LoggingTarget target) {
        final String chan = "LT:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        server.subscribe(chan, new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                try {
                    DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
                    LogLevel level = LogLevel.fromByte(din.readByte());
                    if (!level.atLeastAsImportant(minimum)) {
                        return;
                    }
                    String title = din.readUTF();
                    String body = din.readUTF();
                    target.log(level, title, body);
                } catch (IOException ex) {
                    Logger.log(LogLevel.WARNING, "IOException in contained exceptive block!", ex);
                }
            }
        });
    }

    /**
     * Subscribe to a LoggingTarget from the network. This can be logged to,
     * which will be transmitted to the original shared LoggingTarget.
     *
     * @param minimum the minimum LogLevel to transmit over the network.
     * @param name the name to look for the object under.
     * @return the LoggingTarget that has been received.
     * @see #publishLoggingTarget(java.lang.String, ccre.log.LogLevel,
     * ccre.log.LoggingTarget)
     */
    public LoggingTarget subscribeLoggingTarget(final LogLevel minimum, String name) {
        final String chan = "LT:" + name;
        LoggingTarget out = (LoggingTarget) cache.get(chan);
        if (out != null) {
            return out;
        }
        out = new LoggingTarget() {
            public void log(LogLevel level, String message, Throwable thr) {
                if (!level.atLeastAsImportant(minimum)) {
                    return;
                }
                String body = "";
                if (thr != null) {
                    message += " [" + thr + "]";
                    body = ThrowablePrinter.toStringThrowable(thr);
                }
                log(level, message, body);
            }

            public void log(LogLevel level, String message, String extended) {
                if (!level.atLeastAsImportant(minimum)) {
                    return;
                }
                ByteArrayOutputStream out;
                try {
                    out = new ByteArrayOutputStream();
                    DataOutputStream dout = new DataOutputStream(out);
                    dout.writeByte(LogLevel.toByte(level));
                    dout.writeUTF(message);
                    dout.writeUTF(extended == null ? "" : extended);
                    server.publish(chan, out.toByteArray());
                } catch (IOException ex) {
                    Logger.log(LogLevel.WARNING, "IOException in contained exceptive block!", ex);
                }
            }
        };
        cache.put(chan, out);
        return out;
    }

    /**
     * Publish a BooleanInputProducer so that other encoders can subscribe to
     * it. They will be able to listen to this BooleanInputProducer remotely.
     *
     * @param name the name to put this object under.
     * @param source the BooleanInputProducer to publish.
     * @see #subscribeBooleanInputProducer(java.lang.String)
     */
    public void publishBooleanInputProducer(String name, final BooleanInputProducer source) {
        final String chan = "BI:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        source.addTarget(new BooleanOutput() {
            public void writeValue(boolean value) {
                server.publish(chan, new byte[]{value ? (byte) 1 : (byte) 0});
            }
        });
    }

    /**
     * Subscribe to a BooleanInputProducer from the network. This can be
     * listened to, which will be notified when the original published
     * BooleanInputProducer change.
     *
     * This method returns a BooleanInput instead of a BooleanInputProducer - it
     * keeps track of the current value, defaulting to the default_ parameter
     * before any data is received.
     *
     * @param name the name to look for the object under.
     * @param default_ the boolean to set this to by default.
     * @return the BooleanInputProducer that has been received.
     * @see #publishBooleanInputProducer(java.lang.String,
     * ccre.chan.BooleanInputProducer)
     */
    public BooleanInput subscribeBooleanInputProducer(String name, boolean default_) {
        final String chan = "BI:" + name;
        BooleanInput out = (BooleanInput) cache.get(chan);
        if (out != null) {
            return out;
        }
        final BooleanStatus created = new BooleanStatus();
        created.writeValue(default_);
        server.subscribe(chan, new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                created.writeValue(data[0] != 0);
            }
        });
        cache.put(chan, created);
        return created;
    }

    /**
     * Publish a BooleanOutput so that other encoders can subscribe to it. They
     * will be able to write to this output remotely.
     *
     * @param name the name to put this object under.
     * @param target the BooleanOutput to publish.
     * @see #subscribeBooleanOutput(java.lang.String)
     */
    public void publishBooleanOutput(String name, final BooleanOutput target) {
        final String chan = "BO:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        server.subscribe(chan, new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                target.writeValue(data[0] != 0);
            }
        });
    }

    /**
     * Subscribe to a BooleanOutput from the network. This can be written to,
     * which will write the new state to the original shared BooleanOutput.
     *
     * @param name the name to look for the object under.
     * @return the BooleanOutput that has been received.
     * @see #publishBooleanOutput(java.lang.String, ccre.chan.BooleanOutput)
     */
    public BooleanOutput subscribeBooleanOutput(String name) {
        final String chan = "BO:" + name;
        BooleanOutput out = (BooleanOutput) cache.get(chan);
        if (out != null) {
            return out;
        }
        out = new BooleanOutput() {
            public void writeValue(boolean value) {
                server.publish(chan, new byte[]{value ? (byte) 1 : (byte) 0});
            }
        };
        cache.put(chan, out);
        return out;
    }

    /**
     * Publish a FloatInputProducer so that other encoders can subscribe to it.
     * They will be able to listen to this FloatInputProducer remotely.
     *
     * @param name the name to put this object under.
     * @param source the FloatInputProducer to publish.
     * @see #subscribeFloatInputProducer(java.lang.String)
     */
    public void publishFloatInputProducer(String name, final FloatInputProducer source) {
        final String chan = "FI:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        source.addTarget(new FloatOutput() {
            public void writeValue(float value) {
                int iv = Float.floatToIntBits(value);
                server.publish(chan, new byte[]{(byte) iv, (byte) (iv >> 8), (byte) (iv >> 16), (byte) (iv >> 24)});
            }
        });
    }

    /**
     * Subscribe to a FloatInputProducer from the network. This can be listened
     * to, which will be notified when the original published FloatInputProducer
     * change.
     *
     * This method returns a FloatInput instead of a FloatInputProducer - it
     * keeps track of the current value, defaulting to the default_ parameter
     * before any data is received.
     *
     * @param name the name to look for the object under.
     * @param default_ the float to set this to by default.
     * @return the FloatInputProducer that has been received.
     * @see #publishFloatInputProducer(java.lang.String,
     * ccre.chan.FloatInputProducer)
     */
    public FloatInput subscribeFloatInputProducer(String name, float default_) {
        final String chan = "FI:" + name;
        FloatInput out = (FloatInput) cache.get(chan);
        if (out != null) {
            return out;
        }
        final FloatStatus created = new FloatStatus();
        created.writeValue(default_);
        server.subscribe(chan, new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                created.writeValue(Float.intBitsToFloat((data[0] & 0xff) | ((data[1] & 0xff) << 8) | ((data[2] & 0xff) << 16) | ((data[3] & 0xff) << 24)));
            }
        });
        cache.put(chan, created);
        return created;
    }

    /**
     * Publish a FloatOutput so that other encoders can subscribe to it. They
     * will be able to write to this output remotely.
     *
     * @param name the name to put this object under.
     * @param target the FloatOutput to publish.
     * @see #subscribeFloatOutput(java.lang.String)
     */
    public void publishFloatOutput(String name, final FloatOutput target) {
        final String chan = "FO:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        server.subscribe(chan, new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                target.writeValue(Float.intBitsToFloat((data[0] & 0xff) | ((data[1] & 0xff) << 8) | ((data[2] & 0xff) << 16) | ((data[3] & 0xff) << 24)));
            }
        });
    }

    /**
     * Subscribe to a FloatOutput from the network. This can be written to,
     * which will write the new state to the original shared FloatOutput.
     *
     * @param name the name to look for the object under.
     * @return the FloatOutput that has been received.
     * @see #publishFloatOutput(java.lang.String, ccre.chan.FloatOutput)
     */
    public FloatOutput subscribeFloatOutput(String name) {
        final String chan = "FO:" + name;
        FloatOutput out = (FloatOutput) cache.get(chan);
        if (out != null) {
            return out;
        }
        out = new FloatOutput() {
            public void writeValue(float value) {
                int iv = Float.floatToIntBits(value);
                server.publish(chan, new byte[]{(byte) iv, (byte) (iv >> 8), (byte) (iv >> 16), (byte) (iv >> 24)});
            }
        };
        cache.put(chan, out);
        return out;
    }

    /**
     * Publish a StringHolder to the network. This will synchronize the
     * StringHolder with all other registered StringHolders of the same name.
     *
     * This is the same as a call to registerWithoutPullStringHolder, except
     * that it also overwrites the current value on the network with this
     * StringHolder's value.
     *
     * @param name the name of the StringHolder
     * @param strh the StringHolder to register
     */
    public void publishStringHolder(String name, StringHolder strh) {
        registerWithoutPullStringHolder(name, strh);
        strh.notifyChanged();
    }

    /**
     * Register a StringHolder on the network. This will synchronize the
     * StringHolder with all other registered StringHolders of the same name.
     *
     * This is the same as a call to registerWithoutPullStringHolder, except
     * that it also requests the current value from another node on the network.
     *
     * @param name the name of the StringHolder
     * @param strh the StringHolder to register
     */
    public void registerStringHolder(String name, StringHolder strh) {
        registerWithoutPullStringHolder(name, strh);
        server.publish("STR:" + name, new byte[]{(byte) 0});
    }

    /**
     * Register a StringHolder on the network. This will synchronized the
     * StringHolder with all other registered StringHolders of the same name.
     *
     * This will not send the current value or request a current value from
     * anything on the network. Use publishStringHolder or registerStringHolder
     * for that.
     *
     * @param name the name of the StringHolder
     * @param strh the StringHolder to register
     */
    public void registerWithoutPullStringHolder(String name, final StringHolder strh) {
        final String chan = "STR:" + name;
        provided.add(chan);
        server.publish("ENCODER-LIST-RESPONSE", chan.getBytes());
        final CluckChannelListener recv = new CluckChannelListener() {
            public void receive(String channel, byte[] data) {
                if (!chan.equals(channel)) {
                    return;
                }
                if (data[0] == 0 && strh.hasModified()) {
                    byte[] b = strh.get().getBytes();
                    byte[] out = new byte[b.length + 1];
                    out[0] = 1;
                    System.arraycopy(b, 0, out, 1, b.length);
                    server.publish(chan, out, this);
                } else if (data[0] == 1) {
                    strh.set(new String(data, 1, data.length - 1));
                }
            }
        };
        strh.getModifiedEvent().addListener(new EventConsumer() {
            public void eventFired() {
                byte[] b = strh.get().getBytes();
                byte[] out = new byte[b.length + 1];
                out[0] = 1;
                System.arraycopy(b, 0, out, 1, b.length);
                server.publish(chan, out, recv);
            }
        });
        server.subscribe(chan, recv);
    }

    /**
     * Subscribe to a StringHolder on the network. This will synchronize the
     * StringHolder with all other registered StringHolders of the same name.
     *
     * This is the same as a call to registerWithoutPullStringHolder, except
     * that it creates a StringHolder beforehand.
     *
     * @param name the name of the StringHolder
     * @param default_ the default value of the new StringHolder (does not
     * override anything on the network)
     * @return the newly registered StringHolder.
     */
    public StringHolder subscribeWithoutPullStringHolder(String name, String default_) {
        StringHolder out = new StringHolder(default_, false);
        registerWithoutPullStringHolder(name, out);
        return out;
    }

    /**
     * Subscribe to a StringHolder on the network. This will synchronize the
     * StringHolder with all other registered StringHolders of the same name.
     *
     * This is the same as a call to registerStringHolder, except
     * that it creates a StringHolder beforehand.
     *
     * @param name the name of the StringHolder
     * @param default_ the default value of the new StringHolder (does not
     * override anything on the network)
     * @return the newly registered StringHolder.
     */
    public StringHolder subscribeStringHolder(String name, String default_) {
        StringHolder out = new StringHolder(default_, false);
        registerStringHolder(name, out);
        return out;
    }
}

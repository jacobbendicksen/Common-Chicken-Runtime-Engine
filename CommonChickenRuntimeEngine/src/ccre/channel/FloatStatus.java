/*
 * Copyright 2013-2015 Colby Skeggs
 *
 * This file is part of the CCRE, the Common Chicken Runtime Engine.
 *
 * The CCRE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The CCRE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the CCRE.  If not, see <http://www.gnu.org/licenses/>.
 */
package ccre.channel;

import java.io.Serializable;

import ccre.concurrency.ConcurrentDispatchArray;
import ccre.ctrl.FloatMixing;
import ccre.rconf.RConf;
import ccre.rconf.RConf.Entry;
import ccre.rconf.RConfable;
import ccre.util.CArrayUtils;

/**
 * A virtual node that is both a FloatOutput and a FloatInput. You can modify
 * its value, read its value, and subscribe to changes in its value. FloatStatus
 * also provides a number of useful helper functions.
 *
 * By convention, most float inputs and outputs have states that range from
 * -1.0f to 1.0f.
 *
 * @author skeggsc
 */
public class FloatStatus implements FloatOutput, FloatInput, RConfable, Serializable {

    private static final long serialVersionUID = -579209218982597622L;

    /**
     * The current state of this FloatStatus. Do not directly modify this field.
     * Use the writeValue method instead.
     *
     * By convention, most float inputs and outputs have states that range from
     * -1.0f to 1.0f.
     *
     * @see #set(float)
     */
    private float value = 0;

    /**
     * The list of all the FloatOutputs to modify when this FloatStatus changes
     * value.
     *
     * @see #send(ccre.channel.FloatOutput)
     * @see #unsend(ccre.channel.FloatOutput)
     */
    private ConcurrentDispatchArray<FloatOutput> consumers = null;

    /**
     * Create a new FloatStatus with a value of zero.
     */
    public FloatStatus() {
    }

    /**
     * Create a new FloatStatus with the specified default value.
     *
     * @param value The default value.
     */
    public FloatStatus(float value) {
        this.set(value);
    }

    /**
     * Create a new FloatStatus that automatically updates the specified
     * FloatOutput with the current state of this FloatStatus. This is the same
     * as creating a new FloatStatus and then adding the FloatOutput as a
     * target.
     *
     * @see FloatStatus#send(ccre.channel.FloatOutput)
     * @param target The FloatOutput to automatically update.
     */
    public FloatStatus(FloatOutput target) {
        consumers = new ConcurrentDispatchArray<FloatOutput>();
        consumers.add(target);
        target.set(0);
    }

    /**
     * Create a new FloatStatus that automatically updates all of the specified
     * FloatOutputs with the current state of this FloatStatus. This is the same
     * as creating a new FloatStatus and then adding all of the FloatOutputs as
     * targets.
     *
     * @see FloatStatus#send(ccre.channel.FloatOutput)
     * @param targets The FloatOutputs to automatically update.
     */
    public FloatStatus(FloatOutput... targets) {
        consumers = new ConcurrentDispatchArray<FloatOutput>(CArrayUtils.asList(targets));
        for (FloatOutput t : targets) {
            t.set(0);
        }
    }

    public final synchronized float get() {
        return value;
    }

    /**
     * Returns whether or not this has any targets that will get modified when
     * the value changes If this returns false, the set() method will not notify
     * anyone.
     *
     * @return whether or not the set() method would notify any targets.
     * @see #set(float)
     */
    public boolean hasConsumers() {
        return consumers != null && !consumers.isEmpty();
    }

    public final synchronized void set(float newValue) {
        if (Float.floatToIntBits(value) == Float.floatToIntBits(newValue)) {
            return; // Do nothing. We want to ignore the value if it's the same.
        }
        value = newValue;
        if (consumers != null) {
            for (FloatOutput fws : consumers) {
                fws.set(newValue);
            }
        }
    }

    /**
     * Get an EventOutput that, when fired, will set the state to the given
     * float.
     *
     * @param newValue the value to set the state to.
     * @return the fire-able EventOutput.
     * @see #setWhen(float, ccre.channel.EventInput)
     */
    public final EventOutput getSetEvent(float newValue) {
        return FloatMixing.getSetEvent(this, newValue);
    }

    /**
     * When the specified event occurs, set the state to the specified value.
     *
     * @param value the value to set the state to.
     * @param when when to set the status.
     * @see #getSetEvent(float)
     */
    public final void setWhen(float value, EventInput when) {
        FloatMixing.setWhen(when, this, value);
    }

    public synchronized void send(FloatOutput output) {
        if (consumers == null) {
            consumers = new ConcurrentDispatchArray<FloatOutput>();
        }
        consumers.add(output);
        output.set(value);
    }

    public synchronized void unsend(FloatOutput output) {
        if (consumers != null) {
            if (consumers.remove(output) && consumers.isEmpty()) {
                consumers = null;
            }
        }
    }

    public Entry[] queryRConf() {
        return new Entry[] { RConf.fieldFloat(get()), RConf.fieldInteger(consumers == null ? 0 : consumers.size()) };
    }

    public boolean signalRConf(int field, byte[] data) {
        switch (field) {
        case 0: // change value
            if (data.length >= 4) {
                set(RConf.bytesToFloat(data));
                return true;
            }
            return false;
        }
        return false;
    }
}

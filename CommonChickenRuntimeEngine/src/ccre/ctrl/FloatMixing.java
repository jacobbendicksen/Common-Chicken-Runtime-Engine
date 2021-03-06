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
package ccre.ctrl;

import ccre.channel.BooleanInput;
import ccre.channel.BooleanInputPoll;
import ccre.channel.EventInput;
import ccre.channel.EventOutput;
import ccre.channel.EventStatus;
import ccre.channel.FloatFilter;
import ccre.channel.FloatInput;
import ccre.channel.FloatInputPoll;
import ccre.channel.FloatOutput;
import ccre.channel.FloatStatus;
import ccre.util.Utils;

/**
 * FloatMixing is a class that provides a wide variety of useful static methods
 * to accomplish various common actions primarily relating to float channels.
 *
 * @author skeggsc
 * @see BooleanMixing
 * @see EventMixing
 * @see Mixing
 */
public class FloatMixing {

    /**
     * An operation representing summation, aka addition.
     */
    public static final FloatOperation addition = new FloatOperation() {
        @Override
        public float of(float augend, float addend) {
            return augend + addend;
        }
    };
    /**
     * An operation representing a difference, aka subtracting.
     */
    public static final FloatOperation subtraction = new FloatOperation() {
        @Override
        public float of(float minend, float subtrahend) {
            return minend - subtrahend;
        }
    };
    /**
     * An operation representing a product, aka multiplication.
     */
    public static final FloatOperation multiplication = new FloatOperation() {
        @Override
        public float of(float multiplicand, float multiplier) {
            return multiplicand * multiplier;
        }
    };
    /**
     * An operation representing a quotient, aka division.
     */
    public static final FloatOperation division = new FloatOperation() {
        @Override
        public float of(float dividend, float divisor) {
            return dividend / divisor;
        }
    };
    /**
     * A FloatFilter that negates a value.
     */
    public static final FloatFilter negate = new FloatFilter() {
        @Override
        public float filter(float input) {
            return -input;
        }
    };
    /**
     * A FloatOutput that goes nowhere. All data sent here is ignored.
     */
    public static final FloatOutput ignoredFloatOutput = new FloatOutput() {
        public void set(float newValue) {
        }
    };

    /**
     * Returns an EventOutput that, when fired, writes the specified value to
     * the specified output.
     *
     * @param output the output to write to.
     * @param value the value to write.
     * @return the event to write the value.
     */
    public static EventOutput getSetEvent(final FloatOutput output, final float value) {
        Mixing.checkNull(output);
        return new EventOutput() {
            public void event() {
                output.set(value);
            }
        };
    }

    /**
     * Return a BooleanInputPoll that is true when the specified float input is
     * at least the specified minimum value.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @return an input that represents the value being at least the minimum.
     */
    public static BooleanInputPoll floatIsAtLeast(final FloatInputPoll base, final float minimum) {
        Mixing.checkNull(base);
        return new BooleanInputPoll() {
            public boolean get() {
                return base.get() >= minimum;
            }
        };
    }

    /**
     * Return a BooleanInputPoll that is true when the specified float input is
     * at least the specified minimum value.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @return an input that represents the value being at least the minimum.
     */
    public static BooleanInputPoll floatIsAtLeast(final FloatInputPoll base, final FloatInputPoll minimum) {
        Mixing.checkNull(base, minimum);
        return new BooleanInputPoll() {
            public boolean get() {
                return base.get() >= minimum.get();
            }
        };
    }

    /**
     * Return a BooleanInput that is true when the specified float input is at
     * least the specified minimum value.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @return an input that represents the value being at least the minimum.
     */
    public static BooleanInput floatIsAtLeast(FloatInput base, float minimum) {
        Mixing.checkNull(base);
        return BooleanMixing.createDispatch(floatIsAtLeast((FloatInputPoll) base, minimum), FloatMixing.onUpdate(base));
    }

    /**
     * Return a BooleanInput that is true when the specified float input is at
     * least the specified minimum value.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @return an input that represents the value being at least the minimum.
     */
    public static BooleanInput floatIsAtLeast(FloatInput base, FloatInputPoll minimum) {
        Mixing.checkNull(base, minimum);
        return BooleanMixing.createDispatch(floatIsAtLeast((FloatInputPoll) base, minimum), FloatMixing.onUpdate(base));
    }

    /**
     * Return a BooleanInputPoll that is true when the specified float input is
     * at most the specified maximum value.
     *
     * @param base the value to test
     * @param maximum the maximum value
     * @return an input that represents the value being at most the maximum.
     */
    public static BooleanInputPoll floatIsAtMost(final FloatInputPoll base, final float maximum) {
        Mixing.checkNull(base);
        return new BooleanInputPoll() {
            public boolean get() {
                return base.get() <= maximum;
            }
        };
    }

    /**
     * Return a BooleanInputPoll that is true when the specified float input is
     * at most the specified maximum value.
     *
     * @param base the value to test
     * @param maximum the maximum value
     * @return an input that represents the value being at most the maximum.
     */
    public static BooleanInputPoll floatIsAtMost(final FloatInputPoll base, final FloatInputPoll maximum) {
        Mixing.checkNull(base, maximum);
        return new BooleanInputPoll() {
            public boolean get() {
                return base.get() <= maximum.get();
            }
        };
    }

    /**
     * Return a BooleanInput that is true when the specified float input is at
     * most the specified maximum value.
     *
     * @param base the value to test
     * @param maximum the maximum value
     * @return an input that represents the value being at most the maximum.
     */
    public static BooleanInput floatIsAtMost(final FloatInput base, final float maximum) {
        Mixing.checkNull(base);
        return BooleanMixing.createDispatch(floatIsAtMost((FloatInputPoll) base, maximum), FloatMixing.onUpdate(base));
    }

    /**
     * Return a BooleanInput that is true when the specified float input is at
     * most the specified maximum value.
     *
     * @param base the value to test
     * @param maximum the maximum value
     * @return an input that represents the value being at most the maximum.
     */
    public static BooleanInput floatIsAtMost(final FloatInput base, final FloatInputPoll maximum) {
        Mixing.checkNull(base, maximum);
        return BooleanMixing.createDispatch(floatIsAtMost((FloatInputPoll) base, maximum), FloatMixing.onUpdate(base));
    }

    /**
     * Return a Filter that applies the specified limitation to the value.
     *
     * @param minimum The minimum value to limit to. Use Float.NEGATIVE_INFINITY
     * if you want no lower bound.
     * @param maximum The maximum value to limit to. Use Float.POSITIVE_INFINITY
     * if you want no upper bound.
     * @return The filter representing the specified limit.
     */
    public static FloatFilter limit(final float minimum, final float maximum) {
        if (maximum < minimum) {
            throw new IllegalArgumentException("Maximum is smaller than minimum!");
        }
        return new FloatFilter() {
            @Override
            public float filter(float input) {
                if (input < minimum) {
                    return minimum;
                } else if (input > maximum) {
                    return maximum;
                } else {
                    return input;
                }
            }
        };
    }

    /**
     * Combine two FloatOutputs so that any write to the returned output will go
     * to both of the specified outputs.
     *
     * @param a the first output
     * @param b the second output
     * @return the output that will write to both specified outputs.
     */
    public static FloatOutput combine(final FloatOutput a, final FloatOutput b) {
        Mixing.checkNull(a, b);
        return new FloatOutput() {
            public void set(float value) {
                a.set(value);
                b.set(value);
            }
        };
    }

    /**
     * Combine three FloatOutputs so that any write to the returned output will
     * go to all of the specified outputs.
     *
     * @param a the first output
     * @param b the second output
     * @param c the third output
     * @return the output that will write to all specified outputs.
     */
    public static FloatOutput combine(final FloatOutput a, final FloatOutput b, final FloatOutput c) {
        Mixing.checkNull(a, b, c);
        return new FloatOutput() {
            public void set(float value) {
                a.set(value);
                b.set(value);
                c.set(value);
            }
        };
    }

    /**
     * Return a BooleanInputPoll that is true when the specified float input is
     * outside of the range of the specified minimum and maximum. It will be
     * false at the minimum or maximum.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @param maximum the maximum value
     * @return an input that represents the value being outside the range
     */
    public static BooleanInputPoll floatIsOutsideRange(final FloatInputPoll base, final float minimum, final float maximum) {
        Mixing.checkNull(base);
        return new BooleanInputPoll() {
            public boolean get() {
                float val = base.get();
                return val < minimum || val > maximum;
            }
        };
    }

    /**
     * Return a BooleanInput that is true when the specified float input is
     * outside of the range of the specified minimum and maximum. It will be
     * false at the minimum or maximum.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @param maximum the maximum value
     * @return an input that represents the value being outside the range
     */
    public static BooleanInput floatIsOutsideRange(final FloatInput base, final float minimum, final float maximum) {
        Mixing.checkNull(base);
        return BooleanMixing.createDispatch(floatIsOutsideRange((FloatInputPoll) base, minimum, maximum), FloatMixing.onUpdate(base));
    }

    /**
     * Returns an EventOutput that, when called, pumps the value from the
     * specified input to the specified output
     *
     * @param in the input
     * @param out the output
     * @return the EventOutput that pumps the value
     */
    public static EventOutput pumpEvent(final FloatInputPoll in, final FloatOutput out) {
        Mixing.checkNull(in, out);
        return new EventOutput() {
            public void event() {
                out.set(in.get());
            }
        };
    }

    /**
     * Add a ramping system that will wrap the specified FloatOutput in a
     * ramping controller that will update when the specified event is produced,
     * with the specified limit.
     *
     * @param limit The maximum delta per update.
     * @param updateWhen When to update the ramping system.
     * @param target The target to wrap.
     * @return The wrapped output.
     */
    public static FloatOutput addRamping(final float limit, EventInput updateWhen, final FloatOutput target) {
        Mixing.checkNull(updateWhen);
        FloatStatus temp = new FloatStatus();
        updateWhen.send(createRamper(limit, temp, target));
        return temp;
    }

    /**
     * Add a ramping system that will wrap the specified FloatInputPoll in a
     * ramping controller that will update when the specified event is produced,
     * with the specified limit.
     *
     * @param limit The maximum delta per update.
     * @param updateWhen When to update the ramping system.
     * @param source The source to wrap
     * @return The wrapped input.
     */
    public static FloatInput addRamping(final float limit, EventInput updateWhen, final FloatInputPoll source) {
        Mixing.checkNull(updateWhen, source);
        FloatStatus temp = new FloatStatus();
        updateWhen.send(createRamper(limit, source, temp));
        return temp;
    }

    /**
     * Returns a FloatInputPoll representing the negated version of the
     * specified input.
     *
     * @param value the input to negate.
     * @return the negated input.
     */
    public static FloatInputPoll negate(final FloatInputPoll value) {
        return negate.wrap(value);
    }

    /**
     * Returns a FloatInput representing the negated version of the specified
     * input.
     *
     * @param value the input to negate.
     * @return the negated input.
     */
    public static FloatInput negate(FloatInput value) {
        return negate.wrap(value);
    }

    /**
     * Returns a FloatOutput that, when written to, writes the negation of the
     * value through to the specified output.
     *
     * @param output the output to write negated values to.
     * @return the output to write pre-negated values to.
     */
    public static FloatOutput negate(final FloatOutput output) {
        return negate.wrap(output);
    }

    /**
     * Add a ramping system between the specified input and output, with the
     * specified acceleration limit, and returns the EventOutput to update the
     * ramping system.
     *
     * @param limit The maximum delta per update.
     * @param from The FloatInputPoll to control the expected value.
     * @param target The output to write the current value to.
     * @return The EventOutput that updates the ramping system.
     */
    public static EventOutput createRamper(final float limit, final FloatInputPoll from, final FloatOutput target) {
        Mixing.checkNull(from, target);
        return new EventOutput() {
            private float last = from.get();

            public void event() {
                last = Utils.updateRamping(last, from.get(), limit);
                target.set(last);
            }
        };
    }

    /**
     * Return a BooleanInputPoll that is true when the specified float input is
     * in the range of the specified minimum and maximum, inclusive.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @param maximum the maximum value
     * @return an input that represents the value being in range
     */
    public static BooleanInputPoll floatIsInRange(final FloatInputPoll base, final float minimum, final float maximum) {
        Mixing.checkNull(base);
        return new BooleanInputPoll() {
            public boolean get() {
                float val = base.get();
                return val >= minimum && val <= maximum;
            }
        };
    }

    /**
     * Return a BooleanInput that is true when the specified float input is in
     * the range of the specified minimum and maximum, inclusive.
     *
     * @param base the value to test
     * @param minimum the minimum value
     * @param maximum the maximum value
     * @return an input that represents the value being in range
     */
    public static BooleanInput floatIsInRange(final FloatInput base, final float minimum, final float maximum) {
        Mixing.checkNull(base);
        return BooleanMixing.createDispatch(floatIsInRange((FloatInputPoll) base, minimum, maximum), FloatMixing.onUpdate(base));
    }

    /**
     * Creates a FloatInput that is always the specified value.
     *
     * @param value the value to always have.
     * @return the FloatInput representing that value.
     */
    public static FloatInput always(final float value) {
        return new FloatInput() {
            public float get() {
                return value;
            }

            public void send(FloatOutput consum) {
                consum.set(value);
            }

            public void unsend(FloatOutput consum) {
            }
        };
    }

    /**
     * Return a FloatInput that is the same as the specified FloatInputPoll,
     * except that it is also a producer that will update whenever the specified
     * event is triggered.
     *
     * @param input the original input.
     * @param trigger the event to dispatch at.
     * @return the dispatchable input.
     */
    public static FloatInput createDispatch(FloatInputPoll input, EventInput trigger) {
        Mixing.checkNull(input, trigger);
        FloatStatus fstat = new FloatStatus(input.get());
        FloatMixing.pumpWhen(trigger, input, fstat);
        return fstat;
    }

    /**
     * When the specified EventInput is fired, write the specified value to the
     * specified output
     *
     * @param when when to write the value.
     * @param out the output to write to.
     * @param value the value to write.
     */
    public static void setWhen(EventInput when, FloatOutput out, float value) {
        Mixing.checkNull(when, out);
        when.send(getSetEvent(out, value));
    }

    /**
     * When check is fired, and shouldSet is true, set the output to value.
     *
     * This is intended to be used with a frequent event as check, to
     * effectively hold the output at a specific value.
     *
     * @param check when to update the value.
     * @param shouldSet whether or not the output should be held.
     * @param output the output to hold.
     * @param value the value to hold it at.
     */
    public static void setWhile(EventInput check, BooleanInputPoll shouldSet, FloatOutput output, float value) {
        Mixing.checkNull(check, shouldSet, output);
        setWhen(EventMixing.filterEvent(shouldSet, true, check), output, value);
    }

    /**
     * When check is fired, and shouldSet is false, set the output to value.
     *
     * This is intended to be used with a frequent event as check, to
     * effectively hold the output at a specific value.
     *
     * @param check when to update the value.
     * @param shouldSet whether or not the output should NOT be held.
     * @param output the output to hold.
     * @param value the value to hold it at.
     */
    public static void setWhileNot(EventInput check, BooleanInputPoll shouldSet, FloatOutput output, float value) {
        Mixing.checkNull(check, shouldSet, output);
        setWhen(EventMixing.filterEvent(shouldSet, false, check), output, value);
    }

    /**
     * Return a Filter that applies the specified-size deadzone as defined in
     * Utils.deadzone.
     *
     * @param deadzone The deadzone size to apply.
     * @return The filter representing this deadzone size.
     * @see ccre.util.Utils#deadzone(float, float)
     */
    public static FloatFilter deadzone(final float deadzone) {
        return new FloatFilter() {
            @Override
            public float filter(float input) {
                return Utils.deadzone(input, deadzone);
            }
        };
    }

    /**
     * Returns a FloatInputPoll with a deadzone applied as defined in
     * Utils.deadzone
     *
     * @param inp the input representing the current value.
     * @param range the deadzone to apply.
     * @return the input representing the deadzone applied to the specified
     * value.
     * @see ccre.util.Utils#deadzone(float, float)
     */
    public static FloatInputPoll deadzone(FloatInputPoll inp, float range) {
        return deadzone(range).wrap(inp);
    }

    /**
     * Returns a FloatInput with a deadzone applied as specified in
     * Utils.deadzone.
     *
     * @param inp the input representing the current value.
     * @param range the deadzone to apply.
     * @return the input representing the deadzone applied to the specified
     * value.
     * @see ccre.util.Utils#deadzone(float, float)
     */
    public static FloatInput deadzone(FloatInput inp, float range) {
        return deadzone(range).wrap(inp);
    }

    /**
     * Returns a FloatOutput that writes through a deadzoned version of any
     * values written to it. Deadzones values as specified in Utils.deadzone.
     *
     * @param out the output to write deadzoned values to.
     * @param range the deadzone to apply.
     * @return the output that writes deadzoned values through to the specified
     * output.
     * @see ccre.util.Utils#deadzone(float, float)
     */
    public static FloatOutput deadzone(final FloatOutput out, final float range) {
        return deadzone(range).wrap(out);
    }

    /**
     * When the specified event is fired, pump the value from the specified
     * input to the specified output.
     *
     * @param trigger when to pump the value
     * @param in the input
     * @param out the output
     */
    public static void pumpWhen(EventInput trigger, final FloatInputPoll in, final FloatOutput out) {
        Mixing.checkNull(trigger, in, out);
        trigger.send(pumpEvent(in, out));
    }

    /**
     * Returns a FloatInputPoll representing the delta between the current value
     * of input and the previous value. This _only_ works when you use the
     * result in one place! If you use it in multiple, then it may try to find
     * the deltas between each invocation!
     *
     * To get around this, use findRate with two arguments.
     *
     * @param input The input value to find the rate of.
     * @return The FloatInputPoll representing the rate.
     */
    public static FloatInputPoll findRate(final FloatInputPoll input) {
        Mixing.checkNull(input);
        return new FloatInputPoll() {
            private float lastValue = input.get();

            public synchronized float get() {
                float next = input.get();
                float out = next - lastValue;
                lastValue = next;
                return out;
            }
        };
    }

    /**
     * Returns a FloatInputPoll representing the delta between the current value
     * of input and the value in the last cycle, denoted by the specified
     * EventInput.
     *
     * If you only need to use this in one place, then using findRate with one
     * argument might be a better choice.
     *
     * @param input The input value to find the rate of.
     * @param updateWhen When to update the current state, so that the delta is
     * from the last update of this.
     * @return The FloatInputPoll representing the rate.
     */
    public static FloatInputPoll findRate(final FloatInputPoll input, final EventInput updateWhen) {
        Mixing.checkNull(input, updateWhen);
        return new FloatInputPoll() {
            private float lastValue = input.get();

            {
                updateWhen.send(new EventOutput() {
                    public void event() {
                        lastValue = input.get();
                    }
                });
            }

            public synchronized float get() {
                return input.get() - lastValue;
            }
        };
    }

    /**
     * When the input has changed by more than a certain value, the returned
     * EventInput fires. (This is checked when checkTrigger is fired.)
     *
     * This maximum delta can be over any period of time - the delta is
     * calculated relative to the value the last time the event fired.
     *
     * @param input the input to track.
     * @param delta the value by which the input must change to produce an
     * output.
     * @param checkTrigger when to recheck the input.
     * @return the EventInput that fires when the input changes enough.
     */
    public static EventInput whenFloatChanges(final FloatInputPoll input, final float delta, EventInput checkTrigger) {
        Mixing.checkNull(input, checkTrigger);
        return whenFloatChanges(createDispatch(input, checkTrigger), delta);
    }

    /**
     * When the input has changed by more than a certain value, the returned
     * EventInput fires.
     *
     * This maximum delta can be over any period of time - the delta is
     * calculated relative to the value the last time the event fired.
     *
     * @param input the input to track.
     * @param delta the value by which the input must change to produce an
     * output.
     * @return the EventInput that fires when the input changes enough.
     */
    public static EventInput whenFloatChanges(final FloatInput input, final float delta) {
        Mixing.checkNull(input);
        final EventStatus out = new EventStatus();
        input.send(new FloatOutput() {
            float last = input.get();

            public void set(float value) {
                if (Math.abs(last - value) > delta) {
                    out.produce();
                }
            }
        });
        return out;
    }

    /**
     * Returns a scaled version of the specified input, such that when the value
     * from the specified input is the value in the one parameter, the output is
     * 1.0, and when the value from the specified input is the value in the zero
     * parameter, the output is 0.0. The value is linearly scaled, for example:
     * a value of ((zero + one) / 2) will create an output of 0.5. There is no
     * capping - the output can be any number, including a number out of the
     * range of zero to one.
     *
     * The scaling is equivalent to:
     * <code>(base.get() - zero) / (one - zero)</code>
     *
     * @param base the value to scale.
     * @param zero the value of base that turns into 0.0.
     * @param one the value of base that turns into 1.0.
     * @return the scaled value.
     */
    public static FloatInputPoll normalizeFloat(final FloatInputPoll base, final float zero, float one) {
        Mixing.checkNull(base);
        final float range = one - zero;
        return new FloatInputPoll() {
            public float get() {
                return (base.get() - zero) / range;
            }
        };
    }

    /**
     * Returns a scaled version of the specified input, such that when the value
     * from the specified input is the value in the one parameter, the output is
     * 1.0, and when the value from the specified input is the value in the zero
     * parameter, the output is 0.0. The value is linearly scaled, for example:
     * a value of ((zero + one) / 2) will create an output of 0.5. There is no
     * capping - the output can be any number, including a number out of the
     * range of zero to one.
     *
     * The scaling is equivalent to:
     * <code>(base.get() - zero.get()) / (one.get() - zero.get())</code>
     *
     * @param base the value to scale.
     * @param zero the value of base that turns into 0.0.
     * @param one the value of base that turns into 1.0.
     * @return the scaled value.
     */
    public static FloatInputPoll normalizeFloat(final FloatInputPoll base, final FloatInputPoll zero, final FloatInputPoll one) {
        Mixing.checkNull(base, zero, one);
        return new FloatInputPoll() {
            public float get() {
                float zeroN = zero.get();
                return (base.get() - zeroN) / (one.get() - zeroN);
            }
        };
    }

    /**
     * Returns a scaled version of the specified input, such that when the value
     * from the specified input is the value in the one parameter, the output is
     * 1.0, and when the value from the specified input is the value in the zero
     * parameter, the output is 0.0. The value is linearly scaled, for example:
     * a value of ((zero + one) / 2) will create an output of 0.5. There is no
     * capping - the output can be any number, including a number out of the
     * range of zero to one.
     *
     * The scaling is equivalent to:
     * <code>(base.get() - zero) / (one - zero)</code>
     *
     * @param base the value to scale.
     * @param zero the value of base that turns into 0.0.
     * @param one the value of base that turns into 1.0.
     * @return the scaled value.
     */
    public static FloatInput normalizeFloat(final FloatInput base, final float zero, float one) {
        Mixing.checkNull(base);
        return createDispatch(normalizeFloat((FloatInputPoll) base, zero, one), FloatMixing.onUpdate(base));
    }

    /**
     * Returns a scaled version of the specified input, such that when the value
     * from the specified input is the value in the one parameter, the output is
     * 1.0, and when the value from the specified input is the value in the zero
     * parameter, the output is 0.0. The value is linearly scaled, for example:
     * a value of ((zero + one) / 2) will create an output of 0.5. There is no
     * capping - the output can be any number, including a number out of the
     * range of zero to one.
     *
     * Note that this will only update when the value changes, not (necessarily)
     * when the ranges change.
     *
     * The scaling is equivalent to:
     * <code>(base.get() - zero.get()) / (one.get() - zero.get())</code>
     *
     * @param base the value to scale.
     * @param zero the value of base that turns into 0.0.
     * @param one the value of base that turns into 1.0.
     * @return the scaled value.
     */
    public static FloatInput normalizeFloat(final FloatInput base, final FloatInputPoll zero, final FloatInputPoll one) {
        Mixing.checkNull(base, zero, one);
        return createDispatch(normalizeFloat((FloatInputPoll) base, zero, one), FloatMixing.onUpdate(base));
    }

    /**
     * Get an EventInput that updates whenever the input changes.
     *
     * @param input the input to track.
     * @return an input for when the given input changes.
     */
    public static EventInput onUpdate(FloatInput input) {
        Mixing.checkNull(input);
        EventStatus status = new EventStatus();
        onUpdate(input, status);
        return status;
    }

    /**
     * Fire an EventOutput whenever the specified input changes.
     *
     * @param input the input to track
     * @param output the output to fire
     */
    public static void onUpdate(FloatInput input, EventOutput output) {
        Mixing.checkNull(input, output);
        input.send(onUpdate(output));
    }

    /**
     * Return a FloatOutput that will fire an EventOutput whenever it changes.
     *
     * @param output the output to fire.
     * @return the output to track.
     */
    public static FloatOutput onUpdate(final EventOutput output) {
        Mixing.checkNull(output);
        return new FloatOutput() {
            private float last = Float.NaN;

            public void set(float out) {
                if (out != last) {
                    output.event();
                }
            }
        };
    }

    private FloatMixing() {
    }
}

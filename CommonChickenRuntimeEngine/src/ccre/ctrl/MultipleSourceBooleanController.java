/*
 * Copyright 2013-2014 Colby Skeggs
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
import ccre.channel.BooleanOutput;
import ccre.channel.EventOutput;
import ccre.util.CArrayList;

/**
 * A controller that combines a series of registered BooleanOutputs or
 * BooleanInputs to create a single output or input line.
 *
 * This is a BooleanInput representing the current value, and an EventOutput
 * such that once the event is fired, the output will be correct.
 *
 * @author skeggsc
 */
public final class MultipleSourceBooleanController implements BooleanInput, EventOutput {

    /**
     * Passed to the constructor to make the inputs be combined in an AND
     * fashion.
     */
    public static final boolean AND = true;
    /**
     * Passed to the constructor to make the inputs be combined in an OR
     * fashion.
     */
    public static final boolean OR = false;

    /**
     * The list of polled inputs that are read during the update method.
     */
    private final CArrayList<BooleanInputPoll> ipl = new CArrayList<BooleanInputPoll>();
    /**
     * The list of current values for the asynchronously-updated inputs. Any
     * elements in this list MUST be either Boolean.TRUE or Boolean.FALSE! Even
     * the result of new Boolean(true) is not allowed!
     */
    private final CArrayList<Boolean> bcur = new CArrayList<Boolean>();
    /**
     * The list of consumers to be notified when the value changes.
     */
    private final CArrayList<BooleanOutput> consumers = new CArrayList<BooleanOutput>();
    /**
     * The current value of the result.
     */
    private boolean lastValue = false;
    /**
     * If the operation is an AND operation as opposed to an OR operation.
     */
    private final boolean isAnd;

    /**
     * Create a new MultipleSourceBooleanController, either as an AND operation
     * over its boolean set or an OR operation
     *
     * @param isAndOperation if an AND operation should be used. an OR operation
     * is used otherwise. The constants AND and OR can be used for nicer-looking
     * code.
     */
    public MultipleSourceBooleanController(boolean isAndOperation) {
        isAnd = isAndOperation;
    }

    /**
     * Get one BooleanOutput that can be written to in order to update its
     * element of the boolean set.
     *
     * @param dflt the default value before anything is written.
     * @return the BooleanOutput that can be written to.
     */
    public synchronized BooleanOutput getOutput(boolean dflt) {
        final int cur = bcur.size();
        bcur.add(dflt);
        update();
        return new BooleanOutputElement(cur);
    }

    /**
     * Place the specified BooleanInput as an element in the boolean set.
     *
     * @param inp the boolean to include.
     */
    public synchronized void addInput(BooleanInput inp) {
        inp.send(getOutput(inp.get()));
        update();
    }

    /**
     * Place the specified BooleanInput as an element in the boolean set. Since
     * it is a polling boolean, its value will only be polled when another
     * element changes.
     *
     * @param inp the boolean to include.
     */
    public synchronized void addInput(BooleanInputPoll inp) {
        ipl.add(inp);
        update();
    }

    /**
     * Update the output from the current state.
     */
    private synchronized void update() {
        boolean valOut;
        if (isAnd) {
            if (bcur.contains(Boolean.FALSE)) {
                valOut = false;
            } else {
                valOut = true;
                for (BooleanInputPoll p : ipl) {
                    if (!p.get()) {
                        valOut = false;
                        break;
                    }
                }
            }
        } else {
            if (bcur.contains(Boolean.TRUE)) {
                valOut = true;
            } else {
                valOut = false;
                for (BooleanInputPoll p : ipl) {
                    if (p.get()) {
                        valOut = true;
                        break;
                    }
                }
            }
        }
        if (valOut != lastValue) {
            lastValue = valOut;
            notifyConsumers();
        }
    }

    private void notifyConsumers() {
        for (BooleanOutput cnsm : consumers) {
            cnsm.set(lastValue);
        }
    }

    public boolean get() {
        return lastValue;
    }

    public void send(BooleanOutput output) {
        consumers.add(output);
        output.set(get());
    }

    public void unsend(BooleanOutput output) {
        consumers.remove(output);
    }

    public void event() {
        update();
    }

    private class BooleanOutputElement implements BooleanOutput {

        private final int cur;

        BooleanOutputElement(int cur) {
            this.cur = cur;
        }

        public void set(boolean value) {
            bcur.set(cur, value);
            update();
        }
    }
}

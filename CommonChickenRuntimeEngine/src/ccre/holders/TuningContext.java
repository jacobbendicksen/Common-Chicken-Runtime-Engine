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
package ccre.holders;

import ccre.chan.FloatStatus;
import ccre.cluck.CluckNode;
import ccre.event.EventConsumer;
import ccre.saver.StorageProvider;
import ccre.saver.StorageSegment;

/**
 * A TuningContext represents a context in which variables can be saved and
 * published to the network.
 *
 * @author skeggsc
 */
public class TuningContext { // TODO: Support booleans for tuning.

    /**
     * The node to publish the value to.
     */
    protected CluckNode enc;
    /**
     * The segment to store the value in.
     */
    protected StorageSegment seg;

    /**
     * Create a new TuningContext from a specified CluckNode and name of storage
     * (used to find the StorageSegment)
     *
     * @param node the CluckNode to share values over.
     * @param storageName the storage name to save values to.
     */
    public TuningContext(CluckNode node, String storageName) {
        this(node, StorageProvider.openStorage(storageName));
    }

    /**
     * Create a new TuningContext from a specified CluckNode and a specified
     * StorageSegment.
     *
     * @param enc the CluckNode to share values over.
     * @param seg the segment to save values to.
     */
    public TuningContext(CluckNode enc, StorageSegment seg) {
        this.enc = enc;
        this.seg = seg;
    }

    /**
     * Get a FloatStatus with the specified name and default value. This will be
     * tunable over the network and saved on the cRIO once flush() is called.
     *
     * @param name the name of the tunable value.
     * @param default_ the default value.
     * @return the FloatStatus representing the current value.
     */
    public FloatStatus getFloat(String name, float default_) {
        FloatStatus out = new FloatStatus(default_);
        seg.attachFloatHolder(name, out);
        enc.publish(name, out);
        return out;
    }

    /**
     * Get a FloatStatus with the specified name, default value, and the name of
     * a encoded channel for a FloatInputProducer that should be an option to
     * tune the variable to. This will be tunable over the network and saved on
     * the cRIO once flush() is called.
     *
     * @param name the name of the tunable value.
     * @param default_ the default value.
     * @param targetref the name of the shared value for the tuning's default.
     * @return the FloatStatus representing the current value.
     */
    public FloatStatus getFloat(String name, float default_, String targetref) {
        FloatStatus out = new FloatStatus(default_);
        seg.attachFloatHolder(name, out);
        enc.publish(name, out);
        return out;
    }

    /**
     * Flush the StorageSegment - save the current value.
     */
    public void flush() {
        seg.flush();
    }

    /**
     * Get an event that flushes this object.
     *
     * @return the EventConsumer that will flush this object.
     * @see #flush()
     */
    public EventConsumer getFlushEvent() {
        return new EventConsumer() {
            public void eventFired() {
                flush();
            }
        };
    }

    /**
     * Publish an EventConsumer that can be used to save the tuning variables on
     * this context.
     *
     * @param name The name for the EventConsumer to be published under.
     * (Prefixed by "Save Tuning for ".)
     * @return This TuningContext. Returned for method chaining purposes.
     */
    public TuningContext publishSavingEvent(String name) {
        enc.publish("Save Tuning for " + name, getFlushEvent()); // TODO: Say something when tuned.
        return this;
    }
}

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
package ccre.holders;

import java.io.OutputStream;
import java.io.Serializable;

import ccre.channel.EventInput;
import ccre.channel.EventOutput;
import ccre.channel.EventStatus;
import ccre.util.LineCollectorOutputStream;

/**
 * A holder for a String value. Has methods to modify the state, and subscribe
 * to changes.
 *
 * @author skeggsc
 */
public class StringHolder implements Serializable {

    private static final long serialVersionUID = -7141109669601816028L;
    /**
     * The current value.
     */
    private String value;
    /**
     * If the string has been modified yet from any default/nonexistence value.
     */
    private boolean hasModified;
    /**
     * The event fired when the value is changed.
     */
    private final EventStatus evt = new EventStatus();

    /**
     * Create a new StringHolder as if the value is not a default/nonexistence
     * value.
     *
     * @param value the string value.
     */
    public StringHolder(String value) {
        this.value = value;
        hasModified = true;
    }

    /**
     * Create a new StringHolder. If mod is false, the value is interpreted as a
     * default/nonexistence value.
     *
     * @param value the string value.
     * @param mod whether or not the value should be flagged as modified.
     */
    public StringHolder(String value, boolean mod) {
        this.value = value;
        hasModified = mod;
    }

    /**
     * Get the event that is fired when the value changes.
     *
     * @return the event.
     */
    public EventInput getModifiedEvent() {
        return evt;
    }

    /**
     * When the value changes, fire the specified event.
     *
     * @param event the event to fire.
     */
    public void whenModified(EventOutput event) {
        evt.send(event);
    }

    /**
     * Get the current value of the StringHolder.
     *
     * @return the current value.
     */
    public String get() {
        return value;
    }

    /**
     * Get whether or not the value has been modified. This is only false in
     * some rare scenarios, because usually the default value counts as a
     * modification.
     *
     * @return whether or not the value has been modified.
     */
    public boolean hasModified() {
        return hasModified;
    }

    /**
     * Modify the value, including setting this as modified and firing
     * registered events if the value is different.
     *
     * @param value the new value.
     */
    public void set(String value) {
        hasModified = true;
        if (this.value.equals(value)) {
            return;
        }
        this.value = value;
        evt.produce();
    }

    /**
     * Notify listeners that the value has changed.
     */
    public void notifyChanged() {
        evt.produce();
    }

    /**
     * Get an output stream that writes to this StringHolder. Each line will be
     * a new update to the value of the holder.
     *
     * @return the output stream that modifies the state of this.
     */
    public OutputStream getOutput() {
        return new LineCollectorOutputStream() {
            @Override
            protected void collect(String param) {
                set(param);
            }
        };
    }
}

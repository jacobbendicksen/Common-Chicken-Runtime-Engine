/*
 * Copyright 2013 Vincent Miller
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

package ccre.obsidian;

import ccre.chan.FloatInputPoll;

/**
 *
 * @author millerv
 */
public class EmulatorAnalogInput implements FloatInputPoll {
    private final EmulatorPin pin;
    
    public EmulatorAnalogInput(EmulatorPin pin) {
        this.pin = pin;
    }

    @Override
    public float readValue() {
        if (pin.getMode() == EmulatorPin.Mode.ANALOG_IN) {
            return pin.getFloat();
        } else {
            throw new UnsupportedOperationException("Cannot read float in mode: " + pin.getMode().name());
        }
    }
}
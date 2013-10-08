/*
 * Copyright 2013 Colby Skeggs
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
package ccre.phidget;

import ccre.chan.BooleanInput;
import ccre.chan.BooleanOutput;
import ccre.chan.FloatInput;
import ccre.cluck.CluckGlobals;
import ccre.holders.StringHolder;

/**
 * A system to read data over the network about the Phidget system, and provide
 * it in a structured format.
 *
 * @author skeggsc
 */
public class PhidgetReader {

    static {
        CluckGlobals.ensureInitializedCore();
    }
    /**
     * Digital outputs on the phidget.
     */
    public final static BooleanOutput[] digitalOutputs = new BooleanOutput[8];

    static {
        for (int i = 0; i < digitalOutputs.length; i++) {
            digitalOutputs[i] = CluckGlobals.encoder.subscribeBooleanOutput("phidget-bo" + i);
        }
    }
    /**
     * Digital inputs on the phidget.
     */
    public static final BooleanInput[] digitalInputs = new BooleanInput[8];

    static {
        for (int i = 0; i < digitalInputs.length; i++) {
            digitalInputs[i] = CluckGlobals.encoder.subscribeBooleanInputProducer("phidget-bi" + i, false);
        }
    }
    /**
     * Analog inputs on the phidget.
     */
    public static final FloatInput[] analogInputs = new FloatInput[8];

    static {
        for (int i = 0; i < analogInputs.length; i++) {
            analogInputs[i] = CluckGlobals.encoder.subscribeFloatInputProducer("phidget-ai" + i, 0);
        }
    }
    /**
     * LCD lines on the phidget.
     */
    public static final StringHolder[] lcdLines = new StringHolder[2];

    static {
        for (int i = 0; i < 2; i++) {
            lcdLines[i] = CluckGlobals.encoder.subscribeStringHolder("phidget-lcd" + i, "?");
        }
    }
}
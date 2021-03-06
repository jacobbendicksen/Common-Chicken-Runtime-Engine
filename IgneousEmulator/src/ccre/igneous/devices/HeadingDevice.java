/*
 * Copyright 2014-2015 Colby Skeggs
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
package ccre.igneous.devices;

import ccre.igneous.Device;
import ccre.igneous.components.SpacingComponent;
import ccre.igneous.components.TextComponent;

/**
 * A device simply used before a list of other devices to give a title.
 *
 * @author skeggsc
 */
public class HeadingDevice extends Device {

    /**
     * Create a new HeadingDevice with a fixed title.
     *
     * @param title the title to display.
     */
    public HeadingDevice(String title) {
        add(new SpacingComponent(30));
        add(new TextComponent(title));
    }
}

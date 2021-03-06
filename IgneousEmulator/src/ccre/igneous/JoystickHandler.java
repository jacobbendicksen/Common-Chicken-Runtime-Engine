/*
 * Copyright 2015 Colby Skeggs
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
package ccre.igneous;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.WeakHashMap;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import ccre.channel.BooleanInputPoll;
import ccre.channel.EventInput;
import ccre.channel.EventOutput;
import ccre.channel.FloatInputPoll;
import ccre.ctrl.AbstractJoystickWithPOV;
import ccre.ctrl.IJoystickWithPOV;
import ccre.log.Logger;

/**
 * Uses JInput to allow the Emulator to work with physical joysticks.
 *
 * @author skeggsc
 */
public class JoystickHandler {
    static {
        File f;
        try {
            f = File.createTempFile("joystick-binaries", "");
            if (!f.delete()) {
                Logger.warning("Could not delete JInput temporary file.");
            } else if (!f.mkdir()) {
                Logger.warning("Could not create JInput temporary directory.");
            } else {
                f.deleteOnExit();
                Logger.info("Putting binaries in: " + f);
                Properties props = new Properties();
                props.load(JoystickHandler.class.getResourceAsStream("/natives.properties"));
                Object str = props.get("natives");
                if (!(str instanceof String)) {
                    throw new IOException("Bad type for natives field: " + str);
                }
                String[] natives = ((String) str).split(";");
                for (String s : natives) {
                    File out = new File(f, s);
                    InputStream inp = JoystickHandler.class.getResourceAsStream("/" + s);
                    if (inp == null) {
                        throw new IOException("Could not find resource: /" + s);
                    }
                    try {
                        OutputStream outp = new FileOutputStream(out);
                        try {
                            byte[] data = new byte[4096];
                            while (true) {
                                int len = inp.read(data);
                                if (len == -1) {
                                    break;
                                }
                                outp.write(data, 0, len);
                            }
                        } finally {
                            outp.close();
                        }
                    } finally {
                        inp.close();
                    }
                }
                System.setProperty("net.java.games.input.librarypath", f.getAbsolutePath());
            }
        } catch (IOException e) {
            Logger.warning("Could not unpack binaries for JInput", e);
        }
        ControllerEnvironment.getDefaultEnvironment().getControllers();
    }

    /**
     * A holder for physical Joysticks that translates the active Joystick into
     * a single interface.
     *
     * @author skeggsc
     */
    public static class ExternalJoystickHolder {

        private JoystickWrapper ctrl;

        /**
         * Check if a Joystick is currently associated.
         *
         * @return if any Joystick is associated.
         */
        public boolean hasJoystick() {
            return ctrl != null;
        }

        /**
         * Associate a new Joystick.
         *
         * @param wrapper the Joystick to associate.
         */
        public void setJoystick(JoystickWrapper wrapper) {
            this.ctrl = wrapper;
        }

        /**
         * Create an interface to allow access to the currently associated
         * Joystick.
         *
         * This method does NOT need to be called again when the associated
         * Joystick changes.
         *
         * @param check when to update the Joystick.
         * @return the Joystick interface.
         */
        public IJoystickWithPOV getJoystick(EventInput check) {
            check.send(new EventOutput() {
                public void event() {
                    if (ctrl != null) {
                        ctrl.ctrl.poll();
                    }
                }
            });
            return new AbstractJoystickWithPOV(check) {

                public BooleanInputPoll getButtonChannel(final int button) {
                    return new BooleanInputPoll() {
                        public boolean get() {
                            if (ctrl == null) {
                                return false;
                            }
                            ArrayList<Component> buttons = ctrl.buttons;
                            if (button < 1 || button > buttons.size()) {
                                return false;
                            }
                            return buttons.get(button - 1).getPollData() > 0.5f;
                        }
                    };
                }

                public FloatInputPoll getAxisChannel(final int axis) {
                    return new FloatInputPoll() {
                        public float get() {
                            if (ctrl == null) {
                                return 0.0f;
                            }
                            ArrayList<Component> axes = ctrl.axes;
                            if (ctrl.isXBox()) {
                                // Split axis 3 into axes 3 and 4.
                                if (axis >= 5 && axis <= 6) {
                                    return axes.get(axis - 2).getPollData();
                                } else if (axis == 3) {
                                    float raw = axes.get(2).getPollData();
                                    return raw > 0 ? raw : 0;
                                } else if (axis == 4) {
                                    float raw = axes.get(2).getPollData();
                                    return raw < 0 ? -raw : 0;
                                }
                            }
                            if (axis < 1 || axis > axes.size()) {
                                return 0.0f;
                            }
                            return axes.get(axis - 1).getPollData();
                        }
                    };
                }

                public BooleanInputPoll isPOVPressed(final int id) {
                    return new BooleanInputPoll() {
                        public boolean get() {
                            if (id != 1 || ctrl == null || ctrl.pov == null) {
                                return false;
                            }
                            return ctrl.pov.getPollData() > 0.0f;
                        }
                    };
                }

                public FloatInputPoll getPOVAngle(final int id) {
                    return new FloatInputPoll() {
                        public float get() {
                            if (id != 1 || ctrl == null || ctrl.pov == null) {
                                return 0.0f;
                            }
                            return (ctrl.pov.getPollData() * 360 + 270) % 360;
                        }
                    };
                }
            };
        }

    }

    /**
     * A wrapped physical Joystick that has organized buttons and axes.
     *
     * @author skeggsc
     */
    public static class JoystickWrapper {

        private final Controller ctrl;

        private final ArrayList<Component> buttons = new ArrayList<Component>();
        private final ArrayList<Component> axes = new ArrayList<Component>();
        private Component pov;

        private JoystickWrapper(Controller ctrl) {
            this.ctrl = ctrl;
        }

        private boolean isXBox() {
            return ctrl.getName().contains("XBOX 360 For Windows") && axes.size() == 5;
        }

        public String toString() {
            return ctrl + " on " + ctrl.getPortType() + ":" + ctrl.getPortNumber();
        }

        private void start() {
            Logger.info("Started: " + ctrl + ": " + ctrl.getType());
            axes.clear();
            buttons.clear();
            axes.add(null);
            axes.add(null);
            axes.add(null);
            axes.add(null);
            axes.add(null);
            for (Component comp : ctrl.getComponents()) {
                Logger.info("Component: " + comp);
                if (comp.getIdentifier() instanceof Component.Identifier.Button) {
                    buttons.add(comp);
                } else if (comp.getIdentifier() instanceof Component.Identifier.Axis) {
                    if (comp.getIdentifier() == Component.Identifier.Axis.X) {
                        axes.set(0, comp);
                    } else if (comp.getIdentifier() == Component.Identifier.Axis.Y) {
                        axes.set(1, comp);
                    } else if (comp.getIdentifier() == Component.Identifier.Axis.Z) {
                        axes.set(2, comp);
                    } else if (comp.getIdentifier() == Component.Identifier.Axis.RX) {
                        axes.set(3, comp);
                    } else if (comp.getIdentifier() == Component.Identifier.Axis.RY) {
                        axes.set(4, comp);
                    } else if (comp.getIdentifier() == Component.Identifier.Axis.POV) {
                        pov = comp;
                    } else {
                        axes.add(comp);
                    }
                }
            }
            while (axes.contains(null)) {
                axes.remove(null);
            }
            Logger.info("B/A/P: " + buttons + "/" + axes + "/" + pov);
            if (isXBox()) {
                Logger.info("This is a 5-axis XBOX controller, which means that it's not going to show up the same as on the real robot.");
                Logger.info("To resolve this, the emulator will remap the trigger axis into the two separate axes - but it won't work exactly the same.");
                Logger.info("Notably, unlike the real robot, we don't know the difference between pressing both axes and pressing neither axes.");
                Logger.info("So, it will think that neither are pressed in this scenario.");
            }
        }
    }

    private final WeakHashMap<Controller, JoystickWrapper> wrappings = new WeakHashMap<Controller, JoystickWrapper>();

    private boolean isIgnored(Controller ctrl) {
        Controller.Type t = ctrl.getType();
        return t == Controller.Type.MOUSE || t == Controller.Type.KEYBOARD || t == Controller.Type.UNKNOWN;
    }

    /**
     * Get a Joystick that currently has a button held down.
     *
     * @return the detected Joystick, or null if none are found.
     */
    public JoystickWrapper getActivelyPressedJoystick() {
        for (Controller ctrl : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
            if (isIgnored(ctrl)) {
                continue;
            }
            ctrl.poll();
            for (Component comp : ctrl.getComponents()) {
                if (comp.getIdentifier() instanceof Component.Identifier.Button) {
                    if (comp.getPollData() > 0.5f) {
                        return getOrWrap(ctrl);
                    }
                }
            }
        }
        return null;
    }

    private JoystickWrapper getOrWrap(Controller ctrl) {
        JoystickWrapper w = wrappings.get(ctrl);
        if (w == null) {
            w = new JoystickWrapper(ctrl);
            wrappings.put(ctrl, w);
            w.start();
        }
        return w;
    }
}

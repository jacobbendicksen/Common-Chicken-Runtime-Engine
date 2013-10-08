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
package ccre.igneous;

import ccre.chan.*;
import ccre.ctrl.*;
import ccre.event.EventSource;

/**
 * A Core class for Igneous. Extend this (or SimpleCore, which is easier) in
 * order to write an application to run on the robot.
 *
 * @see SimpleCore
 * @author skeggsc
 */
public abstract class IgneousCore {

    /**
     * The launcher that provides all implementations for this.
     */
    IgneousLauncher launcher;
    /**
     * Produced during every state where the driver station is attached.
     */
    protected EventSource globalPeriodic;
    /**
     * Produced when the robot enters autonomous mode.
     */
    protected EventSource startedAutonomous;
    /**
     * Produced during autonomous mode.
     */
    protected EventSource duringAutonomous;
    /**
     * Produced when the robot enters disabled mode.
     */
    protected EventSource robotDisabled;
    /**
     * Produced while the robot is disabled.
     */
    protected EventSource duringDisabled;
    /**
     * Produced when the robot enters teleop mode.
     */
    protected EventSource startedTeleop;
    /**
     * Produced during teleop mode.
     */
    protected EventSource duringTeleop;
    /**
     * Produced when the robot enters testing mode.
     */
    protected EventSource startedTesting;
    /**
     * Produced during testing mode.
     */
    protected EventSource duringTesting;

    /**
     * Implement this method - it should set up everything that your robot needs
     * to do.
     */
    protected abstract void createRobotControl();

    // Factory methods
    /**
     * Get an ISimpleJoystick for the specified joystick ID.
     *
     * @param id the joystick ID, from 1 to 4, inclusive.
     * @return the ISimpleJoystick.
     * @see #makeDispatchJoystick(int)
     */
    protected final ISimpleJoystick makeSimpleJoystick(int id) {
        return launcher.makeSimpleJoystick(id);
    }

    /**
     * Get an IDispatchJoystick for the specified joystick ID. The joystick will
     * update the inputs and events during teleop mode only.
     *
     * This is equivalent to
     * <code>makeDispatchJoystick(id, duringTeleop)</code>.
     *
     * @param id the joystick ID, from 1 to 4, inclusive.
     * @return the IDispatchJoystick.
     * @see #makeSimpleJoystick(int)
     * @see #makeDispatchJoystick(int, ccre.event.EventSource)
     */
    protected final IDispatchJoystick makeDispatchJoystick(int id) {
        return launcher.makeDispatchJoystick(id, duringTeleop);
    }

    /**
     * Get an IDispatchJoystick for the specified joystick ID. The joystick will
     * update the inputs and events when the specified event is fired.
     *
     * @param id the joystick ID, from 1 to 4, inclusive.
     * @param source when to update the joystick.
     * @return the IDispatchJoystick.
     * @see #makeSimpleJoystick(int)
     * @see #makeDispatchJoystick(int)
     */
    protected final IDispatchJoystick makeDispatchJoystick(int id, EventSource source) {
        return launcher.makeDispatchJoystick(id, source);
    }
    /**
     * Signifies that the motor should be directly outputted without negation.
     */
    public static final boolean MOTOR_FORWARD = false;
    /**
     * Signifies that the motor should be outputted after negating the value.
     */
    public static final boolean MOTOR_REVERSE = true;

    /**
     * Create a reference to a Jaguar speed controller on the specified ID and
     * motor reversal.
     *
     * @param id the motor port ID, from 1 to 10, inclusive.
     * @param negate MOTOR_FORWARD if the motor should be unmodified,
     * MOTOR_REVERSE if the motor should be reversed.
     * @return the output that will output to the specified motor.
     * @see #MOTOR_FORWARD
     * @see #MOTOR_FREVERSE
     */
    protected final FloatOutput makeJaguarMotor(int id, boolean negate) {
        return launcher.makeJaguar(id, negate);
    }

    /**
     * Create a reference to a Victor speed controller on the specified ID and
     * motor reversal.
     *
     * @param id the motor port ID, from 1 to 10, inclusive.
     * @param negate MOTOR_FORWARD if the motor should be unmodified,
     * MOTOR_REVERSE if the motor should be reversed.
     * @return the output that will output to the specified motor.
     * @see #MOTOR_FORWARD
     * @see #MOTOR_FREVERSE
     */
    protected final FloatOutput makeVictorMotor(int id, boolean negate) {
        return launcher.makeVictor(id, negate);
    }

    /**
     * Create a reference to a Talon speed controller on the specified ID and
     * motor reversal.
     *
     * @param id the motor port ID, from 1 to 10, inclusive.
     * @param negate MOTOR_FORWARD if the motor should be unmodified,
     * MOTOR_REVERSE if the motor should be reversed.
     * @return the output that will output to the specified motor.
     * @see #MOTOR_FORWARD
     * @see #MOTOR_FREVERSE
     */
    protected final FloatOutput makeTalonMotor(int id, boolean negate) {
        return launcher.makeTalon(id, negate);
    }

    /**
     * Create a reference to a solenoid on the specified port.
     *
     * @param id the port of the solenoid.
     * @return the output that will control the solenoid.
     */
    protected final BooleanOutput makeSolenoid(int id) {
        return launcher.makeSolenoid(id);
    }

    /**
     * Create a reference to an analog input on the specified port with the
     * specified number of average bits.
     *
     * @param id the port number.
     * @param averageBits the number of averaging bits.
     * @return the analog input, reporting in voltage.
     */
    protected final FloatInputPoll makeAnalogInput(int id, int averageBits) {
        return launcher.makeAnalogInput(id, averageBits);
    }

    /**
     * Create a reference to an analog input's raw value on the specified port
     * with the specified number of average bits.
     *
     * @param id the port number.
     * @param averageBits the number of averaging bits.
     * @return the analog input, reporting in uncalibrated units.
     */
    protected final FloatInputPoll makeAnalogInput_ValueBased(int id, int averageBits) {
        return launcher.makeAnalogInput_ValuedBased(id, averageBits);
    }

    /**
     * Create a reference to a digital input on the spcefiied port.
     *
     * @param id the port number.
     * @return the digital input.
     */
    protected final BooleanInputPoll makeDigitalInput(int id) {
        return launcher.makeDigitalInput(id);
    }

    /**
     * Create a reference to a servo controller for the specified port and
     * minimum and maximum values.
     *
     * @param id the port number.
     * @param minInput the value on the output that should correspond to the
     * servo's minimum position.
     * @param maxInput the value on the output that should correspond to the
     * servo's maximum position.
     * @return
     */
    protected final FloatOutput makeServo(int id, float minInput, float maxInput) {
        return launcher.makeServo(id, minInput, maxInput);
    }

    /**
     * Create an output that will display the current value on the driver
     * station's LCD.
     *
     * @param prefix the prefix, or label, of the output. this is prepended to
     * the value.
     * @param line the line to display the value on, from 1 to 6.
     * @return the output that will write to the LCD.
     */
    protected final FloatOutput makeDSFloatReadout(String prefix, int line) {
        return launcher.makeDSFloatReadout(prefix, line);
    }

    /**
     * Display the current value of the specified FloatInputPoll on the driver
     * station's LCD, whenever the specified event is triggered.
     *
     * @param prefix the prefix, or label, of the output. this is prepended to
     * the value.
     * @param line the line to display the value on, from 1 to 6.
     * @param value the value to display.
     * @param when when to update the output.
     */
    protected final void makeDSFloatReadout(String prefix, int line, FloatInputPoll value, EventSource when) {
        Mixing.pumpWhen(when, value, launcher.makeDSFloatReadout(prefix, line));
    }

    /**
     * Get a boolean input that checks if the robot is currently disabled.
     *
     * @return the input.
     */
    protected final BooleanInputPoll getIsDisabled() {
        return launcher.getIsDisabled();
    }

    /**
     * Get a boolean input that checks if the robot is currently in autonomous,
     * as opposed to teleop.
     *
     * @return the input.
     */
    protected final BooleanInputPoll getIsAutonomous() {
        return launcher.getIsAutonomous();
    }

    /**
     * Activate the compressor on the given pressure switch channel and
     * compressor relay channel.
     *
     * @param pressureSwitchChannel the channel of the pressure switch digital
     * input.
     * @param compressorRelayChannel the channel of the compressor's relay.
     */
    protected final void useCompressor(int pressureSwitchChannel, int compressorRelayChannel) {
        useCustomCompressor(makeDigitalInput(pressureSwitchChannel), compressorRelayChannel);
    }

    /**
     * Activate the compressor on the given pressure switch input and compressor
     * relay channel.
     *
     * @param shouldDisable should the compressor be turned off.
     * @param compressorRelayChannel the channel of the compressor's relay.
     */
    protected final void useCustomCompressor(BooleanInputPoll shouldDisable, int compressorRelayChannel) {
        launcher.useCustomCompressor(shouldDisable, compressorRelayChannel);
    }
}
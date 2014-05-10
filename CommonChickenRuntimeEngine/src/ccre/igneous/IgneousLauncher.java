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
package ccre.igneous;

import ccre.channel.BooleanInputPoll;
import ccre.channel.BooleanOutput;
import ccre.channel.EventInput;
import ccre.channel.FloatInputPoll;
import ccre.channel.FloatOutput;
import ccre.ctrl.IJoystick;

/**
 * This is a launcher for an Igneous application. The reason for this is so that
 * the main program can be ran without a robot. Documentation for all the
 * methods here can be found in IgneousCore, where they are invoked.
 *
 * @author skeggsc
 */
public interface IgneousLauncher {

    public static final int JAGUAR = 1;
    public static final int TALON = 2;
    public static final int VICTOR = 3;

    public IJoystick getJoystick(int id);
    
    public IJoystick getKinectJoystick(boolean isRightArm);

    public FloatOutput makeMotor(int id, int type);

    public BooleanOutput makeSolenoid(int id);

    public BooleanOutput makeDigitalOutput(int id);

    public FloatInputPoll makeAnalogInput(int id, int averageBits);

    public FloatInputPoll makeAnalogInput_ValuedBased(int id, int averageBits);

    public BooleanInputPoll makeDigitalInput(int id);

    public FloatOutput makeServo(int id, float minInput, float maxInput);

    public void sendDSUpdate(String value, int lineid);

    public BooleanInputPoll getIsDisabled();

    public BooleanInputPoll getIsAutonomous();

    public BooleanInputPoll getIsTest();

    public void useCustomCompressor(BooleanInputPoll shouldDisable, int compressorRelayChannel);

    public FloatInputPoll makeEncoder(int aChannel, int bChannel, boolean reverse, EventInput resetWhen);

    public BooleanOutput makeRelayForwardOutput(int channel);

    public BooleanOutput makeRelayReverseOutput(int channel);

    public FloatInputPoll makeGyro(int port, double sensitivity, EventInput object);

    public FloatInputPoll makeAccelerometerAxis(int port, double sensitivity, double zeropoint);

    public FloatInputPoll getBatteryVoltage();

    public EventInput getGlobalPeriodic();

    public EventInput getStartAuto();

    public EventInput getDuringAuto();

    public EventInput getStartTele();

    public EventInput getDuringTele();

    public EventInput getStartTest();

    public EventInput getDuringTest();

    public EventInput getStartDisabled();

    public EventInput getDuringDisabled();
}
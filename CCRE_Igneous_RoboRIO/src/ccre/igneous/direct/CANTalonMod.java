/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2014. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package ccre.igneous.direct;

import edu.wpi.first.wpilibj.hal.CanTalonJNI;
import edu.wpi.first.wpilibj.hal.CanTalonSRX;
import edu.wpi.first.wpilibj.hal.SWIGTYPE_p_CTR_Code;
import edu.wpi.first.wpilibj.hal.SWIGTYPE_p_double;
import edu.wpi.first.wpilibj.hal.SWIGTYPE_p_int;

@SuppressWarnings("javadoc")
class CANTalonMod {

    public enum ControlMode {
        PercentVbus(0), Follower(5), Voltage(4), Position(1), Speed(2), Current(3), Disabled(15);

        public int value;

        public static ControlMode valueOf(int value) {
            for (ControlMode mode : values()) {
                if (mode.value == value) {
                    return mode;
                }
            }

            return null;
        }

        private ControlMode(int value) {
            this.value = value;
        }
    }

    public enum FeedbackDevice {
        QuadEncoder(0), AnalogPot(2), AnalogEncoder(3), EncRising(4), EncFalling(5);

        public int value;

        public static FeedbackDevice valueOf(int value) {
            for (FeedbackDevice mode : values()) {
                if (mode.value == value) {
                    return mode;
                }
            }

            return null;
        }

        private FeedbackDevice(int value) {
            this.value = value;
        }
    }

    /** enumerated types for frame rate ms */
    public enum StatusFrameRate {
        General(0), Feedback(1), QuadEncoder(2), AnalogTempVbat(3);
        public int value;

        public static StatusFrameRate valueOf(int value) {
            for (StatusFrameRate mode : values()) {
                if (mode.value == value) {
                    return mode;
                }
            }
            return null;
        }

        private StatusFrameRate(int value) {
            this.value = value;
        }
    }

    private CanTalonSRX m_impl;
    ControlMode m_controlMode;
    private static long kDelayForSolicitedSignalsMillis = 4;

    int m_deviceNumber;
    boolean m_controlEnabled;
    int m_profile;

    double m_setPoint;

    public CANTalonMod(int deviceNumber) {
        m_deviceNumber = deviceNumber;
        m_impl = new CanTalonSRX(deviceNumber);
        m_controlEnabled = true;
        m_profile = 0;
        m_setPoint = 0;
        setProfile(m_profile);
        applyControlMode(ControlMode.PercentVbus);
    }

    public CANTalonMod(int deviceNumber, int controlPeriodMs) {
        m_deviceNumber = deviceNumber;
        m_impl = new CanTalonSRX(deviceNumber, controlPeriodMs); /*
                                                                  * bound period
                                                                  * to be within
                                                                  * [1 ms,95 ms]
                                                                  */
        m_controlEnabled = true;
        m_profile = 0;
        m_setPoint = 0;
        setProfile(m_profile);
        applyControlMode(ControlMode.PercentVbus);
    }

    public void delete() {
        m_impl.delete();
    }

    /**
     * Sets the appropriate output on the talon, depending on the mode.
     *
     * In PercentVbus, the output is between -1.0 and 1.0, with 0.0 as stopped.
     * In Follower mode, the output is the integer device ID of the talon to
     * duplicate. In Voltage mode, outputValue is in volts. In Current mode,
     * outputValue is in amperes. In Speed mode, outputValue is in position
     * change / 10ms. In Position mode, outputValue is in encoder ticks or an
     * analog value, depending on the sensor.
     *
     * @param outputValue The setpoint value, as described above.
     */
    public void set(double outputValue) {
        if (m_controlEnabled) {
            m_setPoint = outputValue;
            switch (m_controlMode) {
            case PercentVbus:
                m_impl.Set(outputValue);
                break;
            case Follower:
                m_impl.SetDemand((int) outputValue);
                break;
            case Voltage:
                // Voltage is an 8.8 fixed point number.
                int volts = (int) (outputValue * 256);
                m_impl.SetDemand(volts);
                break;
            case Speed:
                m_impl.SetDemand((int) outputValue);
                break;
            case Position:
                m_impl.SetDemand((int) outputValue);
                break;
            default:
                break;
            }
            m_impl.SetModeSelect(m_controlMode.value);
        }
    }

    /**
     * Flips the sign (multiplies by negative one) the sensor values going into
     * the talon.
     *
     * This only affects position and velocity closed loop control. Allows for
     * situations where you may have a sensor flipped and going in the wrong
     * direction.
     *
     * @param flip True if sensor input should be flipped; False if not.
     */
    public void reverseSensor(boolean flip) {
        m_impl.SetRevFeedbackSensor(flip ? 1 : 0);
    }

    /**
     * Flips the sign (multiplies by negative one) the throttle values going
     * into the motor on the talon in closed loop modes.
     *
     * @param flip True if motor output should be flipped; False if not.
     */
    public void reverseOutput(boolean flip) {
        m_impl.SetRevMotDuringCloseLoopEn(flip ? 1 : 0);
    }

    /**
     * Gets the current status of the Talon (usually a sensor value).
     *
     * In Current mode: returns output current. In Speed mode: returns current
     * speed. In Position mode: returns current sensor position. In PercentVbus
     * and Follower modes: returns current applied throttle.
     *
     * @return The current sensor value of the Talon.
     */
    public double get() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        switch (m_controlMode) {
        case Voltage:
            return getOutputVoltage();
        case Current:
            return getOutputCurrent();
        case Speed:
            m_impl.GetSensorVelocity(swigp);
            return (double) CanTalonJNI.intp_value(valuep);
        case Position:
            m_impl.GetSensorPosition(swigp);
            return (double) CanTalonJNI.intp_value(valuep);
        case PercentVbus:
        default:
            m_impl.GetAppliedThrottle(swigp);
            return (double) CanTalonJNI.intp_value(valuep) / 1023.0;
        }
    }

    /**
     * Get the current encoder position, regardless of whether it is the current
     * feedback device.
     *
     * @return The current position of the encoder.
     */
    public int getEncPosition() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetEncPosition(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * Get the current encoder velocity, regardless of whether it is the current
     * feedback device.
     *
     * @return The current speed of the encoder.
     */
    public int getEncVelocity() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetEncVel(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * Get the number of of rising edges seen on the index pin.
     *
     * @return number of rising edges on idx pin.
     */
    public int getNumberOfQuadIdxRises() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetEncIndexRiseEvents(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * @return IO level of QUADA pin.
     */
    public int getPinStateQuadA() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetQuadApin(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * @return IO level of QUADB pin.
     */
    public int getPinStateQuadB() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetQuadBpin(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * @return IO level of QUAD Index pin.
     */
    public int getPinStateQuadIdx() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetQuadIdxpin(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * Get the current analog in position, regardless of whether it is the
     * current feedback device.
     *
     * @return The 24bit analog position. The bottom ten bits is the ADC (0 -
     * 1023) on the analog pin of the Talon. The upper 14 bits tracks the
     * overflows and underflows (continuous sensor).
     */
    public int getAnalogInPosition() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetAnalogInWithOv(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * Get the current analog in position, regardless of whether it is the
     * current feedback device.
     *
     * @return The ADC (0 - 1023) on analog pin of the Talon.
     */
    public int getAnalogInRaw() {
        return getAnalogInPosition() & 0x3FF;
    }

    /**
     * Get the current encoder velocity, regardless of whether it is the current
     * feedback device.
     *
     * @return The current speed of the analog in device.
     */
    public int getAnalogInVelocity() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetAnalogInVel(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    /**
     * Get the current difference between the setpoint and the sensor value.
     *
     * @return The error, in whatever units are appropriate.
     */
    public int getClosedLoopError() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetCloseLoopErr(swigp);
        return CanTalonJNI.intp_value(valuep);
    }

    // Returns true if limit switch is closed. false if open.
    public boolean isFwdLimitSwitchClosed() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetLimitSwitchClosedFor(swigp);
        return (CanTalonJNI.intp_value(valuep) == 0) ? true : false;
    }

    // Returns true if limit switch is closed. false if open.
    public boolean isRevLimitSwitchClosed() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetLimitSwitchClosedRev(swigp);
        return (CanTalonJNI.intp_value(valuep) == 0) ? true : false;
    }

    // Returns true if break is enabled during neutral. false if coast.
    public boolean getBrakeEnableDuringNeutral() {
        long valuep = CanTalonJNI.new_intp();
        SWIGTYPE_p_int swigp = new SWIGTYPE_p_int(valuep, true);
        m_impl.GetBrakeIsEnabled(swigp);
        return (CanTalonJNI.intp_value(valuep) == 0) ? false : true;
    }

    /**
     * Returns temperature of Talon, in degrees Celsius.
     */
    public double getTemp() {
        long tempp = CanTalonJNI.new_doublep(); // Create a new swig pointer.
        m_impl.GetTemp(new SWIGTYPE_p_double(tempp, true));
        return CanTalonJNI.doublep_value(tempp);
    }

    /**
     * Returns the current going through the Talon, in Amperes.
     */
    public double getOutputCurrent() {
        long curp = CanTalonJNI.new_doublep(); // Create a new swig pointer.
        m_impl.GetCurrent(new SWIGTYPE_p_double(curp, true));
        return CanTalonJNI.doublep_value(curp);
    }

    /**
     * @return The voltage being output by the Talon, in Volts.
     */
    public double getOutputVoltage() {
        long throttlep = CanTalonJNI.new_intp();
        m_impl.GetAppliedThrottle(new SWIGTYPE_p_int(throttlep, true));
        double voltage = getBusVoltage() * (double) CanTalonJNI.intp_value(throttlep) / 1023.0;
        return voltage;
    }

    /**
     * @return The voltage at the battery terminals of the Talon, in Volts.
     */
    public double getBusVoltage() {
        long voltagep = CanTalonJNI.new_doublep();
        SWIGTYPE_p_CTR_Code status = m_impl.GetBatteryV(new SWIGTYPE_p_double(voltagep, true));
        /*
         * Note: This section needs the JNI bindings regenerated with
         * pointer_functions for CTR_Code included in order to be able to catch
         * notice and throw errors. if (CanTalonJNI.CTR_Codep_value(status) !=
         * 0) { // TODO throw an error. }
         */

        return CanTalonJNI.doublep_value(voltagep);
    }

    /**
     * TODO documentation (see CANJaguar.java)
     *
     * @return The position of the sensor currently providing feedback. When
     * using analog sensors, 0 units corresponds to 0V, 1023 units corresponds
     * to 3.3V When using an analog encoder (wrapping around 1023 to 0 is
     * possible) the units are still 3.3V per 1023 units. When using quadrature,
     * each unit is a quadrature edge (4X) mode.
     */
    public double getPosition() {
        long positionp = CanTalonJNI.new_intp();
        m_impl.GetSensorPosition(new SWIGTYPE_p_int(positionp, true));
        return CanTalonJNI.intp_value(positionp);
    }

    public void setPosition(double pos) {
        m_impl.SetSensorPosition((int) pos);
    }

    /**
     * TODO documentation (see CANJaguar.java)
     *
     * @return The speed of the sensor currently providing feedback.
     *
     * The speed units will be in the sensor's native ticks per 100ms.
     *
     * For analog sensors, 3.3V corresponds to 1023 units. So a speed of 200
     * equates to ~0.645 dV per 100ms or 6.451 dV per second. If this is an
     * analog encoder, that likely means 1.9548 rotations per sec. For
     * quadrature encoders, each unit corresponds a quadrature edge (4X). So a
     * 250 count encoder will produce 1000 edge events per rotation. An example
     * speed of 200 would then equate to 20% of a rotation per 100ms, or 10
     * rotations per second.
     */
    public double getSpeed() {
        long speedp = CanTalonJNI.new_intp();
        m_impl.GetSensorVelocity(new SWIGTYPE_p_int(speedp, true));
        return CanTalonJNI.intp_value(speedp);
    }

    public ControlMode getControlMode() {
        return m_controlMode;
    }

    /**
     * Fixup the m_controlMode so set() serializes the correct demand value.
     * Also fills the modeSelecet in the control frame to disabled.
     *
     * @param controlMode Control mode to ultimately enter once user calls
     * set().
     * @see #set
     */
    private void applyControlMode(ControlMode controlMode) {
        m_controlMode = controlMode;
        if (controlMode == ControlMode.Disabled)
            m_controlEnabled = false;
        // Disable until set() is called.
        m_impl.SetModeSelect(ControlMode.Disabled.value);
    }

    public void changeControlMode(ControlMode controlMode) {
        if (m_controlMode == controlMode) {
            /* we already are in this mode, don't perform disable workaround */
        } else {
            applyControlMode(controlMode);
        }
    }

    public void setFeedbackDevice(FeedbackDevice device) {
        m_impl.SetFeedbackDeviceSelect(device.value);
    }

    public void setStatusFrameRateMs(StatusFrameRate stateFrame, int periodMs) {
        m_impl.SetStatusFrameRate(stateFrame.value, periodMs);
    }

    public void enableControl() {
        changeControlMode(m_controlMode);
        m_controlEnabled = true;
    }

    public void disableControl() {
        m_impl.SetModeSelect(ControlMode.Disabled.value);
        m_controlEnabled = false;
    }

    public boolean isControlEnabled() {
        return m_controlEnabled;
    }

    /**
     * Get the current proportional constant.
     *
     * @return double proportional constant for current profile.
     * @throws InterruptedException
     */
    public double getP() throws InterruptedException {
        //if(!(m_controlMode.equals(ControlMode.Position) || m_controlMode.equals(ControlMode.Speed))) {
        //      throw new IllegalStateException("PID mode only applies in Position and Speed modes.");
        //}

        // Update the information that we have.
        if (m_profile == 0)
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot0_P);
        else
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot1_P);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long pp = CanTalonJNI.new_doublep();
        m_impl.GetPgain(m_profile, new SWIGTYPE_p_double(pp, true));
        return CanTalonJNI.doublep_value(pp);
    }

    public double getI() throws InterruptedException {
        //if(!(m_controlMode.equals(ControlMode.Position) || m_controlMode.equals(ControlMode.Speed))) {
        //      throw new IllegalStateException("PID mode only applies in Position and Speed modes.");
        //}

        // Update the information that we have.
        if (m_profile == 0)
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot0_I);
        else
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot1_I);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long ip = CanTalonJNI.new_doublep();
        m_impl.GetIgain(m_profile, new SWIGTYPE_p_double(ip, true));
        return CanTalonJNI.doublep_value(ip);
    }

    public double getD() throws InterruptedException {
        //if(!(m_controlMode.equals(ControlMode.Position) || m_controlMode.equals(ControlMode.Speed))) {
        //      throw new IllegalStateException("PID mode only applies in Position and Speed modes.");
        //}

        // Update the information that we have.
        if (m_profile == 0)
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot0_D);
        else
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot1_D);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long dp = CanTalonJNI.new_doublep();
        m_impl.GetDgain(m_profile, new SWIGTYPE_p_double(dp, true));
        return CanTalonJNI.doublep_value(dp);
    }

    public double getF() throws InterruptedException {
        //if(!(m_controlMode.equals(ControlMode.Position) || m_controlMode.equals(ControlMode.Speed))) {
        //      throw new IllegalStateException("PID mode only applies in Position and Speed modes.");
        //}

        // Update the information that we have.
        if (m_profile == 0)
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot0_F);
        else
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot1_F);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long fp = CanTalonJNI.new_doublep();
        m_impl.GetFgain(m_profile, new SWIGTYPE_p_double(fp, true));
        return CanTalonJNI.doublep_value(fp);
    }

    public double getIZone() throws InterruptedException {
        //if(!(m_controlMode.equals(ControlMode.Position) || m_controlMode.equals(ControlMode.Speed))) {
        //      throw new IllegalStateException("PID mode only applies in Position and Speed modes.");
        //}

        // Update the information that we have.
        if (m_profile == 0)
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot0_IZone);
        else
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot1_IZone);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long fp = CanTalonJNI.new_intp();
        m_impl.GetIzone(m_profile, new SWIGTYPE_p_int(fp, true));
        return CanTalonJNI.intp_value(fp);
    }

    /**
     * Get the closed loop ramp rate for the current profile.
     *
     * Limits the rate at which the throttle will change. Only affects position
     * and speed closed loop modes.
     *
     * @return rampRate Maximum change in voltage, in volts / sec.
     * @throws InterruptedException
     * @see #setProfile For selecting a certain profile.
     */
    public double getCloseLoopRampRate() throws InterruptedException {
        //      if(!(m_controlMode.equals(ControlMode.Position) || m_controlMode.equals(ControlMode.Speed))) {
        //              throw new IllegalStateException("PID mode only applies in Position and Speed modes.");
        //      }

        // Update the information that we have.
        if (m_profile == 0)
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot0_CloseLoopRampRate);
        else
            m_impl.RequestParam(CanTalonSRX.param_t.eProfileParamSlot1_CloseLoopRampRate);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long fp = CanTalonJNI.new_intp();
        m_impl.GetCloseLoopRampRate(m_profile, new SWIGTYPE_p_int(fp, true));
        double throttlePerMs = CanTalonJNI.intp_value(fp);
        return throttlePerMs / 1023.0 * 12.0 * 1000.0;
    }

    /**
     * @return The version of the firmware running on the Talon
     * @throws InterruptedException
     */
    public long GetFirmwareVersion() throws InterruptedException {

        // Update the information that we have.
        m_impl.RequestParam(CanTalonSRX.param_t.eFirmVers);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long fp = CanTalonJNI.new_intp();
        m_impl.GetParamResponseInt32(CanTalonSRX.param_t.eFirmVers, new SWIGTYPE_p_int(fp, true));
        return CanTalonJNI.intp_value(fp);
    }

    public long GetIaccum() throws InterruptedException {

        // Update the information that we have.
        m_impl.RequestParam(CanTalonSRX.param_t.ePidIaccum);

        // Briefly wait for new values from the Talon.
        Thread.sleep(kDelayForSolicitedSignalsMillis);

        long fp = CanTalonJNI.new_intp();
        m_impl.GetParamResponseInt32(CanTalonSRX.param_t.ePidIaccum, new SWIGTYPE_p_int(fp, true));
        return CanTalonJNI.intp_value(fp);
    }

    /**
     * Clear the accumulator for I gain.
     */
    public void ClearIaccum()
    {
        SWIGTYPE_p_CTR_Code status = m_impl.SetParam(CanTalonSRX.param_t.ePidIaccum, 0);
    }

    /**
     * Set the proportional value of the currently selected profile.
     *
     * @param p Proportional constant for the currently selected PID profile.
     * @see #setProfile For selecting a certain profile.
     */
    public void setP(double p) {
        m_impl.SetPgain(m_profile, p);
    }

    /**
     * Set the integration constant of the currently selected profile.
     *
     * @param i Integration constant for the currently selected PID profile.
     * @see #setProfile For selecting a certain profile.
     */
    public void setI(double i) {
        m_impl.SetIgain(m_profile, i);
    }

    /**
     * Set the derivative constant of the currently selected profile.
     *
     * @param d Derivative constant for the currently selected PID profile.
     * @see #setProfile For selecting a certain profile.
     */
    public void setD(double d) {
        m_impl.SetDgain(m_profile, d);
    }

    /**
     * Set the feedforward value of the currently selected profile.
     *
     * @param f Feedforward constant for the currently selected PID profile.
     * @see #setProfile For selecting a certain profile.
     */
    public void setF(double f) {
        m_impl.SetFgain(m_profile, f);
    }

    /**
     * Set the integration zone of the current Closed Loop profile.
     *
     * Whenever the error is larger than the izone value, the accumulated
     * integration error is cleared so that high errors aren't racked up when at
     * high errors. An izone value of 0 means no difference from a standard PIDF
     * loop.
     *
     * @param izone Width of the integration zone.
     * @see #setProfile For selecting a certain profile.
     */
    public void setIZone(int izone) {
        m_impl.SetIzone(m_profile, izone);
    }

    /**
     * Set the closed loop ramp rate for the current profile.
     *
     * Limits the rate at which the throttle will change. Only affects position
     * and speed closed loop modes.
     *
     * @param rampRate Maximum change in voltage, in volts / sec.
     * @see #setProfile For selecting a certain profile.
     */
    public void setCloseLoopRampRate(double rampRate) {
        // CanTalonSRX takes units of Throttle (0 - 1023) / 1ms.
        int rate = (int) (rampRate * 1023.0 / 12.0 / 1000.0);
        m_impl.SetCloseLoopRampRate(m_profile, rate);
    }

    /**
     * Set the voltage ramp rate for the current profile.
     *
     * Limits the rate at which the throttle will change. Affects all modes.
     *
     * @param rampRate Maximum change in voltage, in volts / sec.
     */
    public void setVoltageRampRate(double rampRate) {
        // CanTalonSRX takes units of Throttle (0 - 1023) / 10ms.
        int rate = (int) (rampRate * 1023.0 / 12.0 / 100.0);
        m_impl.SetRampThrottle(rate);
    }

    /**
     * Sets control values for closed loop control.
     *
     * @param p Proportional constant.
     * @param i Integration constant.
     * @param d Differential constant.
     * @param f Feedforward constant.
     * @param izone Integration zone -- prevents accumulation of integration
     * error with large errors. Setting this to zero will ignore any izone
     * stuff.
     * @param closeLoopRampRate Closed loop ramp rate. Maximum change in
     * voltage, in volts / sec.
     * @param profile which profile to set the pid constants for. You can have
     * two profiles, with values of 0 or 1, allowing you to keep a second set of
     * values on hand in the talon. In order to switch profiles without
     * recalling setPID, you must call setProfile().
     */
    public void setPID(double p, double i, double d, double f, int izone, double closeLoopRampRate, int profile)
    {
        if (profile != 0 && profile != 1)
            throw new IllegalArgumentException("Talon PID profile must be 0 or 1.");
        m_profile = profile;
        setProfile(profile);
        setP(p);
        setI(i);
        setD(d);
        setF(f);
        setIZone(izone);
        setCloseLoopRampRate(closeLoopRampRate);
    }

    public void setPID(double p, double i, double d) {
        setPID(p, i, d, 0, 0, 0, m_profile);
    }

    /**
     * @return The latest value set using set().
     */
    public double getSetpoint() {
        return m_setPoint;
    }

    /**
     * Select which closed loop profile to use, and uses whatever PIDF gains and
     * the such that are already there.
     */
    public void setProfile(int profile)
    {
        if (profile != 0 && profile != 1)
            throw new IllegalArgumentException("Talon PID profile must be 0 or 1.");
        m_profile = profile;
        m_impl.SetProfileSlotSelect(m_profile);
    }

    // TODO: Documentation for all these accessors/setters for misc. stuff.
    public void setForwardSoftLimit(int forwardLimit) {
        m_impl.SetForwardSoftLimit(forwardLimit);
    }

    public void enableForwardSoftLimit(boolean enable) {
        m_impl.SetForwardSoftEnable(enable ? 1 : 0);
    }

    public void setReverseSoftLimit(int reverseLimit) {
        m_impl.SetReverseSoftLimit(reverseLimit);
    }

    public void enableReverseSoftLimit(boolean enable) {
        m_impl.SetReverseSoftEnable(enable ? 1 : 0);
    }

    public void clearStickyFaults() {
        m_impl.ClearStickyFaults();
    }

    public void enableLimitSwitch(boolean forward, boolean reverse) {
        int mask = 4 + (forward ? 1 : 0) * 2 + (reverse ? 1 : 0);
        m_impl.SetOverrideLimitSwitchEn(mask);
    }

    /**
     * Configure the fwd limit switch to be normally open or normally closed.
     * Talon will disable momentarilly if the Talon's current setting is
     * dissimilar to the caller's requested setting.
     *
     * Since Talon saves setting to flash this should only affect a given Talon
     * initially during robot install.
     *
     * @param normallyOpen true for normally open. false for normally closed.
     */
    public void ConfigFwdLimitSwitchNormallyOpen(boolean normallyOpen)
    {
        SWIGTYPE_p_CTR_Code status = m_impl.SetParam(CanTalonSRX.param_t.eOnBoot_LimitSwitch_Forward_NormallyClosed, normallyOpen ? 0 : 1);
    }

    /**
     * Configure the rev limit switch to be normally open or normally closed.
     * Talon will disable momentarilly if the Talon's current setting is
     * dissimilar to the caller's requested setting.
     *
     * Since Talon saves setting to flash this should only affect a given Talon
     * initially during robot install.
     *
     * @param normallyOpen true for normally open. false for normally closed.
     */
    public void ConfigRevLimitSwitchNormallyOpen(boolean normallyOpen)
    {
        SWIGTYPE_p_CTR_Code status = m_impl.SetParam(CanTalonSRX.param_t.eOnBoot_LimitSwitch_Reverse_NormallyClosed, normallyOpen ? 0 : 1);
    }

    public void enableBrakeMode(boolean brake) {
        m_impl.SetOverrideBrakeType(brake ? 2 : 1);
    }

    public int getFaultOverTemp() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetFault_OverTemp(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getFaultUnderVoltage() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetFault_UnderVoltage(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getFaultForLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetFault_ForLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getFaultRevLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetFault_RevLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getFaultHardwareFailure() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetFault_HardwareFailure(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getFaultForSoftLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetFault_ForSoftLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getFaultRevSoftLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetFault_RevSoftLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getStickyFaultOverTemp() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetStckyFault_OverTemp(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getStickyFaultUnderVoltage() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetStckyFault_UnderVoltage(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getStickyFaultForLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetStckyFault_ForLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getStickyFaultRevLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetStckyFault_RevLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getStickyFaultForSoftLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetStckyFault_ForSoftLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }

    public int getStickyFaultRevSoftLim() {
        long valuep = CanTalonJNI.new_intp();
        m_impl.GetStckyFault_RevSoftLim(new SWIGTYPE_p_int(valuep, true));
        return CanTalonJNI.intp_value(valuep);
    }
}

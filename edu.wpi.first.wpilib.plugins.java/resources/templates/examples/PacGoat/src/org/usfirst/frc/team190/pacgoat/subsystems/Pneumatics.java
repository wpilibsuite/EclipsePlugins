package $package.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import $package.Robot;

/**
 * The Pneumatics subsystem contains the compressor and a pressure sensor.
 *
 * NOTE: The simulator currently doesn't support the compressor or pressure
 * sensors.
 */
public class Pneumatics extends Subsystem {
	AnalogInput pressureSensor = new AnalogInput(3);
	Compressor compressor;

	private static final double kMaxPressure = 2.55;

	public Pneumatics() {
		if (Robot.isReal()) {
			compressor = new Compressor();
		}

		LiveWindow.addSensor("Pneumatics", "Pressure Sensor", pressureSensor);
	}

	/**
	 * No default command
	 */
	@Override
	public void initDefaultCommand() {
	}

	/**
	 * Start the compressor going. The compressor automatically starts and stops
	 * as it goes above and below maximum pressure.
	 */
	public void start() {
		if (Robot.isReal()) {
			compressor.start();
		}
	}

	/**
	 * @return Whether or not the system is fully pressurized.
	 */
	public boolean isPressurized() {
		if (Robot.isReal()) {
			return kMaxPressure <= pressureSensor.getVoltage();
		} else {
			return true; // NOTE: Simulation always has full pressure
		}
	}

	/**
	 * Puts the pressure on the SmartDashboard.
	 */
	public void writePressure() {
		SmartDashboard.putNumber("Pressure", pressureSensor.getVoltage());
	}
}

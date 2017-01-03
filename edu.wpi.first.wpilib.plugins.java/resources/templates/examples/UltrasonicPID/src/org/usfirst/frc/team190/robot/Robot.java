package $package;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * This is a sample program to demonstrate the use of a PIDController with an
 * ultrasonic sensor to reach and maintain a set distance from an object.
 */
public class Robot extends IterativeRobot {
	// distance in inches the robot wants to stay from an object
	private static final double kHoldDistance = 12.0;

	// maximun distance in inches we expect the robot to see
	private static final double kMaxDistance = 24.0;

	// factor to convert sensor values to a distance in inches
	private static final double kValueToInches = 0.125;

	// proportional speed constant
	private static final double kP = 7.0;

	// integral speed constant
	private static final double kI = 0.018;

	// derivative speed constant
	private static final double kD = 1.5;

	private static final int kLeftMotorPort = 0;
	private static final int kRightMotorPort = 1;
	private static final int kUltrasonicPort = 0;

	private AnalogInput ultrasonic = new AnalogInput(kUltrasonicPort);
	private RobotDrive myRobot = new RobotDrive(kLeftMotorPort, kRightMotorPort);
	private PIDController pidController = new PIDController(kP, kI, kD, ultrasonic, new MyPidOutput());

	/**
	 * Drives the robot a set distance from an object using PID control and the
	 * ultrasonic sensor.
	 */
	@Override
	public void teleopInit() {
		// Set expected range to 0-24 inches; e.g. at 24 inches from object go
		// full forward, at 0 inches from object go full backward.
		pidController.setInputRange(0, kMaxDistance * kValueToInches);
		// Set setpoint of the pidController
		pidController.setSetpoint(kHoldDistance * kValueToInches);
		pidController.enable(); // begin PID control
	}

	private class MyPidOutput implements PIDOutput {
		@Override
		public void pidWrite(double output) {
			myRobot.drive(output, 0);
		}
	}
}

package $package;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * This is a sample program demonstrating how to use an ultrasonic sensor and
 * proportional control to maintain a set distance from an object.
 */

public class Robot extends IterativeRobot {

	// distance in inches the robot wants to stay from an object
	private static final double kHoldDistance = 12.0;
	// factor to convert sensor values to a distance in inches
	private static final double kValueToInches = 0.125;
	// proportional speed constant
	private static final double kP = 0.05;

	private AnalogInput ultrasonic;
	private RobotDrive myRobot;

	public void robotInit() {
		myRobot = new RobotDrive(0, 1);
		ultrasonic = new AnalogInput(0);
	}

	/**
	 * Tells the robot to drive to a set distance (in inches) from an object
	 * using proportional control.
	 */
	public void teleopPeriodic() {
		// sensor returns a value from 0-4095 that is scaled to inches
		double currentDistance = ultrasonic.getValue() * kValueToInches;
		// convert distance error to a motor speed
		double currentSpeed = (kHoldDistance - currentDistance) * kP;
		// drive robot
		myRobot.drive(currentSpeed, 0);
	}

}

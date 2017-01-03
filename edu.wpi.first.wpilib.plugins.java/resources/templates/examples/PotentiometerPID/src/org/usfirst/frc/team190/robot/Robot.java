package $package;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;

/**
 * This is a sample program to demonstrate how to use a soft potentiometer and a
 * PID controller to reach and maintain position setpoints on an elevator
 * mechanism.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	final int kPotChannel = 1; // analog input pin
	final int kMotorChannel = 7; // PWM channel
	final int kJoystickChannel = 0; // usb number in DriverStation
	final int kButtonNumber = 4; // button on joystick

	final double kSetPoints[] = { 1.0, 2.6, 4.3 }; // bottom, middle, and top
													// elevator setpoints

	// proportional, integral, and derivative speed constants; motor inverted
	// DANGER: when tuning PID constants, high/inappropriate values for kP,
	// kI,
	// and kD may cause dangerous, uncontrollable, or undesired behavior!
	// these may need to be positive for a non-inverted motor
	final double kP = -5.0;
	final double kI = -0.02;
	final double kD = -2.0;

	PIDController pidController;
	AnalogInput potentiometer;
	Victor elevatorMotor;
	Joystick joystick;

	public Robot() {
		// make objects for potentiometer, the elevator motor controller, and
		// the joystick
		potentiometer = new AnalogInput(kPotChannel);
		elevatorMotor = new Victor(kMotorChannel);
		joystick = new Joystick(kJoystickChannel);

		// potentiometer (AnalogInput) and elevatorMotor (Victor) can be used as
		// a
		// PIDSource and PIDOutput respectively
		pidController = new PIDController(kP, kI, kD, potentiometer, elevatorMotor);
	}

	/**
	 * Runs during autonomous.
	 */
	@Override
	public void autonomous() {

	}

	/**
	 * Uses a PIDController and an array of setpoints to switch and maintain
	 * elevator positions. The elevator setpoint is selected by a joystick
	 * button.
	 */
	@Override
	public void operatorControl() {
		pidController.setInputRange(0, 5); // 0 to 5V
		pidController.setSetpoint(kSetPoints[0]); // set to first setpoint

		int index = 0;
		boolean currentValue;
		boolean previousValue = false;

		while (isOperatorControl() && isEnabled()) {
			pidController.enable(); // begin PID control

			// when the button is pressed once, the selected elevator setpoint
			// is incremented
			currentValue = joystick.getRawButton(kButtonNumber);
			if (currentValue && !previousValue) {
				pidController.setSetpoint(kSetPoints[index]);
				index = (index + 1) % kSetPoints.length; // index of elevator
														// setpoint wraps around
			}
			previousValue = currentValue;
		}
	}

	/**
	 * Runs during test mode.
	 */
	@Override
	public void test() {
	}
}

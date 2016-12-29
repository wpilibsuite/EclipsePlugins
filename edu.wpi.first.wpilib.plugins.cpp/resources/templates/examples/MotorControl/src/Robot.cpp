#include "WPILib.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

/**
 * This sample program shows how to control a motor using a joystick. In the operator
 * control part of the program, the joystick is read and the value is written to the motor.
 *
 * Joystick analog values range from -1 to 1 and speed controller inputs as range from
 * -1 to 1 making it easy to work together. The program also delays a short time in the loop
 * to allow other threads to run. This is generally a good idea, especially since the joystick
 * values are only transmitted from the Driver Station once every 20ms.
 */
class Robot : public SampleRobot {
	Joystick m_stick;

	// The motor to control with the Joystick.
	// This uses a Talon speed controller; use the Victor or Jaguar classes for
	//   other speed controllers.
	Talon m_motor;

	// update every 0.005 seconds/5 milliseconds.
	double kUpdatePeriod = 0.005;

public:
	Robot() :
			m_stick(0), // Initialize Joystick on port 0.
			m_motor(0) // Initialize the Talon on channel 0.
	{
	}

	/**
	 * Runs the motor from the output of a Joystick.
	 */
	void OperatorControl() {
		while (IsOperatorControl() && IsEnabled()) {
			// Set the motor controller's output.
			// This takes a number from -1 (100% speed in reverse) to +1 (100% speed forwards).
			m_motor.Set(m_stick.GetY());

			Wait(kUpdatePeriod); // Wait 5ms for the next update.
		}
	}
};

START_ROBOT_CLASS(Robot)

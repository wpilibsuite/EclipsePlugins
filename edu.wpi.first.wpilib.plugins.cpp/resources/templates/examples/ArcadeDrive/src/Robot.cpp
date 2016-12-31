#include <Joystick.h>
#include <RobotDrive.h>
#include <SampleRobot.h>
#include <Timer.h>

/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will
 * automatically call your Autonomous and OperatorControl methods at the right
 * time as controlled by the switches on the driver station or the field
 * controls.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
class Robot: public SampleRobot {
	RobotDrive myRobot{0, 1};  // robot drive system
	Joystick stick{0};         // only joystick

public:
	Robot() {
		myRobot.SetExpiration(0.1);
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	void OperatorControl() {
		while (IsOperatorControl() && IsEnabled()) {
			// Drive with arcade style (use right stick)
			myRobot.ArcadeDrive(stick);

			// Wait for a motor update time
			Wait(0.005);
		}
	}
};

START_ROBOT_CLASS(Robot)

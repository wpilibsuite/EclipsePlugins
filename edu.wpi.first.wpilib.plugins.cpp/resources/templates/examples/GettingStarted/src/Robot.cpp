#include "WPILib.h"

class Robot: public IterativeRobot {
	RobotDrive myRobot;
	Joystick stick;
	Timer autoTimer;

public:
	Robot() :
			myRobot(0, 1),	// these must be initialized in the same order
			stick(0),		// as they are declared above.
			autoTimer() {

	}

private:
	void AutonomousInit() {
		autoTimer.Reset(); // Reset the timer to 0 seconds
		autoTimer.Start(); // Start the timer
	}

	void AutonomousPeriodic() {
		// Check if 2 seconds has passed
		if (autoTimer.Get() < 2) {
			myRobot.Drive(-0.5, 0.0); // drive forwards half speed
		} else {
			myRobot.Drive(0.0, 0.0); // stop robot
		}
	}

	void TeleopInit() {

	}

	void TeleopPeriodic() {
		myRobot.ArcadeDrive(stick); // drive with arcade style
	}

	void TestPeriodic() {
		LiveWindow::GetInstance()->Run();
	}

};

START_ROBOT_CLASS(Robot)

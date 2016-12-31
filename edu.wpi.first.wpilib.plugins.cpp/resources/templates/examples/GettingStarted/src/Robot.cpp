#include <IterativeRobot.h>
#include <Joystick.h>
#include <LiveWindow.h>
#include <RobotDrive.h>
#include <Timer.h>

class Robot: public IterativeRobot {
public:
	Robot() {
		myRobot.SetExpiration(0.1);
		timer.Start();
	}

private:
	RobotDrive myRobot{0, 1};  // Robot drive system
	Joystick stick{0};         // Only joystick
	LiveWindow* lw = LiveWindow::GetInstance();
	Timer timer;

	void AutonomousInit() {
		timer.Reset();
		timer.Start();
	}

	void AutonomousPeriodic() {
		// Drive for 2 seconds
		if (timer.Get() < 2.0) {
			myRobot.Drive(-0.5, 0.0);  // Drive forwards half speed
		}
		else {
			myRobot.Drive(0.0, 0.0);  // Stop robot
		}
	}

	void TeleopInit() {

	}

	void TeleopPeriodic() {
		// Drive with arcade style (use right stick)
		myRobot.ArcadeDrive(stick);
	}

	void TestPeriodic() {
		lw->Run();
	}
};

START_ROBOT_CLASS(Robot)

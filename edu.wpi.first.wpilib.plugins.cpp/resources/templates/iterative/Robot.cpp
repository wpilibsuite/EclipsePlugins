#include <iostream>
#include <memory>
#include <string>

#include <LiveWindow/LiveWindow.h>
#include <SmartDashboard/SendableChooser.h>
#include <SmartDashboard/SmartDashboard.h>

class Robot: public IterativeRobot {
public:
	void RobotInit() {
		chooser->AddDefault(autoNameDefault, static_cast<void*>(&autoNameDefault));
		chooser->AddObject(autoNameCustom, static_cast<void*>(&autoNameCustom));
		SmartDashboard::PutData("Auto Modes", chooser);
	}

	/*
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * GetString line to get the auto name from the text box below the Gyro.
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * if-else structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	void AutonomousInit() {
		autoSelected = *static_cast<std::string*>(chooser->GetSelected());
		// std::string autoSelected = SmartDashboard::GetString("Auto Selector", autoNameDefault);
		std::cout << "Auto selected: " << autoSelected << std::endl;

		if (autoSelected == autoNameCustom) {
			// Custom Auto goes here
		}
		else {
			// Default Auto goes here
		}
	}

	void AutonomousPeriodic() {
		if (autoSelected == autoNameCustom) {
			// Custom Auto goes here
		}
		else {
			// Default Auto goes here
		}
	}

	void TeleopInit() {

	}

	void TeleopPeriodic() {

	}

	void TestPeriodic() {
		lw->Run();
	}

private:
	LiveWindow* lw = LiveWindow::GetInstance();
	std::unique_ptr<SendableChooser> chooser = std::make_unique<SendableChooser>();
	const std::string autoNameDefault = "Default";
	const std::string autoNameCustom = "My Auto";
	std::string autoSelected;
};

START_ROBOT_CLASS(Robot)

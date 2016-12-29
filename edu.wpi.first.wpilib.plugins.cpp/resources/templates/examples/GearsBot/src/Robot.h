#ifndef ROBOT_H_
#define ROBOT_H_

#include <memory>

#include <Commands/Command.h>
#include <LiveWindow/LiveWindow.h>

#include "Commands/Autonomous.h"
#include "OI.h"
#include "Subsystems/Claw.h"
#include "Subsystems/DriveTrain.h"
#include "Subsystems/Elevator.h"
#include "Subsystems/Wrist.h"

class Robot: public IterativeRobot {
public:
	std::shared_ptr<DriveTrain> drivetrain = std::make_shared<DriveTrain>();
	std::shared_ptr<Elevator> elevator = std::make_shared<Elevator>();
	std::shared_ptr<Wrist> wrist = std::make_shared<Wrist>();
	std::shared_ptr<Claw> claw = std::make_shared<Claw>();
	std::unique_ptr<OI> oi = std::make_unique<OI>();

private:
	Autonomous autonomousCommand;
	LiveWindow *lw = LiveWindow::GetInstance();

	void RobotInit();
	void AutonomousInit();
	void AutonomousPeriodic();
	void TeleopInit();
	void TeleopPeriodic();
	void TestPeriodic();
};


#endif  // ROBOT_H_

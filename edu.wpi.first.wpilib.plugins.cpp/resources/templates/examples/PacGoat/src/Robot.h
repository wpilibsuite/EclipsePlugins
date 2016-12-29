#ifndef MY_ROBOT_H_
#define MY_ROBOT_H_

#include <memory>

#include <Commands/Command.h>
#include <IterativeRobot.h>
#include <SmartDashboard/SendableChooser.h>

#include "Commands/DriveAndShootAutonomous.h"
#include "Commands/DriveForward.h"
#include "OI.h"
#include "Subsystems/Collector.h"
#include "Subsystems/DriveTrain.h"
#include "Subsystems/Pivot.h"
#include "Subsystems/Pneumatics.h"
#include "Subsystems/Shooter.h"

class Robot : public IterativeRobot {
public:
	static std::shared_ptr<DriveTrain> drivetrain;
	static std::shared_ptr<Pivot> pivot;
	static std::shared_ptr<Collector> collector;
	static std::shared_ptr<Shooter> shooter;
	static std::shared_ptr<Pneumatics> pneumatics;
	static std::unique_ptr<OI> oi;

private:
	Command* autonomousCommand = nullptr;
	std::unique_ptr<Command> driveAndShootAuto{new DriveAndShootAutonomous()},
							 driveForwardAuto{new DriveForward()};
	SendableChooser autoChooser;

	void RobotInit();
	void AutonomousInit();
	void AutonomousPeriodic();
	void TeleopInit();
	void TeleopPeriodic();
	void TestPeriodic();
	void DisabledInit();
	void DisabledPeriodic();

	void Log();
};

#endif  // ROBOT_H_

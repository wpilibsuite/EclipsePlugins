
#include "Robot.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

std::shared_ptr<DriveTrain> Robot::drivetrain;
std::shared_ptr<Pivot> Robot::pivot;
std::shared_ptr<Collector> Robot::collector;
std::shared_ptr<Shooter> Robot::shooter;
std::shared_ptr<Pneumatics> Robot::pneumatics;

std::unique_ptr<OI> Robot::oi;

void Robot::RobotInit() {
	drivetrain.reset(new DriveTrain());
	pivot.reset(new Pivot());
	collector.reset(new Collector());
	shooter.reset(new Shooter());
	pneumatics.reset(new Pneumatics());

	oi.reset(new OI());

	// Show what command your subsystem is running on the SmartDashboard
	SmartDashboard::PutData(drivetrain.get());
	SmartDashboard::PutData(pivot.get());
	SmartDashboard::PutData(collector.get());
	SmartDashboard::PutData(shooter.get());
	SmartDashboard::PutData(pneumatics.get());

	// instantiate the command used for the autonomous period
	autoChooser.AddDefault("Drive and Shoot", driveAndShootAuto.get());
	autoChooser.AddObject("Drive Forward", driveForwardAuto.get());
	SmartDashboard::PutData("Auto Mode", &autoChooser);

	pneumatics->Start(); // Pressurize the pneumatics.
}

void Robot::AutonomousInit() {
	autonomousCommand = (Command *)autoChooser.GetSelected();
	autonomousCommand->Start();
}

void Robot::AutonomousPeriodic() {
	Scheduler::GetInstance()->Run();
	Log();
}

void Robot::TeleopInit() {
	// This makes sure that the autonomous stops running when
	// teleop starts running. If you want the autonomous to
	// continue until interrupted by another command, remove
	// this line or comment it out.
	if (autonomousCommand != nullptr) {
		autonomousCommand->Cancel();
	}
	std::cout << "Starting Teleop" << std::endl;
}

void Robot::TeleopPeriodic() {
	Scheduler::GetInstance()->Run();
	Log();
}

void Robot::TestPeriodic() {
  LiveWindow::GetInstance()->Run();
}

void Robot::DisabledInit() {
	shooter->Unlatch();
}

void Robot::DisabledPeriodic() {
	Log();
}

/**
 * Log interesting values to the SmartDashboard.
 */
void Robot::Log() {
	Robot::pneumatics->WritePressure();
	SmartDashboard::PutNumber("Pivot Pot Value", pivot->GetAngle());
	SmartDashboard::PutNumber("Left Distance", drivetrain->GetLeftEncoder()->GetDistance());
	SmartDashboard::PutNumber("Right Distance", drivetrain->GetRightEncoder()->GetDistance());
}

START_ROBOT_CLASS(Robot)

#include "SetWristSetpoint.h"
#include "Robot.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

SetWristSetpoint::SetWristSetpoint(double setpoint) : Command("SetWristSetpoint") {
	this->setpoint = setpoint;
	Requires(Robot::wrist.get());
}

// Called just before this Command runs the first time
void SetWristSetpoint::Initialize() {
	Robot::wrist->SetSetpoint(setpoint);
	Robot::wrist->Enable();
}

// Called repeatedly when this Command is scheduled to run
void SetWristSetpoint::Execute() {}

// Make this return true when this Command no longer needs to run execute()
bool SetWristSetpoint::IsFinished() {
	return Robot::wrist->OnTarget();
}

// Called once after isFinished returns true
void SetWristSetpoint::End() {}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void SetWristSetpoint::Interrupted() {}

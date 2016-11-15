#include "SetCollectionSpeed.h"

#include "Robot.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

SetCollectionSpeed::SetCollectionSpeed(double speed) {
	Requires(Robot::collector.get());
	this->speed = speed;
}

// Called just before this Command runs the first time
void SetCollectionSpeed::Initialize() {
	Robot::collector->SetSpeed(speed);
}

// Called repeatedly when this Command is scheduled to run
void SetCollectionSpeed::Execute() {}

// Make this return true when this Command no longer needs to run execute()
bool SetCollectionSpeed::IsFinished() {
	return true;
}

// Called once after isFinished returns true
void SetCollectionSpeed::End() {}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void SetCollectionSpeed::Interrupted() {}

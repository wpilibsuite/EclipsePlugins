#include "ExtendShooter.h"

#include "Robot.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

ExtendShooter::ExtendShooter() {
	Requires(Robot::shooter.get());
	SetTimeout(1);
}

// Called just before this Command runs the first time
void ExtendShooter::Initialize() {
	Robot::shooter->ExtendBoth();
}

// Called repeatedly when this Command is scheduled to run
void ExtendShooter::Execute() {}

// Make this return true when this Command no longer needs to run execute()
bool ExtendShooter::IsFinished() {
	return IsTimedOut();
}

// Called once after isFinished returns true
void ExtendShooter::End() {
	Robot::shooter->RetractBoth();
}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void ExtendShooter::Interrupted() {
	End();
}

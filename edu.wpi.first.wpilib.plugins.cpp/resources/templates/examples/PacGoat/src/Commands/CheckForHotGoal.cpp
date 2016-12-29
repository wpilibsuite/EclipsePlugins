#include "CheckForHotGoal.h"

#include "Robot.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

CheckForHotGoal::CheckForHotGoal(double time) {
	SetTimeout(time);
}

// Called just before this Command runs the first time
void CheckForHotGoal::Initialize() {}

// Called repeatedly when this Command is scheduled to run
void CheckForHotGoal::Execute() {}

// Make this return true when this Command no longer needs to run execute()
bool CheckForHotGoal::IsFinished() {
	return IsTimedOut() || Robot::shooter->GoalIsHot();
}

// Called once after isFinished returns true
void CheckForHotGoal::End() {}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void CheckForHotGoal::Interrupted() {}

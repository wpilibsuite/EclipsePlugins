#include "WaitForPressure.h"

#include "Robot.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

WaitForPressure::WaitForPressure()
{
	Requires(Robot::pneumatics.get());
}

// Called just before this Command runs the first time
void WaitForPressure::Initialize() {}

// Called repeatedly when this Command is scheduled to run
void WaitForPressure::Execute() {}

// Make this return true when this Command no longer needs to run execute()
bool WaitForPressure::IsFinished()
{
	return Robot::pneumatics->IsPressurized();
}

// Called once after isFinished returns true
void WaitForPressure::End() {}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void WaitForPressure::Interrupted() {}

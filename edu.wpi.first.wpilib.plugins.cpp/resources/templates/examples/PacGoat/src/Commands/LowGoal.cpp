#include "LowGoal.h"

#include "Robot.h"
#include "Commands/SetPivotSetpoint.h"
#include "Commands/SetCollectionSpeed.h"
#include "Commands/ExtendShooter.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

LowGoal::LowGoal() {
	AddSequential(new SetPivotSetpoint(Pivot::LOW_GOAL));
	AddSequential(new SetCollectionSpeed(Collector::REVERSE));
	AddSequential(new ExtendShooter());
}

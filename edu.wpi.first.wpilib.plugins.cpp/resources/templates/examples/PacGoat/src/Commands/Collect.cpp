#include "Collect.h"

#include "Robot.h"
#include "Commands/SetCollectionSpeed.h"
#include "Commands/CloseClaw.h"
#include "Commands/SetPivotSetpoint.h"
#include "Commands/WaitForBall.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

Collect::Collect() {
	AddSequential(new SetCollectionSpeed(Collector::FORWARD));
	AddParallel(new CloseClaw());
	AddSequential(new SetPivotSetpoint(Pivot::COLLECT));
	AddSequential(new WaitForBall());
}

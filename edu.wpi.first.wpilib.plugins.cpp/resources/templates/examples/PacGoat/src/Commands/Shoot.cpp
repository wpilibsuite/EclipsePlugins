#include "Shoot.h"

#include "Robot.h"
#include "Commands/WaitForPressure.h"
#include "Commands/SetCollectionSpeed.h"
#include "Commands/OpenClaw.h"
#include "Commands/ExtendShooter.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

Shoot::Shoot() {
	AddSequential(new WaitForPressure());
	AddSequential(new SetCollectionSpeed(Collector::STOP));
	AddSequential(new OpenClaw());
	AddSequential(new ExtendShooter());
}

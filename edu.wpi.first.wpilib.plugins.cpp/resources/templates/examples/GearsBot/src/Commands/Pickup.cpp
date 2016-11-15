#include "Pickup.h"
#include "CloseClaw.h"
#include "SetWristSetpoint.h"
#include "SetElevatorSetpoint.h"

#include <iostream>

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

Pickup::Pickup() : CommandGroup("Pickup") {
	AddSequential(new CloseClaw());
	AddParallel(new SetWristSetpoint(-45));
	AddSequential(new SetElevatorSetpoint(0.25));
}

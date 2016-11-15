#include "PrepareToPickup.h"
#include "OpenClaw.h"
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

PrepareToPickup::PrepareToPickup() : CommandGroup("PrepareToPickup") {
	AddParallel(new OpenClaw());
	AddParallel(new SetWristSetpoint(0));
	AddSequential(new SetElevatorSetpoint(0));
}

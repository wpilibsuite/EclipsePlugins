#include "Place.h"
#include "OpenClaw.h"
#include "SetWristSetpoint.h"
#include "SetElevatorSetpoint.h"

#include <iostream>

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

Place::Place() : CommandGroup("Place") {
	AddSequential(new SetElevatorSetpoint(0.25));
    AddSequential(new SetWristSetpoint(0));
    AddSequential(new OpenClaw());
}

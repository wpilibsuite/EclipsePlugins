#include "Autonomous.h"
#include "PrepareToPickup.h"
#include "Pickup.h"
#include "Place.h"
#include "SetDistanceToBox.h"
#include "DriveStraight.h"
#include "SetWristSetpoint.h"
#include "CloseClaw.h"

#include <iostream>

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

Autonomous::Autonomous() : CommandGroup("Autonomous") {
	AddSequential(new PrepareToPickup());
    AddSequential(new Pickup());
    AddSequential(new SetDistanceToBox(0.10));
    // AddSequential(new DriveStraight(4)); // Use Encoders if ultrasonic is broken
    AddSequential(new Place());
    AddSequential(new SetDistanceToBox(0.60));
    // addSequential(new DriveStraight(-2)); // Use Encoders if ultrasonic is broken
    AddParallel(new SetWristSetpoint(-45));
    AddSequential(new CloseClaw());
}

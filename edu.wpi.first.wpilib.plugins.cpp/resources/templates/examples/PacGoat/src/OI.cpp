#include "OI.h"

#include "Subsystems/Pivot.h"
#include "Subsystems/Collector.h"
#include "Commands/LowGoal.h"
#include "Commands/Collect.h"
#include "Commands/SetPivotSetpoint.h"
#include "Commands/Shoot.h"
#include "Commands/DriveForward.h"
#include "Commands/SetCollectionSpeed.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

OI::OI()
    : joystick(0),
      L1(&joystick, 11),
      L2(&joystick, 9),
      R1(&joystick, 12),
      R2(&joystick, 10),
      sticks(&joystick, 2, 3) {

	R1.WhenPressed(new LowGoal());
	R2.WhenPressed(new Collect());

	L1.WhenPressed(new SetPivotSetpoint(Pivot::SHOOT));
	L2.WhenPressed(new SetPivotSetpoint(Pivot::SHOOT_NEAR));

	sticks.WhenActive(new Shoot());


	// SmartDashboard Buttons
	SmartDashboard::PutData("Drive Forward", new DriveForward(2.25));
	SmartDashboard::PutData("Drive Backward", new DriveForward(-2.25));
	SmartDashboard::PutData("Start Rollers", new SetCollectionSpeed(Collector::FORWARD));
	SmartDashboard::PutData("Stop Rollers", new SetCollectionSpeed(Collector::STOP));
	SmartDashboard::PutData("Reverse Rollers", new SetCollectionSpeed(Collector::REVERSE));
}


Joystick* OI::GetJoystick() {
	return &joystick;
}

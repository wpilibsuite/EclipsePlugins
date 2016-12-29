#include "TankDriveWithJoystick.h"

#include "Robot.h"

TankDriveWithJoystick::TankDriveWithJoystick() : Command("TankDriveWithJoystick") {
	Requires(Robot::drivetrain.get());
}

// Called repeatedly when this Command is scheduled to run
void TankDriveWithJoystick::Execute() {
	Robot::drivetrain->Drive(Robot::oi->GetJoystick());
}

// Make this return true when this Command no longer needs to run execute()
bool TankDriveWithJoystick::IsFinished() {
	return false;
}

// Called once after isFinished returns true
void TankDriveWithJoystick::End() {
	Robot::drivetrain->Drive(0, 0);
}

#include "DriveForward.h"

#include "Robot.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

void DriveForward::init(double dist, double maxSpeed) {
	Requires(Robot::drivetrain.get());
	distance = dist;
	driveForwardSpeed = maxSpeed;
	error = 0;
}

DriveForward::DriveForward() {
	init(10, 0.5);
}

DriveForward::DriveForward(double dist) {
	init(dist, 0.5);
}

DriveForward::DriveForward(double dist, double maxSpeed) {
	init(dist, maxSpeed);
}

// Called just before this Command runs the first time
void DriveForward::Initialize() {
	Robot::drivetrain->GetRightEncoder()->Reset();
	SetTimeout(2);
}

// Called repeatedly when this Command is scheduled to run
void DriveForward::Execute() {
	error = (distance - Robot::drivetrain->GetRightEncoder()->GetDistance());
	if (driveForwardSpeed * KP * error >= driveForwardSpeed) {
		Robot::drivetrain->TankDrive(driveForwardSpeed, driveForwardSpeed);
	} else {
		Robot::drivetrain->TankDrive(driveForwardSpeed * KP * error,
				driveForwardSpeed * KP * error);
	}
}

// Make this return true when this Command no longer needs to run execute()
bool DriveForward::IsFinished() {
	return (abs(error) <= TOLERANCE) || IsTimedOut();
}

// Called once after isFinished returns true
void DriveForward::End() {
	Robot::drivetrain->Stop();
}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void DriveForward::Interrupted() {
	End();
}

#include "ExtendShooter.h"

#include "Robot.h"

ExtendShooter::ExtendShooter() : TimedCommand(1) {
	Requires(Robot::shooter.get());
}

// Called just before this Command runs the first time
void ExtendShooter::Initialize() {
	Robot::shooter->ExtendBoth();
}

// Called once after isFinished returns true
void ExtendShooter::End() {
	Robot::shooter->RetractBoth();
}

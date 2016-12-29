#include "OpenClaw.h"

#include "Robot.h"

OpenClaw::OpenClaw() {
	Requires(Robot::collector.get());
}

// Called just before this Command runs the first time
void OpenClaw::Initialize() {
	Robot::collector->Open();
}

// Make this return true when this Command no longer needs to run execute()
bool OpenClaw::IsFinished() {
	return Robot::collector->IsOpen();
}

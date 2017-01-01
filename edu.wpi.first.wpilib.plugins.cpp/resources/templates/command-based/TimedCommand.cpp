#include "$classname.h"

$classname::$classname(double timeout) : TimedCommand(timeout) {
	// Use Requires() here to declare subsystem dependencies
	// eg. Requires(chassis);
}

// Called just before this Command runs the first time
void $classname::Initialize() {

}

// Called repeatedly when this Command is scheduled to run
void $classname::Execute() {

}

// Called once after command times out
void $classname::End() {

}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void $classname::Interrupted() {

}

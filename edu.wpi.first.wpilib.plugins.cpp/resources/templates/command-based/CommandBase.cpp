#include "CommandBase.h"

#include <Commands/Scheduler.h>

#include "Subsystems/ExampleSubsystem.h"

// Initialize a single static instance of all of your subsystems
std::unique_ptr<ExampleSubsystem> CommandBase::exampleSubsystem;
std::unique_ptr<OI> CommandBase::oi;

CommandBase::CommandBase(const std::string &name) : Command(name) {

}

void CommandBase::init() {
	// Create a single static instance of all of your subsystems. The following
	// line should be repeated for each subsystem in the project.
	exampleSubsystem.reset(new ExampleSubsystem());

	oi.reset(new OI());
}

#include "Shoot.h"

#include "Commands/WaitForPressure.h"
#include "Commands/SetCollectionSpeed.h"
#include "Commands/OpenClaw.h"
#include "Commands/ExtendShooter.h"
#include "Robot.h"

Shoot::Shoot() {
	AddSequential(new WaitForPressure());
	AddSequential(new SetCollectionSpeed(Collector::kStop));
	AddSequential(new OpenClaw());
	AddSequential(new ExtendShooter());
}

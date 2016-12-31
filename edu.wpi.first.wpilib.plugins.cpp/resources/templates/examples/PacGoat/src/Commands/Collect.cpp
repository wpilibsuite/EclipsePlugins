#include "Collect.h"

#include "Commands/SetCollectionSpeed.h"
#include "Commands/CloseClaw.h"
#include "Commands/SetPivotSetpoint.h"
#include "Commands/WaitForBall.h"
#include "Robot.h"

Collect::Collect() {
	AddSequential(new SetCollectionSpeed(Collector::FORWARD));
	AddParallel(new CloseClaw());
	AddSequential(new SetPivotSetpoint(Pivot::COLLECT));
	AddSequential(new WaitForBall());
}

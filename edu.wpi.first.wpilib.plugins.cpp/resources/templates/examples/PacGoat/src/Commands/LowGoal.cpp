#include "LowGoal.h"

#include "Commands/SetPivotSetpoint.h"
#include "Commands/SetCollectionSpeed.h"
#include "Commands/ExtendShooter.h"
#include "Robot.h"

LowGoal::LowGoal() {
	AddSequential(new SetPivotSetpoint(Pivot::kLowGoal));
	AddSequential(new SetCollectionSpeed(Collector::kReverse));
	AddSequential(new ExtendShooter());
}

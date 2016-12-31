#include "OI.h"

#include "Commands/Collect.h"
#include "Commands/DriveForward.h"
#include "Commands/LowGoal.h"
#include "Commands/SetCollectionSpeed.h"
#include "Commands/SetPivotSetpoint.h"
#include "Commands/Shoot.h"
#include "Subsystems/Collector.h"
#include "Subsystems/Pivot.h"

OI::OI() {
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

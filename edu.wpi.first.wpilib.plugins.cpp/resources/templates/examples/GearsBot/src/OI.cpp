#include "OI.h"

#include "Commands/Autonomous.h"
#include "Commands/CloseClaw.h"
#include "Commands/OpenClaw.h"
#include "Commands/Pickup.h"
#include "Commands/Place.h"
#include "Commands/PrepareToPickup.h"
#include "Commands/SetElevatorSetpoint.h"

OI::OI() {
	SmartDashboard::PutData("Open Claw", new OpenClaw());
	SmartDashboard::PutData("Close Claw", new CloseClaw());

	// Create some buttons
	auto d_up = new JoystickButton(joy, 5);
	auto d_right = new JoystickButton(joy, 6);
	auto d_down = new JoystickButton(joy, 7);
	auto d_left = new JoystickButton(joy, 8);
	auto l2 = new JoystickButton(joy, 9);
	auto r2 = new JoystickButton(joy, 10);
	auto l1 = new JoystickButton(joy, 11);
	auto r1 = new JoystickButton(joy, 12);

	// Connect the buttons to commands
	d_up->WhenPressed(new SetElevatorSetpoint(0.2));
	d_down->WhenPressed(new SetElevatorSetpoint(-0.2));
	d_right->WhenPressed(new CloseClaw());
	d_left->WhenPressed(new OpenClaw());

	r1->WhenPressed(new PrepareToPickup());
	r2->WhenPressed(new Pickup());
	l1->WhenPressed(new Place());
	l2->WhenPressed(new Autonomous());
}

Joystick* OI::GetJoystick() {
	return &joy;
}

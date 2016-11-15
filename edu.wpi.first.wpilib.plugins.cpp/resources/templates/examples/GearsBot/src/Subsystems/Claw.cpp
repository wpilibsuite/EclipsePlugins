#include "Subsystems/Claw.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

Claw::Claw() : Subsystem("Claw") {
    motor = new Victor(7);
    contact = new DigitalInput(5);

	// Let's show everything on the LiveWindow
    // TODO: LiveWindow::GetInstance()->AddActuator("Claw", "Motor", (Victor) motor);
    // TODO: contact
}

void Claw::Open()
{
	motor->Set(-1);
}


void Claw::Close()
{
	motor->Set(1);
}


void Claw::Stop() {
	motor->Set(0);
}

bool Claw::IsGripping() {
	return contact->Get();
}


#include "Collector.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

Collector::Collector() :
		Subsystem("Collector"),
		// Configure devices
		rollerMotor(new Victor(6)),
		ballDetector(new DigitalInput(10)),
		piston(new Solenoid(1)),
		openDetector(new DigitalInput(6))
{

	// Put everything to the LiveWindow for testing.
	// XXX: LiveWindow::GetInstance()->AddActuator("Collector", "Roller Motor", (Victor) rollerMotor);
	LiveWindow::GetInstance()->AddSensor("Collector", "Ball Detector", ballDetector);
	LiveWindow::GetInstance()->AddSensor("Collector", "Claw Open Detector", openDetector);
	LiveWindow::GetInstance()->AddActuator("Collector", "Piston", piston);
}

bool Collector::HasBall() {
	return ballDetector->Get(); // TODO: prepend ! to reflect real robot
}

void Collector::SetSpeed(double speed) {
	rollerMotor->Set(-speed);
}

void Collector::Stop() {
	rollerMotor->Set(0);
}

bool Collector::IsOpen() {
	return openDetector->Get(); // TODO: prepend ! to reflect real robot
}

void Collector::Open() {
	piston->Set(true);
}

void Collector::Close() {
	piston->Set(false);
}

void Collector::InitDefaultCommand() {}

#include "Elevator.h"

#include <LiveWindow/LiveWindow.h>
#include <SmartDashboard/SmartDashboard.h>

Elevator::Elevator() : PIDSubsystem("Elevator", kP_real, kI_real, 0.0) {
	#ifdef SIMULATION  // Check for simulation and update PID values
		GetPIDController()->SetPID(kP_simulation, kI_simulation, 0, 0);
	#endif
	SetAbsoluteTolerance(0.005);

	// Conversion value of potentiometer varies between the real world and simulation
	#ifdef REAL
		pot = std::make_unique<AnalogPotentiometer>(2, -2.0/5);
	#else
		pot = std::make_unique<AnalogPotentiometer>(2);  // Defaults to meters
	#endif

	// Let's show everything on the LiveWindow
	// TODO: LiveWindow::GetInstance()->AddActuator("Elevator", "Motor", (Victor) motor);
	// TODO: LiveWindow::GetInstance()->AddSensor("Elevator", "Pot", (AnalogPotentiometer) pot);
	LiveWindow::GetInstance()->AddActuator("Elevator", "PID", GetPIDController());
}

void Elevator::InitDefaultCommand() {

}

void Elevator::Log() {
	// TODO: SmartDashboard::PutData("Wrist Pot", (AnalogPotentiometer) pot);
}

double Elevator::ReturnPIDInput() {
	return pot->Get();
}

void Elevator::UsePIDOutput(double d) {
	motor->Set(d);
}

#include "Pivot.h"

Pivot::Pivot() : PIDSubsystem("Pivot", 7.0, 0.0, 8.0) {
	SetAbsoluteTolerance(0.005);
	GetPIDController()->SetContinuous(false);
	#ifdef SIMULATION
		// PID is different in simulation.
		GetPIDController()->SetPID(0.5, 0.001, 2);
		SetAbsoluteTolerance(5);
	#endif

	// Put everything to the LiveWindow for testing.
	LiveWindow::GetInstance()->AddSensor("Pivot", "Upper Limit Switch", upperLimitSwitch);
	LiveWindow::GetInstance()->AddSensor("Pivot", "Lower Limit Switch", lowerLimitSwitch);
	// XXX: LiveWindow::GetInstance()->AddSensor("Pivot", "Pot", (AnalogPotentiometer) pot);
	// XXX: LiveWindow::GetInstance()->AddActuator("Pivot", "Motor", (Victor) motor);
	LiveWindow::GetInstance()->AddActuator("Pivot", "PIDSubsystem Controller", GetPIDController());
}

void InitDefaultCommand() {

}

double Pivot::ReturnPIDInput() {
	return pot->Get();
}

void Pivot::UsePIDOutput(double output) {
	motor->PIDWrite(output);
}

bool Pivot::IsAtUpperLimit() {
	return upperLimitSwitch->Get();  // TODO: inverted from real robot (prefix with !)
}

bool Pivot::IsAtLowerLimit() {
	return lowerLimitSwitch->Get();  // TODO: inverted from real robot (prefix with !)
}

double Pivot::GetAngle() {
	return pot->Get();
}

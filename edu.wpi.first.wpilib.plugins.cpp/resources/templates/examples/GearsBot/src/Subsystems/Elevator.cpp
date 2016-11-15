#include "Elevator.h"
#include "SmartDashboard/SmartDashboard.h"
#include "LiveWindow/LiveWindow.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

Elevator::Elevator() : PIDSubsystem("Elevator", kP_real, kI_real, 0.0) {
    #ifdef SIMULATION // Check for simulation and update PID values
        GetPIDController()->SetPID(kP_simulation, kI_simulation, 0, 0);
    #endif
    SetAbsoluteTolerance(0.005);

    motor = new Victor(5);

    // Conversion value of potentiometer varies between the real world and simulation
    #ifdef REAL
        pot = new AnalogPotentiometer(2, -2.0/5);
	#else
        pot = new AnalogPotentiometer(2); // Defaults to meters
	#endif

	// Let's show everything on the LiveWindow
    // TODO: LiveWindow::GetInstance()->AddActuator("Elevator", "Motor", (Victor) motor);
    // TODO: LiveWindow::GetInstance()->AddSensor("Elevator", "Pot", (AnalogPotentiometer) pot);
    LiveWindow::GetInstance()->AddActuator("Elevator", "PID", GetPIDController());
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

#include "Wrist.h"
#include "SmartDashboard/SmartDashboard.h"
#include "LiveWindow/LiveWindow.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

Wrist::Wrist() : PIDSubsystem("Wrist", kP_real, 0.0, 0.0) {
	#ifdef SIMULATION // Check for simulation and update PID values
        GetPIDController()->SetPID(kP_simulation, 0, 0, 0);
	#endif
    SetAbsoluteTolerance(2.5);

    motor = new Victor(6);

    // Conversion value of potentiometer varies between the real world and simulation
	#ifdef REAL
        pot = new AnalogPotentiometer(3, -270.0/5);
	#else
        pot = new AnalogPotentiometer(3); // Defaults to degrees
	#endif

	// Let's show everything on the LiveWindow
    // TODO: LiveWindow::GetInstance()->AddActuator("Wrist", "Motor", (Victor) motor);
    // TODO: LiveWindow::GetInstance()->AddSensor("Wrist", "Pot", (AnalogPotentiometer) pot);
    LiveWindow::GetInstance()->AddActuator("Wrist", "PID", GetPIDController());
}

void Wrist::Log() {
    // TODO: SmartDashboard::PutData("Wrist Angle", (AnalogPotentiometer) pot);
}

double Wrist::ReturnPIDInput() {
    return pot->Get();
}

void Wrist::UsePIDOutput(double d) {
    motor->Set(d);
}

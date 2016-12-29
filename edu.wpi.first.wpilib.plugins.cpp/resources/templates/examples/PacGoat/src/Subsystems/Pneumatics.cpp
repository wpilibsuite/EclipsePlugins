#include "Pneumatics.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

Pneumatics::Pneumatics() :
		Subsystem("Pneumatics"),
		pressureSensor(new AnalogInput(3))
{
	#ifdef REAL
		compressor = new Compressor(uint8_t(1)); // TODO: (1, 14, 1, 8);
	#endif

	LiveWindow::GetInstance()->AddSensor("Pneumatics", "Pressure Sensor", pressureSensor);
}

/**
 * No default command
 */
void Pneumatics::InitDefaultCommand() {}

/**
 * Start the compressor going. The compressor automatically starts and stops as it goes above and below maximum pressure.
 */
void Pneumatics::Start() {
    #ifdef REAL
		compressor->Start();
    #endif
}

/**
 * @return Whether or not the system is fully pressurized.
 */
bool Pneumatics::IsPressurized() {
    #ifdef REAL
		return MAX_PRESSURE <= pressureSensor->GetVoltage();
    #else
		return true; // NOTE: Simulation always has full pressure
	#endif
}

/**
 * Puts the pressure on the SmartDashboard.
 */
void Pneumatics::WritePressure() {
	SmartDashboard::PutNumber("Pressure", pressureSensor->GetVoltage());
}

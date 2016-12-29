#include "WPILib.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See http://wpilib.screenstepslive.com/s/4485/m/13810/l/680719 for more information
 */
using namespace frc;

/**
 * This is a sample program showing how to retrieve information from
 *   the Power Distribution Panel via CAN.
 * The information will be displayed under variables through the SmartDashboard.
 */
class Robot: public SampleRobot
{

	// Object for dealing with the Power Distribution Panel (PDP).
	PowerDistributionPanel m_pdp;

	// Update every 5milliseconds/0.005 seconds.
	const double kUpdatePeriod = 0.005;

public:
	Robot() {
	}

	/**
	 * Retrieve information from the PDP over CAN and
	 *   displays it on the SmartDashboard interface.
	 * SmartDashboard::PutNumber takes a string (for a label) and a double;
	 * GetCurrent takes a channel number and returns a double for current,
	 *   in Amperes. Channel numbers are printed on the PDP and range from 0-15.
	 */
	void OperatorControl()
	{
		while (IsOperatorControl() && IsEnabled())
		{
			// Get the current going through channel 7, in Amperes.
			// The PDP returns the current in increments of 0.125A.
			// At low currents the current readings tend to be less accurate. 
			SmartDashboard::PutNumber("Current Channel 7", m_pdp.GetCurrent(7));
			// Get the voltage going into the PDP, in Volts.
			// The PDP returns the voltage in increments of 0.05 Volts.
			SmartDashboard::PutNumber("Voltage", m_pdp.GetVoltage());
			// Retrieves the temperature of the PDP, in degrees Celsius.
			SmartDashboard::PutNumber("Temperature", m_pdp.GetTemperature());
			Wait(kUpdatePeriod);
		}
	}

};

START_ROBOT_CLASS(Robot)

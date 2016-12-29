#include <AnalogInput.h>
#include <Joystick.h>
#include <SampleRobot.h>
#include <Victor.h>

/**
 * This is a sample program to demonstrate the use of a soft potentiometer and
 * proportional control to reach and maintain position setpoints on an elevator
 * mechanism. A joystick button is used to switch elevator setpoints.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
class Robot : public SampleRobot {
public:
	/**
	 * Runs during autonomous.
	 */
	void Autonomous() {

	}

	/**
	 * Runs during operator control.
	 */
	void OperatorControl() {
		bool buttonState;
		bool prevButtonState = false;

		// Setpoint array index
		int index = 0;

		// Number of setpoints
		const int size = 3;

		// Bottom, middle, and top elevator setpoints
		const double setpoints[size] = {1.0, 2.6, 4.3};

		// Holds desired setpoint, initialized to first setpoint
		double currentSetpoint = setpoints[0];

		while (IsOperatorControl() && IsEnabled()) {
			// Check if button is pressed
			buttonState = joystick.GetRawButton(buttonNumber);

			// If button has been pressed and released once
			if (buttonState && !prevButtonState) {
				// Increment setpoint; reset if at maximum
				index = (index + 1) % size;

				// Set setpoint
				currentSetpoint = setpoints[index];
			}

			// Record previous button state
			prevButtonState = buttonState;

			// Get position value
			currentPosition = potentiometer.GetAverageVoltage();

			// Convert position error to speed
			motorSpeed = (currentPosition - currentSetpoint) * pGain

			// Drive elevator motor
			elevatorMotor.Set(motorSpeed);
		}
	}

	/**
	 * Runs during test mode.
	 */
	void Test() {

	}

private:
	constexpr int potChannel = 1;       // Analog input pin
	constexpr int motorChannel = 7;     // PWM channel
	constexpr int joystickChannel = 0;  // USB number in DriverStation
	constexpr int buttonNumber = 4;     // Joystick button

	// Proportional speed constant
	constexpr double pGain = 1.0;

	double motorSpeed;

	// Sensor voltage reading corresponding to current elevator position
	double currentPosition;

	AnalogInput potentiometer{potChannel};
	Victor elevatorMotor{motorChannel};
	Joystick joystick{joystickChannel};
};

START_ROBOT_CLASS(Robot)

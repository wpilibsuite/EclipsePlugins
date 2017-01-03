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
class Robot: public frc::SampleRobot {
public:
	/**
	 * Runs during autonomous.
	 */
	void Autonomous() override {

	}

	/**
	 * Runs during operator control.
	 */
	void OperatorControl() override {
		bool buttonState;
		bool prevButtonState = false;

		// Setpoint array index
		int index = 0;

		// Number of setpoints
		constexpr int size = 3;

		// Bottom, middle, and top elevator setpoints
		constexpr double setpoints[size] = { 1.0, 2.6, 4.3 };

		// Holds desired setpoint, initialized to first setpoint
		double currentSetpoint = setpoints[0];

		while (IsOperatorControl() && IsEnabled()) {
			// Check if button is pressed
			buttonState = joystick.GetRawButton(kButtonNumber);

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
			motorSpeed = (currentPosition - currentSetpoint) * kP;

			// Drive elevator motor
			elevatorMotor.Set(motorSpeed);
		}
	}

	/**
	 * Runs during test mode.
	 */
	void Test() override {

	}

private:
	static constexpr int kPotChannel = 1;       // Analog input pin
	static constexpr int kMotorChannel = 7;     // PWM channel
	static constexpr int kJoystickChannel = 0;  // USB number in DriverStation
	static constexpr int kButtonNumber = 4;     // Joystick button

	// Proportional speed constant
	static constexpr double kP = 1.0;

	double motorSpeed;

	// Sensor voltage reading corresponding to current elevator position
	double currentPosition;

	frc::AnalogInput potentiometer { kPotChannel };
	frc::Victor elevatorMotor { kMotorChannel };
	frc::Joystick joystick { kJoystickChannel };
};

START_ROBOT_CLASS(Robot)

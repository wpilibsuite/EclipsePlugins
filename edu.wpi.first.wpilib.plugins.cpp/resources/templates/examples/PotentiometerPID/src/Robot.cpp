#include <array>

#include <AnalogInput.h>
#include <Joystick.h>
#include <PIDController.h>
#include <SampleRobot.h>
#include <Victor.h>

/**
 * This is a sample program to demonstrate how to use a soft potentiometer and a
 * PID Controller to reach and maintain position setpoints on an elevator
 * mechanism.
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
	 * Uses a PIDController and an array of setpoints to switch and maintain
	 * elevator positions. The elevator setpoint is selected by a joystick
	 * button.
	 */
	void OperatorControl() override {
		pidController.SetInputRange(0, 5);  // 0 to 5V
		pidController.SetSetpoint(kSetPoints[0]);  // Set to first setpoint

		int index = 0;
		bool currentValue;
		bool previousValue = false;

		while (IsOperatorControl() && IsEnabled()) {
			pidController.Enable(); // begin PID control

			/* when the button is pressed once, the selected elevator setpoint
			 * is incremented
			 */
			currentValue = joystick.GetRawButton(buttonNumber);
			if (currentValue && !previousValue) {
				pidController.SetSetpoint(kSetPoints[index]);

				// Index of elevator setpoint wraps around
				index = (index + 1) % (sizeof(kSetPoints) / 8);
			}
			previousValue = currentValue;
		}
	}

	/**
	 * Runs during test mode.
	 */
	void Test() override {

	}

private:
	static constexpr int potChannel = 1;       // Analog input pin
	static constexpr int motorChannel = 7;     // PWM channel
	static constexpr int joystickChannel = 0;  // USB number in DriverStation
	static constexpr int buttonNumber = 4;     // Button on joystick

	// Bottom, middle, and top elevator setpoints
	static constexpr std::array<double, 3> kSetPoints = { 1.0, 2.6, 4.3 };

	/* proportional, integral, and derivative speed constants; motor inverted
	 * DANGER: when tuning PID constants, high/inappropriate values for pGain,
	 * iGain, and dGain may cause dangerous, uncontrollable, or
	 * undesired behavior!
	 *
	 * These may need to be positive for a non-inverted motor
	 */
	static constexpr double kP = -5.0;
	static constexpr double kI = -0.02;
	static constexpr double kD = -2.0;

	frc::AnalogInput potentiometer { potChannel };
	frc::Joystick joystick { joystickChannel };
	frc::Victor elevatorMotor { motorChannel };

	/* potentiometer (AnalogInput) and elevatorMotor (Victor) can be used as a
	 * PIDSource and PIDOutput respectively. The PIDController takes pointers
	 * to the PIDSource and PIDOutput, so you must use &potentiometer and
	 * &elevatorMotor to get their pointers.
	 */
	frc::PIDController pidController { kP, kI, kD, &potentiometer,
			&elevatorMotor };
};

constexpr std::array<double, 3> Robot::kSetPoints;

START_ROBOT_CLASS(Robot)

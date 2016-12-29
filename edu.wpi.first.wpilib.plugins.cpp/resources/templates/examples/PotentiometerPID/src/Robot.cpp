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
 *  This is a sample program to demonstrate how to use a soft potentiometer and a PID
 *  Controller to reach and maintain position setpoints on an elevator mechanism.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */

class Robot: public SampleRobot {
	const int potChannel = 1; //analog input pin
	const int motorChannel = 7; //PWM channel
	const int joystickChannel = 0; //usb number in DriverStation
	const int buttonNumber = 4; //button on joystick

	const double setPoints[3] = { 1.0, 2.6, 4.3 }; //bottom, middle, and top elevator setpoints

	//proportional, integral, and derivative speed constants; motor inverted
	//DANGER: when tuning PID constants, high/inappropriate values for pGain, iGain,
	//and dGain may cause dangerous, uncontrollable, or undesired behavior!
	const double pGain = -5.0, iGain = -0.02, dGain = -2.0; //these may need to be positive for a non-inverted motor

	AnalogInput potentiometer;
	Victor elevatorMotor;
	Joystick joystick;
	PIDController pidController;

public:
	Robot() :
		//make objects for potentiometer, the elevator motor controller, and the joystick
		potentiometer(potChannel), elevatorMotor(motorChannel), joystick(joystickChannel),
		//potentiometer (AnalogInput) and elevatorMotor (Victor) can be used as a
		//PIDSource and PIDOutput respectively.
		//The PIDController has to take a pointer to the PIDSource and PIDOutput, so
		//you must call &potentiometer and &elevatorMotor to get their pointers.
		pidController(pGain, iGain, dGain, &potentiometer, &elevatorMotor) {}

	/**
	 * Runs during autonomous.
	 */
	void Autonomous() {

	}

	/**
	 *  Uses a PIDController and an array of setpoints to switch and maintain elevator positions.
     *  The elevator setpoint is selected by a joystick button.
	 */
	void OperatorControl() {
		pidController.SetInputRange(0, 5); //0 to 5V
		pidController.SetSetpoint(setPoints[0]); //set to first setpoint

		int index = 0;
		bool currentValue;
		bool previousValue = false;

		while (IsOperatorControl() && IsEnabled()) {
			pidController.Enable(); //begin PID control

			//when the button is pressed once, the selected elevator setpoint is incremented
			currentValue = joystick.GetRawButton(buttonNumber);
			if (currentValue && !previousValue) {
				pidController.SetSetpoint(setPoints[index]);
				index = (index + 1) % (sizeof(setPoints)/8); //index of elevator setpoint wraps around
			}
			previousValue = currentValue;
		}
	}

	/**
	 * Runs during test mode.
	 */
	void Test() {

	}
};

START_ROBOT_CLASS(Robot)

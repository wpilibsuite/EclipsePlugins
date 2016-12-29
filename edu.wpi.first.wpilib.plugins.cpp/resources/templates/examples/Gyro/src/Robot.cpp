#include <AnalogGyro.h>
#include <CANTalon.h>
#include <Joystick.h>
#include <RobotDrive.h>
#include <SampleRobot.h>

/**
 * This is a sample program to demonstrate how to use a gyro sensor to make a robot drive
 * straight. This program uses a joystick to drive forwards and backwards while the gyro
 * is used for direction keeping.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
class Robot: public SampleRobot {
public:
	/**
	 * Runs during autonomous.
	 */
	void Autonomous() {}

	/**
	 * Sets the gyro sensitivity and drives the robot when the joystick is
	 * pushed. The motor speed is set from the joystick while the RobotDrive
	 * turning value is assigned from the error between the setpoint and the
	 * gyro angle.
	 */
	void OperatorControl() {
		double turningValue;

		// Calibrates gyro values to equal degrees
		gyro.SetSensitivity(voltsPerDegreePerSecond);

		while (IsOperatorControl() && IsEnabled()) {
			turningValue = (angleSetpoint - gyro.GetAngle()) * pGain;
			if (joystick.GetY() <= 0) {
				// Forwards
				myRobot.Drive(joystick.GetY(), turningValue);
			}
			else {
				// Backwards
				myRobot.Drive(joystick.GetY(), -turningValue);
			}
		}
	}

	/**
	 * Runs during test mode.
	 */
	void Test() {}

private:
	constexpr int gyroChannel = 0;  // Analog input
	constexpr int joystickChannel = 0;  // USB number in DriverStation

	// Channels for motors
	constexpr int leftFontMotorChannel = 1;
	constexpr int rightFrontMotorChannel = 0;
	constexpr int leftRearMotorChannel = 3;
	constexpr int rightRearMotorChannel = 2;

	double angleSetpoint = 0.0;
	constexpr double pGain = 0.006;  // Propotional turning constant

	// Gyro calibration constant, may need to be adjusted
	// Gyro value of 360 is set to correspond to one full revolution
	constexpr double voltsPerDegreePerSecond = 0.0128;

	// Create the drivetrain from 4 CAN Talon SRXs.
	RobotDrive myRobot{new CANTalon(leftFrontMotorChannel),
	                   new CANTalon(leftRearMotorChannel),
	                   new CANTalon(rightFrontMotorChannel),
	                   new CANTalon(rightRearMotorChannel)};

	// Assign the gyro and joystick channels.
	AnalogGyro gyro{gyroChannel};
	Joystick joystick{joystickChannel};
};

START_ROBOT_CLASS(Robot)

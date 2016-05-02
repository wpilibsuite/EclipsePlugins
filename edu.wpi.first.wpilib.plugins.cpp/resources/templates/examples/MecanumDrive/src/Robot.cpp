#include <Joystick.h>
#include <RobotDrive.h>
#include <SampleRobot.h>

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.
 */
class Robot : public SampleRobot {
public:
	Robot() {
		robotDrive.SetExpiration(0.1);

		// Invert the left side motors
		robotDrive.SetInvertedMotor(RobotDrive::kFrontLeftMotor, true);

		// You may need to change or remove this to match your robot
		robotDrive.SetInvertedMotor(RobotDrive::kRearLeftMotor, true);
	}

	/**
	 * Runs the motors with Mecanum drive.
	 */
	void OperatorControl() {
		robotDrive.SetSafetyEnabled(false);
		while (IsOperatorControl() && IsEnabled()) {
			/* Use the joystick X axis for lateral movement, Y axis for forward
			 * movement, and Z axis for rotation. This sample does not use
			 * field-oriented drive, so the gyro input is set to zero.
			 */
			robotDrive.MecanumDrive_Cartesian(stick.GetX(), stick.GetY(),
			                                  stick.GetZ());

			Wait(0.005); // wait 5ms to avoid hogging CPU cycles
		}
	}

private:
	// Channels for the wheels
	constexpr int frontLeftChannel = 2;
	constexpr int rearLeftChannel = 3;
	constexpr int frontRightChannel = 1;
	constexpr int rearRightChannel = 0;

	constexpr int joystickChannel = 0;

	// Robot drive system
	RobotDrive robotDrive{frontLeftChannel, rearLeftChannel,
	                      frontRightChannel, rearRightChannel};
	// Only joystick
	Joystick stick{joystickChannel};
};

START_ROBOT_CLASS(Robot)

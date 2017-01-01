#include <Joystick.h>
#include <RobotDrive.h>
#include <SampleRobot.h>
#include <Timer.h>

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.
 */
class Robot: public frc::SampleRobot {
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
	void OperatorControl() override {
		robotDrive.SetSafetyEnabled(false);
		while (IsOperatorControl() && IsEnabled()) {
			/* Use the joystick X axis for lateral movement, Y axis for forward
			 * movement, and Z axis for rotation. This sample does not use
			 * field-oriented drive, so the gyro input is set to zero.
			 */
			robotDrive.MecanumDrive_Cartesian(stick.GetX(), stick.GetY(),
					stick.GetZ());

			frc::Wait(0.005); // wait 5ms to avoid hogging CPU cycles
		}
	}

private:
	// Channels for the wheels
	static constexpr int kFrontLeftChannel = 2;
	static constexpr int kRearLeftChannel = 3;
	static constexpr int kFrontRightChannel = 1;
	static constexpr int kRearRightChannel = 0;

	static constexpr int kJoystickChannel = 0;

	// Robot drive system
	frc::RobotDrive robotDrive { kFrontLeftChannel, kRearLeftChannel,
			kFrontRightChannel, kRearRightChannel };
	// Only joystick
	frc::Joystick stick { kJoystickChannel };
};

START_ROBOT_CLASS(Robot)

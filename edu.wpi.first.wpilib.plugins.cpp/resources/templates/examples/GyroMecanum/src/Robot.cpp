#include <AnalogGyro.h>
#include <Joystick.h>
#include <RobotDrive.h>
#include <IterativeRobot.h>

/**
 * This is a sample program that uses mecanum drive with a gyro sensor to
 * maintian rotation vectorsin relation to the starting orientation of the robot
 * (field-oriented controls).
 */
class Robot: public IterativeRobot {
public:
	void robotInit() {
		// invert the left side motors
		// you may need to change or remove this to match your robot
		myRobot.SetInvertedMotor(RobotDrive::kFrontLeftMotor, true);
		myRobot.SetInvertedMotor(RobotDrive::kRearLeftMotor, true);

		gyro.SetSensitivity(VOLTS_PER_DEGREE_PER_SECOND);
	}

	/**
	 * Mecanum drive is used with the gyro angle as an input.
	 */
	void teleopPeriodic() {
		myRobot.MecanumDrive_Cartesian(joystick.GetX(), joystick.GetY(),
				joystick.GetZ(), gyro.GetAngle());
	}

private:
	// Gyro calibration constant, may need to be adjusted
	// Gyro value of 360 is set to correspond to one full revolution
	static constexpr double VOLTS_PER_DEGREE_PER_SECOND = 0.0128;

	RobotDrive myRobot { 0, 1, 2, 3 };
	AnalogGyro gyro { 0 };
	Joystick joystick { 0 };
};

START_ROBOT_CLASS(Robot)

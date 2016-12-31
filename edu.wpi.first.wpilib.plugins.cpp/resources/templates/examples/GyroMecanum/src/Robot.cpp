#include <AnalogGyro.h>
#include <CANTalon.h>
#include <Joystick.h>
#include <RobotDrive.h>
#include <SampleRobot.h>

/**
 * This is a sample program that uses mecanum drive with a gyro sensor to
 * maintian rotation vectors in relation to the starting orientation of the
 * robot (field-oriented controls).
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
class Robot : public SampleRobot {
public:
	Robot() {
		// Invert the left side motors
		myRobot.SetInvertedMotor(RobotDrive::kFrontLeftMotor, true);

		// You may need to change or remove this to match your robot
		myRobot.SetInvertedMotor(RobotDrive::kRearLeftMotor, true);
	}

	/**
	 * Runs during autonomous.
	 */
	void Autonomous() {

	}

	/**
	 * Runs the motors with arcade steering.
	 */
	void OperatorControl() {
		// Calibrate gyro to have the value equal to degrees
		gyro.SetSensitivity(voltsPerDegreePerSecond);

		while (IsOperatorControl() && IsEnabled()) {
			myRobot.MecanumDrive_Cartesian(joystick.GetX(), joystick.GetY(),
			joystick.GetZ(), gyro.GetAngle());
			 Wait(0.005);  // Wait 5ms to avoid hogging CPU cycles
		}
	}

	/**
	 * Runs during test mode.
	 */
	void Test() {

	}

private:
	// Channels for motors
	constexpr int leftFrontMotorChannel = 1;
	constexpr int rightFrontMotorChannel = 0;
	constexpr int leftRearMotorChannel = 3;
	constexpr int rightRearMotorChannel = 2;

	constexpr int gyroChannel = 0; // analog input

	/* Gyro calibration constant, may need to be adjusted so that a gyro value
	 * of 360 equals 360 degrees
	 */
	constexpr double voltsPerDegreePerSecond = .0128;

	Joystick joystick{0};

	/* Create the robot using CANTalons; change as appropriate for different
	 * motors (eg, Victor, Jaguar, Talon, CANJaguar, etc.).
	 */
	RobotDrive myRobot(new CANTalon(leftFrontMotorChannel),
	                   new CANTalon(leftRearMotorChannel),
	                   new CANTalon(rightFrontMotorChannel),
	                   new CANTalon(rightRearMotorChannel));
	AnalogGyro gyro{gyroChannel};
};

START_ROBOT_CLASS(Robot)

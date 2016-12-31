#include <AnalogInput.h>
#include <RobotDrive.h>
#include <SampleRobot.h>

/**
 * This is a sample program demonstrating how to use an ultrasonic sensor and
 * proportional control to maintain a set distance from an object.
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
	 * Tells the robot to drive to a set distance (in inches) from an object
	 * using proportional control.
	 */
	void OperatorControl() {
		// Distance measured from the ultrasonic sensor values
		double currentDistance;

		// Speed for setting the drive train motors
		double currentSpeed;

		while (IsOperatorControl() && IsEnabled()) {
			// Sensor returns a value from 0-4095 that is scaled to inches
			currentDistance = ultrasonic.GetValue() * valueToInches;

			// Convert distance error to a motor speed
			currentSpeed = (holdDistance - currentDistance) * pGain;

			// Drive robot
			myRobot.Drive(currentSpeed, 0);
		}
	}

	/**
	 * Runs during test mode
	 */
	void Test() {

	}

private:
	constexpr int ultrasonicChannel = 3;  // Analog input pin

	// Channels for motors
	constexpr int leftFrontMotorChannel = 1;
	constexpr int rightFrontMotorChannel = 0;
	constexpr int leftRearMotorChannel = 3;
	constexpr int rightRearMotorChannel = 2;

	// Distance in inches the robot wants to stay from an object
	constexpr int holdDistance = 12;

	// Factor to convert sensor values to a distance in inches
	constexpr double valueToInches = 0.125;

	// Proportional speed constant
	constexpr double pGain = 0.05;

	AnalogInput ultrasonic{ultrasonicChannel};  // Ultrasonic sensor
	RobotDrive myRobot{new CANTalon(leftFrontMotorChannel),
	                   new CANTalon(leftRearMotorChannel),
	                   new CANTalon(rightFrontMotorChannel),
	                   new CANTalon(rightRearMotorChannel);
};

START_ROBOT_CLASS(Robot)

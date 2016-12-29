#include <AnalogInput.h>
#include <RobotDrive.h>
#include <IterativeRobot.h>

/**
 * This is a sample program demonstrating how to use an ultrasonic sensor and
 * proportional control to maintain a set distance from an object.
 */
class Robot : public IterativeRobot {
public:
	/**
	 * Tells the robot to drive to a set distance (in inches) from an object
	 * using proportional control.
	 */
	void TeleopPeriodic() {
		// sensor returns a value from 0-4095 that is scaled to inches
		double currentDistance = ultrasonic.GetValue() * VALUE_TO_INCHES;
		// convert distance error to a motor speed
		double currentSpeed = (HOLD_DISTANCE - currentDistance) * KP;
		// drive robot
		myRobot.Drive(currentSpeed, 0);
	}


private:
	// Distance in inches the robot wants to stay from an object
	static constexpr int HOLD_DISTANCE = 12;

	// Factor to convert sensor values to a distance in inches
	static constexpr double VALUE_TO_INCHES = 0.125;

	// Proportional speed constant
	static constexpr double KP = 0.05;

	AnalogInput ultrasonic{0};
	RobotDrive myRobot{0, 1};
};

START_ROBOT_CLASS(Robot)

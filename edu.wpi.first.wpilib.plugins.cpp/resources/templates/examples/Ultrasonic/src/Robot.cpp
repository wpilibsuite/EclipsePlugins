#include <AnalogInput.h>
#include <IterativeRobot.h>
#include <RobotDrive.h>

/**
 * This is a sample program demonstrating how to use an ultrasonic sensor and
 * proportional control to maintain a set distance from an object.
 */
class Robot: public frc::IterativeRobot {
public:
	/**
	 * Tells the robot to drive to a set distance (in inches) from an object
	 * using proportional control.
	 */
	void TeleopPeriodic() override {
		// sensor returns a value from 0-4095 that is scaled to inches
		double currentDistance = ultrasonic.GetValue() * kValueToInches;
		// convert distance error to a motor speed
		double currentSpeed = (kHoldDistance - currentDistance) * kP;
		// drive robot
		myRobot.Drive(currentSpeed, 0);
	}

private:
	// Distance in inches the robot wants to stay from an object
	static constexpr int kHoldDistance = 12;

	// Factor to convert sensor values to a distance in inches
	static constexpr double kValueToInches = 0.125;

	// Proportional speed constant
	static constexpr double kP = 0.05;

	static constexpr int kLeftMotorPort = 0;
	static constexpr int kRightMotorPort = 1;
	static constexpr int kUltrasonicPort = 0;

	frc::AnalogInput ultrasonic { kUltrasonicPort };
	frc::RobotDrive myRobot { kLeftMotorPort, kRightMotorPort };
};

	START_ROBOT_CLASS(Robot)
};

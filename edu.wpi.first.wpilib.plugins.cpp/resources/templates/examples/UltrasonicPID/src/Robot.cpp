#include <AnalogInput.h>
#include <RobotDrive.h>
#include <IterativeRobot.h>
#include <PIDController.h>
#include <PIDOutput.h>

/**
 * This is a sample program demonstrating how to use an ultrasonic sensor and
 * proportional control to maintain a set distance from an object.
 */
class Robot: public IterativeRobot {
public:
	/**
	 * Drives the robot a set distance from an object using PID control and the
	 * ultrasonic sensor.
	 */
	void TeleopInit() {
		// Set expected range to 0-24 inches; e.g. at 24 inches from object go
		// full forward, at 0 inches from object go full backward.
		pidController.SetInputRange(0, 24 * VALUE_TO_INCHES);
		// Set setpoint of the pidController
		pidController.SetSetpoint(HOLD_DISTANCE * VALUE_TO_INCHES);
		pidController.Enable(); // begin PID control
	}

private:
	// internal class to write to myRobot (a RobotDrive object) using a PIDOutput
	class MyPIDOutput: public PIDOutput {
	public:
		MyPIDOutput(RobotDrive& r) :
				rd(r) {
			rd.SetSafetyEnabled(false);
		}

		void PIDWrite(double output) {
			rd.Drive(output, 0); // write to myRobot (RobotDrive) by reference
		}
	private:
		RobotDrive& rd;
	};

	// Distance in inches the robot wants to stay from an object
	static constexpr int HOLD_DISTANCE = 12;

	// Factor to convert sensor values to a distance in inches
	static constexpr double VALUE_TO_INCHES = 0.125;

	AnalogInput ultrasonic{0};
	RobotDrive myRobot{0, 1};
	PIDController pidController{7, 0.018, 1.5, &ultrasonic,
			new MyPIDOutput(myRobot)};
};

START_ROBOT_CLASS(Robot)

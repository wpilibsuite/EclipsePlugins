#include <AnalogInput.h>
#include <CANTalon.h>
#include <PIDController.h>
#include <PIDOutput.h>
#include <RobotDrive.h>
#include <SampleRobot.h>

/**
 * This is a sample program to demonstrate the use of a PID Controller with an
 * ultrasonic sensor to reach and maintain a set distance from an object.
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
	 * Drives robot to set distance from an object using PID control and the ultrasonic
	 * sensor.
	 */
	void OperatorControl() {
		// set setpoint to 12 inches
		pidController.SetSetpoint(holdDistance * VoltsToInches);

		/* set expected range to 0-24 inches; e.g. at 24 inches from object go
		 * full forward, at 0 inches from object go full backward.
		 */
		pidController.SetInputRange(0, 24 * VoltsToInches);

		while (IsOperatorControl() && IsEnabled()) {
			pidController.Enable(); // begin PID control
		}
	}

	/**
	 * Runs during test mode.
	 */
	void Test() {

	}

private:
	constexpr int ultrasonicChannel = 3;  // Analog input

	// Channels for motors
	constexpr int leftFrontMotorChannel = 1;
	constexpr int rightFrontMotorChannel = 0;
	constexpr int leftRearMotorChannel = 3;
	constexpr int rightRearMotorChannel = 2;

	// Distance in inches the robot wants to stay from an object
	constexpr int holdDistance = 12;

	/* proportional, integral, and derivative speed constants
	 * DANGER: when tuning PID constants, high/inappropriate values for pGain,
	 * iGain, and dGain may cause dangerous, uncontrollable, or undesired
	 * behavior!
	 */
	constexpr double pGain = 7;
	constexpr double iGain = 0.018;
	constexpr double dGain = 1.5;

	/* conversion factor specific to the sensor being used. For this sensor,
	 * the sensor returned values from 0.0V to 5.0V with a resolution of
	 * 9.8mV/in.
	 */
	constexpr double VoltsToInches = 0.0098;

	// internal class to write to myRobot (a RobotDrive object) using a PIDOutput
	class MyPIDOutput: public PIDOutput {
	public:
		RobotDrive& rd;
		MyPIDOutput(RobotDrive& r) : rd(r) {
			rd.SetSafetyEnabled(false);
		}

		void PIDWrite(float output) {
			rd.Drive(output, 0); // write to myRobot (RobotDrive) by reference
		}
	};

	AnalogInput ultrasonic{ultrasonicChannel};  // Ultrasonic sensor
	RobotDrive myRobot(new CANTalon(leftFrontMotorChannel),
	                   new CANTalon(leftRearMotorChannel),
	                   new CANTalon(rightFrontMotorChannel),
	                   new CANTalon(rightRearMotorChannel));

	/* ultrasonic (AnalogInput) can be used as a PIDSource without modification,
	 * PIDOutput is an instance of the internal class MyPIDOutput made earlier
	 */
	PIDController pidController{pGain, iGain, dGain, &ultrasonic,
	                            new MyPIDOutput(myRobot)};
};

START_ROBOT_CLASS(Robot)

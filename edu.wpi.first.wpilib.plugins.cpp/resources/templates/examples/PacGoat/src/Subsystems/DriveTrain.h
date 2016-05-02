#ifndef DriveTrain_H
#define DriveTrain_H

#include <memory>

#include <AnalogGyro.h>
#include <Commands/Subsystem.h>
#include <Encoder.h>
#include <RobotDrive.h>
#include <Victor.h>

/**
 * The DriveTrain subsystem controls the robot's chassis and reads in
 * information about it's speed and position.
 */
class DriveTrain : public Subsystem {
public:
	DriveTrain();

	/**
	 * When other commands aren't using the drivetrain, allow tank drive with
	 * the joystick.
	 */
	void InitDefaultCommand();

	/**
	 * @param joy PS3 style joystick to use as the input for tank drive.
	 */
	void TankDrive(Joystick* joy);

	/**
	 * @param leftAxis Left sides value
	 * @param rightAxis Right sides value
	 */
	void TankDrive(double leftAxis, double rightAxis);

	/**
	 * Stop the drivetrain from moving.
	 */
	void Stop();

	/**
	 * @return The encoder getting the distance and speed of left side of the drivetrain.
	 */
	std::shared_ptr<Encoder> GetLeftEncoder();

	/**
	 * @return The encoder getting the distance and speed of right side of the drivetrain.
	 */
	std::shared_ptr<Encoder> GetRightEncoder();

	/**
	 * @return The current angle of the drivetrain.
	 */
	double GetAngle();

private:
	// Subsystem devices
	std::shared_ptr<SpeedController> frontLeftCIM = std::make_shared<Victor>(1);
	std::shared_ptr<SpeedController> frontRightCIM = std::make_shared<Victor>(2);
	std::shared_ptr<SpeedController> backLeftCIM = std::make_shared<Victor>(3);
	std::shared_ptr<SpeedController> backRightCIM = std::make_shared<Victor>(4);
	RobotDrive drive{frontRightCIM, backLeftCIM, frontRightCIM, backRightCIM};
	std::shared_ptr<Encoder> rightEncoder = std::make_shared<Encoder>(1, 2, true, Encoder::k4X);
	std::shared_ptr<Encoder> leftEncoder = std::make_shared<Encoder>(3, 4, false, Encoder::k4X);
	std::shared_ptr<AnalogGyro> gyro = std::make_shared<AnalogGyro>(0);
};

#endif  // DriveTrain_H

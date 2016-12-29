#ifndef DriveTrain_H
#define DriveTrain_H

#include "Commands/Subsystem.h"
#include "WPILib.h"

/**
 * The DriveTrain subsystem controls the robot's chassis and reads in
 * information about it's speed and position.
 */
class DriveTrain: public frc::Subsystem
{
private:
	// Subsystem devices
	std::shared_ptr<frc::SpeedController> frontLeftCIM, frontRightCIM;
	std::shared_ptr<frc::SpeedController> backLeftCIM, backRightCIM;
	frc::RobotDrive drive;
	std::shared_ptr<frc::Encoder> rightEncoder, leftEncoder;
	std::shared_ptr<frc::AnalogGyro> gyro;

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
	void TankDrive(frc::Joystick* joy);

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
  std::shared_ptr<frc::Encoder> GetLeftEncoder();

	/**
	 * @return The encoder getting the distance and speed of right side of the drivetrain.
	 */
  std::shared_ptr<frc::Encoder> GetRightEncoder();

	/**
	 * @return The current angle of the drivetrain.
	 */
	double GetAngle();
};

#endif

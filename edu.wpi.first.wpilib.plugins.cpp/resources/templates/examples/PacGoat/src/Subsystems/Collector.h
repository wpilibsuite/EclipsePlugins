#ifndef Collector_H
#define Collector_H

#include <memory>

#include <Commands/Subsystem.h>
#include <DigitalInput.h>
#include <Solenoid.h>
#include <Victor.h>

/**
 * The Collector subsystem has one motor for the rollers, a limit switch for ball
 * detection, a piston for opening and closing the claw, and a reed switch to
 * check if the piston is open.
 */
class Collector : public Subsystem {
public:
	// Constants for some useful speeds
	static constexpr double FORWARD = 1;
	static constexpr double STOP = 0;
	static constexpr double REVERSE = -1;

	Collector();

	/**
	 * NOTE: The current simulation model uses the the lower part of the claw
	 * since the limit switch wasn't exported. At some point, this will be
	 * updated.
	 *
	 * @return Whether or not the robot has the ball.
	 */
	bool HasBall();

	/**
	 * @param speed The speed to spin the rollers.
	 */
	void SetSpeed(double speed);

	/**
	 * Stop the rollers from spinning
	 */
	void Stop();

	/**
	 * @return Whether or not the claw is open.
	 */
	bool IsOpen();

	/**
	 * Open the claw up. (For shooting)
	 */
	void Open();

	/**
	 * Close the claw. (For collecting and driving)
	 */
	void Close();

	/**
	 * No default command.
	 */
	void InitDefaultCommand();

private:
	// Subsystem devices
	std::shared_ptr<SpeedController> rollerMotor = std::make_shared<Victor>(6);
	std::shared_ptr<DigitalInput> ballDetector = std::make_shared<DigitalInput>(10);
	std::shared_ptr<Solenoid> piston = std::make_shared<Solenoid>(1);
	std::shared_ptr<DigitalInput> openDetector = std::make_shared<DigitalInput>(6);
};

#endif  // Collector_H

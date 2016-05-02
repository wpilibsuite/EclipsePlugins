#ifndef Elevator_H
#define Elevator_H

#include <Commands/PIDSubsystem.h>
#include <Potentiometer.h>
#include <Victor.h>

/**
 * The elevator subsystem uses PID to go to a given height. Unfortunately, in it's current
 * state PID values for simulation are different than in the real world do to minor differences.
 */
class Elevator : public PIDSubsystem {
public:
	Elevator();

	void InitDefaultCommand();

	/**
	 * The log method puts interesting information to the SmartDashboard.
	 */
	void Log();

	/**
	 * Use the potentiometer as the PID sensor. This method is automatically
	 * called by the subsystem.
	 */
	double ReturnPIDInput();


	/**
	 * Use the motor as the PID output. This method is automatically called by
	 * the subsystem.
	 */
	void UsePIDOutput(double d);

private:
	std::unique_ptr<SpeedController> motor = std::make_unique<Victor>(5);
	std::unique_ptr<Potentiometer> pot;

	constexpr double kP_real = 4;
	constexpr double kI_real = 0.07;
	constexpr double kP_simulation = 18;
	constexpr double kI_simulation = 0.2;
};

#endif  // Elevator_H

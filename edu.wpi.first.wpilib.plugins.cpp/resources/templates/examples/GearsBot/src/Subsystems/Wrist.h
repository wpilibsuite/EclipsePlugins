#ifndef Wrist_H
#define Wrist_H

#include <AnalogPotentiometer.h>
#include <Commands/PIDSubsystem.h>
#include <Victor.h>

/**
 * The wrist subsystem is like the elevator, but with a rotational joint instead
 * of a linear joint.
 */
class Wrist: public PIDSubsystem {
public:
	Wrist();

	void InitDefaultCommand() override;

	/**
	 * The log method puts interesting information to the SmartDashboard.
	 */
	void Log();

	/**
	 * Use the potentiometer as the PID sensor. This method is automatically
	 * called by the subsystem.
	 */
	double ReturnPIDInput() override;

	/**
	 * Use the motor as the PID output. This method is automatically called by
	 * the subsystem.
	 */
	void UsePIDOutput(double d) override;

private:
	frc::Victor motor { 6 };

	// Conversion value of potentiometer varies between the real world and simulation
#ifndef SIMULATION
	frc::AnalogPotentiometer pot { 3, -270.0 / 5 };
#else
	frc::AnalogPotentiometer pot { 3 }; // Defaults to degrees
#endif

	static constexpr double kP_real = 1;
	static constexpr double kP_simulation = 0.05;
};

#endif  // Wrist_H

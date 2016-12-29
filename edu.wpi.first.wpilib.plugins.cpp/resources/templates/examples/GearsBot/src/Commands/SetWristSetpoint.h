#ifndef SetWristSetpoint_H
#define SetWristSetpoint_H

#include <Commands/Command.h>

/**
 * Move the wrist to a given angle. This command finishes when it is within
 * the tolerance, but leaves the PID loop running to maintain the position.
 * Other commands using the wrist should make sure they disable PID!
 */
class SetWristSetpoint : public Command {
public:
	SetWristSetpoint(double setpoint);
	void Initialize();
	bool IsFinished();

private:
	double setpoint;
};

#endif  // SetWristSetpoint_H

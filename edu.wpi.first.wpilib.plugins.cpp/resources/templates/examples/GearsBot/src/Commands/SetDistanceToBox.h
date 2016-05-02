#ifndef SetDistanceToBox_H
#define SetDistanceToBox_H

#include <Commands/Command.h>
#include <PIDOutput.h>
#include <PIDSource.h>

/**
 * Drive until the robot is the given distance away from the box. Uses a local
 * PID controller to run a simple PID loop that is only enabled while this
 * command is running. The input is the averaged values of the left and right
 * encoders.
 */
class SetDistanceToBox : public Command {
public:
	SetDistanceToBox(double distance);
	void Initialize();
	bool IsFinished();
	void End();
private:
	PIDController* pid;
};

class SetDistanceToBoxPIDSource : public PIDSource {
public:
	virtual ~SetDistanceToBoxPIDSource() = default;
	double PIDGet();
};

class SetDistanceToBoxPIDOutput : public PIDOutput {
public:
	virtual ~SetDistanceToBoxPIDOutput() = default;
	void PIDWrite(float d);
};

#endif  // SetDistanceToBox_H

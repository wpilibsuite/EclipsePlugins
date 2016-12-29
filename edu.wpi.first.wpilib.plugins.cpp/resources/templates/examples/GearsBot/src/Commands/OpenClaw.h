#ifndef OpenClaw_H
#define OpenClaw_H

#include <Commands/Command.h>

/**
 * Opens the claw for one second. Real robots should use sensors, stalling
 * motors is BAD!
 */
class OpenClaw : public Command {
public:
	OpenClaw();
	void Initialize();
	bool IsFinished();
	void End();
};

#endif  // OpenClaw_H

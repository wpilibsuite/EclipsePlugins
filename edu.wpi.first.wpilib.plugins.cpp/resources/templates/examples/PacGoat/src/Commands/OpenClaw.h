#ifndef OpenClaw_H
#define OpenClaw_H

#include <Command/Command.h>

/**
 * Opens the claw
 */
class OpenClaw : public Command {
public:
	OpenClaw();
	void Initialize();
	bool IsFinished();
};

#endif  // OpenClaw_H

#ifndef OpenClaw_H
#define OpenClaw_H

#include "WPILib.h"

/**
 * Opens the claw
 */
class OpenClaw: public frc::Command {
public:
	OpenClaw();
	void Initialize();
	void Execute();
	bool IsFinished();
	void End();
	void Interrupted();
};

#endif

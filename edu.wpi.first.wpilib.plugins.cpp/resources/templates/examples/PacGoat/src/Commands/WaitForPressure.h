#ifndef WaitForPressure_H
#define WaitForPressure_H

#include <Commands/Command.h>

/**
 * Wait until the pneumatics are fully pressurized. This command does nothing
 * and is intended to be used in command groups to wait for this condition.
 */
class WaitForPressure : public Command {
public:
	WaitForPressure();
	bool IsFinished();
};

#endif  // WaitForPressure_H

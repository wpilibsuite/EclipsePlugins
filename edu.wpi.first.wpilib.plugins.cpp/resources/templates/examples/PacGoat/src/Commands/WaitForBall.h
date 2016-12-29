#ifndef WaitForBall_H
#define WaitForBall_H

#include <Commands/Command.h>

/**
 * Wait until the collector senses that it has the ball. This command does
 * nothing and is intended to be used in command groups to wait for this
 * condition.
 */
class WaitForBall : public Command {
public:
	WaitForBall();
	bool IsFinished();
};

#endif  // WaitForBall_H

#ifndef ExtendShooter_H
#define ExtendShooter_H

#include <Commands/TimedCommand.h>

/**
 * Extend the shooter and then retract it after a second.
 */
class ExtendShooter : public TimedCommand {
public:
	ExtendShooter();
	void Initialize();
	void End();
};

#endif  // ExtendShooter_H

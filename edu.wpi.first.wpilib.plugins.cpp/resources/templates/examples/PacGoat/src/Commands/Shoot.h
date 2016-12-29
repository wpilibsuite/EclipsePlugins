#ifndef Shoot_H
#define Shoot_H

#include <Commands/CommandGroup.h>

/**
 * Shoot the ball at the current angle.
 */
class Shoot : public CommandGroup {
public:
	Shoot();
};

#endif  // Shoot_H

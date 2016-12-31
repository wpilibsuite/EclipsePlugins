#ifndef DriveWithJoystick_H
#define DriveWithJoystick_H

#include <Commands/Command.h>

/**
 * This command allows PS3 joystick to drive the robot. It is always running
 * except when interrupted by another command.
 */
class DriveWithJoystick : public Command {
public:
	DriveWithJoystick();
	void Execute();
	bool IsFinished();
	void End();
};

#endif  // DriveWithJoystick_H

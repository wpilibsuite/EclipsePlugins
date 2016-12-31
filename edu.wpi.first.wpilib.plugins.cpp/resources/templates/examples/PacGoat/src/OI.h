#ifndef OI_H
#define OI_H

#include <Buttons/JoystickButton.h>
#include <Joystick.h>

#include "Triggers/DoubleButton.h"

class OI {
public:
	OI();
	Joystick* GetJoystick();

private:
	Joystick joystick{0};
	JoystickButton L1{&joystick, 11};
	JoystickButton L2{&joystick, 9};
	JoystickButton R1{&joystick, 12};
	JoystickButton R2{&joystick, 10};
	DoubleButton sticks{&joystick, 2, 3};
};

#endif

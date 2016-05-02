#ifndef OI_H_
#define OI_H_

#include <Joystick.h>

class OI {
public:
	OI();
	Joystick* GetJoystick();

private:
	Joystick joy{0};
};

#endif  // OI_H_

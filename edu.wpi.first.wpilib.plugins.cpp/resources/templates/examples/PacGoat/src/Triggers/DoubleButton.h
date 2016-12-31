#ifndef DOUBLEBUTTON_H_
#define DOUBLEBUTTON_H_

#include <Buttons/Trigger.h>

class Joystick;

class DoubleButton : public Trigger {
public:
	DoubleButton(Joystick* joy, int button1, int button2);

	bool Get();

private:
	Joystick* joy;
	int button1, button2;
};

#endif  // DOUBLEBUTTON_H_

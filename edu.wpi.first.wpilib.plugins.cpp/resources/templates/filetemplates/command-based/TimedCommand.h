#ifndef $classname_H
#define $classname_H

#include "Commands/TimedCommand.h"

class $classname : public TimedCommand {
public:
	$classname(double timeout);
	void Initialize();
	void Execute();
	void End();
	void Interrupted();
};

#endif  // $classname_H

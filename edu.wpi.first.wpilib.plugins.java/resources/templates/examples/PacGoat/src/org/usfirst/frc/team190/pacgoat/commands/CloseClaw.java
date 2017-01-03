package $package.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;

import $package.Robot;

/**
 * Close the claw.
 *
 * NOTE: It doesn't wait for the claw to close since there is no sensor to
 * detect that.
 */
public class CloseClaw extends InstantCommand {

	public CloseClaw() {
		requires(Robot.collector);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		Robot.collector.close();
	}
}

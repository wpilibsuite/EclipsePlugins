package $package.commands;

import edu.wpi.first.wpilibj.command.Command;

import $package.Robot;

/**
 * Wait until the collector senses that it has the ball. This command does
 * nothing and is intended to be used in command groups to wait for this
 * condition.
 */
public class WaitForBall extends Command {
	public WaitForBall() {
		requires(Robot.collector);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return Robot.collector.hasBall();
	}
}

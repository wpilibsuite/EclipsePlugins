package $package.commands;

import $package.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

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
    protected void initialize() {
        Robot.collector.close();
    }
}

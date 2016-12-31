package $package.commands;

import $package.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Opens the claw
 */
public class OpenClaw extends Command {

    public OpenClaw() {
        requires(Robot.collector);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        Robot.collector.open();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.collector.isOpen();
    }
}

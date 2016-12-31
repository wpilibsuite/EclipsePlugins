package $package.commands;

import $package.Robot;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Extend the shooter and then retract it after a second.
 */
public class ExtendShooter extends TimedCommand {

    public ExtendShooter() {
        super(1);
        requires(Robot.shooter);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        Robot.shooter.extendBoth();
    }

    // Called once after isFinished returns true
    protected void end() {
        Robot.shooter.retractBoth();
    }
}

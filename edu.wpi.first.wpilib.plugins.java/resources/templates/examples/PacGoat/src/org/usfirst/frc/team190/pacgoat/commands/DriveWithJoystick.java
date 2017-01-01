package $package.commands;

import edu.wpi.first.wpilibj.command.Command;

import $package.Robot;

/**
 * This command allows PS3 joystick to drive the robot. It is always running
 * except when interrupted by another command.
 */
public class DriveWithJoystick extends Command {
	public DriveWithJoystick() {
		requires(Robot.drivetrain);
	}

	@Override
	protected void execute() {
		Robot.drivetrain.tankDrive(Robot.oi.getJoystick());
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		Robot.drivetrain.stop();
	}
}

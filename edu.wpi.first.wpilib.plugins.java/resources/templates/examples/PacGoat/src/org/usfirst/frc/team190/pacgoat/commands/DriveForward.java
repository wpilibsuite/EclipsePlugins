package $package.commands;

import edu.wpi.first.wpilibj.command.Command;

import $package.Robot;

/**
 * This command drives the robot over a given distance with simple proportional
 * control This command will drive a given distance limiting to a maximum speed.
 */
public class DriveForward extends Command {
	private double driveForwardSpeed;
	private double distance;
	private double error;
	private final double kTolerance = 0.1;
	private final double kP = -1.0 / 5.0;

	public DriveForward() {
		this(10, 0.5);
	}

	public DriveForward(double dist) {
		this(dist, 0.5);
	}

	public DriveForward(double dist, double maxSpeed) {
		requires(Robot.drivetrain);
		distance = dist;
		driveForwardSpeed = maxSpeed;
	}

	@Override
	protected void initialize() {
		Robot.drivetrain.getRightEncoder().reset();
		setTimeout(2);
	}

	@Override
	protected void execute() {
		error = (distance - Robot.drivetrain.getRightEncoder().getDistance());
		if (driveForwardSpeed * kP * error >= driveForwardSpeed) {
			Robot.drivetrain.tankDrive(driveForwardSpeed, driveForwardSpeed);
		} else {
			Robot.drivetrain.tankDrive(driveForwardSpeed * kP * error, driveForwardSpeed * kP * error);
		}
	}

	@Override
	protected boolean isFinished() {
		return (Math.abs(error) <= kTolerance) || isTimedOut();
	}

	@Override
	protected void end() {
		Robot.drivetrain.stop();
	}
}

package $package.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;

import $package.Robot;

/**
 * This command sets the collector rollers spinning at the given speed. Since
 * there is no sensor for detecting speed, it finishes immediately. As a result,
 * the spinners may still be adjusting their speed.
 */
public class SetCollectionSpeed extends InstantCommand {
	private double speed;

	public SetCollectionSpeed(double speed) {
		requires(Robot.collector);
		this.speed = speed;
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		Robot.collector.setSpeed(speed);
	}
}

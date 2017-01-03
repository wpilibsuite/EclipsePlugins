/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package $package.commands;

import edu.wpi.first.wpilibj.command.Command;

import $package.Robot;

/**
 * Moves the pivot to a given angle. This command finishes when it is within the
 * tolerance, but leaves the PID loop running to maintain the position. Other
 * commands using the pivot should make sure they disable PID!
 */
public class SetPivotSetpoint extends Command {
	private double setpoint;

	public SetPivotSetpoint(double setpoint) {
		this.setpoint = setpoint;
		requires(Robot.pivot);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		Robot.pivot.enable();
		Robot.pivot.setSetpoint(setpoint);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return Robot.pivot.onTarget();
	}
}

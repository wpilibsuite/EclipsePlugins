package $package;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * This is a sample program to demonstrate how to use a gyro sensor to make a
 * robot drive straight. This program uses a joystick to drive forwards and
 * backwards while the gyro is used for direction keeping.
 */
public class Robot extends IterativeRobot {

    private static final double kAngleSetpoint = 0.0;
    private static final double kP = 0.005; // propotional turning constant

    // gyro calibration constant, may need to be adjusted;
    // gyro value of 360 is set to correspond to one full revolution
    private static final double kVoltsPerDegreePerSecond = 0.0128;

    private RobotDrive myRobot;
    private AnalogGyro gyro;
    private Joystick joystick;

    public void robotInit() {
        myRobot = new RobotDrive(0, 1);
        gyro = new AnalogGyro(0);
        joystick = new Joystick(0);

        gyro.setSensitivity(kVoltsPerDegreePerSecond);
    }

    /**
     * The motor speed is set from the joystick while the RobotDrive turning
     * value is assigned from the error between the setpoint and the gyro angle.
     */
    public void teleopPeriodic() {
        double turningValue = (kAngleSetpoint - gyro.getAngle()) * kP;
        // Invert the direction of the turn if we are going backwards
        turningValue = Math.copySign(turningValue, joystick.getY());
        myRobot.drive(joystick.getY(), turningValue);
    }

}

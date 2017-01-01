package $package;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;

/**
 * This is a sample program that uses mecanum drive with a gyro sensor to
 * maintian rotation vectorsin relation to the starting orientation of the robot
 * (field-oriented controls).
 */
public class Robot extends IterativeRobot {

    // gyro calibration constant, may need to be adjusted;
    // gyro value of 360 is set to correspond to one full revolution
    private static final double kVoltsPerDegreePerSecond = 0.0128;

    private static final int kFrontLeftMotorPort = 0;
    private static final int kFrontRightMotorPort = 1;
    private static final int kRearLeftMotorPort = 2;
    private static final int kRearRightMotorPort = 3;
    private static final int kGyroPort = 0;
    private static final int kJoystickPort = 0;

    private RobotDrive myRobot;
    private AnalogGyro gyro;
    private Joystick joystick;

    public void robotInit() {
        myRobot = new RobotDrive(kFrontLeftMotorPort, kFrontRightMotorPort,
            kRearLeftMotorPort, kRearRightMotorPort);
        gyro = new AnalogGyro(kGyroPort);
        joystick = new Joystick(kJoystickPort);

        // invert the left side motors
        // you may need to change or remove this to match your robot
        myRobot.setInvertedMotor(MotorType.kFrontLeft, true);
        myRobot.setInvertedMotor(MotorType.kRearLeft, true);

        gyro.setSensitivity(kVoltsPerDegreePerSecond);
    }

    /**
     * Mecanum drive is used with the gyro angle as an input.
     */
    public void teleopPeriodic() {
        myRobot.mecanumDrive_Cartesian(joystick.getX(), joystick.getY(), joystick.getZ(), gyro.getAngle());
    }

}

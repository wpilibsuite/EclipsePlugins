package $package;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    RobotDrive myRobot;
    Joystick stick;
    Timer timer;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        myRobot = new RobotDrive(0,1);
        stick = new Joystick(0);
        timer = new Timer();
    }

    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
        timer.reset();
        timer.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        // Drive for 2 seconds
        if (timer.get() < 2.0) {
            myRobot.drive(-0.5, 0.0); // drive forwards half speed
        } else {
            myRobot.drive(0.0, 0.0); // stop robot
        }
    }

    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit(){
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        myRobot.arcadeDrive(stick);
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}

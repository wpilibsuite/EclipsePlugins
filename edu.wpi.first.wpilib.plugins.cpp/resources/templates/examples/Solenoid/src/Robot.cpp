#include <DoubleSolenoid.h>
#include <Joystick.h>
#include <SampleRobot.h>
#include <Solenoid.h>
#include <Timer.h>

/**
 * This is a sample program showing the use of the solenoid classes during
 *   operator control.
 * Three buttons from a joystick will be used to control two solenoids:
 *   One button to control the position of a single solenoid and the other
 *   two buttons to control a double solenoid.
 * Single solenoids can either be on or off, such that the air diverted through
 *   them goes through either one channel or the other.
 * Double solenoids have three states: Off, Forward, and Reverse. Forward and
 *   Reverse divert the air through the two channels and correspond to the
 *   on and off of a single solenoid, but a double solenoid can also be "off",
 *   where both channels are diverted to exhaust such that there is no pressure
 *   in either channel.
 * Additionally, double solenoids take up two channels on your PCM whereas
 *   single solenoids only take a single channel.
 * During Operator Control, the loop waits for a brief time before continuing
 *   in order to allow other threads to run. This is generally a good idea,
 *   especially as joystick values are only received every 20ms.
 */
class Robot: public frc::SampleRobot {
public:
	/**
	 * Sets the solenoids from the position of joystick buttons.
	 */
	void OperatorControl() override {
		while (IsOperatorControl() && IsEnabled()) {
			/* The output of GetRawButton is true/false depending on whether
			 *   the button is pressed; Set takes a boolean for for whether to
			 *   use the default (false) channel or the other (true).
			 */
			m_solenoid.Set(m_stick.GetRawButton(kSolenoidButton));

			/* In order to set the double solenoid, we will say that if neither
			 *   button is pressed, it is off, if just one button is pressed,
			 *   set the solenoid to correspond to that button, and if both
			 *   are pressed, set the solenoid to Forwards.
			 */
			if (m_stick.GetRawButton(kDoubleSolenoidForward)) {
				m_doubleSolenoid.Set(frc::DoubleSolenoid::kForward);
			} else if (m_stick.GetRawButton(kDoubleSolenoidReverse)) {
				m_doubleSolenoid.Set(frc::DoubleSolenoid::kReverse);
			} else {
				m_doubleSolenoid.Set(frc::DoubleSolenoid::kOff);
			}

			frc::Wait(kUpdatePeriod);  // Wait for a motor update time
		}
	}

private:
	// Joystick with buttons to control solenoids with.
	frc::Joystick m_stick { 0 };

	// Solenoids to control with the joystick.
	// Solenoid corresponds to a single solenoid.
	frc::Solenoid m_solenoid { 0 };

	/* DoubleSolenoid corresponds to a double solenoid.
	 * Use double solenoid with forward channel of 1 and reverse of 2
	 */
	frc::DoubleSolenoid m_doubleSolenoid { 1, 2 };

	// Update every 5milliseconds/0.005 seconds.
	static constexpr double kUpdatePeriod = 0.005;

	// Numbers of the buttons to use for triggering the solenoids.
	static constexpr int kSolenoidButton = 1;
	static constexpr int kDoubleSolenoidForward = 2;
	static constexpr int kDoubleSolenoidReverse = 3;
};

START_ROBOT_CLASS(Robot)

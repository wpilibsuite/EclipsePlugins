#include <Joystick.h>
#include <Relay.h>
#include <SampleRobot.h>
#include <Timer.h>

/**
 * This is a sample program which uses joystick buttons to control a relay.
 * A Relay (generally a spike) has two outputs, each of which can be at either
 *   0V or 12V and so can be used for actions such as turning a motor off,
 *   full forwards, or full reverse, and is generally used on the compressor.
 * This program uses two buttons on a joystick and each button corresponds to
 *   one output; pressing the button sets the output to 12V and releasing sets
 *   it to 0V.
 * During Operator Control, the loop waits for a brief time before continuing
 *   in order to allow other threads to run. This is generally a good idea,
 *   especially as joystick values are only received every 20ms.
 */
class Robot: public frc::SampleRobot {
public:
	/**
	 * Control a Relay using Joystick buttons.
	 */
	void OperatorControl() override {
		while (IsOperatorControl() && IsEnabled()) {
			/* Retrieve the button values. GetRawButton will return
			 * true if the button is pressed and false if not.
			 */
			bool forward = m_stick.GetRawButton(kRelayForwardButton);
			bool reverse = m_stick.GetRawButton(kRelayReverseButton);

			/* Depending on the button values, we want to use one of
			 *   kOn, kOff, kForward, or kReverse.
			 * kOn sets both outputs to 12V, kOff sets both to 0V,
			 * kForward sets forward to 12V and reverse to 0V, and
			 * kReverse sets reverse to 12V and forward to 0V.
			 */
			if (forward && reverse) {
				m_relay.Set(Relay::kOn);
			} else if (forward) {
				m_relay.Set(Relay::kForward);
			} else if (reverse) {
				m_relay.Set(Relay::kReverse);
			} else {
				m_relay.Set(Relay::kOff);
			}

			// Insert 5ms delay in loop.
			frc::Wait(kUpdatePeriod);
		}
	}

private:
	// Joystick with which to control the relay.
	frc::Joystick m_stick { 0 };

	// Relay to use for the
	frc::Relay m_relay { 0 };

	// Numbers of the buttons to be used for controlling the Relay.
	static constexpr int kRelayForwardButton = 1;
	static constexpr int kRelayReverseButton = 2;

	// Update every 5milliseconds/0.005 seconds.
	static constexpr double kUpdatePeriod = 0.005;
};

START_ROBOT_CLASS(Robot)

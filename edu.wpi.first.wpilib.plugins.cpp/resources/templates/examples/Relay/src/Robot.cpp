#include <IterativeRobot.h>
#include <Joystick.h>
#include <Relay.h>

/**
 * This is a sample program which uses joystick buttons to control a relay.
 * A Relay (generally a spike) has two outputs, each of which can be at either
 *   0V or 12V and so can be used for actions such as turning a motor off,
 *   full forwards, or full reverse, and is generally used on the compressor.
 * This program uses two buttons on a joystick and each button corresponds to
 *   one output; pressing the button sets the output to 12V and releasing sets
 *   it to 0V.
 */
class Robot: public frc::IterativeRobot {
public:
	void TeleopPeriodic() override {
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
	}

private:
	frc::Joystick m_stick { 0 };
	frc::Relay m_relay { 0 };

	static constexpr int kRelayForwardButton = 1;
	static constexpr int kRelayReverseButton = 2;
};

START_ROBOT_CLASS(Robot)

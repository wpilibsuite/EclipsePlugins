#ifndef Pneumatics_H
#define Pneumatics_H

#include <memory>

#include <AnalogInput.h>
#include <Commands/Subsystem.h>
#include <Compressor.h>

/**
 * The Pneumatics subsystem contains the compressor and a pressure sensor.
 *
 * NOTE: The simulator currently doesn't support the compressor or pressure sensors.
 */
class Pneumatics : public Subsystem {
public:
	Pneumatics();

	/**
	 * No default command
	 */
	void InitDefaultCommand();

	/**
	 * Start the compressor going. The compressor automatically starts and stops as it goes above and below maximum pressure.
	 */
	void Start();

	/**
	 * @return Whether or not the system is fully pressurized.
	 */
	bool IsPressurized();

	/**
	 * Puts the pressure on the SmartDashboard.
	 */
	void WritePressure();

private:
	std::shared_ptr<AnalogInput> pressureSensor = std::make_shared<AnalogInput>(3);
	#ifdef REAL
		std::unique_ptr<Compressor> compressor;
	#endif

	static constexpr double MAX_PRESSURE = 2.55;
};

#endif  // Pneumatics_H

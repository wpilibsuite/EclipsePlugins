#include "DriveTrain.h"

#include <cmath>

#include "Commands/DriveWithJoystick.h"

DriveTrain::DriveTrain() : Subsystem("DriveTrain") {
	// XXX: LiveWindow::GetInstance()->AddActuator("DriveTrain", "Front Left CIM", (Victor) frontLeftCIM);
	// XXX: LiveWindow::GetInstance()->AddActuator("DriveTrain", "Front Right CIM", (Victor) frontRightCIM);
	// XXX: LiveWindow::GetInstance()->AddActuator("DriveTrain", "Back Left CIM", (Victor) backLeftCIM);
	// XXX: LiveWindow::GetInstance()->AddActuator("DriveTrain", "Back Right CIM", (Victor) backRightCIM);

	// Configure the RobotDrive to reflect the fact that all our motors are
	// wired backwards and our drivers sensitivity preferences.
	drive.SetSafetyEnabled(false);
	drive.SetExpiration(0.1);
	drive.SetSensitivity(0.5);
	drive.SetMaxOutput(1.0);
	drive.SetInvertedMotor(RobotDrive::kFrontLeftMotor, true);
	drive.SetInvertedMotor(RobotDrive::kRearLeftMotor, true);
	drive.SetInvertedMotor(RobotDrive::kFrontRightMotor, true);
	drive.SetInvertedMotor(RobotDrive::kRearRightMotor, true);

	// Configure encoders
	rightEncoder->SetPIDSourceType(PIDSourceType::kDisplacement);
	leftEncoder->SetPIDSourceType(PIDSourceType::kDisplacement);

	#ifdef REAL
		// Converts to feet
		rightEncoder->SetDistancePerPulse(0.0785398);
		leftEncoder->SetDistancePerPulse(0.0785398);
	#else
		// Convert to feet 4in diameter wheels with 360 tick simulated encoders
		rightEncoder->SetDistancePerPulse((4.0/*in*/*M_PI)/(360.0*12.0/*in/ft*/));
		leftEncoder->SetDistancePerPulse((4.0/*in*/*M_PI)/(360.0*12.0/*in/ft*/));
	#endif

	LiveWindow::GetInstance()->AddSensor("DriveTrain", "Right Encoder", rightEncoder);
	LiveWindow::GetInstance()->AddSensor("DriveTrain", "Left Encoder", leftEncoder);

	// Configure gyro
	#ifdef REAL
		gyro->SetSensitivity(0.007); // TODO: Handle more gracefully?
	#endif
	LiveWindow::GetInstance()->AddSensor("DriveTrain", "Gyro", gyro);
}

void DriveTrain::InitDefaultCommand() {
	SetDefaultCommand(new DriveWithJoystick());
}

void DriveTrain::TankDrive(Joystick* joy) {
	drive.TankDrive(joy->GetY(), joy->GetRawAxis(4));
}

void DriveTrain::TankDrive(double leftAxis, double rightAxis) {
	drive.TankDrive(leftAxis, rightAxis);
}

void DriveTrain::Stop() {
	drive.TankDrive(0.0, 0.0);
}

std::shared_ptr<Encoder> DriveTrain::GetLeftEncoder() {
	return leftEncoder;
}

std::shared_ptr<Encoder> DriveTrain::GetRightEncoder() {
	return rightEncoder;
}

double DriveTrain::GetAngle() {
	return gyro->GetAngle();
}

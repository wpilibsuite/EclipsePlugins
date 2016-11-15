#include "WPILib.h"

/*
 * All WPILib classes are in the FRC namespace. Either `using namespace frc` 
 * or frc scope (ex. `frc::RobotDrive`) are required to use WPILib functionality. 
 * For headers, use scope instead of `using namespace frc` to avoid global namespace
 * pollution. For source files, either option is viable.
 * See 
 */
using namespace frc;

/**
 * Uses AxisCamera class to manually acquire a new image each frame, and annotate the image by drawing
 * a circle on it, and show it on the FRC Dashboard.
 */
class AxisCameraSample : public SampleRobot
{
	IMAQdxSession session;
	Image *frame;
	IMAQdxError imaqError;
	std::unique_ptr<AxisCamera> camera;

public:
	void RobotInit() override {
	    // create an image
		frame = imaqCreateImage(IMAQ_IMAGE_RGB, 0);

		// open the camera at the IP address assigned. This is the IP address that the camera
		// can be accessed through the web interface.
		camera.reset(new AxisCamera("axis-camera.local"));
	}

	void OperatorControl() override {
        // grab an image, draw the circle, and provide it for the camera server which will
        // in turn send it to the dashboard.
		while(IsOperatorControl() && IsEnabled()) {
			camera->GetImage(frame);
			imaqDrawShapeOnImage(frame, frame, { 10, 10, 100, 100 }, DrawMode::IMAQ_DRAW_VALUE, ShapeMode::IMAQ_SHAPE_OVAL, 0.0f);
			CameraServer::GetInstance()->SetImage(frame);
			Wait(0.05);
		}
	}
};

START_ROBOT_CLASS(AxisCameraSample)


#include <string>

#include <SampleRobot.h>
#include <Timer.h>
#include <Vison/VisionAPI.h>

/**
 * Uses IMAQdx to manually acquire a new image each frame, and annotate the
 * image by drawing a circle on it, and show it on the FRC Dashboard.
 */
class IntermediateVisionRobot : public SampleRobot {
public:
	void RobotInit() override {
		// Create an image
		frame = imaqCreateImage(IMAQ_IMAGE_RGB, 0);

		/* the camera name (ex "cam0") can be found through the roborio web
		 * interface
		 */
		imaqError = IMAQdxOpenCamera("cam0", IMAQdxCameraControlModeController,
		                             &session);
		if (imaqError != IMAQdxErrorSuccess) {
			DriverStation::ReportError("IMAQdxOpenCamera error: " +
			                           std::to_string(static_cast<long>(imaqError)) + "\n");
		}

		imaqError = IMAQdxConfigureGrab(session);
		if (imaqError != IMAQdxErrorSuccess) {
			DriverStation::ReportError("IMAQdxConfigureGrab error: " +
			                           std::to_string(static_cast<long>(imaqError)) + "\n");
		}
	}

	void OperatorControl() override {
		// Acquire images
		IMAQdxStartAcquisition(session);

		/* Grab an image, draw the circle, and provide it for the camera server
		 * which will in turn send it to the dashboard.
		 */
		while (IsOperatorControl() && IsEnabled()) {
			IMAQdxGrab(session, frame, true, nullptr);
			if (imaqError != IMAQdxErrorSuccess) {
				DriverStation::ReportError("IMAQdxGrab error: " +
				                           std::to_string(static_cast<long>(imaqError)) +
				                           "\n");
			}
			else {
				imaqDrawShapeOnImage(frame, frame, {10, 10, 100, 100},
				                     DrawMode::IMAQ_DRAW_VALUE,
				                     ShapeMode::IMAQ_SHAPE_OVAL, 0.0f);
				CameraServer::GetInstance()->SetImage(frame);
			}
			Wait(0.005);  // Wait for a motor update time
		}

		// stop image acquisition
		IMAQdxStopAcquisition(session);
	}

private:
	IMAQdxSession session;
	Image* frame;
	IMAQdxError imaqError;
};

START_ROBOT_CLASS(IntermediateVisionRobot)

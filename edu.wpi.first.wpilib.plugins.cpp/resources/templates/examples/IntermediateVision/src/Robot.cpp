#include <CameraServer.h>
#include <IterativeRobot.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/core/core.hpp>

/**
 * This is a demo program showing the use of OpenCV to do vision processing. The
 * image is acquired from the USB Webcam, then is converted to grayscale and
 * sent to the dashboard. OpenCV has many methods for different types of
 * processing.
 */
class Robot: public IterativeRobot {
private:
	static void VisionThread() {
		// Get the UsbCamera from CameraServer
		cs::UsbCamera camera =
				CameraServer::GetInstance()->StartAutomaticCapture();
		// Set the resolution
		camera.SetResolution(640, 480);

		// Get a CvSink. This will capture Mats from the UsbCamera
		cs::CvSink cvSink = CameraServer::GetInstance()->GetVideo();
		// Setup a CvSource. This will send images back to the Dashboard
		cs::CvSource outputStreamStd = CameraServer::GetInstance()->PutVideo(
				"Gray", 640, 480);

		// Mats are very memory expensive. Lets reuse these 2 Mats for all
		// of our operations.
		cv::Mat source;
		cv::Mat output;

		while (true) {
			// Tell the CvSink to grab a frame from the UsbCamera and put it
			// in the source mat
			cvSink.GrabFrame(source);
			// This line converts the source mat to grayscale and saves it
			// in the output mat
			cvtColor(source, output, cv::COLOR_BGR2GRAY);
			// Give the output stream a new image to display
			outputStreamStd.PutFrame(output);
		}
	}

	void RobotInit() {
		// We need to run our vision program in a separate Thread.
		// If not, our robot program will not run
		std::thread visionThread(VisionThread);
		visionThread.detach();
	}

};

START_ROBOT_CLASS(Robot)

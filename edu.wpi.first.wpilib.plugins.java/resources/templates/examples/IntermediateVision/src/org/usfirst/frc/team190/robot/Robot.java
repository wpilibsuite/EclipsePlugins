package $package;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * This is a demo program showing the use of OpenCV to do vision processing. The
 * image is acquired from the USB Webcam, then is converted to grayscale and
 * sent to the dashboard. OpenCV has many methods for different types of
 * processing.
 */
public class Robot extends IterativeRobot {

	public void robotInit() {
		new Thread(() -> {
			// Get the UsbCamera from CameraServer
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			// Set the resolution
			camera.setResolution(640, 480);

			// Get a CvSink. This will capture Mats from the UsbCamera
			CvSink cvSink = CameraServer.getInstance().getVideo();
			// Setup a CvSource. This will send images back to the Dashboard
			CvSource outputStream = CameraServer.getInstance().putVideo("Gray", 640, 480);

			// Mats are very memory expensive. Lets reuse these 2 Mats for all
			// of our operations.
			Mat source = new Mat();
			Mat output = new Mat();

			while (true) {
				// Tell the CvSink to grab a frame from the UsbCamera and put it
				// in the source mat
				cvSink.grabFrame(source);
				// This line converts the source mat to grayscale and saves it
				// in the output mat
				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
				// Give the output stream a new image to display
				outputStream.putFrame(output);
			}
		}).start();
	}

}

package xonxt.gesturedetector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.objdetect.CascadeClassifier;


public class Hello {

	public static void main(String[] args) {
		// ��������� OpenCV
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		// ������� ������
		NamedWindow window = new NamedWindow("video");
		window.setVisible(true);
		
		// ������� ���������� ������
		FrameProcessor frameProcessor = new FrameProcessor();
		
		if ( !frameProcessor.initialize() ) {
			System.out.println("Failed to initialize frame processor!");
			System.exit(-1);
		}
		
		// ��������� ����� � ������
		VideoCapture cap = new VideoCapture(0);	

		if (!cap.isOpened()) {
			System.out.println("Error opening webcam!");
			System.exit(-2);
		}
		else {
			Mat frame = new Mat();
			// ������ ���
			ArrayList<Hand> hands = new ArrayList<Hand>();
			
			while (true) {
				if (cap.read(frame)) {
					// ������������ ����
					frameProcessor.processFrame(frame);
					// �������� ��� ����
					hands = frameProcessor.getHands();
					// ���������� ���� �� �����					
					frame = Visualizer.drawHands(frame, hands);
					// ���������� ����� �����
					frame = Visualizer.drawTrackLines(frame, hands);
					// ���������� ����
					window.imshow(frame);
				}
			}
		}
		cap.release();

	}
	


}

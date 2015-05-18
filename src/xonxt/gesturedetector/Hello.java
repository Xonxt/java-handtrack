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
		// загружаем OpenCV
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		// создаем окошко
		NamedWindow window = new NamedWindow("video");
		window.setVisible(true);
		
		// создаем обработчик кадров
		FrameProcessor frameProcessor = new FrameProcessor();
		
		if ( !frameProcessor.initialize() ) {
			System.out.println("Failed to initialize frame processor!");
			System.exit(-1);
		}
		
		// открываем видео с камеры
		VideoCapture cap = new VideoCapture(0);	

		if (!cap.isOpened()) {
			System.out.println("Error opening webcam!");
			System.exit(-2);
		}
		else {
			Mat frame = new Mat();
			// массив рук
			ArrayList<Hand> hands = new ArrayList<Hand>();
			
			while (true) {
				if (cap.read(frame)) {
					// обрабатываем кадр
					frameProcessor.processFrame(frame);
					// забираем все руки
					hands = frameProcessor.getHands();
					// отображаем руки на кадре					
					frame = Visualizer.drawHands(frame, hands);
					// отображает линии трека
					frame = Visualizer.drawTrackLines(frame, hands);
					// показываем кадр
					window.imshow(frame);
				}
			}
		}
		cap.release();

	}
	


}

package xonxt.gesturedetector;

import java.util.ArrayList;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

/**
 * отвечает за детектирование новых рук на изображении
 * @author Xonxt
 *
 */
public class HandDetector {
	private CascadeClassifier handDetector;
	
	public HandDetector() {
			
	}
	
	public Boolean initialize() {
		handDetector = new CascadeClassifier("hogcascade_righthand_v3.0.xml");
		if (handDetector.empty()) {
			System.out.println("Error initializing Cascade Classifier!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * детектировать все новые руки на изображении
	 * @param image исходное изображение
	 * @return
	 */
	public ArrayList<Hand> detectHands(Mat image) {
		MatOfRect hands = new MatOfRect();
		
		handDetector.detectMultiScale(image, hands);
		Rect[] rects = hands.toArray();
		
		ArrayList<Hand> handsArray = new ArrayList<Hand>();
		
		for(Rect rect : rects) {
			handsArray.add(new Hand(rect));
		}
		
		return handsArray;
	}
}

package xonxt.gesturedetector;

import java.util.ArrayList;
import java.util.Arrays;

import org.omg.PortableServer.ImplicitActivationPolicy;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Visualizer {
	
	/**
	 * отображает для каждой руки в кадре ограничивающую рамку
	 * @param image Исходное изображение
	 * @param hands Массив рук
	 * @return
	 */
	public static Mat drawHands(Mat image, ArrayList<Hand> hands) {
		Mat changedImage = image.clone();
		
		for(Hand hand : hands) {
			Rect rect = hand.getBoundingBox();
			Core.rectangle(changedImage, rect.tl(), rect.br(), new Scalar(255, 255, 255), 3);
		}
		
		return changedImage;
	}
	
	/**
	 * отображает для каждой руки линию трека
	 * @param image Изображение
	 * @param hands Массив рук
	 * @return
	 */
	public static Mat drawTrackLines(Mat image, ArrayList<Hand> hands) {
		Mat changedImage = image.clone();
		
		for(Hand hand : hands) {
			ArrayList<Point> trackLine = hand.getTrackLine();
			
			if (trackLine.size() > 1) {
				for (int i = 0; i < trackLine.size()-1; i++) {
					Core.line(changedImage, trackLine.get(i), trackLine.get(i+1), new Scalar(255,0,255), 3);
				}
			}
		}
		
		return changedImage;
	}
	
	/**
	 * Рисует контур вокруг руки
	 * @param image изображение
	 * @param hands руки
	 * @return
	 */
	public static Mat drawContours(Mat image, ArrayList<Hand> hands) {
		Mat changedImage = image.clone();
		
		for(Hand hand : hands) {
			
			MatOfPoint contour = hand.getContour();
			
			if (!contour.empty()) {
				Imgproc.drawContours(changedImage, Arrays.asList(contour), 0, new Scalar(0, 255, 0), 2);
			}
		}
		
		return changedImage;
	}
	
	/**
	 * отображает кончики пальцев как небольшие точки
	 * @param image изображение
	 * @param hands руки
	 * @return
	 */
	public static Mat drawFingertips(Mat image, ArrayList<Hand> hands) {
		Mat changedImage = image.clone();
		
		for(Hand hand : hands) {
			
			ArrayList<Point> fingers = hand.getFingers();
			
			if (fingers.size() > 0) {
				for (Point pt : fingers) {
					Core.circle(changedImage, pt, 5, new Scalar(255, 255, 255), -1);
					Core.circle(changedImage, pt, 9, new Scalar(255, 255, 255), 3);
				}
				
			}
		}
		
		return changedImage;
	}

}

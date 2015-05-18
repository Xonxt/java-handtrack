package xonxt.gesturedetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.video.Video;
import org.opencv.core.Core;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.*;

/**
 *  ласс, содержащий все необходимое дл€ трекинга рук на видео
 * @author Xonxt
 *
 */
public class HandTracker {
	
	private Mat hsvImage;
	private Mat bwMask;
	private Mat backProjection;
	
	/**
	 * ѕроизводит трекинг каждой руки на изображении	
	 * @param inputImage Ќовое изображение, на котором нужно найти новое положение рук
	 * @param oldHands —писок ранее найденных рук
	 * @return ¬озвращает новый список рук
	 */
	public ArrayList<Hand> trackHands(Mat inputImage, ArrayList<Hand> oldHands) {
		Mat image = inputImage.clone();
		
		hsvImage = inputImage.clone();
		Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
		
		backProjection = Mat.zeros(hsvImage.size(), CvType.CV_8U);
		
		bwMask = new Mat(image.size(), CvType.CV_8U);
		bwMask = Mat.zeros(image.size(), CvType.CV_8U);
				
		// диапазон цвета кожи в YCbCr
		//.....√раница............Y.....Cr.....Cb....///////////
		Scalar lower = new Scalar(0,    133,   77);  // нижн€€
		Scalar upper = new Scalar(255,  173,   127); // верхн€€		
		
		Mat ycrcbImage = image.clone();
		Imgproc.cvtColor(image, ycrcbImage, Imgproc.COLOR_BGR2YCrCb);	
		 
		Core.inRange(ycrcbImage, lower, upper, bwMask);
		
		removeSmallBlobs(bwMask, 100);
		
		Highgui.imwrite("mask.jpg", bwMask);
		
		ArrayList<Hand> newHands = new ArrayList<Hand>();
		
		for (int i = 0; i < oldHands.size(); i++) {
			Hand hand = findNewPosition(oldHands.get(i));
			if (hand != null) {
				newHands.add(hand);				
			}
		}
		
		return newHands;
	}
	
	/**
	 * Ќаходим новое положение на кадре дл€ конкретной руки
	 * @param oldHand –ука с предыдущего кадра
	 * @return Ќова€ рука с новым положением
	 */
	private Hand findNewPosition(Hand oldHand) {
		Hand newHand = oldHand.clone();
		
		Rect selection = newHand.getBoundingBox();
		Rect trackWindow = newHand.getTrackWindow();
		
		MatOfInt histSize = new MatOfInt(30, 32);		
		MatOfFloat ranges = new MatOfFloat(0, 179, 0, 255);		
		MatOfInt channels = new MatOfInt(0, 1);
		
		// если эта рука еще не отслеживалась
		if (!newHand.getTrackedState()) {
			Mat roi = new Mat(hsvImage, selection);
			Mat maskroi = new Mat(bwMask, selection);
					
			Mat hist = new Mat();
			// вычислить гистограмму дл€ этой руки
			Imgproc.calcHist(Arrays.asList(roi), channels, maskroi, hist, histSize, ranges, false);
			// нормализовать гистограммы
			Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX, -1, new Mat());
			newHand.setHist(hist);
			
			newHand.setTrackedState(true);
		}
		
		Mat backproj = new Mat();
		backproj = Mat.zeros(hsvImage.size(), CvType.CV_8U);
		Mat hist = newHand.getHist();
		// вычисл€ем Back Projection дл€ руки
		Imgproc.calcBackProject(Arrays.asList(hsvImage), channels, hist, backproj, ranges, 1);
		// примен€ем маску
		Core.bitwise_and(backproj, bwMask, backproj);
		// используем алгоритм CamShift дл€ нахождени€ нового положени€ руки
		RotatedRect trackBox = Video.CamShift(backproj, trackWindow, 
				new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 10, 1));
		
		if (trackBox.size.width == 0 || trackBox.size.height == 0) {
			return null;
		}
		
		newHand.setNewPosition(trackBox);
		
		Core.bitwise_or(backProjection, backproj, backProjection);
		
		RotatedRect tempWnd = trackBox.clone();
		tempWnd.size.height += 10;
		tempWnd.size.width  += 10;
		
		newHand.setTrackWindow(tempWnd.boundingRect());
		
		// добавл€ем новую точку к линии трека
		newHand.addTrackPoint(trackBox.center);
		
		return newHand;
	}
	
	/**
	 * ќчищает на изображении все "блобы", размер которых не превышает
	 * заданного значени€
	 * @param image ¬ходное изображение
	 * @param minSize ћинимальный размер "блоба"
	 * @return
	 */
	private Mat removeSmallBlobs(Mat image, int minSize) {
		Mat newImage = image.clone();
		
		if (image.channels() != 1 || image.type() != CvType.CV_8U) {
			System.out.println("wrong image type!");
			return image;
		}
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(newImage, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		
		for (int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			
			if (area >= 0) {
				if (area <= minSize) {
					Imgproc.drawContours(newImage, contours, i, new Scalar(0,0,0), -1);
				}
				else {
					Imgproc.drawContours(newImage, contours, i, new Scalar(255,255,255), -1);
				}
			}			
		}
		
		return newImage;
	}

}

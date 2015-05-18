package xonxt.gesturedetector;

import java.util.ArrayList;

import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

public class FrameProcessor {
	
	private ArrayList<Hand> hands;
	private HandDetector handDetector;
	private HandTracker handTracker;
	private CascadeClassifier faceDetector;
	
	private Mat image;
	
	public FrameProcessor() {
		hands = new ArrayList<Hand>();
	}	
	
	public boolean initialize() {
		handDetector = new HandDetector();
		if ( !handDetector.initialize() ) {
			return false;
		}
		
		handTracker = new HandTracker();
				
		faceDetector = new CascadeClassifier("lbpcascade_frontalface.xml");
		if (faceDetector.empty()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * ������������ ����, ������� �� ��� ����� ���� � ���������� ������� ��� ��������� ���
	 * @param frame ������� ����
	 */
	public void processFrame(Mat frame) {
		
		image = frame.clone();
		
		// ���������� ������� ��������� ���
		hands = handTracker.trackHands(frame, hands);
		
		// ����� � ����� ����� ����
		ArrayList<Hand> newHands = handDetector.detectHands(frame);
		
		// ��� ������ ��������� ���� ���������, ��� �� � ��� � ������ ���������
		int i = 0;
		for (i = 0; i < newHands.size(); i++) {
			boolean handAlreadyFound = false;
			for (int j = 0; j < hands.size(); j++) {
				Rect oldHand = hands.get(j).getBoundingBox();
				Rect newHand = newHands.get(i).getBoundingBox();
				
				if (overlapRatio(oldHand, oldHand, 0.75)) {
					// ��� ���� ��� ���� �������, ����� �� �����
					handAlreadyFound = true;
					break;
				}							
			}

			if (!handAlreadyFound) {
				// ��� ����� ����, ������� ��� �� ���� � ������
				hands.add(newHands.get(i));
			}
		}
		
		// ������ ��� ������ ������������ � ������ ����
		removeBadHands();
	}
	
	public ArrayList<Hand> getHands() {
		return hands;
	}
	
	/**
	 * ������� �� ������ ��� ������ ���� � ������ ������������
	 * ��� ������� ������ �������� ����� �� ����, ������� ���� ��������
	 * ������� ��� ���� � �������� ���� � �� �� ����� �� �����������,
	 * � ����� �� ������, ����� ���� �������� ���� �������� �������
	 * �� ����, ��-�� �������� �� �� �����
	 */
	private void removeBadHands() {
		
		// ������ ��, ��� ������������
		int N = hands.size();
		
		for (int i = 0; i < N; i++) {
			int M = N;
			for (int j = 0; j < M; j++) {
				if (i == j)
					continue;
				
				Rect oneHand = hands.get(i).getBoundingBox();
				Rect secHand = hands.get(j).getBoundingBox();
				
				if (overlapRatio(oneHand, secHand, 0.85)) {
					hands.remove(i);
					N--;
					break;
				}
			}
		}
		
		// ������ ��, ��� �� ������ �������� �� ���-������
		for (int i = 0; i < hands.size(); i++) {
			if (hands.get(i).getBoundingBox().area() >= (image.width() * image.height() * 0.5)) {
				hands.remove(i--);
			}
		}
		
		// ������ ����, �� ������ �������� �� ����
		MatOfRect faces = new MatOfRect();
		faceDetector.detectMultiScale(image, faces);
		Rect[] faceRect = faces.toArray();
		
		for (int i = 0; i < hands.size(); i++) {
			for (int j = 0; j < faceRect.length; j++) {
				Rect handRect = hands.get(i).getBoundingBox();
				
				if (overlapRatio(handRect, faceRect[j], 0.5)) {
					hands.remove(i--);
					break;
				}
			}
		}
	}
	
	/**
	 * ���������, ��������� ��� �������������� ������������, ��� ��� ���� �������������
	 * ���������� ����� ratio ��������� �� �������
	 * @param rect1 ������ �������������
	 * @param rect2 ������ �������������
	 * @param ratio ���������� �����������, ������ ��� ����� ���� � ��������� 0..1
	 * (�������� 0.75 ��� 75%)
	 * @return ���������� true, ���� ���� ������� ����������� ���������� ��������� ���� �� �������
	 * �������������� ��� ������
	 */
	private boolean overlapRatio(Rect rect1, Rect rect2, double ratio) {
		return (intersectArea(rect1, rect2) >= (rect1.area() * ratio));
	}
	
	private double intersectArea(Rect rect1, Rect rect2) {
		double area = 0;		
		
		double x_overlap = Math.max(0, Math.min(rect1.br().x,rect2.br().x) - Math.max(rect1.tl().x, rect2.tl().x));
		double y_overlap = Math.max(0, Math.min(rect1.br().y,rect2.br().y) - Math.max(rect1.tl().y,rect2.tl().y));
		area = x_overlap * y_overlap;
		
		return area;
	}
}

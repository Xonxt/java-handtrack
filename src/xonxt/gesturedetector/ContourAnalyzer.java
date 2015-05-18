package xonxt.gesturedetector;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

public class ContourAnalyzer {

	private int mR;
	private int mStep;
	private double mCosThreshold;
	private double mEqualThreshold;
	
	public ContourAnalyzer() {
		mCosThreshold = 0.5;
		mEqualThreshold = 1e-7;
		mR = 40; // 40;
		mStep = 2; // 16;		
	}
	
	/**
	 * Проводит контурный анализ для руки и извлекает кончики пальцев 
	 * @param hand Текущая рука
	 * @return Возвращает количество найденных пальчиков 
	 */
	public int extractFingers(Hand hand) {
		int countFingers = 0;
		
		List<Point> contour = hand.getContour().toList();
		
		if (contour.size() <= 0)
			return -1;

		for (int j = 0; j < contour.size(); j += mStep) {			
			double cos0 = getPointsAangle(contour, j, mR);
	
			if ((cos0 > 0.5) && (j + mStep < contour.size())) {
				double cos1 = getPointsAangle(contour, j - mStep, mR);
				double cos2 = getPointsAangle(contour, j + mStep, mR);
				double maxCos = Math.max(Math.max(cos0, cos1), cos2);
				boolean equal = isEqual(maxCos, cos0);
				int z = getRotation(contour, j, mR);
				
				ArrayList<Point> fingers = new ArrayList<Point>();
	
				if (equal && z < 0) {
					// < init finger >
					fingers.add(contour.get(j));
	
					if (++countFingers >= 5)
						break;
				}
				
				hand.setFingers(fingers);
			}
		}
		return countFingers;
	}
	
	// Determine if two floating point values are ~equal, with a threshold
	private boolean isEqual(double a, double b) {
		return Math.abs(a - b) <= mEqualThreshold;
	}

	// get the angle between two points in a contour
	private double getPointsAangle(List<Point> contour, int pt, int r) {
		
		int size = (int)contour.size();
		Point p0 = (pt > 0) ? contour.get(pt % size) : contour.get(size - 1 + pt);
		Point p1 = contour.get((pt + r) % size);
		Point p2 = (pt > r) ? contour.get(pt - r) : contour.get(size - 1 - r);

		double ux = p0.x - p1.x;
		double uy = p0.y - p1.y;
		double vx = p0.x - p2.x;
		double vy = p0.y - p2.y;
		
		return (ux * vx + uy * vy) / Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
	}

	// get the rotation between two points of the contour	
	private int getRotation(List<Point> contour, int pt, int r) {
		int size = (int)contour.size();
		Point p0 = (pt > 0) ? contour.get(pt % size) : contour.get(size - 1 + pt);
		Point p1 = contour.get((pt + r) % size);
		Point p2 = (pt > r) ? contour.get(pt - r) : contour.get(size - 1 - r);

		double ux = p0.x - p1.x;
		double uy = p0.y - p1.y;
		double vx = p0.x - p2.x;
		double vy = p0.y - p2.y;

		int res = (int) (ux * vy - vx * uy);

		return res;
	}
	
}

package xonxt.gesturedetector;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Point;

/**
 * Класс, содержащий в себе всю информацию о руке
 * @author Xonxt
 *
 */
public class Hand {
	
	private RotatedRect trackedBox;
	private Rect detectedBox;
	private ArrayList<Point> trackLine;
	private boolean isTracked;
	private Rect trackWindow;
	private Mat hist;
	
	public Point getCenter() {
		return trackedBox.center;
	}
	
	public Rect getBoundingBox() {
		return trackedBox.boundingRect();
	}
	
	public ArrayList<Point> getTrackLine() {
		return trackLine;
	}
	
	public void addTrackPoint(Point pt) {
		trackLine.add(pt);
	}
	
	public boolean getTrackedState() {
		return isTracked;
	}
	
	public void setTrackedState(boolean value) {
		isTracked = value;
	}
	
	public Rect getTrackWindow() {
		return trackWindow;
	}
	
	public void setTrackWindow(Rect newTrackwindow) {
		trackWindow = newTrackwindow;
	}
	
	public Mat getHist() {
		return hist;
	}
	
	public void setHist(Mat _hist) {
		hist = _hist;
	}
	
	public Hand() {
		// TODO Auto-generated constructor stub
		isTracked = false;
	}
	
	public Hand(Hand obj) {		
		detectedBox = obj.getBoundingBox();
		trackedBox = new RotatedRect(new Point(detectedBox.x + detectedBox.width / 2, detectedBox.y + detectedBox.height / 2), 
				detectedBox.size(), 0.0);	
		trackWindow = obj.getTrackWindow();
		
		isTracked = obj.getTrackedState();
		
		trackLine = new ArrayList<Point>();
		trackLine.clear();
		for(Point pt : obj.getTrackLine()) {
			trackLine.add(pt);
		}		
		
		hist = obj.getHist();
	}
	
	public Hand clone() {
		return new Hand(this);
	}
		
	public Hand(Rect newBox) {
		detectedBox = newBox;
		trackedBox = new RotatedRect(new Point(newBox.x + newBox.width / 2, newBox.y + newBox.height / 2), 
				newBox.size(), 0.0);	
		
		isTracked = false;
		
		trackLine = new ArrayList<Point>();
		
		trackWindow = detectedBox.clone();
	}
	
	public void setNewPosition(RotatedRect newBox) {
		trackedBox = newBox;
	}
}

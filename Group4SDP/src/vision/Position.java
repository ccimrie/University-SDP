package vision;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Represents the centre point of an object, for example the ball or a robot.
 * 
 * @author s0840449
 * @author Alex Adams (s1046358) - made some optimizatons, tidied a few comments
 */
public class Position {
	private int x;
	private int y;

	/**
	 * Default constructor.
	 * 
	 * @param x
	 *            The x-coordinate of the object.
	 * @param y
	 *            The y-coordinate of the object.
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Return the x-coordinate.
	 * 
	 * @return The x-coordinate of the object.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Set the x-coordinate.
	 * 
	 * @param x
	 *            The value to set as the x-coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Return the y-coordinate.
	 * 
	 * @return The y-coordinate of the object.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set the y-coordinate.
	 * 
	 * @param y
	 *            The value to set as the y-coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Compares the current x and y co-ordinates to another set of co-ordinates
	 * (usually the previous co-ordinates for the position), fixing the current
	 * co-ordinates based on the previous ones.
	 * 
	 * @param oldX
	 *            The old x-coordinate.
	 * @param oldY
	 *            The old y-coordinate.
	 */
	public void fixValues(int oldX, int oldY) {
		// Use old values if nothing found
		if (this.getX() == 0) {
			this.setX(oldX);
		}
		if (this.getY() == 0) {
			this.setY(oldY);
		}

		// Use old values if not changed much
		if (sqrdEuclidDist(this.getX(), this.getY(), oldX, oldY) < 9) {
			this.setX(oldX);
			this.setY(oldY);
		}
	}

	/**
	 * Updates the centre point of the object, given a list of new points to
	 * compare it to. Any points too far away from the current centre are
	 * removed, then a new mean point is calculated and set as the centre point.
	 * 
	 * @param xs
	 *            The new set of x points.
	 * @param ys
	 *            The new set of y points.
	 */
	public void filterPoints(ArrayList<Integer> xs, ArrayList<Integer> ys) {
		if (xs.size() > 0) {
			int stdev = 0;

			// Standard deviation
			for (int i = 0; i < xs.size(); i++) {
				int x = xs.get(i);
				int y = ys.get(i);

				stdev += sqrdEuclidDist(x, y, this.getX(), this.getY());
			}
			stdev = (int) Math.sqrt((double) stdev / xs.size());

			int count = 0;
			int newX = 0;
			int newY = 0;

			// Remove points further than standard deviation
			for (int i = 0; i < xs.size(); i++) {
				int x = xs.get(i);
				int y = ys.get(i);
				if (Math.abs(x - this.getX()) < stdev
						&& Math.abs(y - this.getY()) < stdev) {
					newX += x;
					newY += y;
					count++;
				}
			}

			int oldX = this.getX();
			int oldY = this.getY();

			if (count > 0) {
				this.setX(newX / count);
				this.setY(newY / count);
			}

			this.fixValues(oldX, oldY);
		}
	}

	/**
	 * TODO: find out why 1.17 was chosen by the previous year. Removes points
	 * which are more than 1.17 standard deviations from the centroid and
	 * returns the rest
	 * 
	 * @param xs
	 *            The x coordinates of the points
	 * @param ys
	 *            The y coordinates of the points
	 * @param centroid
	 *            The centroid of the points
	 * @return An ArrayList of points which are within 1.17 standard deviations
	 *         of the centroid
	 */
	public static ArrayList<Point> removeOutliers(ArrayList<Integer> xs,
			ArrayList<Integer> ys, Point centroid) {
		ArrayList<Point> goodPoints = new ArrayList<Point>();
		if (xs.size() > 0) {
			double stdDev = standardDeviation(xs, ys, centroid);
			// Remove points further than 1.17 standard deviations
			stdDev *= 1.17;
			for (int i = 0; i < xs.size(); i++) {
				int x = xs.get(i);
				int y = ys.get(i);
				if (Math.abs(x - centroid.getX()) < stdDev
						&& Math.abs(y - centroid.getY()) < stdDev) {
					goodPoints.add(new Point(x, y));
				}
			}
		}

		return goodPoints;
	}

	/**
	 * Calculates the standard deviation for a set of points in two dimensions
	 * given the centroid
	 * 
	 * @param xs
	 *            The x coordinates of the points
	 * @param ys
	 *            The y coordinates of the points
	 * @param centroid
	 *            The centroid of the points
	 * @return The two-dimensional standard deviation of the points
	 */
	public static double standardDeviation(ArrayList<Integer> xs,
			ArrayList<Integer> ys, Point centroid) {
		double variance = 0.0;

		// Standard deviation
		for (int i = 0; i < xs.size(); i++) {
			int x = xs.get(i);
			int y = ys.get(i);

			variance += sqrdEuclidDist(x, y, (int) centroid.getX(),
					(int) centroid.getY());
		}

		return Math.sqrt(variance / (double) (xs.size()));
	}

	/**
	 * Calculates the squared euclidean distance between two 2D points.
	 * 
	 * @param x1
	 *            The x-coordinate of the first point.
	 * @param y1
	 *            The y-coordinate of the first point.
	 * @param x2
	 *            The x-coordinate of the second point.
	 * @param y2
	 *            The y-coordinate of the second point.
	 * 
	 * @return The squared euclidean distance between the two points.
	 */
	public static int sqrdEuclidDist(int x1, int y1, int x2, int y2) {
		int xdiff = x1 - x2, ydiff = y1 - y2;
		return xdiff * xdiff + ydiff * ydiff;
	}
}

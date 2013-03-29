package movement;

import java.awt.geom.Point2D;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class to allow RobotMover to move to a point which is itself moving
 * 
 * @author Alex Adams (s1046358)
 */
public final class MovingPoint {
	/** The current x-coordinate of the point */
	private double x;
	/** The current y-coordinate of the point */
	private double y;
	/**
	 * A lock to ensure RobotMover only has the very latest copy of the
	 * coordinates
	 */
	private final ReentrantLock lock = new ReentrantLock(true);

	/**
	 * Creates a point with coordinates (0,0)
	 */
	public MovingPoint() {
		this.x = 0.0;
		this.y = 0.0;
	}

	/**
	 * Creates a point with the specified coordinates
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 */
	public MovingPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Updates the coordinates of the moving point
	 * 
	 * @param x
	 *            The new x coordinate
	 * @param y
	 *            The new y coordinate
	 */
	public void set(double x, double y) {
		try {
			lock.lockInterruptibly();
			this.x = x;
			this.y = y;
		} catch (InterruptedException ignore) {
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	/**
	 * Retrieves the current coordinates of the moving point
	 * 
	 * @return A Point2D object representing the current coordinates of the
	 *         moving point
	 */
	public Point2D get() {
		Point2D.Double result = null;
		try {
			lock.lockInterruptibly();
			result = new Point2D.Double(x, y);
		} catch (InterruptedException ignore) {
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		return result;
	}
}

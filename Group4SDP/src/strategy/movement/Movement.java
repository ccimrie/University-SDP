package strategy.movement;

import strategy.calculations.DistanceCalculator;
import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

/**
 * A movement class, that provides calculations for different move commands for
 * the robot.
 * 
 * @author Jakov Smelkin
 */
public class Movement extends Thread {

	// private WorldState worldState;
	private RobotController robot;
	private Robot us;
	private static int DIST_TH = 10;
	private boolean die = false;
	private double movetopointx = 0;
	private double movetopointy = 0;
	private double speedx = 0;
	private double speedy = 0;
	private double angle = 0.0;
	private int methodtouse = 0;

	/**
	 * Constructor for the movement class
	 * 
	 * @param worldState
	 *            a world state from the vision, giving us information on
	 *            robots, ball etc.
	 * @param robot
	 *            A {@link world.state.RobotController} class that prepares byte
	 *            commands to the robot.
	 * @param movetoPointX
	 *            Move to position X units down from top left corner of the
	 *            video feed
	 * @param movetoPointY
	 *            Move to position Y units right from top left corner of the
	 *            video feed
	 * @param speedX
	 *            Speed right (for positive values) or left (for negative ones).
	 * @param speedY
	 *            Speed forward (for positive values) or backward (for negative
	 *            ones).
	 * @param angle
	 *            Angle, in radians (0 to 2*PI)
	 * @param methodToUse
	 *            A method to call: </br>1 -
	 *            {@link #move(double speedX, double speedY)}, </br> 2 -
	 *            {@link #move(double angle)},</br> 3 -
	 *            {@link #moveToPoint(double movetoPointX, double movetoPointY)}
	 *            , </br>4 -
	 *            {@link #moveToPointAndStop(double movetoPointX, double movetoPointY)}
	 *            , </br>5 -
	 *            {@link #moveTowardsPoint (double movetoPointX, double movetoPointY)}
	 *            , </br>6 - {@link #rotate (double angle)}
	 */
	public Movement(WorldState worldState, RobotController robot,
			double movetopointx, double movetopointy, double speedx,
			double speedy, double angle, int methodtouse) {
		super();
		// this.worldState = worldState;
		this.robot = robot;
		us = worldState.ourRobot;
		this.movetopointx = movetopointx;
		this.movetopointy = movetopointy;
		this.speedx = speedx;
		this.speedy = speedy;
		this.angle = angle;
		this.methodtouse = methodtouse;

	}

	public void run() {
		try {
			switch (methodtouse) {
			case 1:
				System.out
						.println("Moving at (" + speedx + ", " + speedy + ")");
				move(speedx, speedy);
				break;
			case 2:
				System.out.println("Moving at " + angle + " degrees");
				move(angle);
				break;
			case 3:
				System.out.println("Moving to (" + movetopointx + ", "
						+ movetopointy + ")");
				moveToPoint(movetopointx, movetopointy);
				break;
			case 4:
				System.out.println("Moving to (" + movetopointx + ", "
						+ movetopointy + ") and stopping");
				moveToPointAndStop(movetopointx, movetopointy);
				break;
			case 5:
				System.out.println("Moving towards (" + movetopointx + ", "
						+ movetopointy + ")");
				moveTowardsPoint(movetopointx, movetopointy);
				break;
			case 6:
				System.out.println("Rotating by " + angle + " degrees");
				rotate(angle);
				break;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void die() {
		System.out.println("Killing movement");
		die = true;
		robot.stop();
	}

	/**
	 * A general move function as seen from the position of the robot.</br>
	 * Speeds take values between -100 and 100.</br>
	 * 
	 * @param speedX
	 *            Speed right (for positive values) or left (for negative ones).
	 * @param speedY
	 *            Speed forward (for positive values) or backward (for negative
	 *            ones).
	 */
	public void move(double speedX, double speedY) {
		robot.move((int) speedX, (int) speedY);
	}

	/**
	 * A general move function where you specify a clockwise angle from the
	 * front of the robot to move at.
	 * 
	 * @param angle
	 *            Angle, in radians (0 to 2*PI)
	 */
	public void move(double angle) {
		double speedX = 100 * Math.sin(angle);
		double speedY = 100 * Math.cos(angle);
		move(speedX, speedY);
	}

	/**
	 * Moves to a point on a video stream (within a certain margin). Does not
	 * stop when reaches the point.
	 * 
	 * @param x
	 *            Move to position x units down from top left corner of the
	 *            video feed
	 * @param y
	 *            Move to position y units right from top left corner of the
	 *            video feed
	 * @throws InterruptedException
	 *             when Thread.sleep() is interrupted.
	 * @see #moveToPointAndStop(double x, double y)
	 * @see #moveTowardsPoint(double x, double y)
	 */
	public void moveToPoint(double x, double y) throws InterruptedException {

		int i = 0;
		while (DistanceCalculator.Distance(us.x, us.y, x, y) > DIST_TH
				&& i < 50 && !die) {
			// Not to send unnecessary commands
			Thread.sleep(42);
			moveTowardsPoint(x, y);
			// If we can't get to the point for some reason, it should cancel
			// after some iterations
			i++;

		}
	}

	/**
	 * Moves to a point on a video stream (within a certain margin) and stops.
	 * 
	 * @param x
	 *            Move to position x units down from top left corner of the
	 *            video feed
	 * @param y
	 *            Move to position y units left from top left corner of the
	 *            video feed
	 * @throws InterruptedException
	 *             when Thread.sleep() is interrupted.
	 * @see #moveToPoint(double, double)
	 */
	public void moveToPointAndStop(double x, double y)
			throws InterruptedException {
		moveToPoint(x, y);
		// Stop once we reach the point
		robot.stop();
	}

	/**
	 * Starts moving to the direction of the point and return immediately.
	 * 
	 * @param x
	 *            Move to position x units down from top left corner of the
	 *            video feed
	 * @param y
	 *            Move to position y units left from top left corner of the
	 *            video feed
	 * @see #moveToPoint(double, double)
	 */
	public void moveTowardsPoint(double x, double y) {
		/*
		 * We make a vector (xt, yt) pointing from the robot to point, then use
		 * rotational transformation to put it in robots perspective, then
		 * normalise the speeds to a scale of 0-100.
		 */
		double theta;
		double xt, yt, xtc, ytc;
		// Vector from robot to point in the camera axis
		xtc = x - us.x;
		ytc = y - us.y;
		// Clockwise angle of the robot from the north
		theta = us.bearing;

		// Unit vector in camera axis in the direction of the robot
		xt = Math.sin(theta);
		yt = -Math.cos(theta);

		// Dot product of the two vectors
		double dotProductForward = xt * xtc + yt * ytc;

		// Turned dot product
		xt = Math.sin(theta + Math.PI / 2.0);
		yt = -Math.cos(theta + Math.PI / 2.0);
		double dotProductEast = xt * xtc + yt * ytc;

		// Finding the angle from dot product

		double angle = Math.acos(dotProductForward
				/ (Math.sqrt(xtc * xtc + ytc * ytc) * Math.sqrt(xt * xt + yt
						* yt)));
		// Adjusting for negative values
		if (dotProductEast < 0)
			angle = -angle;

		// Calling the generic move function
		move(angle);
	}

	/**
	 * Move to a point (x,y) while avoiding point (avoidX, avoidY). Should go in
	 * an arc by default.
	 * 
	 * @param x
	 *            Point in the X axis to move to
	 * @param y
	 *            Point in the Y axis to move to
	 * @param avoidX
	 *            Point in the X axis avoid
	 * @param avoidY
	 *            Point in the X axis avoid
	 */
	// TODO: Finish this.
	public void moveToPointAndAvoid(double x, double y, double avoidX,
			double avoidY) {
		if (avoidX <= Math.max(x, us.x)) {
		}
	}

	/**
	 * Calls robot controller to rotate the robot by an angle
	 * 
	 * @param rotationAngle
	 *            clockwise angle to rotate (in Radians)
	 */
	public void rotate(double rotationAngle) {
		rotationAngle = Math.toDegrees(rotationAngle);
		robot.rotate((int) rotationAngle);
	}

}

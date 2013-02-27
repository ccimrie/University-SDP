package strategy.planning;

import strategy.calculations.DistanceCalculator;
import vision.WorldState;
import world.state.PossessionType;
import world.state.Robot;
import world.state.RobotController;

public class Everything {

	private WorldState worldState;
	private RobotController robot;
	private Robot us;
	private static int DIST_TH = 10;
	private boolean start;

	public Everything(WorldState worldState, RobotController robot) {
		super();
		this.robot = robot;
		this.worldState = worldState;
		us = worldState.ourRobot;
		start = true;
	}

	public void doAllTheThings() {
//		if (start){
//			start = false;
//			rotate(Math.PI/2);
//		}
		while (true) {			
			try {
				moveTowardsPoint(worldState.ball.x, worldState.ball.y);

				while (worldState.hasPossession == PossessionType.Us) {
					moveTowardsPoint(worldState.getTheirGoal().getX(), worldState.getTheirGoal().getY());
					Thread.sleep(30);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (worldState.ballIsInGoal()) {
				robot.stop();
				break;
			}
		}
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
		while (DistanceCalculator.Distance(us.x, us.y, x, y) > DIST_TH && i < 50) {
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
	public void moveToPointAndStop(double x, double y) throws InterruptedException {
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

		double angle = Math.acos(dotProductForward / (Math.sqrt(xtc * xtc + ytc * ytc) * Math.sqrt(xt * xt + yt * yt)));
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
	public void moveToPointAndAvoid(double x, double y, double avoidX, double avoidY) {
		if (avoidX <= Math.max(x, us.x)) {}
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

package strategy.movement;

import strategy.calculations.DistanceCalculator;
import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

/**
 * A movement class, that provides calculations for
 * different move commands for the robot.
 * 
 * @author Jakov Smelkin
 */
public class Movement extends Thread {

	// private WorldState worldState;
	private RobotController robot;
	private Robot us;	
	private static int distanceThreshold = 10;
	private boolean die = false;
	private double moveToPointX = 0;
	private double moveToPointY = 0;
	private double avoidX = 0;
	private double avoidY = 0;
	private double speedX = 0;
	private double speedY = 0;
	private double angle = 0.0;

	
	private enum Methods {
		MOVE, MOVEANGLE, MOVETOPOINT, MOVETOPOINTANDSTOP, MOVETOWARDSPOINT, ROTATE, MOVETOPOINTANDAVAOID
	};
	
	/**
	 * A method to call: </br>{@link #move(double speedX, double speedY)}, </br> {@link #move(double angle)} ,</br>
	 * {@link #moveToPoint(double moveToPointX, double moveToPointY)} , </br>
	 * {@link #moveToPointAndStop(double moveToPointX, double moveToPointY)} , </br>
	 * {@link #moveTowardsPoint (double moveToPointX, double moveToPointY)} , </br> {@link #rotate (double angle)}
	 */
	private Methods methodToUse;

	/**
	 * Constructor for the movement class.
	 * 
	 * @param worldState
	 *            a world state from the vision, giving us information on robots, ball etc.
	 * @param robot
	 *            A {@link world.state.RobotController} class that prepares byte commands to the robot.
	 */
	public Movement(WorldState worldState, RobotController robot) {
		super();
		// this.worldState = worldState;
		this.robot = robot;
		us = worldState.ourRobot;
	}

	/**
	 * Runner for our movement, don't forget to set what you plan to do before running this.
	 * 
	 * @see Thread#run
	 */
	public void run() {
		try {
			switch (methodToUse) {
			case MOVE:
				move(speedX, speedY);
				break;
			case MOVEANGLE:
				move(angle);
				break;
			case MOVETOPOINT:
				moveToPoint(moveToPointX, moveToPointY);
				break;
			case MOVETOPOINTANDSTOP:
				moveToPointAndStop(moveToPointX, moveToPointY);
				break;
			case MOVETOWARDSPOINT:
				moveTowardsPoint(moveToPointX, moveToPointY);
				break;
			case MOVETOPOINTANDAVAOID:
				moveToPointAndAvoid(moveToPointX, moveToPointY, avoidX, avoidY);
				break;
			case ROTATE:
				rotate(angle);
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("Try setting up with one of the setUp methods.");
		}
		robot.clearBuff();
	}

	public void die() throws InterruptedException {
		die = true;
		Thread.sleep(50);
		robot.stop();
		robot.clearBuff();
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
	public void setUpMove(double speedX, double speedY) {
		this.speedX=speedX;
		this.speedY=speedX;
		methodToUse=Methods.MOVE;		
	}
	private void move(double speedX, double speedY) {
		robot.move((int) speedX, (int) speedY);
	}

	/**
	 * A general move function where you specify a clockwise angle from the
	 * front of the robot to move at.
	 * 
	 * @param angle
	 *            Angle, in radians (0 to 2*PI)
	 */
	public void setUpMove(double angle) {
		this.angle=angle;
		methodToUse=Methods.MOVEANGLE;		
	}
	private void move(double angle) {
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
	 * @see #moveToPointAndStop(double x, double y)
	 * @see #moveTowardsPoint(double x, double y)
	 */
	public void setUpMoveToPoint(double x, double y){
		this.moveToPointX=x;
		this.moveToPointY=y;
		methodToUse=Methods.MOVETOPOINT;
	}
	private void moveToPoint(double x, double y) throws InterruptedException {

		int i = 0;
		while (DistanceCalculator.Distance(us.x, us.y, x, y) > distanceThreshold && i < 50 && !die) {
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
	public void setUpMoveToPointAndStop(double x, double y){
		this.moveToPointX=x;
		this.moveToPointY=y;
		methodToUse=Methods.MOVETOPOINTANDSTOP;
	}
	private void moveToPointAndStop(double x, double y) throws InterruptedException {
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
	public void setUpMoveTowards(double x, double y){
		this.moveToPointX=x;
		this.moveToPointY=y;
		methodToUse=Methods.MOVETOWARDSPOINT;
	}
	private void moveTowardsPoint(double x, double y) {
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
	public void setUpMoveToPointAndAvoid(double x, double y){
		this.moveToPointX=x;
		this.moveToPointY=y;
		methodToUse=Methods.MOVETOPOINTANDAVAOID;
	}
	// TODO: Finish this.
	private void moveToPointAndAvoid(double x, double y, double avoidX, double avoidY) {
		if (avoidX <= Math.max(x, us.x)) {}
	}

	/**
	 * Calls robot controller to rotate the robot by an angle
	 * 
	 * @param rotationAngle
	 *            clockwise angle to rotate (in Radians)
	 */
	public void setUpRotate(double angle) {
		this.angle=angle;
		methodToUse=Methods.ROTATE;		
	}
	private void rotate(double rotationAngle) {
		rotationAngle = Math.toDegrees(rotationAngle);
		robot.rotate((int) rotationAngle);
	}

}

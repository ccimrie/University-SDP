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
	private static int distanceThreshold = 20;
	private boolean interruptMove = false;
	private boolean die = false;
	private double moveToPointX = 0;
	private double moveToPointY = 0;
	private double avoidX = 0;
	private double avoidY = 0;
	private double speedX = 0;
	private double speedY = 0;
	private double angle = 0.0;

	private enum MovementMode {
		IDLE, MOVE_VECTOR, MOVE_TO_POINT, MOVE_TO_POINT_STOP, MOVE_TOWARDS_POINT, ROTATE, MOVE_TO_POINT_AVOIDING, STOP
	};

	/**
	 * A method to call: </br>{@link #doMove(double speedX, double speedY)},
	 * </br> {@link #doMove(double angle)} ,</br>
	 * {@link #doMoveTo(double moveToPointX, double moveToPointY)} , </br>
	 * {@link #doMoveToAndStop(double moveToPointX, double moveToPointY)} ,
	 * </br> {@link #doMoveTowards (double moveToPointX, double moveToPointY)} ,
	 * </br> {@link #doRotate (double angle)}
	 */
	private MovementMode methodToUse = MovementMode.IDLE;

	/**
	 * Constructor for the movement class.
	 * 
	 * @param worldState
	 *            a world state from the vision, giving us information on
	 *            robots, ball etc.
	 * @param robot
	 *            A {@link world.state.RobotController} class that prepares byte
	 *            commands to the robot.
	 */
	public Movement(WorldState worldState, RobotController robot) {
		super();
		// this.worldState = worldState;
		this.robot = robot;
		us = worldState.ourRobot;
	}

	/**
	 * Runner for our movement, don't forget to set what you plan to do before
	 * running this.
	 * 
	 * @see Thread#run
	 */
	public synchronized void run() {
		try {
			while (!die) {
				switch (methodToUse) {
				case IDLE:
					System.out.println("Mover is idle");
					break;
				case MOVE_VECTOR:
					System.out.println("Moving at speed (" + speedX + ", "
							+ speedY + ")");
					doMove(speedX, speedY);
					break;
				case MOVE_TO_POINT:
					System.out.println("Moving to point (" + moveToPointX
							+ ", " + moveToPointY + ")");
					doMoveTo(moveToPointX, moveToPointY);
					break;
				case MOVE_TO_POINT_STOP:
					System.out.println("Moving to point (" + moveToPointX
							+ ", " + moveToPointY + ") and stopping");
					doMoveToAndStop(moveToPointX, moveToPointY);
					break;
				case MOVE_TOWARDS_POINT:
					System.out.println("Moving towards point (" + moveToPointX
							+ ", " + moveToPointY + ")");
					doMoveTowards(moveToPointX, moveToPointY);
					break;
				case MOVE_TO_POINT_AVOIDING:
					System.out.println("Moving to point (" + moveToPointX
							+ ", " + moveToPointY + ") avoiding (" + avoidX
							+ ", " + avoidY + ")");
					doMoveToAvoiding(moveToPointX, moveToPointY, avoidX, avoidY);
					break;
				case ROTATE:
					System.out.println("Rotating by " + angle + " radians ("
							+ Math.toDegrees(angle) + " degrees)");
					doRotate(angle);
					break;
				case STOP:
					System.out.println("Stopping robot");
					robot.stop();
					break;
				default:
					System.out.println("DERP! Unknown movement mode specified");
					assert (false);
				}
				methodToUse = MovementMode.IDLE;
				// Signal movement operation has completed.
				this.notify();
				// Wait for next movement operation
				this.wait();
			}
			// Stop the robot when the movement thread has been told to exit
			robot.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		robot.clearBuff();
		// Signal robot is stopped and safe to disconnect
		this.notify();
	}

	/**
	 * Tells the move thread to stop executing
	 */
	public synchronized void kill() {
		System.out.println("Killing movement");
		die = true;
		this.notify();
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
	public synchronized void move(double speedX, double speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
		methodToUse = MovementMode.MOVE_VECTOR;
		this.notify();
	}

	/**
	 * Internal method to execute a call to move(speedX, speedY)
	 * 
	 * @param speedX
	 * @param speedY
	 * @see #move(double speedX, double speedY)
	 */
	private void doMove(double speedX, double speedY) {
		robot.move((int) speedX, (int) speedY);
	}

	/**
	 * A general move function where you specify a clockwise angle from the
	 * front of the robot to move at.
	 * 
	 * @param angle
	 *            Angle, in radians (0 to 2*PI)
	 */
	public synchronized void move(double angle) {
		speedX = 100 * Math.sin(angle);
		speedY = 100 * Math.cos(angle);
		methodToUse = MovementMode.MOVE_VECTOR;
		this.notify();
	}

	/**
	 * Internal method to execute a call to move(angle)
	 * 
	 * @param angle
	 * @see #move(double angle)
	 */
	private void doMove(double angle) {
		speedX = 100 * Math.sin(angle);
		speedY = 100 * Math.cos(angle);
		doMove(speedX, speedY);
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
	 * 
	 * @see #moveToAndStop(double x, double y)
	 * @see #moveTowards(double x, double y)
	 */
	public synchronized void moveTo(double x, double y) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		methodToUse = MovementMode.MOVE_TO_POINT;
		interruptMove = true;
		this.notify();
	}

	/**
	 * Internal method to execute a call to moveTo(x, y)
	 * 
	 * @param x
	 * @param y
	 * @see #moveTo(double x, double y)
	 */
	private void doMoveTo(double x, double y) throws InterruptedException {
		int i = 0;
		interruptMove = false;
		while (DistanceCalculator.Distance(us.x, us.y, x, y) > distanceThreshold
				&& i < 50 && !interruptMove) {
			// Not to send unnecessary commands
			// 42 because it's The Answer to the Ultimate Question of Life, the
			// Universe, and Everything
			Thread.sleep(42);
			System.out.println("Our position: (" + us.x + ", " + us.y + ")");
			System.out.println("Moving towards: (" + x + ", " + y + ")");
			System.out.println("Distance: " + DistanceCalculator.Distance(us.x, us.y, x, y));
			doMoveTowards(x, y);
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
	 *            Move to position y units right from top left corner of the
	 *            video feed
	 * 
	 * @see #moveTo(double, double)
	 */
	public synchronized void moveToAndStop(double x, double y) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		methodToUse = MovementMode.MOVE_TO_POINT_STOP;
		interruptMove = true;
		this.notify();
	}

	/**
	 * Internal method to execute a call to moveToAndStop(x, y)
	 * 
	 * @param x
	 * @param y
	 * @see #moveToAndStop(double x, double y)
	 */
	private void doMoveToAndStop(double x, double y)
			throws InterruptedException {
		doMoveTo(x, y);
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
	 *            Move to position y units right from top left corner of the
	 *            video feed
	 * @see #moveTo(double, double)
	 */
	public synchronized void moveTowards(double x, double y) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		methodToUse = MovementMode.MOVE_TOWARDS_POINT;
		this.notify();
	}

	/**
	 * Internal method to execute a call to moveTowards(x, y)
	 * 
	 * @param x
	 * @param y
	 * @see #moveTowards(double x, double y)
	 */
	private void doMoveTowards(double x, double y) {
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
		double dotProductRight = xt * xtc + yt * ytc;

		// Finding the angle from dot product

		double angle = Math.acos(dotProductForward
				/ (Math.sqrt(xtc * xtc + ytc * ytc) * Math.sqrt(xt * xt + yt
						* yt)));

		// Adjusting for negative values
		if (dotProductRight < 0)
			angle = -angle;

		// Calling the generic move function
		doMove(angle);
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
	 *            Point in the X axis to avoid
	 * @param avoidY
	 *            Point in the Y axis to avoid
	 */
	public synchronized void moveToAvoiding(double x, double y, double avoidX,
			double avoidY) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		this.avoidX = avoidX;
		this.avoidY = avoidY;

		methodToUse = MovementMode.MOVE_TO_POINT_AVOIDING;
		this.notify();
	}

	// TODO: Finish this.
	/**
	 * Internal method to execute a call to moveTowards(x, y)
	 * 
	 * @param x
	 * @param y
	 * @param avoidX
	 * @param avoidY
	 * 
	 * @see #moveToAvoiding(double x, double y, double avoidX, double avoidY)
	 */
	private void doMoveToAvoiding(double x, double y, double avoidX,
			double avoidY) {
		if (avoidX <= Math.max(x, us.x)) {
		}
	}

	/**
	 * Calls robot controller to rotate the robot by an angle <br/>
	 * 
	 * @param rotationAngle
	 *            clockwise angle to rotate (in Radians)
	 */
	public synchronized void rotate(double angle) {
		this.angle = angle;
		methodToUse = MovementMode.ROTATE;
		this.notify();
	}

	private void doRotate(double rotationAngle) {
		rotationAngle = Math.toDegrees(rotationAngle);
		robot.rotate((int) rotationAngle);
	}

	/**
	 * Stops the robot
	 */
	public synchronized void stopRobot() {
		methodToUse = MovementMode.STOP;
		this.notify();
	}

	/**
	 * Makes the robot kick
	 */
	public synchronized void kick() {
		robot.kick();
	}
}

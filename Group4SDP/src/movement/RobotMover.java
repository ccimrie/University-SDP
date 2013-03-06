package movement;

import java.util.concurrent.Semaphore;

import strategy.calculations.DistanceCalculator;
import strategy.movement.AStar.AStarPathFinder;
import strategy.movement.AStar.Path;
import strategy.movement.AStar.PathFinder;
import strategy.movement.AStar.ReducedMap;
import strategy.movement.AStar.UnitMover;
import world.state.Robot;
import world.state.WorldState;

import communication.RobotController;

/**
 * A movement class, that provides calculations for different move commands for
 * the robot.
 * 
 * @author Jakov Smelkin - movement
 * @author Alex Adams (s1046358) - threading
 */
public class RobotMover extends Thread {

	private WorldState worldState;
	private RobotController robot;
	private Robot us;
	private static int distanceThreshold = 20;
	private boolean interruptMove = false;
	private boolean running = false;
	private boolean die = false;
	private double moveToPointX = 0;
	private double moveToPointY = 0;
	private boolean avoidBall = false;

	private double speedX = 0;
	private double speedY = 0;
	private double angle = 0.0;

	private Semaphore jobSem = new Semaphore(0, true);
	private Semaphore killSem = new Semaphore(0, true);
	private Semaphore waitSem = new Semaphore(0, true);

	private enum Mode {
		IDLE, STOP, KICK, MOVE_VECTOR, MOVE_TO_POINT, MOVE_TO_POINT_STOP, MOVE_TOWARDS_POINT, ROTATE, MOVE_TO_POINT_ASTAR
	};

	/**
	 * A method to call: </br>{@link #doMove(double speedX, double speedY)},
	 * </br> {@link #doMove(double angle)} ,</br>
	 * {@link #doMoveTo(double moveToPointX, double moveToPointY)} , </br>
	 * {@link #doMoveToAndStop(double moveToPointX, double moveToPointY)} ,
	 * </br> {@link #doMoveTowards (double moveToPointX, double moveToPointY)} ,
	 * </br> {@link #doRotate (double angle)}
	 */
	private Mode mode = Mode.IDLE;

	/**
	 * Constructor for the movement class.
	 * 
	 * @param worldState
	 *            a world state from the vision, giving us information on
	 *            robots, ball etc.
	 * @param robot
	 *            A low-level controller for the robot
	 */
	public RobotMover(WorldState worldState, RobotController robot) {
		super();
		this.worldState = worldState;
		this.robot = robot;
		us = worldState.ourRobot;
	}

	/**
	 * Runner for our movement, don't forget to set what you plan to do before
	 * running this.
	 * 
	 * @see Thread#run()
	 */
	public void run() {
		try {
			while (!die) {
				// Wait for next movement operation
				jobSem.acquire();
				// Clear the movement interrupt flag for the new movement
				interruptMove = false;
				running = true;
				switch (mode) {
				case IDLE:
					System.out.println("Mover is idle");
					break;
				case STOP:
					System.out.println("Stopping robot");
					robot.stop();
					break;
				case KICK:
					System.out.println("Kicking!");
					robot.kick();
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
					doMoveTo(moveToPointX, moveToPointY);
					robot.stop();
					break;
				case MOVE_TOWARDS_POINT:
					System.out.println("Moving towards point (" + moveToPointX
							+ ", " + moveToPointY + ")");
					doMoveTowards(moveToPointX, moveToPointY);
					break;
				case MOVE_TO_POINT_ASTAR:
					System.out.println("Moving to point (" + moveToPointX
							+ ", " + moveToPointY + ") using A*");
					doMoveToAStar(moveToPointX, moveToPointY, avoidBall);
					break;
				case ROTATE:
					System.out.println("Rotating by " + angle + " radians ("
							+ Math.toDegrees(angle) + " degrees)");
					doRotate(angle);
					break;
				default:
					System.out.println("DERP! Unknown movement mode specified");
					assert (false);
				}
				running = false;
				mode = Mode.IDLE;

				// Only wake up the waiting thread if there's a waiting thread
				// to wake up
				if (waitSem.hasQueuedThreads())
					waitSem.release();
			}
			// Stop the robot when the movement thread has been told to exit
			robot.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		robot.clearBuff();
		// Signal that robot is stopped and safe to disconnect
		// Only if there is actually a thread waiting
		if (killSem.hasQueuedThreads())
			killSem.release();
	}

	/**
	 * Tells the move thread to stop executing
	 * 
	 * @throws InterruptedException
	 */
	public void kill() throws InterruptedException {
		System.out.println("Thread called mover.kill(): "
				+ Thread.currentThread().getName());
		die = true;
		interruptMove = true;
		jobSem.release();
		// Wait for the thread to die
		killSem.acquire();
	}

	/**
	 * Triggers an interrupt in movement
	 */
	public void interruptMove() {
		System.out.println("Interrupting movement");
		interruptMove = true;
	}

	/**
	 * Checks if there's an active movement
	 * 
	 * @return true if the mover is doing something, false otherwise
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Waits for the movement to complete before returning
	 */
	public void waitForCompletion() throws InterruptedException {
		waitSem.acquire();
	}

	/**
	 * A general move function as seen from the position of the robot.</br>
	 * Speeds take values between -100 and 100.</br> NOTE: this method will
	 * complete almost immediately
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
		mode = Mode.MOVE_VECTOR;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
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
	 * front of the robot to move at.<br/>
	 * NOTE: this method will complete almost immediately
	 * 
	 * @param angle
	 *            Angle, in radians (0 to 2*PI)
	 */
	public synchronized void move(double angle) {
		speedX = 70 * Math.sin(angle);
		speedY = 70 * Math.cos(angle);
		mode = Mode.MOVE_VECTOR;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
	}

	/**
	 * Internal method to execute a call to move(angle)
	 * 
	 * @param angle
	 * @see #move(double angle)
	 */
	private void doMove(double angle) {
		speedX = 70 * Math.sin(angle);
		speedY = 70 * Math.cos(angle);
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
	 * @see #waitForCompletion()
	 */
	public synchronized void moveTo(double x, double y) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		mode = Mode.MOVE_TO_POINT;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
	}

	/**
	 * Internal method to execute a call to moveTo(x, y)
	 * 
	 * @param x
	 * @param y
	 * @see #moveTo(double x, double y)
	 */
	private void doMoveTo(double x, double y) {
		int i = 0;
		while (DistanceCalculator.Distance(us.x, us.y, x, y) > distanceThreshold
				&& i < 50 && !interruptMove) {
			// Not to send unnecessary commands
			// 42 because it's The Answer to the Ultimate Question of Life, the
			// Universe, and Everything
			try {
				Thread.sleep(42);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Our position: (" + us.x + ", " + us.y + ")");
			System.out.println("Moving towards: (" + x + ", " + y + ")");
			System.out.println("Distance: "
					+ DistanceCalculator.Distance(us.x, us.y, x, y));
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
	 * @see #waitForCompletion()
	 */
	public synchronized void moveToAndStop(double x, double y) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		mode = Mode.MOVE_TO_POINT_STOP;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
	}

	/**
	 * Starts moving to the direction of the point<br/>
	 * NOTE: this method will complete almost immediately
	 * 
	 * @param x
	 *            Move to position x units down from top left corner of the
	 *            video feed
	 * @param ymode
	 *            = Mode.MOVE_TOWARDS_POINT;
	 * 
	 *            Move to position y units right from top left corner of the
	 *            video feed
	 * @see #moveTo(double, double)
	 */
	public synchronized void moveTowards(double x, double y) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		mode = Mode.MOVE_TOWARDS_POINT;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
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
	 * Move to a point (x,y) while avoiding point enemy robot and optionally the
	 * ball. Should go in an arc by default.
	 * 
	 * @param x
	 *            Point in the X axis to move to.
	 * @param y
	 *            Point in the Y axis to move to.
	 * 
	 * @param avoidBall
	 *            Should A* avoid the ball.
	 * @see #waitForCompletion()
	 */
	public synchronized void moveToAStar(double x, double y, boolean avoidBall) {
		this.moveToPointX = x;
		this.moveToPointY = y;
		this.avoidBall = avoidBall;
		interruptMove = true;

		mode = Mode.MOVE_TO_POINT_ASTAR;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
	}

	/**
	 * Internal method to execute a call to moveTowards(x, y)
	 * 
	 * @param x
	 *            Point in the X axis to move to
	 * @param y
	 *            Point in the Y axis to move to
	 * 
	 * @see #moveToAStar(double x, double y)
	 */
	private void doMoveToAStar(double x, double y, boolean avoidball) {
		ReducedMap map = new ReducedMap(worldState, avoidball);
		System.out.println("Height: " + map.getHeightInTiles());
		System.out.println("Width: " + map.getWidthInTiles());

		System.out.println("Height: "
				+ worldState.goalInfo.pitchConst.getPitchHeight() + "px");

		System.out.println("Width: "
				+ worldState.goalInfo.pitchConst.getPitchWidth() + "px");

		PathFinder finder = new AStarPathFinder(map, 100, true);
		int selectedx = map.reduceRound(us.y);
		int selectedy = map.reduceRound(us.x);
		int goToX = map.reduceRound(y);
		int goToY = map.reduceRound(x);
		Path path = finder.findPath(
				new UnitMover(map.getUnit(selectedx, selectedy)), selectedx,
				selectedy, goToX, goToY);
		if (path != null) {
			int l = path.getLength();

			int i = 0;
			while (i < l && !interruptMove) {
				map.terrain[path.getX(i)][path.getY(i)] = 7;
				i++;
			}
			for (int k = 0; k < map.getHeightInTiles(); k++) {
				String brr = "";
				for (int j = 0; j < map.getWidthInTiles(); j++) {
					brr += " " + map.getTerrain(k, j);
				}
				System.out.println(brr);
			}

			i = 0;
			distanceThreshold = 30;
			while (i < l && !interruptMove) {
				// map.terrain[path.getX(i)][path.getY(i)] = 7;
				System.out.println("AStar: Calling movement to ("
						+ path.getY(i) * map.REDUCTION + ", " + path.getX(i)
						* map.REDUCTION + ")");
				doMoveTo(path.getY(i) * map.REDUCTION, path.getX(i)
						* map.REDUCTION);
				/*
				 * try { Thread.sleep(100); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */
				i++;
				// robot.stop();
			}
			distanceThreshold = 20;
		}
		System.out.println("AStar: Calling stop");
		robot.stop();
		/*
		 * for (int i = 0; i < map.getHeightInTiles(); i++) { String brr = "";
		 * for (int j = 0; j < map.getWidthInTiles(); j++) { brr += " " +
		 * map.getTerrain(i, j); } System.out.println(brr);
		 * 
		 * }
		 */

	}

	/**
	 * Calls robot controller to rotate the robot by an angle <br/>
	 * 
	 * @param angleRad
	 *            clockwise angle to rotate (in Radians)
	 * @see #waitForCompletion()
	 */
	public synchronized void rotate(double angleRad) {
		this.angle = angleRad;
		mode = Mode.ROTATE;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
	}

	/**
	 * Internal method to execute a call to rotate(angle)
	 * 
	 * @param angleRad
	 *            clockwise angle to rotate (in Radians)
	 */
	private void doRotate(double angleRad) {
		angleRad = Math.toDegrees(angleRad);
		robot.rotate((int) angleRad);
	}

	/**
	 * Stops the robot
	 * 
	 * @see #waitForCompletion()
	 */
	public synchronized void stopRobot() {
		mode = Mode.STOP;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
	}

	/**
	 * Makes the robot kick
	 * 
	 * @see #waitForCompletion()
	 */
	public synchronized void kick() {
		mode = Mode.KICK;
		interruptMove = true;
		// Let the mover know it has a new job, only if it's waiting for a new
		// job
		if (jobSem.hasQueuedThreads())
			jobSem.release();
	}
}

package movement;

import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import strategy.calculations.DistanceCalculator;
import strategy.movement.AStar.AStarPathFinder;
import strategy.movement.AStar.Path;
import strategy.movement.AStar.PathFinder;
import strategy.movement.AStar.ReducedMap;
import strategy.movement.AStar.UnitMover;
import utility.SafeSleep;
import world.state.Robot;
import world.state.WorldState;

import communication.RobotController;

/**
 * TODO: Add a queue to permit multiple threads waiting on the mover thread to
 * complete (will need to store the point in the movement queue it's waiting
 * on). <br/>
 * TODO: Discuss implementing moveToAStar for moving points <br/>
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
	private double speedCoef = 1.0;

	public void setSpeedCoef(double coef) {
		coefLock.lock();
		speedCoef = coef;
		coefLock.unlock();
	}

	/** The distance at which a moveTo command decides it's close enough */
	public static int distanceThreshold = 20;

	/**
	 * A method to call: <br/>
	 * {@link RobotMover#doMove(double speedX, double speedY)}, <br/>
	 * {@link RobotMover#doMove(double angle)} ,<br/>
	 * {@link RobotMover#doMoveTo(double x, double y)} , <br/>
	 * {@link RobotMover#doMoveTo(MovingPoint p)} , <br/>
	 * {@link RobotMover#doMoveToAStar(double x, double y, boolean avoidBall)} ,<br/>
	 * {@link RobotMover#doMoveTowards (double x, double y)} , <br/>
	 * {@link RobotMover#doRotate (double angle)}
	 */
	private enum Mode {
		STOP, KICK, DELAY, MOVE_VECTOR, MOVE_ANGLE, MOVE_TO, MOVE_TO_ASTAR, MOVE_TOWARDS, ROTATE, DRIBBLEON, DRIBBLEOFF
	};

	/** Settings info class to permit queueing of movements */
	private class MoverConfig {
		public double x = 0;
		public double y = 0;
		public double angle = 0;
		public boolean avoidBall = false;
		public boolean avoidEnemy = false;
		public long milliseconds = 0;
		private int dribblemode = 0;
		private MovingPoint movPoint = null;

		public Mode mode;
	};

	/** A flag to permit busy-waiting */
	private boolean running = false;
	/** A flag to interrupt any active movements */
	private boolean interruptMove = false;
	/** A flag to tell the RobotMover thread to die */
	private boolean die = false;

	/** A thread-safe queue for movement commands */
	private ConcurrentLinkedQueue<MoverConfig> moveQueue = new ConcurrentLinkedQueue<MoverConfig>();
	/** A reentrant mutex lock for the movement queue */
	private ReentrantLock queueLock = new ReentrantLock(true);
	/** A semaphore used to signal the RobotMover has jobs queued */
	private Semaphore jobSem = new Semaphore(0, true);
	/** A semaphore used to signal the RobotMover has completed its job queue */
	private Semaphore waitSem = new Semaphore(0, true);

	private ReentrantLock coefLock = new ReentrantLock(true);

	/**
	 * Constructor for the movement class.
	 * 
	 * @param worldState
	 *            A persistent copy of the world state which is assumed to be
	 *            updated periodically
	 * @param robot
	 *            A low-level controller for the robot
	 */
	public RobotMover(WorldState worldState, RobotController robot) {
		super("RobotMover");
		this.worldState = worldState;
		this.robot = robot;
		us = worldState.ourRobot;
	}

	/**
	 * Repeatedly tries to push the movement onto the move queue, giving up
	 * after 10 attempts
	 * 
	 * @param movement
	 *            The movement to push onto the queue
	 * @return true if the movement was successfully pushed, false otherwise
	 */
	private boolean pushMovement(MoverConfig movement) {
		int pushAttempts = 0;
		try {
			queueLock.lockInterruptibly();
		} catch (InterruptedException e) {
			return false;
		}
		// Try to push the movement 10 times before giving up
		while (!moveQueue.offer(movement) && pushAttempts < 10)
			++pushAttempts;
		queueLock.unlock();
		// If we gave up, return false to indicate it
		if (pushAttempts >= 10)
			return false;
		return true;
	}

	/**
	 * Wakes up any threads waiting on a movement queue to complete
	 */
	private void wakeUpWaitingThreads() {
		waitSem.release();
		running = false;
	}

	/**
	 * Processes a single movement
	 * 
	 * @param movement
	 *            The movement to process
	 * @throws Exception
	 *             If an error occurred
	 */
	private void processMovement(MoverConfig movement) throws Exception {
		switch (movement.mode) {
		case STOP:
			robot.stop();
			break;
		case KICK:
			robot.kick();
			break;
		case DRIBBLEON:
			robot.dribble(movement.dribblemode);
			break;
		case DRIBBLEOFF:
			robot.stopdribble();
			break;
		case DELAY:
			SafeSleep.sleep(movement.milliseconds);
			break;
		case MOVE_VECTOR:
			doMove(movement.x, movement.y);
			break;
		case MOVE_ANGLE:
			doMove(movement.angle);
			break;
		case MOVE_TO:
			// Check if it's a static or moving point
			if (movement.movPoint == null)
				// Static
				doMoveTo(movement.x, movement.y);
			else
				// Moving
				doMoveTo(movement.movPoint);
			break;
		case MOVE_TOWARDS:
			doMoveTowards(movement.x, movement.y);
			break;
		case MOVE_TO_ASTAR:
			doMoveToAStar(movement.x, movement.y, movement.avoidBall, movement.avoidEnemy);
			break;
		case ROTATE:
			doRotate(movement.angle);
			break;
		default:
			System.out.println("DERP! Unknown movement mode specified");
			assert (false);
		}
	}

	/**
	 * Main method for the movement thread
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
				// Set the running flag to true for busy-waiting
				running = true;

				queueLock.lockInterruptibly();
				if (!moveQueue.isEmpty() && !die) {
					MoverConfig movement = moveQueue.poll();
					queueLock.unlock();
					assert (movement != null) : "moveQueue.poll() returned null when non-empty";
					assert (movement.mode != null) : "invalid movement generated";

					processMovement(movement);
				} else {
					queueLock.unlock();
				}

				// If we just did the last move in the queue, wake up the
				// waiting threads
				if (moveQueue.isEmpty())
					wakeUpWaitingThreads();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Try to prevent deadlocks when the mover breaks
			wakeUpWaitingThreads();
		} finally {
			// Stop the robot when the movement thread has been told to exit
			robot.stop();
			// Clear the robot's buffer to potentially allow restart of a
			// RobotMover thread
			robot.clearBuff();
		}
	}

	/**
	 * Tells the move thread to stop executing and immediately returns. <br/>
	 * Call join() after this if you want to wait for the mover thread to die.
	 */
	public void kill() {
		die = true;
		interruptMove();
		// Wake up the RobotMover thread if it's waiting for a new job
		jobSem.release();
	}

	/**
	 * Triggers an interrupt in any active movement. <br/>
	 * NOTE: this will have no effect if the active movement is a delay
	 */
	public void interruptMove() {
		interruptMove = true;
	}

	/**
	 * Resets the queue of movements to allow for an immediate change in planned
	 * movements <br/>
	 * NOTE: This does not interrupt an active movement
	 * 
	 * @throws InterruptedException
	 *             if the RobotMover thread was interrupted
	 */
	public void resetQueue() throws InterruptedException {
		// Block changes in the queue until the queue is finished
		// resetting
		queueLock.lockInterruptibly();
		// Reset the job semaphore since there will be no more queued jobs
		jobSem.drainPermits();
		if (moveQueue.isEmpty()) {
			queueLock.unlock();
			return;
		}

		moveQueue.clear();
		queueLock.unlock();
	}

	/**
	 * Checks if the mover is running a job
	 * 
	 * @return true if the mover is doing something, false otherwise
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Checks if the mover has queued jobs, not including the one currently
	 * running
	 * 
	 * @return true if there are queued jobs, false otherwise
	 */
	public boolean hasQueuedJobs() {
		try {
			queueLock.lockInterruptibly();
			boolean result = !moveQueue.isEmpty();
			queueLock.unlock();
			return result;
		} catch (InterruptedException e) {
			// InterruptedException can only occur if the thread has been
			// interrupted - therefore there can't be jobs waiting
			return false;
		}
	}

	/**
	 * Determines how many jobs have been queued, not including the one
	 * currently running
	 * 
	 * @return The number of jobs currently queued
	 */
	public int numQueuedJobs() {
		try {
			// Get a lock on the queue to prevent changes while determining how
			// many
			// jobs there are
			queueLock.lockInterruptibly();
			int result = moveQueue.size();
			queueLock.unlock();
			return result;
		} catch (InterruptedException e) {
			// If the thread has been interrupted, there can't be jobs queued.
			return 0;
		}
	}

	/**
	 * Waits for the movement queue to complete before returning
	 */
	public void waitForCompletion() throws InterruptedException {
		waitSem.acquire();
	}

	/**
	 * Queues a change in the robot's movement vector using the vector.
	 * components directly<br/>
	 * Speeds take values between -100 and 100.<br/>
	 * NOTE: this movement will complete almost immediately.
	 * 
	 * @param speedX
	 *            Speed right (for positive values) or left (for negative ones),
	 *            relative to the robot.
	 * @param speedY
	 *            Speed forward (for positive values) or backward (for negative
	 *            ones), relative to the robot
	 * @return true if the move was successfully queued, false otherwise
	 */
	public synchronized boolean move(double speedX, double speedY) {
		MoverConfig movement = new MoverConfig();
		movement.x = speedX;
		movement.y = speedY;
		movement.mode = Mode.MOVE_VECTOR;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Internal method to execute a call to move(speedX, speedY)
	 * 
	 * @param speedX
	 * @param speedY
	 * @see #move(double speedX, double speedY)
	 */
	private void doMove(double speedX, double speedY) {
		coefLock.lock();
		robot.move((int) (speedCoef * speedX), (int) (speedCoef * speedY));
		coefLock.unlock();
	}

	/**
	 * Queues a change in the robot's movement vector using a clockwise angle
	 * from the front of the robot.<br/>
	 * NOTE: this movement will complete almost immediately.
	 * 
	 * @param angle
	 *            The clockwise angle to move at, relative to the front of the
	 *            robot (in radians)
	 * @return true if the move was successfully queued, false otherwise
	 */
	public synchronized boolean move(double angle) {
		MoverConfig movement = new MoverConfig();
		movement.angle = angle;
		movement.mode = Mode.MOVE_ANGLE;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Internal method to execute a call to move(angle)
	 * 
	 * @param angle
	 * @see #move(double angle)
	 */
	private void doMove(double angle) {
		double x = Math.sin(angle);
		double absX = Math.abs(x);
		double y = Math.cos(angle);
		double absY = Math.abs(y);

		double speed = 100.0;
		if (Math.abs(y) > Math.abs(x)) {
			speed /= absY;
		} else {
			speed /= absX;
		}

		doMove(speed * x, speed * y);
	}

	/**
	 * Queues a movement to a point on the camera feed's coordinate system.<br/>
	 * NOTE: The robot does not stop when it reaches the point.
	 * 
	 * @param x
	 *            The x coordinate of the point. (This is the number of pixels
	 *            from the left edge of the camera feed.)
	 * @param y
	 *            The y coordinate of the point. (This is the number of pixels
	 *            from the top edge of the camera feed.)
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #moveToAndStop(double x, double y)
	 * @see #moveTowards(double x, double y)
	 * @see #waitForCompletion()
	 */
	public synchronized boolean moveTo(double x, double y) {
		MoverConfig movement = new MoverConfig();
		movement.x = x;
		movement.y = y;
		movement.mode = Mode.MOVE_TO;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Queues a movement to a moving point on the camera feed's coordinate
	 * system.<br/>
	 * NOTE: The robot does not stop when it reaches the point.
	 * 
	 * @param p
	 *            The moving point the robot should move to
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #moveToAndStop(MovingPoint)
	 * @see #waitForCompletion()
	 */
	public synchronized boolean moveTo(MovingPoint p) {
		MoverConfig movement = new MoverConfig();
		movement.movPoint = p;
		movement.mode = Mode.MOVE_TO;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
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
		while (DistanceCalculator.Distance(us.x, us.y, x, y) > distanceThreshold && i < 500 && !interruptMove) {
			doMoveTowards(x, y);
			// Not to send unnecessary commands
			// 42 because it's The Answer to the Ultimate Question of Life, the
			// Universe, and Everything
			try {
				SafeSleep.sleep(42);
			} catch (InterruptedException e) {
				System.out.println("Failed to sleep");
				e.printStackTrace();
			}
			// If we can't get to the point for some reason, it should cancel
			// after some iterations
			i++;
		}
	}

	/**
	 * Internal method to execute a call to moveTo(p)
	 * 
	 * @param p
	 * @see #moveTo(MovingPoint)
	 */
	private void doMoveTo(MovingPoint p) {
		int i = 0;
		Point2D point = p.get();
		assert point != null;
		while (DistanceCalculator.Distance(us.x, us.y, point.getX(), point.getY()) > distanceThreshold && i < 500 && !interruptMove) {

			doMoveTowards(point.getX(), point.getY());
			try {
				SafeSleep.sleep(42);
			} catch (InterruptedException e) {
				System.out.println("Failed to sleep");
				e.printStackTrace();
			}

			// Update the point
			point = p.get();
			// If we can't get to the point for some reason, it should cancel
			// after some iterations
			++i;
		}
	}

	/**
	 * Queues a movement to a point on the camera feed's coordinate system, and
	 * stops the robot when it gets there.
	 * 
	 * @param x
	 *            Move to position x units down from top left corner of the
	 *            video feed
	 * @param y
	 *            Move to position y units right from top left corner of the
	 *            video feed
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #moveTo(double, double)
	 * @see #waitForCompletion()
	 */
	public synchronized boolean moveToAndStop(double x, double y) {
		// Queue the movement
		MoverConfig movement = new MoverConfig();
		movement.x = x;
		movement.y = y;
		movement.mode = Mode.MOVE_TO;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();

		// Queue the stop
		movement = new MoverConfig();
		movement.mode = Mode.STOP;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a second new job
		jobSem.release();
		return true;
	}

	/**
	 * Queues a movement to a moving point on the camera feed's coordinate
	 * system, and stops the robot when it gets there.
	 * 
	 * @param p
	 *            The moving point the robot should move to
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #moveTo(MovingPoint)
	 * @see #waitForCompletion()
	 */
	public synchronized boolean moveToAndStop(MovingPoint p) {
		// Queue the movement
		MoverConfig movement = new MoverConfig();
		movement.movPoint = p;
		movement.mode = Mode.MOVE_TO;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();

		// Queue the stop
		movement = new MoverConfig();
		movement.mode = Mode.STOP;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a second new job
		jobSem.release();
		return true;
	}

	/**
	 * Queues an instruction to start moving towards a point<br/>
	 * NOTE: this movement will complete almost immediately
	 * 
	 * @param x
	 *            Move to position x units down from top left corner of the
	 *            video feed
	 * @param y
	 *            Move to position y units right from top left corner of the
	 *            video feed
	 * @return true if the move was successfully queued, false otherwise
	 * @see #moveTo(double, double)
	 */
	public synchronized boolean moveTowards(double x, double y) {
		MoverConfig movement = new MoverConfig();
		movement.x = x;
		movement.y = y;
		movement.mode = Mode.MOVE_TOWARDS;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Internal method to execute a call to moveTowards(x, y)
	 * 
	 * @param x
	 * @param y
	 * @see #moveTowards(double x, double y)
	 */
	private void doMoveTowards(double x, double y) {
		// Get the turn angle for the point
		double angle = angleCalculator(us.x, us.y, x, y, us.bearing);
		// Move in the direction the turn angle points to
		doMove(angle);
	}

	/**
	 * Calculates the turn angle between a robot and a point
	 * 
	 * @param x1
	 *            The x coordinate of the robot (camera coordinates)
	 * @param y1
	 *            The y coordinate of the robot (camera coordinates)
	 * @param x2
	 *            The x coordinate of the point (camera coordinates)
	 * @param y2
	 *            The y coordinate of the point (camera coordinates)
	 * @param bearing
	 *            The bearing of the robot (in radians), relative to up on the
	 *            camera feed
	 * @return The turn angle (in radians)
	 */
	public double angleCalculator(double x1, double y1, double x2, double y2, double bearing) {
		double xt, yt, xtc, ytc;
		// Vector from robot to point in the camera axis
		xtc = x2 - x1;
		ytc = y2 - y1;

		// Unit vector in camera coordinates in the forward direction of the
		// robot
		xt = Math.sin(bearing);
		yt = -Math.cos(bearing);
		// Dot product between the forward vector and the direction of the point
		double dotProductForward = xt * xtc + yt * ytc;

		// Unit vector in camera coordinates at a clockwise right angle to the
		// forward vector (i.e. pointing to the robot's right)
		xt = Math.sin(bearing + Math.PI / 2.0);
		yt = -Math.cos(bearing + Math.PI / 2.0);
		// Dot product between the right vector and the direction of the point
		double dotProductRight = xt * xtc + yt * ytc;

		// Find the turn angle using the dot product
		double angle = Math.acos(dotProductForward / (Math.sqrt((xtc * xtc + ytc * ytc) * (xt * xt + yt * yt))));

		// Ensure the turn angle is in the correct direction
		if (dotProductRight < 0)
			angle = -angle;
		return angle;
	}

	/**
	 * Queues a movement to a point while avoiding optionally either the ball,
	 * the enemy robot, or both.
	 * 
	 * @param x
	 *            Point in the X axis to move to.
	 * @param y
	 *            Point in the Y axis to move to.
	 * @param avoidBall
	 *            Should A* avoid the ball.
	 * @param avoidEnemy
	 *            Should A* avoid the enemy robot.
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #waitForCompletion()
	 */
	public synchronized boolean moveToAStar(double x, double y, boolean avoidBall, boolean avoidEnemy) {
		MoverConfig movement = new MoverConfig();
		movement.x = x;
		movement.y = y;
		movement.avoidBall = avoidBall;
		movement.avoidEnemy = avoidEnemy;
		movement.mode = Mode.MOVE_TO_ASTAR;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
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
	private void doMoveToAStar(double x, double y, boolean avoidball, boolean avoidenemy) {
		ReducedMap map = new ReducedMap(worldState, avoidball, avoidenemy);
		System.out.println("Map Height: " + map.getHeightInTiles());
		System.out.println("Map Width: " + map.getWidthInTiles());

		System.out.println("Height: " + worldState.goalInfo.pitchConst.getPitchHeight() + "px");

		System.out.println("Width: " + worldState.goalInfo.pitchConst.getPitchWidth() + "px");

		PathFinder finder = new AStarPathFinder(map, 100, true);
		int selectedx = map.reduceRound(us.x);
		int selectedy = map.reduceRound(us.y);
		int goToX = map.reduceRound(x);
		int goToY = map.reduceRound(y);
		System.out.println("(" + goToX + ", " + goToY + ")");
		Path path = finder.findPath(new UnitMover(map.getUnit(selectedx, selectedy)), selectedx, selectedy, goToX, goToY);
		if (path != null) {
			distanceThreshold = 40;
			int l = path.getLength();
			if (l > 4) {
				l = 4;
			}
			int i = 0;
			while (i < l && !interruptMove) {
				map.terrain[path.getX(i)][path.getY(i)] = 7;
				i++;
			}
			for (int k = 0; k < map.getHeightInTiles(); k++) {
				String brr = "";
				for (int j = 0; j < map.getWidthInTiles(); j++) {
					brr += " " + map.getTerrain(j, k);
				}
				System.out.println(brr);
			}

			
			if (l > 3) {
				i = 0;
				while (i < l && !interruptMove) {
					// map.terrain[path.getX(i)][path.getY(i)] = 7;
					System.out.println("AStar: Calling movement to (" + path.getX(i) * map.REDUCTION + ", " + path.getY(i) * map.REDUCTION + ")");
					doMoveTo(path.getX(i) * map.REDUCTION, path.getY(i) * map.REDUCTION);
					// doMoveTowards(path.getX(i) * map.REDUCTION, path.getY(i) * map.REDUCTION);
					i++;
					// robot.stop();
				}
			} else {
				l--;
				System.out.println("AStar: Short path, direct movement to (" + path.getX(l) * map.REDUCTION + ", " + path.getY(l) * map.REDUCTION + ")");
				doMoveTo(path.getX(l) * map.REDUCTION, path.getY(l) * map.REDUCTION);
			}
			distanceThreshold = 20;
		}
		System.out.println("AStar: Calling stop");
		robot.stop();
		/*
		 * for (int i = 0; i < map.getHeightInTiles(); i++) { String brr = "";
		 * for (int j = 0; j < map.getWidthInTiles(); j++) { brr += " " +
		 * map.getTerrain(j, i); } System.out.println(brr); }
		 */

	}

	/**
	 * Queues a rotation by an angle.
	 * 
	 * @param angleRad
	 *            clockwise angle to rotate by (in Radians)
	 * @return true if the rotate was successfully queued, false otherwise
	 * 
	 * @see #waitForCompletion()
	 */
	public synchronized boolean rotate(double angleRad) {
		MoverConfig movement = new MoverConfig();
		movement.angle = angleRad;
		movement.mode = Mode.ROTATE;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Internal method to execute a call to rotate(angle)
	 * 
	 * @param angleRad
	 *            clockwise angle to rotate (in Radians)
	 */
	private void doRotate(double angleRad) {
		robot.rotate((int) Math.toDegrees(angleRad));
	}

	/**
	 * Queues a command to stop the robot
	 * 
	 * @return true if the stop was successfully queued, false otherwise
	 * 
	 * @see #waitForCompletion()
	 * @see #interruptMove()
	 * @see #resetQueue()
	 */
	public synchronized boolean stopRobot() {
		MoverConfig movement = new MoverConfig();
		movement.mode = Mode.STOP;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Queues a command to make the robot kick
	 * 
	 * @return true if the kick was successfully queued, false otherwise
	 * 
	 * @see #waitForCompletion()
	 */
	public synchronized boolean kick() {
		MoverConfig movement = new MoverConfig();
		movement.mode = Mode.KICK;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Turns the dribbler on
	 * 
	 * @return true if the dribbler command was successfully queued, false
	 *         otherwise
	 * @param direction
	 *            - 1 for forward, 2 for backwards
	 * @see #waitForCompletion()
	 */
	public synchronized boolean dribble(int direction) {
		MoverConfig movement = new MoverConfig();
		movement.dribblemode = direction;
		movement.mode = Mode.DRIBBLEON;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Turns the dribbler off.
	 * 
	 * @return true if the dribbler command was successfully queued, false
	 *         otherwise
	 * @param direction
	 *            - 1 for forward, 2 for backwards
	 * @see #waitForCompletion()
	 */

	public synchronized boolean stopdribble() {
		MoverConfig movement = new MoverConfig();
		movement.mode = Mode.DRIBBLEOFF;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Queues a delay job, where the RobotMover will wait for the specified time <br/>
	 * WARNING: Once a delay job has started, it cannot be interrupted
	 * 
	 * @param milliseconds
	 *            The time in milliseconds to sleep for
	 * 
	 * @return true if the delay was successfully queued, false otherwise
	 */
	public synchronized boolean delay(long milliseconds) {
		MoverConfig movement = new MoverConfig();
		movement.milliseconds = milliseconds;
		movement.mode = Mode.DELAY;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}
}

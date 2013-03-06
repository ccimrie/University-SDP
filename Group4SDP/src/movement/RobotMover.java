package movement;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
 * TODO: Add a queue to permit multiple threads waiting on the mover thread to
 * complete (will need to store the point in the movement queue it's waiting
 * on). <br/>
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

	/**
	 * A method to call: <br/>
	 * {@link RobotMover#doMove(double speedX, double speedY)}, <br/>
	 * {@link RobotMover#doMove(double angle)} ,<br/>
	 * {@link RobotMover#doMoveTo(double x, double y)} , <br/>
	 * {@link RobotMover#doMoveToAndStop(double x, double y)} , <br/>
	 * {@link RobotMover#doMoveToAStar(double x, double y, boolean avoidBall)} ,<br/>
	 * {@link RobotMover#doMoveTowards (double x, double y)} , <br/>
	 * {@link RobotMover#doRotate (double angle)}
	 */
	private enum Mode {
		STOP, KICK, DELAY, MOVE_VECTOR, MOVE_ANGLE, MOVE_TO, MOVE_TO_STOP, MOVE_TO_ASTAR, MOVE_TOWARDS, ROTATE
	};

	/** Settings info class to permit queueing of movements */
	private class MoverConfig {
		public double x = 0;
		public double y = 0;
		public double angle = 0;
		public boolean avoidBall = false;
		public long milliseconds = 0;

		public Mode mode;
	};

	private boolean running = false;
	private boolean interruptMove = false;
	private boolean die = false;

	private ConcurrentLinkedQueue<MoverConfig> moveQueue = new ConcurrentLinkedQueue<MoverConfig>();
	private Semaphore queueSem = new Semaphore(1, true);

	private Semaphore jobSem = new Semaphore(0, true);
	private Semaphore killSem = new Semaphore(0, true);
	private Semaphore waitSem = new Semaphore(0, true);

	/** Thread-safe sleep scheduler */
	private final ScheduledExecutorService sleepScheduler = Executors
			.newScheduledThreadPool(1);

	/** Thread-safe sleep */
	private void safeSleep(long millis) throws InterruptedException {
		final Semaphore sleepSem = new Semaphore(0, true);
		// Schedule a wake-up after the specified time
		sleepScheduler.schedule(new Runnable() {
			@Override
			public void run() {
				sleepSem.release();
			}
		}, millis, TimeUnit.MILLISECONDS);
		// Wait for the wake-up
		sleepSem.acquire();
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
			queueSem.acquire();
		} catch (InterruptedException e) {
			return false;
		}
		// Try to push the movement 10 times before giving up
		while (!moveQueue.offer(movement) && pushAttempts < 10)
			++pushAttempts;
		queueSem.release();
		// If we gave up, return false to indicate it
		if (pushAttempts >= 10)
			return false;
		return true;
	}

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
		// safeSleep requires some initial use to prevent lag spikes on the
		// first few runs - Thread.sleep has similar problems but less
		// noticeable
		System.out.println("Mover: safeSleep initial clearing started");
		try {
			for (int i = 0; i < 3; ++i)
				safeSleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Mover: safeSleep initial clearing finished");
		try {
			while (!die) {
				// Temporarily block queue changes while retrieving the next job
				System.out.println("Acquiring jobSem");
				// Wait for next movement operation
				jobSem.acquire();
				System.out.println("Mover: Got a job");
				// Clear the movement interrupt flag for the new movement
				interruptMove = false;
				// Set the running flag to true for busy-waiting
				running = true;

				System.out.println("Acquiring queueSem");
				queueSem.acquire();
				if (!moveQueue.isEmpty() && !die) {
					MoverConfig movement = moveQueue.poll();
					// Queue will not change from here on, so release the
					// semaphore
					queueSem.release();
					assert (movement != null) : "moveQueue.poll() returned null when non-empty";

					if (movement.mode == null) {
						System.out.println("Mover is idle");
						continue;
					}

					try {
						System.out.println("Mover: Job received: "
								+ movement.mode.toString());
						switch (movement.mode) {
						case STOP:
							System.out.println("Stopping robot");
							robot.stop();
							break;
						case KICK:
							System.out.println("Kicking!");
							robot.kick();
							break;
						case DELAY:
							System.out.println("Waiting for "
									+ movement.milliseconds + " milliseconds");
							safeSleep(movement.milliseconds);
							break;
						case MOVE_VECTOR:
							System.out.println("Moving at speed (" + movement.x
									+ ", " + movement.y + ")");
							doMove(movement.x, movement.y);
							break;
						case MOVE_ANGLE:
							System.out.println("Moving at angle "
									+ movement.angle + " radians ("
									+ Math.toDegrees(movement.angle)
									+ " degrees)");
							doMove(movement.angle);
							break;
						case MOVE_TO:
							System.out.println("Moving to point (" + movement.x
									+ ", " + movement.y + ")");
							doMoveTo(movement.x, movement.y);
							break;
						case MOVE_TO_STOP:
							System.out.println("Moving to point (" + movement.x
									+ ", " + movement.y + ") and stopping");
							doMoveTo(movement.x, movement.y);
							robot.stop();
							break;
						case MOVE_TOWARDS:
							System.out.println("Moving towards point ("
									+ movement.x + ", " + movement.y + ")");
							doMoveTowards(movement.x, movement.y);
							break;
						case MOVE_TO_ASTAR:
							System.out.println("Moving to point (" + movement.x
									+ ", " + movement.y + ") using A*");
							doMoveToAStar(movement.x, movement.y,
									movement.avoidBall);
							break;
						case ROTATE:
							System.out.println("Rotating by " + movement.angle
									+ " radians ("
									+ Math.toDegrees(movement.angle)
									+ " degrees)");
							doRotate(movement.angle);
							break;
						default:
							System.out
									.println("DERP! Unknown movement mode specified");
							assert (false);
						}
					} catch (Exception e) {
						System.out.println("Error occurred executing job: ");
						e.printStackTrace();
						resetQueue();
					}
				} else {
					queueSem.release();
				}

				System.out.println("Mover: job completed");
				// If we just did the last move in the queue, wake up the
				// waiting threads
				if (moveQueue.isEmpty()) {
					// Only wake up the waiting threads if there are waiting
					// threads to wake up
					System.out.println("Waking up waiter");
					waitSem.release();
					running = false;
				}
			}
			// Stop the robot when the movement thread has been told to exit
			robot.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		robot.clearBuff();
		// Signal that robot is stopped and safe to disconnect
		// Only if there is actually any threads waiting
	}

	/**
	 * Tells the move thread to stop executing, and waits until the move thread
	 * has terminated before returning
	 * 
	 * @throws InterruptedException
	 */
	public void kill() throws InterruptedException {
		die = true;
		resetQueue();
	}

	/**
	 * Triggers an interrupt in movement
	 */
	public void interruptMove() {
		System.out.println("Interrupting movement");
		interruptMove = true;
	}

	/**
	 * Resets the queue of movements to allow for an immediate change in planned
	 * movements, and also interrupts the current movement
	 * 
	 * @throws InterruptedException
	 */
	public void resetQueue() throws InterruptedException {
		if (moveQueue.isEmpty())
			return;
		interruptMove();

		// Block changes in the queue until the queue is finished
		// resetting
		queueSem.acquire();
		// Acquire the permits for queued jobs to cancel the execution for them
		jobSem.acquire(moveQueue.size());
		moveQueue.clear();
		// Reactivate the movement thread
		queueSem.release();
	}

	/**
	 * Checks if the mover is running jobs
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
		// Semaphore not required since isEmpty is constant runtime and the
		// queue is thread-safe
		return !moveQueue.isEmpty();
	}

	/**
	 * @return The number of jobs currently queued, not including the one
	 *         currently running
	 */
	public int numQueuedJobs() {
		// Get a lock on the queue to prevent changes while determining how many
		// jobs there are
		try {
			queueSem.acquire();
			int result = moveQueue.size();
			queueSem.release();
			return result;
		} catch (InterruptedException e) {
			return 0;
		}
	}

	/**
	 * Waits for the movement to complete before returning
	 */
	public void waitForCompletion() throws InterruptedException {
		waitSem.acquire();
	}

	/**
	 * A general move function as seen from the position of the robot.<br/>
	 * Speeds take values between -100 and 100.<br/>
	 * NOTE: this movement will complete almost immediately
	 * 
	 * @param speedX
	 *            Speed right (for positive values) or left (for negative ones).
	 * @param speedY
	 *            Speed forward (for positive values) or backward (for negative
	 *            ones).
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
		robot.move((int) speedX, (int) speedY);
	}

	/**
	 * A general move function where you specify a clockwise angle from the
	 * front of the robot to move at.<br/>
	 * NOTE: this movement will complete almost immediately
	 * 
	 * @param angle
	 *            Angle, in radians (0 to 2*PI)
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
		double speedX = 70 * Math.sin(angle);
		double speedY = 70 * Math.cos(angle);
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
				safeSleep(42);
			} catch (InterruptedException e) {
				System.out.println("Failed to sleep");
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
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #moveTo(double, double)
	 * @see #waitForCompletion()
	 */
	public synchronized boolean moveToAndStop(double x, double y) {
		MoverConfig movement = new MoverConfig();
		movement.x = x;
		movement.y = y;
		movement.mode = Mode.MOVE_TO_STOP;

		if (!pushMovement(movement))
			return false;

		// Let the mover know it has a new job
		jobSem.release();
		return true;
	}

	/**
	 * Starts moving to the direction of the point<br/>
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
	 * @param avoidBall
	 *            Should A* avoid the ball.
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #waitForCompletion()
	 */
	public synchronized boolean moveToAStar(double x, double y,
			boolean avoidBall) {
		MoverConfig movement = new MoverConfig();
		movement.x = x;
		movement.y = y;
		movement.avoidBall = true;
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
	 * @return true if the move was successfully queued, false otherwise
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
	 * Stops the robot
	 * 
	 * @return true if the move was successfully queued, false otherwise
	 * 
	 * @see #waitForCompletion()
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
	 * Makes the robot kick
	 * 
	 * @return true if the move was successfully queued, false otherwise
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

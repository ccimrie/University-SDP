package strategy.movement;

import strategy.calculations.DistanceCalculator;
import vision.WorldState;
import world.state.Ball;
import world.state.Robot;
import world.state.RobotController;

/**
 * A movement class, that provides calculations for
 * different move commands for the robot.
 * 
 * @author Jakov Smelkin
 */
public class Movement {

	private WorldState worldState;
	private RobotController robot;
	private Robot us;
	private static int DIST_TH = 30;

	/**
	 * Constructor for the movement class
	 * 
	 * @param worldState
	 *            a world state from the vision, giving us information on robots, ball etc.
	 * @param robot
	 *            A {@link world.state.RobotController} class that prepares byte commands to the robot.
	 */
	public Movement(WorldState worldState, RobotController robot) {
		super();
		this.worldState = worldState;
		this.robot = robot;
		us = worldState.ourRobot;
	}

	/**
	 * A general move function as seen from the position of the robot.</br>
	 * Speeds take values between -100 and 100.</br>
	 * 
	 * @param speedX
	 *            Speed right (for positive values) or left (for negative ones).
	 * @param speedY
	 *            Speed forward (for positive values) or backward (for negative ones).
	 */
	public void move(double speedX, double speedY) {
		robot.move((int) speedX, (int) speedY);
	}

	/**
	 * A general move function where you specify a clockwise
	 * angle from the front of the robot to move at.
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
	 * Moves to a point on a video stream, (within a ceratain margin)
	 * 
	 * @param x
	 *            Move to position x units down from top left corner of the video feed
	 * @param y
	 *            Move to position y units left from top left corner of the video feed
	 * @throws InterruptedException
	 *             when Thread.sleep() is interrupted.
	 */
	public void moveToPoint(double x, double y) throws InterruptedException {

		double theta;
		// TODO: Remove the i iterations once it is tested properly.
		double xt, yt, xtc, ytc;
		int i = 0;
		System.out.println("--------------");
		while (DistanceCalculator.Distance(us.x, us.y, x, y) > DIST_TH && i < 10) {
			/*
			 * We make a vector (xt, yt) pointing from the robot to point,
			 * then use rotational transformation to put it in robots perspective,
			 * then normalise the speeds to a scale of 0-100. Stops when reaches the point.
			 */
			xtc = x - us.x; //
			ytc = y - us.y; // To do a mirror transformation
			theta = us.bearing;

			System.out.println("Iteration: " + i);
			System.out.println("xt: " + xtc);
			System.out.println("yt: " + ytc);
			System.out.println("theta: " + Math.toDegrees(theta));

			xt = Math.sin(theta);
			yt = -Math.cos(theta);

			double dotProductForward = xt * xtc + yt * ytc;
			
			xt = Math.sin(theta + Math.PI / 2.0);
			yt = -Math.cos(theta + Math.PI / 2.0);

			double dotProductEast = xt * xtc + yt * ytc;
			double angle = Math.acos(dotProductForward / (Math.sqrt(xtc * xtc + ytc * ytc) * Math.sqrt(xt * xt + yt * yt)));
			if (dotProductEast < 0)
				angle = -angle;
			System.out.println(Math.toDegrees(angle));

			move(angle);
			i++;
			Thread.sleep(100);
		}
		robot.stop();
	}

}

package strategy.planning;

import movement.RobotMover;
import strategy.calculations.DistanceCalculator;
import utility.SafeSleep;
import vision.Position;
import world.state.Ball;
import world.state.Robot;
import world.state.WorldState;

public class InterceptBall2 extends StrategyInterface {
	private static final double distanceThreshold = 20;
	private static final double angleThreshold = Math.toRadians(15);
	private Ball ball;
	private final double initx;
	private final double inity;
	private Robot us;
	private int ourRobot = 2;
	// private DribbleBall5 dribble = new DribbleBall5();
	private Offense42 offence42;

	public InterceptBall2(WorldState world, RobotMover mover) {
		super(world, mover);
		this.ball = world.ball;
		initx = ball.x;
		inity = ball.y;
		us = world.getOurRobot();
		// For use with the possession manager.
		if (world.areWeBlue())
			ourRobot = 1;
		else
			ourRobot = 2;
	}

	@Override
	public void run() {
		System.out.println("InterceptBall started");
		/*
		 * This while loop waits until the other robot kicks the ball, before
		 * going into intercept mode. Comment it out if you don't want to use
		 * it.
		 */
		/*
		 * double change = Math.abs(initx-ball.x) + Math.abs(inity-ball.y);
		 * while (change < 10){ try { SafeSleep.sleep(10); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } change = Math.abs(initx-ball.x) +
		 * Math.abs(inity-ball.y); }
		 * System.out.println("BALL MOVED! Intercept it!");
		 */
		double ballTurnAngle;

		try {
			System.out.println("shouldidie: " + shouldidie);
			System.out.println("Strategy.alldie: " + Strategy.alldie);

			double targetX = (world.areWeOnLeft()) ? 60 : 560;
			while (!shouldidie && !Strategy.alldie) {
				Position projBallPos = world.projectedBallPos();

				double distance = DistanceCalculator.Distance(world.ourRobot.x, world.ourRobot.y, targetX, projBallPos.getY());
				if (distance > distanceThreshold) {
					double xDiff = targetX - world.ourRobot.x;
					double yDiff = projBallPos.getY() - world.ourRobot.y;
					double speedForward = 0, speedRight = 0;
					// our robot is more than the threshold to the left of the target X
					if (xDiff > distanceThreshold) {
						speedForward = -100;
					} else {
						speedForward = 0;
					}
					// We are below where we need to be
					if (yDiff < -distanceThreshold) {
						speedRight = 100;
					}
					// We are above where we need to be
					else if (yDiff > distanceThreshold) {
						speedRight = -100;
					}
					// We are where we need to be
					else {
						speedForward = 100;
						speedRight = 0;
					}
					// Slow down as we approach to avoid overshooting
					if (Math.abs(yDiff) > 2.0 * distanceThreshold) {
						mover.setSpeedCoef(1.0);
					} else {
						mover.setSpeedCoef(0.7);
					}
					// Transform speeds to world coordinates instead of local, in case our robot is not facing the enemy
					// goal
					double angle = world.ourRobot.bearing - Math.PI * 1.5;
					double cos = Math.cos(angle), sin = Math.sin(angle);
					mover.move(speedRight * cos - speedForward * sin, speedForward * cos + speedRight * sin);
				} else {
					mover.resetQueue();
					mover.stopRobot();
					mover.moveTowards(projBallPos.getX(), projBallPos.getY());
				}
				SafeSleep.sleep(50);
				mover.setSpeedCoef(1.0);
				// Proceed to dribble if we have the ball
				if (world.distanceToBall() < 70)
					break;
			}
			System.out.println("Exited intercept part");
			// In case we don't intercept the ball in time, ie it hits us on
			// the side, start dribbleball5 from the previous milestone
			// immediately.

			// Check if the ball is closer to our goal then us.
			/*
			 * if (Math.abs(ball.x - world.getOurGoal().getX()) > Math.abs(us.x - world.getOurGoal().getX())) {
			 * // dribble.dribbleBall(world, mover);
			 * } else {
			 * 
			 * // After we are on the trajectory, rotate to face the ball.
			 * // and start moving towards it.
			 * ballTurnAngle = Math.toRadians(TurnToBall.Turner(us, ball));
			 * mover.rotate(ballTurnAngle);
			 * mover.waitForCompletion();
			 * mover.move(0, 100);
			 * while (world.whoHasTheBall() != ourRobot && !shouldidie && !Strategy.alldie) {
			 * SafeSleep.sleep(50);
			 * }
			 * mover.stopRobot();
			 * 
			 * // dribble.dribbleBall(world, mover);
			 * offence42 = new Offense42(world, mover);
			 * offence42.run();
			 * }
			 */
			offence42 = new Offense42(world, mover);
			offence42.run();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
		System.out.println("InterceptBall ended");
	}
}

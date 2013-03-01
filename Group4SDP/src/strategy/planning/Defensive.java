package strategy.planning;

import strategy.movement.Movement;
import strategy.movement.TurnToBall;
import vision.Position;
import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

//The defensive strategy is triggered when the ball (and the enemy robot) are in our part of 
//the pitch.

public class Defensive extends StrategyInterface implements Runnable {

	public Defensive(WorldState world, Robot us, Robot them, RobotController rc) {
		super(world, us, them, rc);
	}

	private final int threshold = 50;

	public void run() {
		System.out.println("Defensive strategy activated");
		// Sanity check
		// if (!Possession.hasPossession(world, RobotType.Them))
		// return;

		// Determine which goal is ours
		Position ourGoalTop;
		Position ourGoalCenter;
		Position ourGoalBottom;
		Position ourGoalDefendPosition;
		if (world.areWeOnLeft()) {
			ourGoalCenter = world.goalInfo.getLeftGoalCenter();
			ourGoalTop = world.goalInfo.getLeftGoalTop();
			ourGoalBottom = world.goalInfo.getLeftGoalBottom();
			ourGoalDefendPosition = new Position(ourGoalCenter.getX()
					+ threshold, ourGoalCenter.getY());
		} else {
			ourGoalCenter = world.goalInfo.getRightGoalCenter();
			ourGoalTop = world.goalInfo.getRightGoalTop();
			ourGoalBottom = world.goalInfo.getRightGoalBottom();
			ourGoalDefendPosition = new Position(ourGoalCenter.getX()
					- threshold, ourGoalCenter.getY());
		}

		Movement move = new Movement(world, rc);
		Thread moverthr = new Thread(move, "I'm a mover thread");

		// Defining auxiliary parameters used for calculating
		double angle, ythreshold, destY;
		double previousTheirBearing = 0;
		double theirBearing;

		try {

			// TODO: if they don't have possession, this strategy shouldn't even
			// be running!!!
			// In case they have possession of the ball, our robot goes to
			// defend the goal
			// if (Possession.hasPossession(world, RobotType.Them)) {
			move.setUpMoveToPoint((ourGoalCenter.getX() + threshold),
					ourGoalCenter.getY());
			System.out.println("Left: x "
					+ (int) (ourGoalCenter.getX() + threshold) + " y "
					+ (int) ourGoalCenter.getY());

			moverthr.start();
			// Complete the command before returning control to the thread.
			moverthr.join();

			System.out.println("Point reached");

			// Now turn to face the ball (and the other robot correspondingly)

			while (!Strategy.alldie && !shouldidie) {
				TurnToBall.Turner(us, world.ball);
				// Calculating the ball kicking line
				// Given their bearing, calculate where exactly in our goal they
				// are aiming

				theirBearing = them.bearing;

				if (Math.abs(previousTheirBearing - theirBearing) < 15) {
					angle = Math.abs(270 - theirBearing);
					ythreshold = (them.x - ourGoalCenter.getX())
							* Math.tan(angle);

					// Upper half of the field
					if (them.y < ourGoalCenter.getY()) {
						destY = them.y + ythreshold;
					} else
						destY = them.y - ythreshold;
					try {
						move.setUpMoveToPoint(
								(ourGoalCenter.getX() + threshold), destY);
						System.out.println("Left: x "
								+ (int) (ourGoalCenter.getX() + threshold)
								+ " y " + (int) ourGoalCenter.getY());

						moverthr = new Thread(move, "I'm a mover thread");
						moverthr.start();
						moverthr.join();
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					previousTheirBearing = theirBearing;

					// The kick line of the attacking robot is calculated, the
					// target point on the y axis is destY

					/** Don't delete for future strategy */
					// In case the other robot is aiming for a point inside the
					// goal
					/*
					 * if((destY < ourGoalBottom.getY()) && (destY >
					 * ourGoalTop.getY())){ We have to move to the point with
					 * coordinates (ourGoalcenter.getX(), destY)
					 */
				}
				// else {
				// System.out.println("Enter ricochet defence mode");
				// Enter the ricochet strategy
				// }
				// TODO: as above
				/*
				 * } else { move.setUpMoveToPoint(world.ball.x, world.ball.y);
				 * 
				 * moverthr = new Thread(move, "I'm a mover thread");
				 * moverthr.start(); moverthr.join();
				 * System.out.println("Ball reached"); }
				 */
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

package strategy.planning;

import movement.RobotMover;
import strategy.calculations.Possession;
import strategy.movement.TurnToBall;
import vision.Position;
import world.state.RobotType;
import world.state.WorldState;

//The defensive strategy is triggered when the ball (and the enemy robot) are in our part of 
//the pitch.

public class Defensive extends StrategyInterface implements Runnable {

	public Defensive(WorldState world, RobotMover mover) {
		super(world, mover);
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

		// Defining auxiliary parameters used for calculating
		double angle, ythreshold, destY;
		double previousTheirBearing = 0;
		double theirBearing;

		try {

			// TODO: if they don't have possession, this strategy shouldn't even
			// be running!!!
			// In case they have possession of the ball, our robot goes to
			// defend the goal
			if (Possession.hasPossession(world, RobotType.Them)) {

				System.out.println("Left: x "
						+ (int) (ourGoalCenter.getX() + threshold) + " y "
						+ (int) ourGoalCenter.getY());

				// Move to our goal
				// synchronized is needed to call mover.wait(), any movement
				// commands should also be inside a synchronized block if wait
				// is called, otherwise weird things will happen
				synchronized (mover) {
					mover.moveTo(ourGoalDefendPosition.getX(),
							ourGoalDefendPosition.getY());

					// Complete the command before returning control to the
					// thread.
					mover.wait();
				}

				System.out.println("Point reached");

				// Now turn to face the ball (and the other robot
				// correspondingly)

				while (!Strategy.alldie && !shouldidie) {
					TurnToBall.Turner(world.ourRobot, world.ball);
					// Calculating the ball kicking line
					// Given their bearing, calculate where exactly in our goal
					// they are aiming

					theirBearing = world.theirRobot.bearing;

					if (Math.abs(previousTheirBearing - theirBearing) < 15) {
						// TODO: fix the angle to account for both sides of the
						// pitch - we don't need (massive) separate cases
						// either! something in the if (world.areWeOnLeft())
						// above will do
						angle = Math.abs(270 - theirBearing);
						ythreshold = (world.theirRobot.x - ourGoalCenter.getX())
								* Math.tan(angle);

						// Upper half of the field
						if (world.theirRobot.y < ourGoalCenter.getY()) {
							destY = world.theirRobot.y + ythreshold;
						} else
							destY = world.theirRobot.y - ythreshold;

						synchronized (mover) {
							mover.moveTo(ourGoalDefendPosition.getX(), destY);
							mover.wait();
						}
						previousTheirBearing = theirBearing;

						// The kick line of the attacking robot is calculated,
						// the
						// target point on the y axis is destY

						/** Don't delete for future strategy */
						// In case the other robot is aiming for a point inside
						// the
						// goal
						/*
						 * if((destY < ourGoalBottom.getY()) && (destY >
						 * ourGoalTop.getY())){ We have to move to the point
						 * with coordinates (ourGoalcenter.getX(), destY)
						 */
					}
					// else {
					// System.out.println("Enter ricochet defence mode");
					// Enter the ricochet strategy
					// }
				}
			} else {
				synchronized (mover) {
					mover.moveTo(world.ball.x, world.ball.y);
					mover.wait();
				}
				System.out.println("Ball reached");
			}

		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

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

	private final int threshold = 30;

	public void run() {

		System.out.println("Defensive strategy activated");

		/** Determine which goal is ours and set the goal constants */
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
			System.out.println("We are on the left");
		} else {
			ourGoalCenter = world.goalInfo.getRightGoalCenter();
			ourGoalTop = world.goalInfo.getRightGoalTop();
			ourGoalBottom = world.goalInfo.getRightGoalBottom();
			ourGoalDefendPosition = new Position(ourGoalCenter.getX()
					- threshold, ourGoalCenter.getY());
			System.out.println("We are on the right");
		}

		// Defining auxiliary parameters used for calculating
		double angle, ythreshold, destY;
		double previousTheirBearing = 0;
		double theirBearing;

		try {

			// TODO: Main planner has to handle this -In case they have
			// possession of the ball, our robot goes to defend the goal
			if (Possession.hasPossession(world, RobotType.Them)) {
				System.out.println("They have the posession");
				System.out.println("Left: x "
						+ (int) (ourGoalDefendPosition.getX()) + " y "
						+ (int) ourGoalDefendPosition.getY());

				// Move to our goal
				// Synchronized is needed to call mover.wait(), any movement
				// commands should also be inside a synchronized block if wait
				// is called, otherwise weird things will happen
				synchronized (mover) {
					mover.moveToAndStop(ourGoalDefendPosition.getX(),
							ourGoalDefendPosition.getY());
					// Complete the command before returning control to the
					// thread.
					mover.wait();
				}

				System.out.println("Point reached");

				while (!Strategy.alldie && !shouldidie) {

					/**
					 * Now turn to face the ball (and the other robot
					 * correspondingly)
					 */
					angle = TurnToBall.turner(world.ourRobot, world.theirRobot.x, world.theirRobot.y);
					
					if (Math.abs(angle) > 15) {
						System.out.println("Should turn now");
						synchronized (mover) {
							mover.rotate(Math.toRadians(angle));
							mover.wait();
						}
					}

					/**
					 * Given their bearing, calculate where exactly in our goal
					 * they are aiming.
					 */

					theirBearing = Math.toDegrees(world.theirRobot.bearing);

					// First check if it the initial run, then check if the
					// angle has changed due to fluctuation and if it has
					// stabilised
					if ((previousTheirBearing == 0)
							|| ((Math.abs(previousTheirBearing - theirBearing) > 5) && (Math
									.abs(previousTheirBearing - theirBearing) < 15))) {
						if (world.areWeOnLeft()) {
							angle = Math.abs(270 - theirBearing);
							double tan = Math.tan(Math.toRadians(angle));
							ythreshold = (world.theirRobot.x - ourGoalCenter
									.getX()) * tan;
							ythreshold -= Math.abs(world.theirRobot.y
									- world.ourRobot.y);
						} else {
							angle = Math.abs(90 - theirBearing);
							ythreshold = (ourGoalCenter.getX() - world.theirRobot.x)
									* Math.tan(Math.toRadians(angle));
							ythreshold -= Math.abs(world.theirRobot.y
									- world.ourRobot.y);
						}

						// Upper half of the field
						if (world.theirRobot.y < ourGoalCenter.getY())
							destY = world.ourRobot.y + ythreshold;
						else
							// Lower half of the field
							destY = world.ourRobot.y - ythreshold;

						// Move to the right place on the Y axis
						synchronized (mover) {
							mover.moveToAndStop(ourGoalDefendPosition.getX(),
									destY);
							mover.wait();
						}
						// Turn to face the ball
						angle = TurnToBall.turner(world.ourRobot, world.theirRobot.x, world.theirRobot.y);

						if (Math.abs(angle) > 15) {
							System.out.println("Should turn now");
							synchronized (mover) {
								mover.rotate(Math.toRadians(angle));
								mover.wait();
							}
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
				// TODO: The Main planner should handle this
				System.out.println("The other team does not have posession.");
				System.out.println("Hence, we're moving to the ball. X: "
						+ world.ball.x + " Y: " + world.ball.y);
				synchronized (mover) {
					mover.moveToAndStop(world.ball.x, world.ball.y);
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

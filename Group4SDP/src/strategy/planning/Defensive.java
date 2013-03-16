package strategy.planning;

import movement.RobotMover;
import strategy.calculations.Possession;
import utility.SafeSleep;
import vision.Position;
import world.state.RobotType;
import world.state.WorldState;

//The defensive strategy is triggered when the ball (and the enemy robot) are in our part of 
//the pitch.

public class Defensive extends StrategyInterface {

	public Defensive(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	private final int threshold = 30;

	@Override
	public void run() {

		System.out.println("Defensive strategy activated");
		if (!Possession.hasPossession(world, RobotType.Them)) {
			System.out
					.println("The other team does not have posession of the ball, kill the defence strategy");
			return;
		}
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

			System.out.println("They have the posession");
			System.out.println("Left: x "
					+ (int) (ourGoalDefendPosition.getX()) + " y "
					+ (int) ourGoalDefendPosition.getY());

			// Move to our goal
			mover.moveToAndStop(ourGoalDefendPosition.getX(),
					ourGoalDefendPosition.getY());
			mover.waitForCompletion();

			System.out.println("Point reached");
			System.out.println(!Strategy.alldie);
			System.out.println(!shouldidie);
			while (!Strategy.alldie && !shouldidie) {
				System.out.println("While loop");
				/**
				 * Now turn to face the ball (and the other robot
				 * correspondingly)
				 */
				angle = Math.toDegrees(world.theirRobot.bearing + Math.PI
						- world.ourRobot.bearing);
				if (angle > 180)
					angle -= 360;
				
				if (Math.abs(angle) > 7) {
					System.out.println("Angle to turn initially " + angle);
					System.out.println("Should turn now");
					mover.rotate(Math.toRadians(angle));
					mover.waitForCompletion();
					if (shouldidie)
						return;
				}

				/**
				 * Given their bearing, calculate where exactly in our goal they
				 * are aiming.
				 */

				theirBearing = Math.toDegrees(world.theirRobot.bearing);

				// First check if it the initial run, then check if the
				// angle has changed due to fluctuation and if it has
				// stabilised
				if ((previousTheirBearing == 0)
						|| ((Math.abs(previousTheirBearing - theirBearing) > 5) && (Math
								.abs(previousTheirBearing - theirBearing) < 10))) {
					if (world.areWeOnLeft()) {
						angle = Math.abs(270 - theirBearing);
						double tan = Math.tan(Math.toRadians(angle));
						ythreshold = (world.theirRobot.x - ourGoalCenter.getX())
								* tan;
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
					System.out.println("Moving on the y axis");
					mover.moveToAndStop(ourGoalDefendPosition.getX(), destY);
					mover.waitForCompletion();
					if (shouldidie)
						return;

//					/**
//					 * Now turn to face the ball (and the other robot
//					 * correspondingly)
//					 */
//					angle = Math.toDegrees(world.theirRobot.bearing + Math.PI
//							- world.ourRobot.bearing);
//					if (angle > 180)
//						angle -= 360;
//					System.out.println("Second angle turn " + angle);
//					if (Math.abs(angle) > 5) {
//						System.out.println("Should turn now");
//						mover.rotate(Math.toRadians(angle));
//						mover.waitForCompletion();
//					if (shouldidie)
//						return;
//					}

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
					 * ourGoalTop.getY())){ We have to move to the point with
					 * coordinates (ourGoalcenter.getX(), destY)
					 */
				} else {
					SafeSleep.sleep(50);
				}
			}

		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

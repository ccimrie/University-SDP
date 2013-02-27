package strategy.planning;

import strategy.calculations.Possession;
import strategy.movement.Movement;
import strategy.movement.TurnToBall;
import vision.Position;
import vision.WorldState;
import world.state.Ball;
import world.state.Robot;
import world.state.RobotController;
import world.state.RobotType;

//The defensive strategy is triggered when the ball (and the enemy robot) are in our part of 
//the pitch.

public class Defensive extends StrategyInterface implements Runnable {
	private static int INTERVAL = 50;

	public Defensive(WorldState world, Robot us, Robot them, RobotController rc) {
		super(world, us, them, rc);
	}

	private final int threshold = 80;
	private Movement move = null;

	Position ourGoalTop;
	Position ourGoalCenter;
	Position ourGoalBottom;

	@Override
	public void kill() {
		super.kill();
		// Propagate the thread stop to the movement thread
		if (move != null && move.isAlive())
			move.die();
	}

	public void run() {
		System.out.println("Defensive strategy activated");

		// Determine which goal is ours
		if (world.areWeOnLeft()) {
			ourGoalCenter = world.goalInfo.getLeftGoalCenter();
			ourGoalTop = world.goalInfo.getLeftGoalTop();
			ourGoalBottom = world.goalInfo.getLeftGoalBottom();
		} else {
			ourGoalCenter = world.goalInfo.getRightGoalCenter();
			ourGoalTop = world.goalInfo.getRightGoalTop();
			ourGoalBottom = world.goalInfo.getRightGoalBottom();
		}

		System.out.println("Our goal: (" + (ourGoalCenter.getX() + threshold)
				+ ", " + ourGoalCenter.getY() + ")");

		while (!shouldidie && !Strategy.alldie) {
			System.out.println("Defensive strategy iteration");
			try {
				// If they don't have the ball, defense should not be running,
				// so wait for the planner to kill the thread
				if (!Possession.hasPossession(world, RobotType.Them)) {
					Thread.sleep(INTERVAL);
					continue;
				}

				// Move to our goal
				// move = new Movement(world, rc, ourGoalCenter.getX()
				// + threshold, ourGoalCenter.getY(), 0, 0, 0.0, 3);
				// move.start();

				// Now turn to face the ball (and the other robot
				// correspondingly)
				TurnToBall.Turner(us, world.ball);
				// Calculating the ball kicking line
				// Given their bearing, calculate where exactly in our goal
				// they are aiming

				double theirBearing = them.bearing;

				// angle = Math.abs(270 - theirBearing);
				// ythreshold = (them.x - ourGoalCenter.getX()) *
				// Math.tan(angle);
				//
				// // Upper half of the field
				// if (them.y < ourGoalCenter.getY()) {
				// destY = them.y + ythreshold;
				// } else
				// destY = them.y - ythreshold;
				// Movement move = new Movement(world, rc,
				// (int) (ourGoalCenter.getX() + threshold), (int) destY,
				// 0, 0, 0.0, 3);
				// System.out.println("Left: x "
				// + (int) (ourGoalCenter.getX() + threshold) + " y "
				// + (int) ourGoalCenter.getY());
				// Thread moverthr = new Thread(move, "I'm a mover thread");
				// moverthr.start();
				//
				// while (moverthr.isAlive()) {
				// Thread.sleep(50);
				// }
				// previousTheirBearing = theirBearing;

				// The kick line of the attacking robot is calculated,
				// the
				// target point on the
				// y axis is destY
				/** Don't delete for future strategy */
				// In case the other robot is aiming for a point inside
				// the
				// goal
				// if((destY < ourGoalBottom.getY()) && (destY >
				// ourGoalTop.getY())){
				// We have to move to the point with coordinates
				// (ourGoalcenter.getX(), destY)

				// pos stands for the position behind the ball
				// Vector pos;
				//
				// if (world.areWeOnLeft()) {
				// pos = new Vector(ball.getPosition().getX() - 20, ball
				// .getPosition().getY());
				// } else {
				// pos = new Vector(ball.getPosition().getX() + 20, ball
				// .getPosition().getY());
				// }

				// if(Vector.distanceSquared(pos, us.getPosition()) <= 100){
				//
				// double angle = TurnToBall.Turner(us, ball);
				// server.rotate(-angle);
				// while(server.isMoving()){
				// System.out.println("Fuck loops");
				// }
				// //world.deleteObservers();
				// //server.stop();
				//
				// } else if(((us.getPosition().getX() <
				// ball.getPosition().getX()
				// -5) && world.areWeOnLeft()) || ((us.getPosition().getX() >
				// ball.getPosition().getX() + 5) && !world.areWeOnLeft())){
				//
				// GoToPoint.goToPoint(world, server, pos,
				// AvoidanceStrategy.IgnoringBall);
				//
				// } else {
				//
				// GoToPoint.goToPoint(world, server, pos,
				// AvoidanceStrategy.AvoidingBall);
				//
				// }
			}

			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

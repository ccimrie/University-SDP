package strategy.planning;

import geometry.Vector;
import strategy.calculations.Possession;
import strategy.movement.Movement;
import strategy.movement.TurnToBall;
import vision.WorldState;
import world.state.Ball;
import world.state.PitchInfo;
import world.state.Robot;
import world.state.RobotController;
import world.state.RobotType;

//The defensive strategy is triggered when the ball (and the enemy robot) are in our part of 
//the pitch.

public class Defensive extends StrategyInterface implements Runnable {
	private static int INTERVAL = 50;

	public Defensive(WorldState world, Robot us, Robot them, RobotController rc) {
		super(world, us, them, rc);
		this.ball = world.ball;
	}

	private Ball ball;
	private static final int threshold = 80;
	private Movement moveThread = null;

	Vector ourGoalTop;
	Vector ourGoalCenter;
	Vector ourGoalBottom;

	@Override
	public void kill() {
		super.kill();
		// Propagate the thread stop to the movement thread
		if (moveThread != null && moveThread.isAlive())
			moveThread.die();
	}

	public void run() {
		while (!shouldidie && !Strategy.alldie) {
			try {
				// If they don't have the ball, defense should not be running
				if (!Possession.hasPossession(world, RobotType.Them)) {
					Thread.sleep(INTERVAL);
					continue;
				}

				// Defining auxiliary parameters used for calculating

				// Determine which goal is ours
				if (world.areWeOnLeft()) {
					ourGoalCenter = PitchInfo.getLeftGoalCentreSide();
					ourGoalTop = PitchInfo.getLeftGoalTop();
					ourGoalBottom = PitchInfo.getLeftGoalBottom();
				} else {
					ourGoalCenter = PitchInfo.getRightGoalCentreSide();
					ourGoalTop = PitchInfo.getRightGoalTop();
					ourGoalBottom = PitchInfo.getRightGoalBottom();
				}

				// Move to our goal
				Movement move = new Movement(world, rc,
						(int) ourGoalCenter.getX() + threshold,
						(int) ourGoalCenter.getY(), 0, 0, 0.0, 3);
				System.out.println("Our goal: x " + (int) ourGoalCenter.getX()
						+ threshold + " y " + (int) ourGoalCenter.getY());
				// Thread moverThread = new Thread(move, "moverThread");
				// moverThread.start();
				// while (moverThread.isAlive()) {
				// Thread.sleep(INTERVAL);
				// }

				// Now turn to face the ball (and the other robot
				// correspondingly)
				TurnToBall.Turner(us, ball);
				// Calculating the ball kicking line
				// Given their bearing, calculate where exactly in our goal
				// they are aiming

				double theirBearing = them.bearing;

				angle = Math.abs(270 - theirBearing);
				ythreshold = (them.x - ourGoalCenter.getX()) * Math.tan(angle);

				// Upper half of the field
				if (them.y < ourGoalCenter.getY()) {
					destY = them.y + ythreshold;
				} else
					destY = them.y - ythreshold;
				Movement move = new Movement(world, rc,
						(int) (ourGoalCenter.getX() + threshold), (int) destY,
						0, 0, 0.0, 3);
				System.out.println("Left: x "
						+ (int) (ourGoalCenter.getX() + threshold) + " y "
						+ (int) ourGoalCenter.getY());
				Thread moverthr = new Thread(move, "I'm a mover thread");
				moverthr.start();

				while (moverthr.isAlive()) {
					Thread.sleep(50);
				}
				previousTheirBearing = theirBearing;

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

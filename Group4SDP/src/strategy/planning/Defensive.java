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

	public Defensive(WorldState world, Robot us, Robot them, RobotController rc) {
		super(world, us, them, rc);
		this.ball = world.ball;
	}

	private Ball ball;
	private static final double threshold = 80;

	// The upper and lower bounds stay for the upper and lower y coordinates of
	// the goal

	public void run() {
		double previousTheirBearing = 0;
		double theirBearing;

		while (!Strategy.alldie && !shouldidie) {

			// Defining auxiliary parameters used for calculating

			double angle, ythreshold, destY;

			Vector ourGoalCenter, ourGoalTop, ourGoalBottom;

			/** IF OUR GOAL IS ON THE LEFT OF THE PITCH WE DEFEND IT */
			if (world.areWeOnLeft()) {
				ourGoalCenter = PitchInfo.getLeftGoalCentreSide();
				ourGoalTop = PitchInfo.getLeftGoalTop();
				ourGoalBottom = PitchInfo.getLeftGoalBottom();
				// In case they have possession of the ball, our robot goes to
				// defend the door
				if (Possession.hasPossession(world, RobotType.Them)) {
					try {
						Movement move = new Movement(world, rc);
						move.setUpMoveToPoint((ourGoalCenter.getX() + threshold),
								ourGoalCenter.getY());
						System.out.println("Left: x "
								+ (int) (ourGoalCenter.getX() + threshold)
								+ " y " + (int) ourGoalCenter.getY());
						Thread moverthr = new Thread(move, "I'm a mover thread");
						moverthr.start();
						moverthr.join(); //Complete the command before returning control
						//to the thread.

						System.out.println("Point reached Left");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Now turn to face the ball (and the other robot
					// correspondingly)
					TurnToBall.Turner(us, ball);
					// Calculating the ball kicking line
					// Given their bearing, calculate where exactly in our goal
					// they are aiming

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
							Movement move = new Movement(world, rc);
							move.setUpMoveToPoint((ourGoalCenter.getX() + threshold),
									destY);
							System.out.println("Left: x "
									+ (int) (ourGoalCenter.getX() + threshold)
									+ " y " + (int) ourGoalCenter.getY());
							Thread moverthr = new Thread(move,
									"I'm a mover thread");
							moverthr.start();
							moverthr.join();
						} catch (InterruptedException e) {

							e.printStackTrace(); }
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
					}
					// else {
					// System.out.println("Enter rikushet defence mode");
					// Enter the rikushet strategy
					// }
				} else {
					try {
						Movement move = new Movement(world, rc);
						move.setUpMoveToPoint(ball.x,ball.y);
						Thread moverthr = new Thread(move, "I'm a mover thread");
						moverthr.start();
						moverthr.join();
						System.out.println("Ball reached");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					}
				}
			} else {
				ourGoalCenter = PitchInfo.getRightGoalCentreSide();
				ourGoalTop = PitchInfo.getRightGoalTop();
				ourGoalBottom = PitchInfo.getRightGoalBottom();
				// In case they have possession of the ball, our robot goes to
				// defend the goal
				if (Possession.hasPossession(world, RobotType.Them)) {
					try {
						Movement move = new Movement(world, rc);
						move.setUpMoveToPoint((ourGoalCenter.getX() - threshold),
								ourGoalCenter.getY());
						System.out.println("Right: x "
								+ (int) (ourGoalCenter.getX() + threshold)
								+ " y " + (int) ourGoalCenter.getY());
						Thread moverthr = new Thread(move, "I'm a mover thread");
						moverthr.start();
						moverthr.join();
						System.out.println("Point reached Right");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Now turn to face the ball (and the other robot
					// correspondingly)
					TurnToBall.Turner(us, ball);
					// Calculating the ball kicking line
					// Given their bearing, calculate where exactly in our goal
					// they are aiming
					theirBearing = them.bearing;
					if (Math.abs(previousTheirBearing - theirBearing) < 15) {
						angle = Math.abs(90 - theirBearing);
						ythreshold = (ourGoalCenter.getX() - them.x)
								* Math.tan(angle);
						// Upper half of the field
						if (them.y < ourGoalCenter.getY()) {
							destY = them.y + ythreshold;
						} else
							destY = them.y - ythreshold;
						try {
							Movement move = new Movement(world, rc);
							move.setUpMoveToPoint((ourGoalCenter.getX() - threshold),destY);
							System.out.println("Right: x "
									+ (int) (ourGoalCenter.getX() + threshold)
									+ " y " + (int) ourGoalCenter.getY());
							Thread moverthr = new Thread(move,
									"I'm a mover thread");
							moverthr.start();
							moverthr.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						previousTheirBearing = theirBearing;
					}

				} else {
					try {
						Movement move = new Movement(world, rc);
						move.setUpMoveToPoint(ball.x,ball.y);
						Thread moverthr = new Thread(move, "I'm a mover thread");
						moverthr.start();
						while (moverthr.isAlive()) {
							Thread.sleep(50);
						}
						System.out.println("Ball reached");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

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
			// } else if(((us.getPosition().getX() < ball.getPosition().getX()
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
	}

}

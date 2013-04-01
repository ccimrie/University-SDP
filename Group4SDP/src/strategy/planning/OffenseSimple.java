package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import world.state.Ball;
import world.state.Robot;
import world.state.WorldState;

public class OffenseSimple extends StrategyInterface {
	private Ball ball;
	private Robot us;

	public OffenseSimple(WorldState world, RobotMover mover) {
		super(world, mover);
		ball = world.ball;
		us = world.getOurRobot();
	}

	@Override
	public void run() {
		while (!(shouldidie || Strategy.alldie)) {
			double angle = mover.angleCalculator(us.x, us.y, ball.x, us.y, us.bearing);
			System.out.println("Angle to rotate: " + Math.toDegrees(angle));
			if (Math.abs(Math.toDegrees(angle)) >20){
				mover.rotate(angle);
				try {
					mover.waitForCompletion();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (world.distanceBetweenBallAndOurGoalX() < 150) {
				// Ball is very close to our goal
				if (ball.y < 240) {
					mover.moveToAStar(ball.x, ball.y + 70, true, true);
					try {
						mover.waitForCompletion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if ((shouldidie || Strategy.alldie))
						break;
					mover.moveTo(ball.x, ball.y - 50);
				} else {
					mover.moveToAStar(ball.x, ball.y - 70, true, true);
					try {
						mover.waitForCompletion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if ((shouldidie || Strategy.alldie))
						break;

					mover.moveTo(ball.x, ball.y + 50);
				}

			} else {
				// Position behind the ball
				boolean astarperformed = false; //Keep track of running a*
				if (world.areWeOnLeft()) {
					/*double slope = (242 - ball.y) / (610 - ball.x);
					if (ball.y > 240) {
						slope = -slope;
					}*/
					/*double value = 20;
					if (ball.y < 240) {
						value = -value;
						if (ball.y < 50) value=0;
					} else if (ball.y > 430) value = 0; */
					//check if we are not too close to the ball
					if (world.distanceBetweenUsAndBall() > 70 || us.x > ball.x-40){
						mover.moveToAStar(ball.x - 60, ball.y /*+ slope * 70*/, true, true);
					}
					// mover.delay(50);
					// RobotMover.distanceThreshold = 10;
					// mover.moveToAndStop(ball.x - 40, ball.y + slope * 60);

				} else {
					/*double slope = (244 - ball.y) / (28 - ball.x);
					if (ball.y < 240) {
						slope = -slope;
					}*/
					//double value = -20;
/*					if (ball.y < 240) {
						value = -value;
						if (ball.y < 50) value=0;
					} else if (ball.y > 430) value = 0; */
					//Check if we are not too close to the ball
					if (world.distanceBetweenUsAndBall() > 70 || us.x < ball.x + 40){
						mover.moveToAStar(ball.x + 60, ball.y /*+ 70 * slope*/, true, true);
					}
					// mover.delay(50);
					// RobotMover.distanceThreshold = 10;
					// mover.moveToAndStop(ball.x + 60, ball.y + slope * 60);
					if (astarperformed){
						try {
							mover.waitForCompletion();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (!(shouldidie || Strategy.alldie)) {					
					mover.move(0, 100);
					try {
						SafeSleep.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mover.moveTowards(world.getTheirGoal().getX(), world.getTheirGoal().getY());
					try {
						SafeSleep.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (world.distanceBetweenUsAndBall() < 40){
						mover.kick();
					}
					mover.stopRobot();
				}

			}
		}
	}
}

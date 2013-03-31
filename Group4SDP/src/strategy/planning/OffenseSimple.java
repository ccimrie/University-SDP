package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import world.state.Ball;
import world.state.WorldState;

public class OffenseSimple extends StrategyInterface {
	private Ball ball;

	public OffenseSimple(WorldState world, RobotMover mover) {
		super(world, mover);
		ball = world.ball;
	}

	@Override
	public void run() {
		while (!(shouldidie || Strategy.alldie)) {
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
					mover.moveTo(ball.x, ball.y);
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
					mover.moveTo(ball.x, ball.y);
				}

			} else {
				// Position behind the ball
				if (world.areWeOnLeft()) {
					double slope = (242 - ball.y) / (610 - ball.x);
					if (ball.y > 240) {
						slope = -slope;
					}
					mover.moveToAStar(ball.x - 40, ball.y + slope * 70, true, true);
					mover.delay(50);
					RobotMover.distanceThreshold = 10;
					mover.moveToAndStop(ball.x - 40, ball.y + slope * 60);
					try {
						mover.waitForCompletion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					double slope = (244 - ball.y) / (28 - ball.x);
					if (ball.y < 240) {
						slope = -slope;
					}
					mover.moveToAStar(ball.x + 70, ball.y + 70 * slope, true, true);
					mover.delay(50);
					RobotMover.distanceThreshold = 10;
					mover.moveToAndStop(ball.x + 60, ball.y + slope * 60);
					try {
						mover.waitForCompletion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!(shouldidie || Strategy.alldie)) {
					double angle = Math.toRadians(world.angleToBall());
					mover.rotate(angle);
					try {
						mover.waitForCompletion();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					mover.move(0, 100);
					try {
						SafeSleep.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mover.kick();
				}
			}
		}
	}
}

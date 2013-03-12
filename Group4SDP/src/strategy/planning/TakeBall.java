package strategy.planning;

import strategy.calculations.Possession;
import movement.RobotMover;
import utility.SafeSleep;
import vision.Position;
import world.state.Ball;
import world.state.RobotType;
import world.state.WorldState;

public class TakeBall extends StrategyInterface {
	private static final double distanceThreshold = 40;
	private static final double angleThreshold = Math.toRadians(15);
	public TakeBall(WorldState world, RobotMover mover) {
		super(world, mover);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		double ballTurnAngle;
		Ball ball = world.ball;
		System.out.println("[Take Ball] strategy activated");
		// finish if ball in not neutral any more
		if (!(world.whoHasTheBall() == -1)) {
			if (world.whoHasTheBall() == 1){
				System.out.println("[Take Ball] Our Team took  the ball" );
			}
			else if (world.whoHasTheBall() == 2){
			System.out.println("[Take Ball] The other team took the ball");
			}
			System.out.println("[Take Ball] finishing TakeBall Strategy");
			return;
		}else{
			// 1) take ball from normal position
			// 2) take ball from wall
			
			try {
				while (!shouldidie && !Strategy.alldie) {
					Position projBallPos = world.projectedBallPos();
					
					if (world.distanceBetweenUsAndBall() > distanceThreshold) {
						mover.moveTowards(projBallPos.getX(), projBallPos.getY());
						if (world.areWeOnLeft()) {
							double slope = (242 - ball.y) / (610 - ball.x);
							if (ball.y > 240){
								slope = -slope;
							}
							mover.moveToAStar(ball.x - 70, ball.y + slope * 70, true, false);
							mover.delay(50);
							RobotMover.distanceThreshold = 10;
							mover.moveToAndStop(ball.x - 60, ball.y + slope * 60);
							

						} else {
							double slope = (244 - ball.y) / (28 - ball.x);
							if (ball.y < 240){
								slope = -slope;
							}
							mover.moveToAStar(ball.x + 70, ball.y + 70 * slope, true, false);
							mover.delay(50);
							RobotMover.distanceThreshold = 10;
							mover.moveToAndStop(ball.x + 60, ball.y + slope * 60);
							
						}
					} else {
						ballTurnAngle = mover.angleCalculator(world.ourRobot.x,
								world.ourRobot.y, projBallPos.getX(),
								projBallPos.getY(), world.ourRobot.bearing);
						
						while (Math.abs(ballTurnAngle) > angleThreshold) {
							mover.rotate(ballTurnAngle);
							mover.waitForCompletion();
							if (shouldidie || Strategy.alldie)
								return;
							ballTurnAngle = mover.angleCalculator(world.ourRobot.x,
									world.ourRobot.y, projBallPos.getX(),
									projBallPos.getY(), world.ourRobot.bearing);
						}
					}
					mover.interrupt();
					SafeSleep.sleep(50);
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		
		
	}

	
}

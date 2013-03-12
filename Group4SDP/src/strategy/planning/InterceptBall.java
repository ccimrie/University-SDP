package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import vision.Position;
import world.state.Ball;
import world.state.WorldState;

public class InterceptBall extends StrategyInterface {
	private static final double distanceThreshold = 40;
	private static final double angleThreshold = Math.toRadians(15);
	

	public InterceptBall(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	/**
	 * Projects where the ball is going to be based on its current position and
	 * velocity
	 * 
	 * @return The projected position of the ball
	 */
	

	@Override
	public void run() {
		double ballTurnAngle;

		try {
			while (!shouldidie && !Strategy.alldie) {
				Position projBallPos = world.projectedBallPos();
				
				if (world.distanceBetweenUsAndBall() > distanceThreshold) {
					mover.moveTowards(projBallPos.getX(), projBallPos.getY());
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
				SafeSleep.sleep(50);
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

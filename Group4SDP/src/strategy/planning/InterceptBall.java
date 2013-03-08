package strategy.planning;

import movement.RobotMover;
import vision.Position;
import world.state.Ball;
import world.state.WorldState;

public class InterceptBall extends StrategyInterface {
	private static final double distanceThreshold = 10;
	private static final double angleThreshold = Math.toRadians(10);
	private static final double sqVelocityThreshold = 1.0;
	private static final double velocityFactor = 5.0;

	public InterceptBall(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	/**
	 * Projects where the ball is going to be based on its current position and
	 * velocity
	 * 
	 * @return The projected position of the ball
	 */
	private Position projectedBallPos() {
		Ball ball = world.ball;

		/*
		 * Don't bother projecting where the ball's going to be if it's barely
		 * moving
		 */
		if (ball.speedX * ball.speedX + ball.speedY * ball.speedY > sqVelocityThreshold) {
			double projX = ball.x + velocityFactor * ball.speedX;
			double projY = ball.y + velocityFactor * ball.speedY;
			return new Position((int) projX, (int) projY);
		} else
			return new Position((int) ball.x, (int) ball.y);
	}

	@Override
	public void run() {
		double ballTurnAngle;

		try {
			while (!shouldidie && !Strategy.alldie
					&& world.distanceBetweenUsAndBall() > distanceThreshold) {
				Position projBallPos = projectedBallPos();
				ballTurnAngle = mover.angleCalculator(world.ourRobot.x,
						world.ourRobot.y, projBallPos.getX(),
						projBallPos.getY(), world.ourRobot.bearing);
				while (Math.abs(ballTurnAngle) > angleThreshold) {
					mover.rotate(ballTurnAngle);
					mover.waitForCompletion();
					if (shouldidie || Strategy.alldie)
						return;
				}

				mover.moveTowards(projBallPos.getX(), projBallPos.getY());
				mover.delay(100);
				mover.waitForCompletion();
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

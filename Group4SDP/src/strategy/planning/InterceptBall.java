package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import vision.Position;
import world.state.WorldState;

public class InterceptBall extends StrategyInterface {
	private static final double distanceThreshold = 50;
	private static final double angleThreshold = Math.toRadians(15);

	public InterceptBall(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		System.out.println("InterceptBall started");
		double ballTurnAngle;

		try {
			System.out.println("shouldidie: " + shouldidie);
			System.out.println("Strategy.alldie: " + Strategy.alldie);
			while (!shouldidie && !Strategy.alldie) {
				Position projBallPos = world.projectedBallPos();

				if (world.distanceBetweenUsAndBall() > distanceThreshold) {
					System.out.println("Projected ball pos: (" + projBallPos.getX() + ", " + projBallPos.getY() + ")");
					mover.moveTowards(projBallPos.getX(), projBallPos.getY());
				} else {
					ballTurnAngle = mover.angleCalculator(world.ourRobot.x, world.ourRobot.y, projBallPos.getX(), projBallPos.getY(), world.ourRobot.bearing);

					while (Math.abs(ballTurnAngle) > angleThreshold) {
						mover.rotate(ballTurnAngle);
						mover.waitForCompletion();
						if (shouldidie || Strategy.alldie)
							return;
						ballTurnAngle = mover.angleCalculator(world.ourRobot.x, world.ourRobot.y, projBallPos.getX(), projBallPos.getY(), world.ourRobot.bearing);
					}
				}
				SafeSleep.sleep(50);
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
		System.out.println("InterceptBall ended");
	}
}

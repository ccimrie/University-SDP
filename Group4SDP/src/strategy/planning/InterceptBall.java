package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import vision.Position;
import world.state.WorldState;

public class InterceptBall extends StrategyInterface {
	private static final double distanceThreshold = 65;
	private static final double angleThreshold = Math.toRadians(30);

	public InterceptBall(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		System.out.println("InterceptBall started");
		double turnAngle;

		try {
			System.out.println("shouldidie: " + shouldidie);
			System.out.println("Strategy.alldie: " + Strategy.alldie);
			while (!shouldidie && !Strategy.alldie) {
				Position projBallPos = world.projectedBallPos();
				turnAngle = Math.toRadians(world.angleToTheirGoal());
						/*mover.angleCalculator(world.ourRobot.x,
						world.ourRobot.y, world.getTheirGoal().getX(),
						 world.getTheirGoal().getY(), world.ourRobot.bearing);*/

				int rotAttempts = 0;
				while (Math.abs(turnAngle) > angleThreshold && rotAttempts < 10) {
					mover.rotate(turnAngle);
					mover.waitForCompletion();
					if (shouldidie || Strategy.alldie)
						return;
					turnAngle = Math.toRadians(world.angleToTheirGoal());
//					turnAngle = mover.angleCalculator(world.ourRobot.x,
//							world.ourRobot.y, projBallPos.getX(),
//							projBallPos.getY(), world.ourRobot.bearing);
					++rotAttempts;
				}

				if (world.distanceBetweenUsAndBall() > distanceThreshold) {
					System.out.println("Projected ball pos: ("
							+ projBallPos.getX() + ", " + projBallPos.getY()
							+ ")");
					mover.moveTowards(projBallPos.getX(), projBallPos.getY());
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

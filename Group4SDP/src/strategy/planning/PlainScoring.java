package strategy.planning;

import movement.RobotMover;
import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import vision.Position;
import world.state.Ball;
import world.state.HittingObstacle;
import world.state.WorldState;

// TODO: convert to a proper strategy-like class.........
public class PlainScoring {

	public Ball ball;
	HittingObstacle wall = new HittingObstacle();
	MoveToPoint mpoint = new MoveToPoint();
	Position theirGoal;

	public WorldState world;
	public RobotMover mover;

	public void domination(WorldState world, RobotMover mover)
			throws InterruptedException {
		this.ball = world.ball;
		this.theirGoal = world.getTheirGoal();
		this.world = world;
		this.mover = mover;

		System.out.println("Domination is started");
		// Case where we have the ball and the opponent is behind us and
		double angle = IsRobotFacingPoint.Turner(world.ourRobot,
				theirGoal.getX(), theirGoal.getY());
		double angleT = IsRobotFacingPoint.Turner(world.ourRobot,
				world.theirRobot.x, world.theirRobot.y);
		// double angleDeg = Math.toDegrees(angle);
		double realangle = TurnToBall.AngleTurner(world.getOurRobot(),
				theirGoal.getX(), theirGoal.getY());
		double angleDeg = Math.abs(realangle);

		if (world.ourRobot.x - world.theirRobot.x > 0
				&& (angleDeg < 15 || angleDeg > 345)) {
			System.out.println("enemy is behind, straight punch!");
			faceGoal();
			mover.move(0, 100);
			Thread.sleep(1500);
			mover.kick();
			mover.stopRobot();
			return;

		}
		if (world.ourRobot.x - world.theirRobot.x > 0
				&& (angleDeg > 15 || angleDeg < 345)) {
			System.out.println("enemy is behind, navigate and punch ");
			faceGoal();
			mover.move(0, 100);
			Thread.sleep(1500);

			mover.kick();
			mover.stopRobot();
			return;
		}
		if ((angleDeg < 20 || angleDeg > 340)
				&& (DistanceToBall.Distance(world.ourRobot.x,
						world.theirRobot.y, theirGoal.getX(), theirGoal.getY()) < 200)) {

			System.out.println("Blind punch to side");
			synchronized (mover) {
				mover.rotate(Math.toRadians(5));
				mover.wait();
				mover.kick();
			}
			return;
		}
		if (world.theirRobot.x > world.ourRobot.x
				&& (angleT < 20 || angleT > 340)) {
			System.out.println("enemy is really ahead of us ");
			double mid = world.theirRobot.x - world.ourRobot.x;
			double nu = world.theirRobot.y + 20;
			if (DistanceToBall.Distance(world.ourRobot.x, world.ourRobot.y,
					world.theirRobot.x, world.theirRobot.y) > 200) {
				System.out.println("try to score ");
				double goalX = theirGoal.getX();
				double goalY = theirGoal.getY();
				double gpt = 165;
				double gpb = 310;
				double d = DistanceToBall.Distance(world.ourRobot.x,
						world.ourRobot.y, goalX, goalY);
				double newAngle = 0;
				if (Math.abs(gpb - world.ourRobot.y) < Math.abs(gpt
						- world.ourRobot.y)) {

				} else
					newAngle = (TurnToBall.AngleTurner(world.getOurRobot(),
							goalX, gpb));
				System.out.println("This angle to test sin navigation! it is "
						+ newAngle);

				synchronized (mover) {
					mover.rotate(Math.toRadians(5));
					mover.wait();
					mover.kick();
				}
			} else {
				System.out.println("atempt to dodge");
				double mang = 0;
				if (world.ourRobot.x - 104 > 395 - world.ourRobot.x) {
					mang = 45;
				} else
					mang = -45;
				int t = 0;

				mover.move(Math.toRadians(mang));

				while (world.ourRobot.x < world.theirRobot.x || t < 3
						|| wall.notHittingWall(world)) {

					Thread.sleep(1000);
					t += 1;

				}
				mover.stopRobot();
				return;
			}
		}
		if (wall.inCorner(world)
				&& (world.theirRobot.x > world.ourRobot.x && (angleT < 20 || angleT > 340))) {
			System.out.println("run away from corner");
			int p1 = 0;
			int p2 = 0;
			if (world.ourRobot.x > 300) {
				p1 = 412;
				p2 = 232;
			} else {
				p1 = 222;
				p2 = 235;
			}
			mover.moveToAndStop(p1, p2);
			mover.waitForCompletion();
			return;
		}

	}

	public void faceGoal() throws InterruptedException {
		Position a = world.getTheirGoal();
		System.out.println(a.getX() + ", " + a.getY());

		double angle = (TurnToBall.AngleTurner(world.getOurRobot(), a.getX(),
				a.getY()));

		System.out.println(angle);

		mover.rotate(Math.toRadians(angle));
		mover.waitForCompletion();
	}

}

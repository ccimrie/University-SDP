package strategy.planning;

import strategy.movement.Movement;
import vision.WorldState;
import world.state.RobotController;

public class PenaltyDefence extends StrategyInterface implements Runnable {

	public PenaltyDefence(WorldState world, RobotController rc, Movement mover) {
		super(world, rc, mover);
	}

	public void run() {

		double theirOriginal = Math.toDegrees(world.theirRobot.bearing);

		int counter = 0;

		System.out.println(Math.toDegrees(world.theirRobot.bearing) + " "
				+ theirOriginal);

		if (Math.toDegrees(world.theirRobot.bearing) > theirOriginal + 5
				&& counter > -1) {
			System.out.println("go backwards");
			mover.move(0, -100);
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mover.stopRobot();

			theirOriginal = Math.toDegrees(world.theirRobot.bearing);
			counter--;
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		if (Math.toDegrees(world.theirRobot.bearing) < theirOriginal - 5
				&& counter < 1) {
			System.out.println("going forwards");

			mover.move(0, 100);
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mover.stopRobot();

			theirOriginal = Math.toDegrees(world.theirRobot.bearing);
			counter++;
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		System.out.println(counter);

	}

}

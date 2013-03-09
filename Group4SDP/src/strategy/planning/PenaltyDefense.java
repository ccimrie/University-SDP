package strategy.planning;

import movement.RobotMover;
import world.state.WorldState;

public class PenaltyDefense extends StrategyInterface {

	public PenaltyDefense(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
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

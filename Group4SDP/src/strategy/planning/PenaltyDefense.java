package strategy.planning;

import java.util.Random;

import strategy.movement.Movement;
import vision.WorldState;
import world.state.RobotController;

public class PenaltyDefense extends StrategyInterface implements Runnable {
	public PenaltyDefense(WorldState worldState, Movement mover) {
		super(worldState, mover);
	}

	/*
	 * This class exists to randomly pick which part of the goal to defend in
	 * the event of a penalty. Since we don't know the penalty strategy our
	 * opponent will employ, it seems as good a method as any.
	 */
	@Override
	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Random generator = new Random();
		int randomInt = generator.nextInt(3);
		if (randomInt == 0) {
			// forward();
		} else if (randomInt == 1) {
			// backward();
		} else {
			// stop();
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

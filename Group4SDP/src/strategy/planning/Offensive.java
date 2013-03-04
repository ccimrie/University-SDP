package strategy.planning;

import movement.RobotMover;
import strategy.movement.Inteception;
import world.state.WorldState;

public class Offensive extends StrategyInterface {
	Inteception take = new Inteception();

	public Offensive(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		try {
			while (!shouldidie && !Strategy.alldie) {
				System.out.println("Going to the ball");

				mover.moveToAndStop(world.ball.x, world.ball.y);
				mover.waitForCompletion();

				PlainScoring killthemALL = new PlainScoring();
				killthemALL.domination(world, mover);

				mover.stopRobot();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mover.stopRobot();
	}
}

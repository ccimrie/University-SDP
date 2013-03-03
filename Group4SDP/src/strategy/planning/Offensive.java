package strategy.planning;

import movement.RobotMover;
import strategy.movement.Inteception;
import world.state.WorldState;

public class Offensive extends StrategyInterface implements Runnable {
	Inteception take = new Inteception();

	public Offensive(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {

		while (!shouldidie && !Strategy.alldie) {

			System.out.println("Going to the ball");

			synchronized (mover) {
				mover.moveToAndStop(world.ball.x, world.ball.y);
				try {
					mover.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			PlainScoring killthemALL = new PlainScoring();

			try {
				killthemALL.domination(world, mover);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (mover) {
				mover.stopRobot();
				try {
					mover.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		mover.stopRobot();
	}
}

package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import world.state.WorldState;

public class EndOfGame extends StrategyInterface {

	public EndOfGame(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		try {
			while (!shouldidie && !Strategy.alldie) {
				SafeSleep.sleep(50);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

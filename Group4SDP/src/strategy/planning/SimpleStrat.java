package strategy.planning;

import movement.RobotMover;
import world.state.WorldState;

public class SimpleStrat extends StrategyInterface implements Runnable {

	public SimpleStrat(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		
	}

}

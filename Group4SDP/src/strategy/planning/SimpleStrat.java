package strategy.planning;

import strategy.movement.Movement;
import vision.WorldState;
import world.state.RobotController;

public class SimpleStrat extends StrategyInterface implements Runnable {

	public SimpleStrat(WorldState world, Movement mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		
	}

}

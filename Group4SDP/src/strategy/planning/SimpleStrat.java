package strategy.planning;

import strategy.movement.Movement;
import vision.WorldState;
import world.state.RobotController;

public class SimpleStrat extends StrategyInterface implements Runnable {

	public SimpleStrat(WorldState world, RobotController rc, Movement mover) {
		super(world, rc, mover);
	}

	@Override
	public void run() {
		
	}

}

package strategy.planning;

import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

public class SimpleStrat extends StrategyInterface implements Runnable {

	public SimpleStrat(WorldState world, Robot us, Robot them, RobotController rc) {
		super(world, us, them, rc);
	}

	@Override
	public void run() {
		
	}

}

package balle.strategy;

import balle.controller.Controller;
import balle.strategy.planner.AbstractPlanner;
import balle.world.Snapshot;

public class BachTestingStrat extends AbstractPlanner {
	
    @FactoryMethod(designator = "NullStrategy", parameterNames = {})
	public static BachTestingStrat gameFactory() {
		return new BachTestingStrat();
	}

	public BachTestingStrat() {

	}

	@Override
	public void onStep(Controller controller, Snapshot snapshot) {
		if (snapshot == null) {
			System.out.println("No Snapshot");
			return;
		}
	}

}
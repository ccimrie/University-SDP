package strategy.planning;

import movement.RobotMover;
import world.state.WorldState;

public class PenaltyManager extends StrategyInterface {
	private final boolean attack;

	public PenaltyManager(WorldState world, RobotMover mover, boolean isAttack) {
		super(world, mover);
		this.attack = isAttack; 
	}

	@Override
	public void run() {
		StrategyInterface penalty;
		if (attack)
			penalty = new PenaltyAttack(world, mover);
		else
			penalty = new PenaltyDefense(world, mover);
		
		Thread penaltyThread = new Thread(penalty);
		penaltyThread.start();
		try {
			penaltyThread.join(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		MainPlanner strategy = new MainPlanner(world, mover);
		strategy.run();
	}

}

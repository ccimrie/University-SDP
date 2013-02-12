package strategy.planning;

import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;


public class PenaltyAttack extends StrategyInterface implements Runnable {
	
	boolean turned = false;
	boolean moved = false;
	boolean kicked = false;

	public PenaltyAttack(WorldState world, Robot us, Robot them, RobotController rc){
		super(world, us, them, rc);
	}
	public void run() {
		rc.kick();
	}
}

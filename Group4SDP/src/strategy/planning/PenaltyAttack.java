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
		int a = (int) (Math.random()*10);
		if (a >5 ) rc.rotate(-22);
		else rc.rotate(22);
		
		
		rc.kick();
	}
}

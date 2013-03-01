package strategy.planning;

import strategy.movement.Movement;
import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

public abstract class StrategyInterface {
	public static boolean shouldidie = false;
	
	WorldState world;
	Movement mover;
	
	public StrategyInterface(WorldState world, Movement mover){
		this.world = world;
		this.mover = mover;
	}
	
	public void kill(){
		shouldidie = true;
		// Terminate any active movements
		mover.interruptMove();
		try { //Sleep for a bit, because we want movement to die.
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

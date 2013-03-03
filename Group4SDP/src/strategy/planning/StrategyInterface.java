package strategy.planning;

import movement.RobotMover;
import world.state.WorldState;

public abstract class StrategyInterface {
	public static boolean shouldidie = false;
	
	WorldState world;
	RobotMover mover;
	
	public StrategyInterface(WorldState world, RobotMover mover){
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

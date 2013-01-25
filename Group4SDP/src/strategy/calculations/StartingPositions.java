package strategy.calculations;

import world.state.WorldInterface;

// detects if robots are in their starting position (ie centre kick)
public class StartingPositions {
	
	public static boolean atStartPos(WorldInterface world) {
		if ((world.getOurRobot().x < 100 || world.getTheirRobot().x < 100) 
			&& (world.getOurRobot().x > 500 || world.getTheirRobot().x < 500)
			&& (world.getBall().x < 340 && world.getBall().x < 300)) {
			return true;
		}	
		return false;	
	}
}

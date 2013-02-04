package strategy.calculations;

import world.state.WorldInterface;

public class Penalty {
	// robot determines by positions whether robots are arranged for a penalty kick
	public static boolean isPenalty(WorldInterface world) {
		if ((world.getTheirRobot().x < 80 
				|| world.getOurRobot().x < 80
				|| world.getTheirRobot().x > 560 
				|| world.getOurRobot().x > 560)
				&& (Compare.compareDouble(world.getTheirRobot().x,world.getOurRobot().x, 100))
				&& (Compare.compareDouble(world.getTheirRobot().y,world.getOurRobot().y,10))
				&& (Compare.compareDouble(world.getTheirRobot().y,world.getBall().y,10))
				&& (Compare.compareDouble(world.getOurRobot().y,world.getBall().y,10))) {
			return true;
		}
		
		return false;
	}
	
}

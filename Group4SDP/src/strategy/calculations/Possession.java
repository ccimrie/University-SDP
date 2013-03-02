package strategy.calculations;

import world.state.Ball;
import world.state.Robot;
import world.state.RobotType;
import world.state.WorldState;
import strategy.movement.TurnToBall;

public class Possession {
	
	private static final int possessionThreshold = 45;
	
	public static boolean hasPossession(WorldState world, RobotType type) {
	
		boolean possession = false;
		
		// setting local variables to make code more readable, thanks for suggestion Oli
		Robot robot = null;
		Ball ball = world.getBall();
		if (type == RobotType.Them) {
			robot = world.getTheirRobot();
		} else {
			robot = world.getOurRobot();
		}
		
		Double bearing_degrees = Math.toDegrees(robot.bearing);
		// if robot is near the ball and facing it
		if (((DistanceCalculator.Distance(robot.x,robot.y,ball.x,ball.y) < possessionThreshold)
				&& Compare.compareDouble(bearing_degrees, TurnToBall.findBearing(robot, ball), 180)) 
				|| (type == RobotType.Us && world.ball.x == 0 && world.ball.y == 0)
		) {
			
			possession = true;
		}
		
		return possession;
	}
}
package strategy.planning;

import java.util.Observable;

import strategy.calculations.BlockGoal;
import strategy.calculations.Compare;
import strategy.calculations.DistanceCalculator;
import strategy.calculations.IsRobotFacingPoint;
import world.state.PossessionType;

public class MainPlanner extends Strategy {
	
	private final int KICK_TIMEOUT = 50; 
	private int kickCounterTimeout = 0;
	private int stuckCounter = 20;
	private int goBackCounter = 0;
	private boolean start = true, startKick = false;
	double speed = 0.2;
	double delta = 0.015;

	Offensive o = new Offensive();
	Defensive d = new Defensive();
	
	@Override
	public void update(Observable arg, Object obj) {
		// Sets default speeds
		rc.setDefaultRotateSpeed(50.0);
    	rc.setDefaultTravelSpeed(0.3);

		// TODO: Create start of match strategy.
		
    	// OBSTACLE DETECTION STRATEGY
    	// When touch sensor is pressed, go back.
		if(goBackCounter > 0)
		{
			rc.travel(-0.3d, 0.3d);
			goBackCounter --;
		}
		else if(stuckCounter <= 0)
		{
			stuckCounter = 20;
			goBackCounter = 7;
		}
		else if (rc.isTouchPressed()) {
			//rc.travel(-0.3d, 0.3d);
			stuckCounter--;
		}
		
		else
		{
		
			// If the ball is in the goal then stop playing.
			// TODO: These values should not be hard coded.
			if (world.ball.x < 40 || world.ball.x > 644) {
				rc.stop();
			}
		
			stuckCounter = 20;
			// If our robot is facing another robot, go back.
			if ((IsRobotFacingPoint.Turner(world.ourRobot, world.theirRobot.x, world.theirRobot.y) < 10)
					&& (world.ourRobot.distance(world.theirRobot) < 20)) {

				rc.travel(0.3d,-1d);
			}
			
			if (world.hasPossession == PossessionType.Both) {
				System.out.println("Both teams have the ball.");
				speed = 0.3;
				rc.setDefaultTravelSpeed(speed);
				d.defensive(world, rc);
			} else if (world.hasPossession == PossessionType.Them) {
				System.out.println("The enemy has the ball.");
				speed = 0.3;
				rc.setDefaultTravelSpeed(speed);
				d.defensive(world, rc);
			} else if (world.hasPossession == PossessionType.Us) {
				System.out.println("We have the ball.");
				o.offensive(world, rc);
				rc.setDefaultTravelSpeed(speed);
				speed += delta;
				//kickCounterTimeout++;
			//	if (kickCounterTimeout == KICK_TIMEOUT) kickCounterTimeout = 0;
			} else {
				double x = BlockGoal.Blockx(world.ball.x, world.ball.y);
				double y = BlockGoal.Blocky(world.ball.x, world.ball.y); 
				if ((world.ourRobot.x < world.ball.x && world.areWeOnLeft() && Compare.compareDouble(world.ourRobot.y,world.ball.y,40)) 
						|| (world.ourRobot.x >= world.ball.x && !world.areWeOnLeft() && Compare.compareDouble(world.ourRobot.y,world.ball.y,40))) {
					System.out.println("Nobody has the ball, we're gonna go get it.");
					speed = 0.3;
					rc.setDefaultTravelSpeed(speed);
					BallRetrievalStrategy.ballRetrieval(world, rc);
				} else {
					System.out.println("Nobody has the ball, we're getting behind it.");
					speed = 0.3;
					rc.setDefaultTravelSpeed(speed);
					d.defensive(world, rc);
				}
			}
		}
	}
}

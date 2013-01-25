package strategy.planning;


import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import world.state.World;

import comms.control.Server;

public class BallRetrievalStrategy {
	
	public static void ballRetrieval(World world, Server rc) {
		//MoveToPoint mp = new MoveToPoint();
		GoToPoint.goToPoint(world, rc, world.ball.getPosition(), AvoidanceStrategy.IgnoringBall);
		return;
	}
}
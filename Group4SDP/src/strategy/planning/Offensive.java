package strategy.planning;

import geometry.Vector;

import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import world.state.Robot;
import world.state.World;

import comms.control.Server;

public class Offensive {
	

	public void offensive(World world, Server server){
				
		Robot us = world.getOurRobot();
		Vector theirGoal = world.getTheirGoal();

		if((us.x < world.getMidPoint() && world.ourHalfLeft()) 
		|| (us.x >= world.getMidPoint() && !world.ourHalfLeft())){
			
			System.out.println("OUR HALF YEAH!");
			GoToPoint.goToPoint(world, server, theirGoal, AvoidanceStrategy.Aggressive);
						
		} else {
			
			double angle = IsRobotFacingPoint.Turner(us, theirGoal.getX(), theirGoal.getY());
			System.out.println(Math.abs(angle));
			if(Math.abs(angle) > 40){
				
				System.out.println("Their half wrong angle");
				GoToPoint.goToPoint(world, server, theirGoal, AvoidanceStrategy.Aggressive);
				
			} else {
				
				System.out.println("Kicking!");
				server.kick();
			}
		
		}
	}
}
package strategy.planning;

import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;
import world.state.World;
import geometry.Vector;

import comms.control.Server;

public class Defensive{
		
	public void defensive(World world, Server server){
		
		Ball ball = world.getBall();
		Robot us = world.getOurRobot();
		
		//double x; //= BlockGoal.Blockx(ball.x, ball.y);
		//double y; //= BlockGoal.Blocky(ball.x, ball.y);
		Vector pos;
		
		if(world.areWeOnLeft()){
			pos = new Vector(ball.getPosition().getX() - 20, ball.getPosition().getY());
		} else {
			pos = new Vector(ball.getPosition().getX() + 20, ball.getPosition().getY());
		}
		
		if(Vector.distanceSquared(pos, us.getPosition()) <= 100){
			
			double angle = TurnToBall.Turner(us, ball);
			server.rotate(-angle);
			while(server.isMoving()){
				System.out.println("Fuck loops");
			}
			//world.deleteObservers();
			//server.stop();
			
		} else if(((us.getPosition().getX() < ball.getPosition().getX() -5) && world.areWeOnLeft()) || ((us.getPosition().getX() > ball.getPosition().getX() + 5) && !world.areWeOnLeft())){
			
			GoToPoint.goToPoint(world, server, pos, AvoidanceStrategy.IgnoringBall);
			
		} else {	
			
			GoToPoint.goToPoint(world, server, pos, AvoidanceStrategy.AvoidingBall);
			
		}
		
	}
	
}

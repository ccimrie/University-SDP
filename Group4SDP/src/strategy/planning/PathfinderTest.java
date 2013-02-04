package strategy.planning;

import geometry.Vector;

import java.util.Observable;
import java.util.Observer;

import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import world.state.Robot;

public class PathfinderTest extends Strategy implements Observer {

	@Override
	public void update(Observable obj, Object arg) {
	
		Robot us = world.ourRobot;
    	
    	
    	rc.setDefaultRotateSpeed(40.0);
    	rc.setDefaultTravelSpeed(0.3);
    	double distanceSqr = (us.x - 10)*(us.x - 10) + (us.y - 180)*(us.y - 180);
    	if(distanceSqr > 30*30) {
    		if (!world.areWeOnLeft()) {
    			GoToPoint.goToPoint(world, rc, new Vector(10, 193), AvoidanceStrategy.AvoidingBall);
    		} else {
    			GoToPoint.goToPoint(world,rc, new Vector(620, 193), AvoidanceStrategy.AvoidingBall);
    		}
    	} 
    	else
    	{
			rc.stop();
			stop();
    	}
	}
}

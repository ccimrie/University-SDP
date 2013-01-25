/*
 * Copyright (c) 2012. University of Edinburgh
 */

package strategy.movement;

import geometry.Vector;

import world.state.PitchInfo;
import world.state.World;

import comms.control.Server;


//class will attempt to follow ball around the pitch

public class FollowBall {
	
	public void followBall(World world, Server rc,double val){
		double k = 50;
		double w = 50;
		double valueForDist = val;
		
		//double minDist = 40.0;
		double distInFront = val*world.distanceBetweenUsAndBall()/k;
		
		double arc =world.distanceBetweenUsAndBall()/w;

		Vector prevBall = world.prevBall.getPosition();
		Vector ball = world.ball.getPosition();
		Vector vector = ball.subtract(prevBall);
		
		double deltax = distInFront*vector.getX();
		double deltay = distInFront*vector.getY();
		
		Vector dest = new Vector(prevBall.getX() + deltax,prevBall.getY() + deltay);
		
		if(world.isMainPitch()){
			dest = PitchInfo.outOfBoundsMain(dest);

		}else{
			dest = PitchInfo.outOfBoundsSide(dest);
		}
		System.out.println("Ball: " + ball.getX() + ", " + ball.getY());
		System.out.println("Destination: " + dest.getX() + ", " + dest.getY());
		System.out.println("We are at: " + world.ourRobot.x + ", " + world.ourRobot.y);
		
		GoToPoint.goToPoint(world, rc, dest, AvoidanceStrategy.VariableRadius);
	}
}

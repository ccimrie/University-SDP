package simulator;

import java.util.ArrayList;

import strategy.movement.AvoidanceStrategy;
import simulator.FollowVector;

import comms.control.ServerInterface;
import communication.BluetoothRobot;
import geometry.Vector;
import strategy.movement.DistanceToBall;


import world.state.Ball;
import world.state.Robot;
import world.state.WorldState;

public class gotoball {
	
	private static final double distanceFromBallToStop = 0.5;
	
	public static void approach(SimWorld worldState, SimServer robot) throws InterruptedException {
		// First we turn to the ball
		//this.vision = vis;
		//WorldState t = new WorldState();
		
		//worldState.setOurRobot();
		//System.out.println(worldState.areWeBlue());
    	Robot us = worldState.getOurRobot();
    	Ball ball = worldState.getBall();
    	
    	// Plan:
    	// 0. Get bearings
    	// 1. Turn to face ball
    	// 2. Move forwards
		double distance = DistanceToBall.Distance(us.x, us.y, ball.x, ball.y);
        System.out.println(String.format("Distance to ball is %f", distance));
		double angle = TurnToBallSim.Turner(us, ball);
       
        System.out.println(String.format("Angle of ball to robot is %f", angle));

        
		if(Math.abs(angle) > 20) {
			// We need to get to the desired angle first.
			System.out.println("Stop and turn");
			robot.stop();
			robot.rotate(angle);
			// We don't want to carry on after this command!
			// This also removes the need for that else block
			//angle = TurnToBall.Turner(us, ball);
			//return;
			
		}
	
		 while(distance > distanceFromBallToStop) {
			//System.out.println("Forward");
			angle = TurnToBallSim.Turner(us, ball);
			if((Math.abs(angle) > 15) && (Math.abs(angle) < 40) ) {
				//Stop everything and turn
				System.out.println("The final angle is " + angle);
				robot.stop();
				robot.rotate((angle/2));
			}else if (Math.abs(angle) > 40){
				robot.stop();
				robot.rotate(angle);
			}
			//robot.move(0,80);
			//robot.travel(0.0, 80.0);
			robot.travel(80.0);
			distance = DistanceToBall.Distance(us.x, us.y, ball.x, ball.y);
			System.out.println("Distance to ball: " + distance);
			Thread.sleep(100);
		}
		
		// Being close to the ball we can perform one last minor turn
		angle = TurnToBallSim.Turner(us, ball);
		if(Math.abs(angle) > 15) {
			// Stop everything and turn
			System.out.println("Making final correction");
			robot.stop();
			robot.rotate(angle);
		}else{
			robot.stop();
		}	//In case we are away from the ball, recurse.
		if (distance > distanceFromBallToStop){
			approach(worldState, robot);
		}
	}
	
	
}

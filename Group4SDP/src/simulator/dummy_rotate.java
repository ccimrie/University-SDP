package simulator;

import geometry.Vector;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.DistanceToBall;
import strategy.movement.GoToPoint;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;
import simulator.gotoball;

import comms.control.ServerInterface;

public class dummy_rotate {
	private static final int distanceFromBallToStop = 60;
	/**
	 * @param args
	 */
	public static void goToPoint(SimWorld world, SimServer rc)
	{ 
		
		// First we turn to the ball
    	Robot us = world.ourRobot;
    	Ball ball = world.ball;
    	
    	// Plan:
    	// 0. Get bearings
    	// 1. Turn to face ball
    	// 2. Move forwards
		double distance = DistanceToBall.Distance(us.x, us.y, ball.x, ball.y);
        System.out.println(String.format("Distance to ball is %f", distance));
		double angle = TurnToBall.Turner(us, ball);
        System.out.println(String.format("Angle of ball to robot is %f", angle));
        
        if(rc.isTurning) {
        	// This is to simulate turning "blocking"
        	return;
        }
        
		if(Math.abs(angle) > 30) {
			// Stop everything and turn
			System.out.println("Stop and turn");
			rc.stop();
			rc.rotate(-angle);
			// We don't want to carry on after this command!
			// This also removes the need for that else block
			return;
		}
		
		if(distance > distanceFromBallToStop) {
			System.out.println("Forward");
			rc.forward();
			return;
			// Let's not arc for this milestone as it's too complicated
			/*if(Math.abs(angle) > 10) {
				//TODO: Perfect this with different values for the arc radius (maybe relate it to distance / angle)
				System.out.println("Arcing");
				int direction;
				if (angle > 0) {
					direction = 1;
				} else {
					direction = -1;
				}
				rc.arcForward(direction * 0.25);
			} else {
				System.out.println("Forward");
				rc.forward();
			}
			return;
			*/
		}
		
		System.out.println("Stop");
		rc.stop();
		//rc.travel(80.0);
		//rc.rotate(10.0);
		// TODO Auto-generated method stub

	}

}

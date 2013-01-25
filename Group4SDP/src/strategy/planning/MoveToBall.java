package strategy.planning;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import communication.BluetoothCommunication;
import communication.DeviceInfo;

import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;

import strategy.planning.Strategy;

public class MoveToBall extends Strategy implements Observer {
	
	private static final int distanceFromBallToStop = 60;
	private static boolean rotating = false;
	BluetoothCommunication connection = new BluetoothCommunication(DeviceInfo.NXT_NAME, DeviceInfo.NXT_MAC_ADDRESS);

	@Override
	public void update(Observable obj, Object arg) {
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
        
        if(rotating && connection.isMoving()) {
        	// This is to simulate turning "blocking"
        	System.out.println("Still turning");
        	return;
        }
        rotating = false;
        
		if(Math.abs(angle) > 30) {
			// Stop everything and turn
			System.out.println("Stop and turn");
			int[] stop_command = {Commands.STOP,0,0,0};
			//TODO - take the angle of rotation into account. Depends on rotate implementation
			int[] rotate_command = {Commands.ROTATE,0,0,0};
			//rc.rotate(-angle);
			
			// Send commands
			try {
				connection.sendToRobot(stop_command);
				connection.sendToRobot(rotate_command);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Command not sent - check bluetooth connection");
			}
			
			rotating = true;
			// We don't want to carry on after this command!
			// This also removes the need for that else block
			return;
		}
		
		if(distance > distanceFromBallToStop) {
			System.out.println("Forward");
			int[] forward_command = {Commands.FORWARDS,0,0,0};
			try {
				connection.sendToRobot(forward_command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Command not sent - check bluetooth connection.");
			}
			//TODO - take into account distance to travel forward
			//connection.forward(0.2);
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
		
		// Being close to the ball we can perform one last minor turn
		if(Math.abs(angle) > 10) {
			// Stop everything and turn
			System.out.println("Making final correction");
			//rc.stop();
			//rc.rotate(-angle);
			int[] stop_command = {Commands.STOP,0,0,0};
			// TODO - take into account rotation angle
			int[] rotate_command = {Commands.ROTATE,0,0,0};
			try {
				connection.sendToRobot(stop_command);
				connection.sendToRobot(rotate_command);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Command not sent - check bluetooth connection");
			}
			rotating = true;
			// We don't want to carry on after this command!
			// This also removes the need for that else block
			return;
		}
		
		System.out.println("Stop");
		//rc.stop();
		stop();
	}
	
}

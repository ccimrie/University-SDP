package strategy.planning;

import vision.WorldState;
import world.state.RobotController;
import world.state.Ball;
import world.state.Robot;
//import world.state.PitchInfo;

public class DribbleBall3 {
	
	// Setting up the threshold for the target point behind the ball !!!
		private static final double threshold = 20;
		private static double dribbleDistance = 100;
		private MoveToBall moveToBall = new MoveToBall();
		
	public void dribbleBall(WorldState worldState, RobotController robot)
			throws InterruptedException{
		
		// Constructing the world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;
		
		if(us.x > ball.x) {
			System.out.println("X coordinate: " + ball.x + "," + "Y coordinate:" + ball.y);
			System.out.println("The robot bearing is " + us.bearing);
			double angle = 90.0 - us.bearing;
			if (angle > 180){ 
				angle = -360 + angle;
				System.out.println("Robot bearing after rotating is " + us.bearing);
			}
			System.out.println("The angle is " + angle);
			robot.rotate((int) angle);
			
			//If the robot is ahead of the ball on the pitch, move it backwards
			while ((ball.x - threshold) < us.x) {	
				robot.move(0, -127);
				Thread.sleep(100);
			}
			robot.stop();
		}
		
		// Use move to ball
		moveToBall.approach(worldState, robot);
		
		//In order to face the door we need us.bearing == 90.
				
		double angle = 90.0 - us.bearing;
		if (angle > 180){ 
				angle = -360 + angle;
			}
		System.out.println("The angle is " + angle);
		robot.rotate((int) angle);
		
		//Fix the position of the ball so that we can measure 30 cm from that point to dribble
		double temp = ball.x;
		
		// Now dribble for dribbleDistance distance :)
		while (us.x < temp + dribbleDistance) {
			robot.move(0, 127);
			Thread.sleep(100);
		}
		robot.stop();
	}
	
	
}

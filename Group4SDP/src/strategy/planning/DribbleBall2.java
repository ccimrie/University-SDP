package strategy.planning;

import vision.WorldState;
import world.state.RobotController;

import world.state.Ball;
import world.state.Robot;
import world.state.PitchInfo;

//author: SP + MV

public class DribbleBall2 extends Strategy {

	// Setting up the threshold for the target point behind the ball !!!
	private static final double threshold = 75;
	private static double dribbleDistance = 100;
	
	//Makes little adjustments towards the bearing of the robot, i.e. checks if it is facing the door
	
	public void dribbleBall(WorldState worldState, RobotController robot)
			throws InterruptedException {
		// Constructing the world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;

		// Rotate so that the robot faces the door
		//In order to face the door we need us.bearing == 90.
		System.out.println("The robot bearing is " + us.bearing);
		double angle = 90.0 - us.bearing;
		if (angle > 180){ 
			angle = -360 + angle;
			System.out.println("Angle greater than 180. New angle is " + angle);
		}
		System.out.println("The angle is " + angle);
		robot.rotate((int) angle);

		//Check if the robot is in front of the ball on the y axis and go around the ball
		if (((us.y < ball.y + threshold) || (us.y > ball.y - threshold)) && (us.x>ball.x)) {
			//Check which is the safer side to go around the ball
			if (Math.abs(us.y  - PitchInfo.safeUpperBoundSide.getY()) > Math.abs(us.y  - PitchInfo.safeLowerBoundSide.getY())){
				while ((ball.y - threshold) < us.y){
					robot.move(-127, 0); //Robot's left
				}
				robot.stop();
			}
			else {
				while ((ball.y + threshold) < us.y){
					robot.move(127, 0); //Robot's right
				}
				robot.stop();
			}
		}
		
		//Check if the robot has kept its bearing, i.e. still facing the door
		System.out.println("Before check. Bearing is " + us.bearing);
		if ((us.bearing > 100) || (us.bearing < 80)) {
			angle = 90.0 - us.bearing;
			if (angle > 180) angle = -360 + angle;
			robot.rotate((int) angle);
		} 
		
		//The condition checks where the robot is in respect to the ball and aligns it properly on the x axis
		//If the robot is ahead of the ball on the pitch
		if (us.x > ball.x){
			while ((ball.x - threshold) < us.x) {	
				robot.move(0, -127);
				Thread.sleep(100);
			}
			robot.stop();
		}
		
		//If the robot is already behind the ball on the pitch
		else {
			while ((ball.x - threshold) > us.x) {
				robot.move(0, -127);
				Thread.sleep(100);
			}
			robot.stop();
		}
		
		//Check if the robot has kept its bearing, i.e. still facing the door
		if ((us.bearing > 100) || (us.bearing < 80)) {
			angle = 90.0 - us.bearing;
			if (angle > 180) angle = -360 + angle;
			robot.rotate((int) angle);
		} 
		
		// Move either up or down along the y axis until aligning properly
		if (us.y > ball.y) {
			System.out.println("Moving up the y axis"); //Robot's left
			
			while (us.y > ball.y) {
				robot.move(-127, 0);
				Thread.sleep(200);
			}
		} else {
			System.out.println("Moving down the y axis"); //Robot's right
			robot.move(127, 0);
			while (us.y < ball.y) {
				Thread.sleep(200);
			}
		}
		if ((us.bearing > 100) || (us.bearing < 80)) {
			angle = 90.0 - us.bearing;
			if (angle > 180) angle = -360 + angle;
			robot.rotate((int) angle);
		} 
		
		//Check if the robot has kept its bearing, i.e. still facing the door
		if ((us.bearing > 100) || (us.bearing < 80)) {
			angle = 90.0 - us.bearing;
			if (angle > 180) angle = -360 + angle;
			robot.rotate((int) angle);
		} 
		
		
		//Fix the position of the ball so that we can measure 30 cm from that point to dribble
		double temp = ball.x;
		
		// Now dribble for dribbleDistance distance :)
		while (us.x < temp + dribbleDistance) {
			robot.move(0, -127);
			Thread.sleep(100);
		}
		robot.stop();
		
	}

}

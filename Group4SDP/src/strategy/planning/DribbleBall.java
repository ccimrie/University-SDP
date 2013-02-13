
package strategy.planning;

import vision.WorldState;
import world.state.RobotController;
import world.state.Ball;
import world.state.Robot;
import strategy.movement.TurnToBall;


public class DribbleBall extends Strategy {

	//Setting up the threshold for the target point behind the ball !!!
	private static final double xthreshold = 40;
	private static final double ythreshold = 30;
	private static double dribbleDistance = 120;
	private MoveToPoint moveToPoint = new MoveToPoint();
	private MoveToBall moveToBall = new MoveToBall();
	
	public void dribbleBall (WorldState worldState, RobotController robot) throws InterruptedException {
		//Get robot and ball from world
		worldState.setOurRobot();
    	Robot us = worldState.ourRobot;
    	Ball ball = worldState.ball;
    	
    	if (us.x > ball.x) {
    		//Calculate the target point which appears behind the ball
        	double xvalue = ball.x - xthreshold;
        	double yvalue;
        	if (us.y > ball.y) {
        		 yvalue = ball.y + ythreshold;
        	} else {
        		 yvalue = ball.y - ythreshold;
        	}
        	//Sends the the robot to the target point
        	moveToPoint.moveToPoint(worldState, robot, xvalue, yvalue);
    	}
    	
    	//Send the robot after to find the ball
    	moveToBall.approach(worldState, robot);
    	
    	//Make an adjustment to face the door
    	double angle = TurnToBall.AngleTurner(us, 540, us.y);
    	robot.rotate((int) angle);
//    	double angle = 90.0 - us.bearing;
//		if (angle > 180){ 
//				angle = -360 + angle;
//			}
//    	System.out.println("The last adjustment angle is " + angle);
//		robot.rotate((int) angle);
    	
    	//-----Test
//    	double damn = Math.toDegrees(us.bearing);
//    	while(damn < 85 || damn > 95){
//	    	double angle;
//	    	if(damn>=0 && damn <=270){
//	    		angle = 90 - damn;
//	    		System.out.println("Rotating angle is " + angle);
//	    		robot.rotate((int) angle);
//	    	} else if (damn>270 && damn<=360){
//	    		angle = 450 - damn;
//	    		System.out.println("Rotating angle is " + angle);
//	    		robot.rotate((int) angle);
//	    	}
//    	}
    	/*if(us.bearing <= 0 && us.bearing < 180){
    		angle = 90 - us.bearing;
    		System.out.println("Rotating angle is " + angle);
    		robot.rotate((int) angle);
    	}
    	else if(us.bearing >= 180 && us.bearing < 270){
    		angle = -270 - us.bearing;
    		System.out.println("Rotating angle is " + angle);
    		robot.rotate((int) angle);
    	}
    	else if(us.bearing >= 270 && us.bearing < 360){
    		angle = 90 - us.bearing;
    		System.out.println("Rotating angle is " + angle);
    		robot.rotate((int) angle);
		}*/
    	
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

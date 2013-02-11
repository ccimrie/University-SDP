
package strategy.planning;

import vision.WorldState;
import world.state.RobotController;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;


public class DribbleBall extends Strategy {

	//Setting up the threshold for the target point behind the ball !!!
	private static final double threshold = 75;
	
	public void dribbleBall (WorldState worldState, RobotController robot) throws InterruptedException {
		//Get robot and ball from world
		worldState.setOurRobot();
    	Robot us = worldState.ourRobot;
    	Ball ball = worldState.ball;
    	
    	//Calculate intermediate point
    	double x = ball.x - threshold;
    	double y1;
    	
    	//Calculate a target point ensuring a safe distance from the ball
    	if(us.y > ball.y) y1 = us.y + threshold;
    	else y1 = us.y - threshold;
    	
    	//Sends the the robot to the intermediate point
    	MoveToPoint.moveToPoint(worldState, robot, x, y1);
    	
    	//Sends the robot exactly behind the ball, align on y axis
    	MoveToPoint.moveToPoint(worldState, robot, x, ball.y);
    	
    	//Rotate to properly face the ball
    	double angle = TurnToBall.Turner(us, ball);
    	if (angle > 20) robot.rotate((int)angle);
    	
    	//Dribble now
    	robot.move(0, 10);
	}
	
	

}

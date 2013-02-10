
package strategy.planning;

import vision.WorldState;
import world.state.RobotController;
import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;

import vision.Vision;



public class DribbleBall extends Strategy {

	//Setting up the threshold for the target point behind the ball !!!
	private static final double threshold = 75;
	
	public void dribbleBall (WorldState worldState, RobotController robot) throws InterruptedException {
		//Constructing the world
		worldState.setOurRobot();
    	Robot us = worldState.ourRobot;
    	Ball ball = worldState.ball;
    	
    	//Setting up the target point coordinates of the point behind the ball
    	//TODO For future reference, check if we are on the left or right and +/- the threshold
    	double x = ball.x - threshold;
    	double y = ball.y;
    	
    	//Sends the the robot at the position behind the ball
    	MoveToPoint.moveToPoint(worldState, robot, x, y);
    	
    	//Rotate to properly face the ball
    	double angle = TurnToBall.Turner(us, ball);
    	robot.rotate((int)angle);
    	
    	//Dribble now!
    	robot.move(0, 10);
	}

}

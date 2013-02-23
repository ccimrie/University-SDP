package strategy.movement;

import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

/**
 * A movement class, that provides calculations for
 * different move commands for the robot.
 * 
 * @author Jakov Smelkin
 */
public class Movement {

	private WorldState worldState;
	private RobotController robot;
	private Robot us;
	/**
	 * Constructor for the movement class
	 * 
	 * @param worldState
	 *            a world state from the vision, giving us information on robots, ball etc.
	 * @param robot
	 *            A {@link world.state.RobotController} class that prepares byte commands to the robot.
	 */
	public Movement(WorldState worldState, RobotController robot) {
		super();
		this.worldState = worldState;
		this.robot = robot;
		/*us = worldState.ourRobot;*/
	}

	/**
	 * A general move function as seen from the position of the robot.</br>
	 * Speeds take values between -100 and 100.</br>
	 * The differnce between 0 and 1 is significant to start moving the motors,
	 * later it scales up linearly.</br>
	 * 
	 * @param speedX
	 *            Speed left (for positive values) or right (for negative ones).
	 * @param speedY
	 *            Speed forward (for positive values) or backward (for negative ones).
	 */
	public void move(double speedX, double speedY) {
		robot.move((int) speedX, (int) speedY);
	}
	
	/**
	 * A general move function where you specify a clockwise
	 * angle from the front of the robot to move at.
	 * @param angle Angle, in radians (0 to 2*PI)
	 */
	public void move(double angle){
		double speedX=100*Math.sin(angle);
		double speedY=100*Math.cos(angle);
		move(speedX, speedY);
	}
	
	public void moveToPoint(double x, double y){
		
	}

}

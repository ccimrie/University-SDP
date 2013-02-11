package strategy.planning;

import vision.WorldState;
import world.state.RobotController;

import world.state.Ball;
import world.state.Robot;

//author: SP

public class DribbleBall2 extends Strategy {

	// Setting up the threshold for the target point behind the ball !!!
	private static final double threshold = 75;

	public void dribbleBall(WorldState worldState, RobotController robot)
			throws InterruptedException {
		// Constructing the world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;

		// Rotate so that you face the door
		double angle = 90.0 - us.bearing;
		if (angle > 180) angle = -360 + angle;
		robot.rotate((int) angle);

		// Move backwards along the x axis until aligning properly
		robot.move(0, -127);
		while ((ball.x - threshold) < us.x) {
			Thread.sleep(100);
		}
		robot.stop();

		// Move either left or right along the y axis until aligning properly
		if (us.y > ball.y) {
			System.out.println("Moving down the y axis");
			robot.move(-127, 0);
			while (us.y > ball.y) {
				Thread.sleep(200);
			}

		} else {
			System.out.println("Moving up the y axis");
			robot.move(127, 0);
			while (us.y < ball.y) {
				Thread.sleep(200);
			}
		}
		// Now dribble I guess
		robot.move(0, 10);
	}

}

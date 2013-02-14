package strategy.planning;

import vision.WorldState;
import world.state.Ball;
import world.state.Robot;
import world.state.RobotController;

//author: SP + MV

public class DribbleBall2 extends Strategy {

	// Setting up the threshold for the target point behind the ball !!!
	private static final double threshold = 50;

	// private static double dribbleDistance = 100;

	public void rotateToExit(Robot us, RobotController robot) {
		double damn = Math.toDegrees(us.bearing);
		double angle = 0;
		while (damn < 86 || damn > 94) {
			damn = Math.toDegrees(us.bearing);
			if (damn >= 0 && damn <= 270) {
				angle = 90 - damn;
			} else if (damn > 270 && damn <= 360) {
				angle = 450 - damn;
			} else
				break;
			System.out.println("Rotating angle is " + angle);
			robot.rotate((int) angle);
		}
	}

	public void dribbleBall(WorldState worldState, RobotController robot) throws InterruptedException {
		// Constructing the world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;
		rotateToExit(us, robot);
		robot.stop();
		if (us.x > ball.x) {
			// Move infront of a ball a little
			if (us.x < ball.x + threshold) {
				robot.move(0, 10);
				while (us.x < ball.x + threshold) {
					Thread.sleep(20);
				}
				robot.stop();
				rotateToExit(us, robot);
				robot.stop();
			}
			// Ball is behind
			if (us.y >= ball.y - threshold && ball.y >= 300) {
				robot.move(-80, 0);
				while (us.y >= ball.y - threshold) {
					Thread.sleep(200);
					rotateToExit(us, robot);
					robot.move(-80, 0);
				}
				robot.stop();
				rotateToExit(us, robot);
				robot.stop();
			} else if (us.y < ball.y + threshold && ball.y < 300) {
				robot.move(80, 0);
				while (us.y < ball.y + threshold) {
					Thread.sleep(20);
				}
				robot.stop();
				rotateToExit(us, robot);
				robot.stop();
			}

		}
		// Now the ball is behind us and out of the way.
		robot.move(0, -80);
		while (us.x > ball.x - threshold) {
			Thread.sleep(20);
		}
		robot.stop();
		rotateToExit(us, robot);
		robot.stop();
		robot.move(-55, 0);
		while (us.y > ball.y) {
			Thread.sleep(10);
		}
		rotateToExit(us, robot);
		robot.stop();
		robot.move(55, 0);
		while (us.y < ball.y) {
			Thread.sleep(10);
		}
		rotateToExit(us, robot);
		robot.stop();
		robot.move(0, 100);
		Thread.sleep(2500);
		robot.stop();

	}
}
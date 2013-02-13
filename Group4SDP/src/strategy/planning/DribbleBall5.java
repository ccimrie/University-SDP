package strategy.planning;

import vision.WorldState;
import world.state.RobotController;
import world.state.Ball;
import world.state.Robot;
import strategy.movement.TurnToBall;

public class DribbleBall5 {

	private static final double xthreshold = 70;
	private static final double ythreshold = 70;
	private static double dribbleDistance = 200;
	private MoveToPoint moveToPoint = new MoveToPoint();

	public void dribbleBall(WorldState worldState, RobotController robot)
			throws InterruptedException {
		// Get robot and ball from world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;

		if (ball.y < 250) {
			moveToPoint.moveToPoint(worldState, robot, ball.x, ball.y
					+ ythreshold);
			moveToPoint.moveToPoint(worldState, robot, ball.x - xthreshold,
					ball.y + ythreshold);
		} else {
			moveToPoint.moveToPoint(worldState, robot, ball.x, ball.y
					- ythreshold);
			moveToPoint.moveToPoint(worldState, robot, ball.x - xthreshold,
					ball.y - ythreshold);
		}
		moveToPoint.moveToPoint(worldState, robot, ball.x - xthreshold, ball.y);

		double angle = TurnToBall.AngleTurner(us, 5400, us.y);
		int attempt = 0;
		robot.stop();
		while (Math.abs(angle) > 15 && attempt < 10) {
			if ((Math.abs(angle) > 15) && (Math.abs(angle) < 50)) {
				robot.rotate((int) (angle / 2));
			} else if (Math.abs(angle) > 50) {
				robot.rotate((int) angle);
			}
			++attempt;
			angle = TurnToBall.AngleTurner(us, 5400, us.y);
		}

		robot.move(0, 100);
		// Stop preferably at 200 pixels, but 550 if we hit the edge of the
		// pitch first
		double stopX = Math.min(ball.x + dribbleDistance, 550);
		while (us.x < stopX)
			Thread.sleep(50);

		robot.stop();
	}
}

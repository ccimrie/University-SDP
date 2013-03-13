package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import world.state.WorldState;

public class Offense3 extends StrategyInterface {

	public Offense3(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	private void goToWall() throws InterruptedException {
		double farPointX, targetY;
		if (world.areWeOnLeft()) {
			farPointX = 5000;
			targetY = 100;
		} else {
			farPointX = -5000;
			targetY = 360;
		}

		if (!world.weFaceTheirGoal()) {
			// Rotate to face their goal
			double angleToRotate = mover.angleCalculator(world.ourRobot.x,
					world.ourRobot.y, farPointX, 240, world.ourRobot.bearing);
			mover.rotate(angleToRotate);
			mover.waitForCompletion();
		}

		double ySpeed = 60;
		if (world.ourRobot.y > 240)
			ySpeed = -ySpeed;
		mover.move(ySpeed, 80);
		while (!shouldidie && !Strategy.alldie
				&& Math.abs(world.ourRobot.y - targetY) > 20) {
			SafeSleep.sleep(50);
		}
	}

	private void goToShortWall() throws InterruptedException {
		double targetX;
		if (world.areWeOnLeft()) {
			targetX = 590;
		} else {
			targetX = 50;
		}

		mover.move(0, 100);
		while (!shouldidie && !Strategy.alldie
				&& Math.abs(world.ourRobot.x - targetX) > 20) {
			SafeSleep.sleep(50);
		}
	}

	private void moveToGoalAndShoot() throws InterruptedException {
		double speedY, targetY = 240;
		if (world.ourRobot.y > 240) {
			speedY = 100;
		} else {
			speedY = -100;
		}
		mover.move(speedY, 0);
		while (!shouldidie && !Strategy.alldie
				&& Math.abs(world.ourRobot.y - targetY) > 50) {
			SafeSleep.sleep(50);
		}
		mover.kick();
		mover.stopRobot();
	}

	@Override
	public void run() {
		try {
			// Get to the side wall
			goToWall();
			if (shouldidie || Strategy.alldie)
				return;

			// Get to the wall their goal is on
			goToShortWall();
			if (shouldidie || Strategy.alldie)
				return;

			// Move towards their goal center and then shoot
			moveToGoalAndShoot();
		} catch (InterruptedException ignore) {
		}
	}

}

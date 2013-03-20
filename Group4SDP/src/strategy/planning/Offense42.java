package strategy.planning;

import movement.RobotMover;
import strategy.calculations.AngleCalculator;
import utility.SafeSleep;
import world.state.WorldState;

public class Offense42 extends StrategyInterface {

	public Offense42(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		try {
			while (!(shouldidie || Strategy.alldie)) {
				mover.resetQueue();
				// Get to the side wall
				mover.setSpeedCoef(0.5);
				// int i=0;
				// while (!(((world.ourRobot.x - world.ball.x) < 80) && ((world.ourRobot.x - world.ball.x) > 0)) &&
				// !(shouldidie || Strategy.alldie)) {
				double t = 0;
				if (world.ball.x < 500) {
					t = 20;
				}
				System.out.println("Ball: (" + world.ball.x + ", " + world.ball.y + ")");
				mover.moveToAStar(world.ball.x + 60 + t, world.ball.y - 5, true, true);
				mover.waitForCompletion();
				mover.stopRobot();
				mover.moveToAndStop(world.ball.x + 60 + t, world.ball.y);
				mover.waitForCompletion();
				mover.moveToAndStop(world.ball.x + 30, world.ball.y);
				mover.waitForCompletion();
				mover.stopRobot();
				// i++;
				// if (i>10) {
				// mover.move(100, 100);
				// SafeSleep.sleep(100);
				// mover.stopRobot();
				// i=7;
				// }
				// }
				if (shouldidie || Strategy.alldie)
					return;
				// AngleCalculator a = new AngleCalculator(world);
				// double pointBearing = a.findPointBearing(world.ourRobot, 0, world.ourRobot.y);
				// double angleToRotate = Math.toRadians(a.turnAngle(world.ourRobot.bearing, pointBearing));
				// double angleToRotate = mover.angleCalculator(world.ourRobot.x, world.ourRobot.y, 0, world.ourRobot.y,
				// world.ourRobot.bearing);
				// if (Math.abs(angleToRotate) > 15) {
				double angleToRotate = mover.angleCalculator(world.ourRobot.x, world.ourRobot.y, 0, world.ourRobot.y, world.ourRobot.bearing);
				System.out.println("Angle to rotate: " + angleToRotate);
				mover.rotate(angleToRotate);
				mover.waitForCompletion();
				// }
				mover.stopRobot();
				if (shouldidie || Strategy.alldie)
					return;
				// Get to the wall their goal is on
				mover.moveTowards(world.ball.x, world.ball.y);
				SafeSleep.sleep(50);
				if (shouldidie || Strategy.alldie)
					return;
				if ((world.whoHasTheBall() == 1 && world.areWeBlue()) || (world.whoHasTheBall() == 2 && !world.areWeBlue())) {
					if (world.ourRobot.y < 240) {
						if (world.theirRobot.y < 200) {
							mover.moveTo(70, 260);
						} else {
							mover.moveTo(70, 170);
						}
						mover.waitForCompletion();
						// SafeSleep.sleep(5000);
						// mover.move(-100, 0);
						// SafeSleep.sleep(50);
						mover.stopRobot();
					} else {
						if (world.theirRobot.y > 280) {
							mover.moveTo(70, 210);
						} else {
							mover.moveTo(70, 310);
						}
						mover.waitForCompletion();
						// SafeSleep.sleep(5000);
						// mover.move(100, 0);
						// SafeSleep.sleep(50);
						mover.stopRobot();
					}
					// SafeSleep.sleep(5000);
					mover.kick();
					mover.waitForCompletion();
					mover.stopRobot();
				}
				// Move towards their goal center and then shoot
				mover.setSpeedCoef(1.0);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

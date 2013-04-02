package strategy.planning;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import movement.RobotMover;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.PossessionType;
import world.state.Robot;
import world.state.WorldState;

public class DribbleBall5 {

	private final ScheduledExecutorService sleepScheduler = Executors
			.newScheduledThreadPool(1);

	/** Thread-safe sleep */
	private void safeSleep(long millis) throws InterruptedException {
		final Semaphore sleepSem = new Semaphore(0, true);
		// Schedule a wake-up after the specified time
		sleepScheduler.schedule(new Runnable() {
			@Override
			public void run() {
				sleepSem.release();
			}
		}, millis, TimeUnit.MILLISECONDS);
		// Wait for the wake-up
		sleepSem.acquire();
	}

	public static boolean die = false;

	public void dribbleBall(WorldState worldState, RobotMover mover)
			throws InterruptedException {
		// Get robot and ball from world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;

		// Determine a different position behind the ball depending on which
		// door we are shooting in
		while (!die) {
			if (die)
				return;
			if (worldState.areWeOnLeft()) {
				double slope = (242 - ball.y) / (610 - ball.x);
				if (ball.y > 240) {
					slope = -slope;
				}
				if (((ball.y + slope*70) < 100) || ((ball.y + slope*70) > 370)){
					slope = 0;
				}
				while (worldState.distanceBetweenUsAndBall() > 90 && !die &&
						!(worldState.getPosession() == PossessionType.Us && worldState.getPosession() == PossessionType.Them)){
					mover.moveToAStar(ball.x - 70, ball.y + slope * 70, true, true);
					mover.waitForCompletion();
					if (worldState.getPosession() == PossessionType.Us &&
							worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
						mover.kick();
					}
				}
				//mover.moveToAStar(ball.x - 70, ball.y + slope * 70, true, true);
				//mover.waitForCompletion();
				if (worldState.distanceBetweenUsAndBall() > 150) continue;
				RobotMover.distanceThreshold = 25;
				//mover.setSpeedCoef(0.8);
				//mover.moveToAndStop(ball.x - 60, ball.y + slope * 60);
				mover.moveTo(ball.x - 60, ball.y + slope * 60);
				mover.waitForCompletion();
				System.out.println("Angle to their goal " + worldState.angleToTheirGoal());
				if (worldState.getPosession() == PossessionType.Us &&
						worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
					mover.kick();
					
				}
				mover.stopRobot();
				mover.waitForCompletion();
				mover.setSpeedCoef(1);
			} else {
				double slope = (244 - ball.y) / (28 - ball.x);
				if (ball.y < 240) {
					slope = -slope;
				}
				if (((ball.y + slope*70) < 100) || ((ball.y + slope*70) > 370)){
					slope = 0;
				}
				while (worldState.distanceBetweenUsAndBall() > 90 && !die &&
						!(worldState.getPosession() == PossessionType.Us && worldState.getPosession() == PossessionType.Them) ){
					mover.moveToAStar(ball.x + 70, ball.y + slope * 70, true, true);
					mover.waitForCompletion();
					if (worldState.getPosession() == PossessionType.Us &&
							worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
						mover.kick();
					}
				}
				//mover.moveToAStar(ball.x - 70, ball.y + slope * 70, true, true);
				//mover.waitForCompletion();
				if (worldState.distanceBetweenUsAndBall() > 150) continue;
				RobotMover.distanceThreshold = 25;
				//mover.setSpeedCoef(0.8);
				//mover.moveToAndStop(ball.x + 60, ball.y + slope * 60);
				mover.moveTo(ball.x + 60, ball.y + slope * 60);
				mover.waitForCompletion();
				System.out.println("Angle to their goal " + worldState.angleToTheirGoal());
				if (worldState.getPosession() == PossessionType.Us &&
						worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
					mover.kick();
				}
				mover.stopRobot();
				mover.waitForCompletion();
				mover.setSpeedCoef(1);
			}
			if (worldState.distanceBetweenUsAndBall() > 150) continue;
			double angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
			safeSleep(50);
			int attempt = 0;
			System.out.printf("Angle is, %d\n", (int) angle);
			while (Math.abs(angle) > 5 && attempt < 10) {
				if ((Math.abs(angle) > 5) && (Math.abs(angle) < 50)) {
					mover.rotate(Math.toRadians(angle / 2));
					mover.waitForCompletion();
				} else if (Math.abs(angle) > 50) {
					mover.rotate(Math.toRadians(angle / 2));
					mover.waitForCompletion();
				}
				++attempt;
				if (worldState.getPosession() == PossessionType.Us &&
						worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
					mover.kick();
				}
				angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
				System.out.println("Angle for rotation " + angle);
				safeSleep(10);
				if (die)
					return;
			}
			if (worldState.distanceBetweenUsAndBall() > 150) continue;
			System.out.println("Reached position behind the ball!");
			
			// In case the ball is not straight on the goal, we need to
			// Move near the ball, rotate and then move more.
			if (worldState.areWeOnLeft()) {
				angle = TurnToBall.AngleTurner(us, 610, 242);
			} else {
				angle = TurnToBall.AngleTurner(us, 28, 242);
			}
			// If the angle to the goal is > 10 degrees
			// if (angle > 10){
			if (worldState.distanceBetweenUsAndBall() > 150) continue;
			if (worldState.getPosession() == PossessionType.Us &&
					worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
				mover.kick();
			}
			mover.move(0, 60);
			System.out.println("Moving towards the ball");
			if (worldState.getPosession() == PossessionType.Us &&
					worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
				mover.kick();
			}
			safeSleep(600);
			
			if (worldState.getPosession() == PossessionType.Us &&
					worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
				mover.kick();
			}
			System.out.println("Moving towards the ball finished");
			mover.stopRobot();
			mover.waitForCompletion();
			if (worldState.distanceBetweenUsAndBall() > 150) continue;
			if (worldState.getPosession() == PossessionType.Us &&
					worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
				mover.kick();
			}
			mover.rotate(Math.toRadians(angle));
			mover.waitForCompletion();
			if (worldState.getPosession() == PossessionType.Us &&
					worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
				mover.kick();
			}
			if (worldState.distanceBetweenUsAndBall() > 150) continue;
			// }

			// Now we move to the goal and then kick
			//mover.move(0, 80);
			if (worldState.distanceBetweenUsAndBall() > 150) continue;
			mover.moveTowards(worldState.getTheirGoal().getX(), worldState.getTheirGoal().getY());
			if (worldState.distanceBetweenUsAndBall() > 150) continue;
			long starttime = System.currentTimeMillis();
			long finishtime = 0;
			while (finishtime <1500){
				if (worldState.getPosession() == PossessionType.Us &&
						worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
					mover.kick();
					break;
				}
				safeSleep(50);
				finishtime = System.currentTimeMillis() - starttime;
			}
			//safeSleep(1500);
			/*if (worldState.areWeOnLeft()) {
				while (Math.abs(ball.x - 610) > 300) {
					safeSleep(10);
				}
			} else {
				while (Math.abs(ball.x - 28) > 300) {
					safeSleep(10);
				}
			}*/

			if (worldState.getPosession() == PossessionType.Us &&
					worldState.angleToTheirGoal() <30 && worldState.angleToTheirGoal() > -30){
				mover.kick();
			}
			mover.stopRobot();
			mover.waitForCompletion();
		}
	}
}

package strategy.planning;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import movement.RobotMover;
import strategy.movement.TurnToBall;
import vision.Position;
import world.state.Ball;
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

	private static final double xthreshold = 70;
	private static final double ythreshold = 70;
	private static double dribbleDistance = 200;

	public void dribbleBall(WorldState worldState, RobotMover mover)
	throws InterruptedException {
		// Get robot and ball from world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;

		// Find which goal we need to be shooting.
		Position targetgoal = worldState.getOurGoal();
		int goalx = targetgoal.getX();
		int goaly = targetgoal.getY();

		//Determine a different position behind the ball depending on which door we are shooting in
		if (worldState.areWeOnLeft()){		
			double slope = (238 - ball.y)/(606 - ball.x);
			if (die)
				return;
			mover.moveToAStar(ball.x - 70, ball.y + slope*70 , true);
			mover.waitForCompletion();
			safeSleep(50);
			mover.moveToAndStop(ball.x - 70, ball.y + slope*70);
			mover.waitForCompletion();

		} else {
			double slope = (33 - ball.y)/(247 - ball.x);
			mover.moveToAStar(ball.x + 70, ball.y + 70*slope, true);
			mover.waitForCompletion();
			safeSleep(50);
			mover.moveToAndStop(ball.x + 70, ball.y + 70*slope);
			mover.waitForCompletion();
		}
		double angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
		safeSleep(50);
		System.out.println("Angle for rotation " + angle);
		int attempt = 0;
		System.out.printf("Angle is, %d\n", (int)angle);
		while (Math.abs(angle) > 15 && attempt < 10) {
			if ((Math.abs(angle) > 0) && (Math.abs(angle) < 50)) {
				System.out.println("Code block reached");
				mover.rotate(Math.toRadians(angle / 2));
				mover.waitForCompletion();
			} else if (Math.abs(angle) > 50) {
				System.out.println("Other code block reached");
				mover.rotate(Math.toRadians(angle));
				mover.waitForCompletion();
			}
			++attempt;
			angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
			safeSleep(50);
			if (die)
				return;
		}

		System.out.println("Reached position behind the ball!");

		if (die)
			return;
/* Shouldn't be necessary
		System.out.println("Adjusting to goal.");
		double adjustAngle;
		if (worldState.areWeOnLeft()){
			adjustAngle = TurnToBall.AngleTurner(us, 606, 238);
			System.out.println("Angle for rotation " + angle);
			int attempt2 = 0;
			System.out.printf("Angle is, %d\n", (int)angle);
			while (Math.abs(angle) > 5 && attempt < 10) {
				if ((Math.abs(angle) > 0) && (Math.abs(angle) < 50)) {
					System.out.println("Code block reached");
					mover.rotate(Math.toRadians(angle / 2));
					mover.waitForCompletion();
				} else if (Math.abs(angle) > 50) {
					System.out.println("Other code block reached");
					mover.rotate(Math.toRadians(angle));
					mover.waitForCompletion();
				}
				//Thread.sleep(50);
				++attempt2;
				angle = TurnToBall.AngleTurner(us, 606, 238);
				if (die)
					return;
			}
		}
		else{
			adjustAngle = TurnToBall.AngleTurner(us, 33, 247);
			System.out.println("Angle for rotation " + angle);
			int attempt2 = 0;
			System.out.printf("Angle is, %d\n", (int)angle);
			while (Math.abs(angle) > 5 && attempt < 10) {
				if ((Math.abs(angle) > 0) && (Math.abs(angle) < 50)) {
					System.out.println("Code block reached");
					mover.rotate(Math.toRadians(angle / 2));
					mover.waitForCompletion();
				} else if (Math.abs(angle) > 50) {
					System.out.println("Other code block reached");
					mover.rotate(Math.toRadians(angle));
					mover.waitForCompletion();
				}
				//Thread.sleep(50);
				++attempt2;
				angle = TurnToBall.AngleTurner(us, 33, 247);
				if (die)
					return;
			}
		}

		//Thread.sleep(50);
		 * 
		 */
		if (die)
			return;
		mover.move(0, 50);

		double previousx = ball.x;
		double previousy = ball.y;
		//double previousy = ball.y;
		while(true){
			if (((Math.abs(previousx - ball.x)) > 10) ||
					((Math.abs(previousy) - ball.y) >10)){
				break;
			}
		}

		mover.kick();
		if (die)
			return;
		mover.stopRobot();
		mover.waitForCompletion();

	}
}

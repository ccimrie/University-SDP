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
		if (die)
			return;
		if (worldState.areWeOnLeft()){		
			double slope = (238 - ball.y)/(606 - ball.x);
			mover.moveToAStar(ball.x - 70, ball.y + slope*70 , true);
			mover.delay(50);
			mover.moveToAndStop(ball.x - 70, ball.y + slope*70);
			mover.waitForCompletion();

		} else {
			double slope = (33 - ball.y)/(247 - ball.x);
			mover.moveToAStar(ball.x + 70, ball.y + 70*slope, true);
			mover.delay(50);
			mover.moveToAndStop(ball.x - 70, ball.y + slope*70);
			mover.waitForCompletion();
		}
		double angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
		safeSleep(50);
		int attempt = 0;
		System.out.printf("Angle is, %d\n", (int)angle);
		while (Math.abs(angle) > 15 && attempt < 10) {
			if ((Math.abs(angle) > 10) && (Math.abs(angle) < 50)) {
				mover.rotate(Math.toRadians(angle / 3));
				mover.waitForCompletion();
			} else if (Math.abs(angle) > 50) {
				mover.rotate(Math.toRadians(angle/2));
				mover.waitForCompletion();
			}
			++attempt;
			angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
			System.out.println("Angle for rotation " + angle);
			safeSleep(50);
			if (die)
				return;
		}

		System.out.println("Reached position behind the ball!");

		mover.move(0, 50);

		mover.delay(800);
		//double previousy = ball.y;
		/*while(true){
			if (((Math.abs(previousx - ball.x)) > 10) ||
					((Math.abs(previousy) - ball.y) >10)){
				break;
			}
		}*/

		mover.kick();
		mover.delay(20);
		mover.stopRobot();
		mover.waitForCompletion();

	}
}

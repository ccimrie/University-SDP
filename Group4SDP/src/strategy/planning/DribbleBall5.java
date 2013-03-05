package strategy.planning;

import movement.RobotMover;
import strategy.movement.TurnToBall;
import vision.Position;
import world.state.Ball;
import world.state.Robot;
import world.state.WorldState;

public class DribbleBall5 {
	public static boolean die = false;

	private static final double xthreshold = 70;
	private static final double ythreshold = 70;
	private static double dribbleDistance = 200;
	private MoveToPoint moveToPoint = new MoveToPoint();

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
			if (die)
				return;
			mover.moveToAStar(ball.x - 70, ball.y, true);
			while(true){
				double diffx;
				double diffy;
				diffx = ball.x - 70 - us.x;
				diffy = ball.y - us.y;
				System.out.printf("Waiting for movement to finish, current diff is x %d, y %d\n", 
						(int)diffx, (int)diffy);
				if (Math.abs(diffx) < 40 && (Math.abs(diffy) <40)){
					break;
				}
				Thread.sleep(50);
			}
			//mover.waitForCompletion();
			if (die)
				return;
			Thread.sleep(50);
		}
		else {
			if (die)
				return;
			mover.moveToAStar(ball.x + 70, ball.y, true);
			while(true){
				double diffx;
				double diffy;
				diffx = ball.x - 70 - us.x;
				diffy = ball.y - us.y;
				System.out.printf("Waiting for movement to finish, current diff is x %d, y %d\n", 
						(int)diffx, (int)diffy);
				if (Math.abs(diffx) < 40 && (Math.abs(diffy) <40)){
					break;
				}
				Thread.sleep(50);
			}
			if (die)
				return;
			Thread.sleep(50);
		}
		double angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
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
			Thread.sleep(50);
			++attempt;
			angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
			if (die)
				return;
		}
		
		// Move to the ball before stoping
		mover.move(0,80);
		mover.waitForCompletion();
		if (die)
			return;
		Thread.sleep(50);
		mover.stopRobot();
		mover.waitForCompletion();
		
		System.out.println("Reached position behind the ball!");
		/*
		 * if(us.y < 241){ double adjustAngle = TurnToBall.AngleTurner(us, 600,
		 * 310); System.out.println("Angle for rotation " + adjustAngle);
		 * mover.rotate(Math.toRadians(adjustAngle*0.8));
		 * mover.waitForCompletion(); } else { double adjustAngle =
		 * TurnToBall.AngleTurner(us, 600, 160);
		 * System.out.println("Angle for rotation " + adjustAngle); }
		 * mover.rotate(Math.toRadians(adjustAngle*0.8));
		 * mover.waitForCompletion();
		 */

		// Rotate to face the middle of the right goal
		if (die)
			return;
		
		double adjustAngle;
		if (worldState.areWeOnLeft()){
			adjustAngle = TurnToBall.AngleTurner(us, 600, 241);
		}
		else{
			adjustAngle = TurnToBall.AngleTurner(us, 32, 250);
		}
		
		System.out.println("Angle for rotation " + adjustAngle);
		mover.rotate(Math.toRadians(adjustAngle));
		mover.waitForCompletion();

		if (die)
			return;
		mover.move(0, 80);
		mover.waitForCompletion();
		Thread.sleep(90);

		mover.kick();
		if (die)
			return;
		mover.stopRobot();
		mover.waitForCompletion();
	
	}
}

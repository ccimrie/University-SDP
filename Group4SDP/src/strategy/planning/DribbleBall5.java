package strategy.planning;

import communication.BluetoothRobot;

import world.state.Ball;
import world.state.Robot;
import world.state.WorldState;
import strategy.movement.TurnToBall;

public class DribbleBall5 {

	private static final double xthreshold = 70;
	private static final double ythreshold = 70;
	private static double dribbleDistance = 200;
	private MoveToPoint moveToPoint = new MoveToPoint();
	private MoveToBall moveToBall = new MoveToBall();

	public void dribbleBall(WorldState worldState, BluetoothRobot robot)
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
		
		System.out.println("Reached adjusting fase");
		double angle = TurnToBall.AngleTurner(us, 600, 241);
		int attempt = 0;
		robot.stop();
		while (Math.abs(angle) > 15 && attempt < 10) {
			if ((Math.abs(angle) > 15) && (Math.abs(angle) < 50)) {
				System.out.println("Rotation " + angle/2);
				robot.rotate((int) (angle / 2));
			} else if (Math.abs(angle) > 50) {
				robot.rotate((int) angle/2);
				System.out.println("Rotation " + angle/2);
			}
			++attempt;
			angle = TurnToBall.AngleTurner(us, 600, 241);
			//System.out.println(angle);
		}
		robot.move(0, 40);
		Thread.sleep(30);
		robot.stop();
		System.out.println("Reached position behind the ball!");
		if(us.y < 241){
			double adjustAngle = TurnToBall.AngleTurner(us, 600, 310);	
			robot.rotate((int)(adjustAngle*0.8));
			System.out.println("Angle for rotation " + adjustAngle);
		} else {
			double adjustAngle = TurnToBall.AngleTurner(us, 600, 160);
			robot.rotate((int)(adjustAngle*0.8));
			System.out.println("Angle for rotation " + adjustAngle);
		}
//		//Rotate to face the middle of the right goal
//		double adjustAngle = TurnToBall.AngleTurner(us, 600, 241);
//		robot.rotate((int)(1.5*adjustAngle));
//		System.out.println("Angle for rotation " + adjustAngle);
		
		
			
			robot.move(0, 80);
			
//			// Stop preferably at 200 pixels, but 550 if we hit the edge of the
//			// pitch first
			double stopX = us.x + 30;
			while (us.x < stopX){
				Thread.sleep(50);
			}
			robot.kick();
			robot.stop();
//			
////			//Move to the ball in case we lost it
////			moveToBall.approach(worldState, robot);
////			
////			//Kick into the goal
////			robot.kick();
//			
		
		
	}
}

package strategy.planning;

import strategy.movement.TurnToBall;
import vision.Position;
import world.state.Ball;
import world.state.Robot;
import world.state.WorldState;
import movement.RobotMover;

import communication.RobotController;

public class DribbleBall5 {

	private static final double xthreshold = 70;
	private static final double ythreshold = 70;
	private static double dribbleDistance = 200;
	private MoveToPoint moveToPoint = new MoveToPoint();
	

	public void dribbleBall(WorldState worldState, RobotController robot, RobotMover mover)
			throws InterruptedException {
		// Get robot and ball from world
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
		Ball ball = worldState.ball;
		//Find which goal we need to be shooting.
		Position targetgoal = worldState.getOurGoal();
		int goalx = targetgoal.getX();
		int goaly = targetgoal.getY();
		
		synchronized (mover) {
		mover.moveToAStar(ball.x - 70, ball.y, true);
		mover.wait();
		}
		
		double angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
		int attempt = 0;
		while (Math.abs(angle) > 15 && attempt < 10) {
			if ((Math.abs(angle) > 15) && (Math.abs(angle) < 50)) {
				System.out.println("Code block reached");
				mover.rotate(Math.toRadians(angle / 2));
			} else if (Math.abs(angle) > 50) {
				System.out.println("Other code block reached");
				mover.rotate(Math.toRadians(angle));
			}
			++attempt;
			angle = TurnToBall.AngleTurner(us, ball.x, ball.y);
		}
		synchronized (mover) {
			mover.moveToAndStop(ball.x - 20, ball.y); //Move to the ball before stoping
			mover.wait();
		}
		System.out.println("Reached position behind the ball!");
		/*if(us.y < 241){
			double adjustAngle = TurnToBall.AngleTurner(us, 600, 310);	
			robot.rotate((int)(adjustAngle*0.8));
			System.out.println("Angle for rotation " + adjustAngle);
		} else {
			double adjustAngle = TurnToBall.AngleTurner(us, 600, 160);
			robot.rotate((int)(adjustAngle*0.8));
			System.out.println("Angle for rotation " + adjustAngle);
		}*/
	
		//Rotate to face the middle of the right goal
		double adjustAngle = TurnToBall.AngleTurner(us, 600, 241);
		robot.rotate((int)(adjustAngle));
		System.out.println("Angle for rotation " + adjustAngle);

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




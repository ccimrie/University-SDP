package strategy.planning;

import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;
import world.state.RobotController;
import vision.Vision;
import vision.WorldState;

public class MoveToPoint{
	
	private static final int distanceFromBallToStop = 60;
	private static boolean rotating = false;
	
	private Vision vision;

	public static void moveToPoint(WorldState worldState, RobotController robot, double moveToX, double moveToY)  throws InterruptedException{
		
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
    	Ball ball = worldState.ball;
    	
    	// Plan:
    	// 0. Get bearings
    	// 1. Turn to face ball
    	// 2. Move forwards
		double distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY);
        System.out.println(String.format("Distance to ball is %f", distance));
		double angle = TurnToBall.AngleTurner(us, moveToX, moveToY);
        System.out.println(String.format("Angle of point to robot is %f", angle));
        
//        if(rotating && rc.isMoving()) {
//        	// This is to simulate turning "blocking"
//        	System.out.println("Still turning");
//        	return;
//        }
//        rotating = false;
        
		if(Math.abs(angle) > 20) {
			// Stop everything and turn
			System.out.println("Stop and turn");
			robot.stop();
			robot.rotate((int)angle);
			rotating = true;
			// We don't want to carry on after this command!
			// This also removes the need for that else block
			//return;
		}
		
		 while(distance > distanceFromBallToStop) {
				//System.out.println("Forward");
				angle = TurnToBall.AngleTurner(us, moveToX, moveToY);
				System.out.println("The final angle is " + angle);
				if((Math.abs(angle) > 20) && (Math.abs(angle) < 40) ) {
					//Stop everything and turn
					robot.stop();
					if (angle>0){
						System.out.println("Rotating 5");
						robot.rotate(5);
					}else{
						System.out.println("Rotating -5");
						robot.rotate(-5);
					}
					Thread.sleep(1000);
				}else if (Math.abs(angle) > 40){
					robot.stop();
					System.out.println("Rotating " + (int)angle / 2);
					robot.rotate((int)angle/2);
					Thread.sleep(2000);
				}
				robot.move(0,10);
				distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY);
				System.out.println("Distance to ball: " + distance);
//				System.out.println("Distance to ball: " + distance);
				Thread.sleep(100);
				//return;
			}
		
		// Being close to the ball we can perform one last minor turn
//		if(Math.abs(angle) > 10) {
//			// Stop everything and turn
//			System.out.println("Making final correction");
//			rc.stop();
//			rc.rotate(-angle);
//			rotating = true;
//			// We don't want to carry on after this command!
//			// This also removes the need for that else block
//			return;
//		}
		
		System.out.println("Stop");
		robot.stop();
		
	}
	
}

package strategy.planning;

import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import vision.Vision;
import vision.WorldState;
import world.state.Ball;
import world.state.Robot;
import world.state.RobotController;

public class MoveToBall2 {
	
	private static final int distanceFromBallToStop = 60;
	private static boolean rotating = false;
	//private WorldState t = new WorldState();
	private Vision vision;
	//private static RobotController robot;
	
	public void approach(WorldState worldState, RobotController robot) throws InterruptedException {
		// First we turn to the ball
		//this.vision = vis;
		//WorldState t = new WorldState();
		
		worldState.setOurRobot();
		System.out.println(worldState.areWeBlue());
    	Robot us = worldState.ourRobot;
    	Ball ball = worldState.ball;
    	
    	// Plan:
    	// 0. Get bearings
    	// 1. Turn to face ball
    	// 2. Move forwards
		double distance = DistanceToBall.Distance(us.x, us.y, ball.x, ball.y);
        System.out.println(String.format("Distance to ball is %f", distance));
		double angle = TurnToBall.Turner(us, ball);
       
        System.out.println(String.format("Angle of ball to robot is %f", angle));

        
		if(Math.abs(angle) > 20) {
			// Stop and turn
			System.out.println("Stop and turn");
			robot.stop();
			robot.rotate((int)angle);
			rotating = true;
			// We don't want to carry on after this command!
			// This also removes the need for that else block
			//angle = TurnToBall.Turner(us, ball);
			//return;
			
		}
	
		 while(distance > distanceFromBallToStop) {
			//System.out.println("Forward");
			angle = TurnToBall.Turner(us, ball);
			if((Math.abs(angle) > 30) && (Math.abs(angle) < 40) ) {
				//Stop everything and turn
				System.out.println("The final angle is " + angle);
				robot.stop();
				if (angle>0){
					robot.rotate(5);
				}else{
					robot.rotate(-5);
				}
				Thread.sleep(1000);
			}else if (Math.abs(angle) > 40){
				robot.stop();
				robot.rotate((int)angle/2);
				Thread.sleep(2000);
			}
			robot.move(0,10);
			distance = DistanceToBall.Distance(us.x, us.y, ball.x, ball.y);
			System.out.println("Distance to ball: " + distance);
//			System.out.println("Distance to ball: " + distance);
			Thread.sleep(100);
			//return;
		}
		
//		// Being close to the ball we can perform one last minor turn
//		if(Math.abs(angle) > 10) {
//			// Stop everything and turn
//			System.out.println("Making final correction");
//			robot.stop();
//			robot.rotate((int) angle);
//			//rotating = true;
//			// We don't want to carry on after this command!
//			// This also removes the need for that else block
//			//return;
//		}
		
		robot.stop();
		
	}
///	public WorldState getWorldState(){
//		return this.vision.getWorldState();
//	}
	
}

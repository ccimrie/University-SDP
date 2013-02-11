package strategy.planning;

import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import world.state.Robot;
import world.state.RobotController;
import vision.WorldState;

public class MoveToPoint{
	
	private static final int distanceFromPointToStop = 60;

	public static void moveToPoint(WorldState worldState, RobotController robot, double moveToX, double moveToY)  throws InterruptedException{
		
		worldState.setOurRobot();
		Robot us = worldState.ourRobot;
    	
    	// Plan:
    	// 0. Get bearings
    	// 1. Turn to face ball
    	// 2. Move forwards
		double distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY);
        System.out.println(String.format("Distance to point is %f", distance));
		double angle = TurnToBall.AngleTurner(us, moveToX, moveToY);
        System.out.println(String.format("Angle of point to robot is %f", angle));
        
        
		if(Math.abs(angle) > 20) {
			// Turn to angle required
			System.out.println("Stop and turn");
			robot.stop();
			robot.rotate((int)angle);
		}
		
		 while(distance > distanceFromPointToStop) {
			 	
				angle = TurnToBall.AngleTurner(us, moveToX, moveToY);
			
				if((Math.abs(angle) > 20) && (Math.abs(angle) < 40) ) {
					//Stop everything and turn
					System.out.println("The final angle is " + angle);
					robot.stop();
					robot.rotate((int)(angle/5));
				}else if (Math.abs(angle) > 40){
						robot.stop();
						robot.rotate((int)(angle/2));
				}

				robot.move(0,10);
				distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY);
				System.out.println("Distance to ball: " + distance);
				Thread.sleep(100);
			}

			// Being close to the ball we can perform one last minor turn
			if(Math.abs(angle) > 10) {
				// Stop everything and turn
				System.out.println("Making final correction");
				robot.stop();
				robot.rotate((int) angle);
			}else{
				robot.stop();
			}		
		}
	
}

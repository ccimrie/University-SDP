package simulatorold;

import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;

public class dummy_rotate {
	private static final int distanceFromBallToStop = 60;
	/**
	 * @param args
	 */
	public static void dummy(SimWorld world, SimServer rc)
	{ 
		
		
	   	Robot us = world.getOurRobot();
    	Ball ball = world.getBall();
    	double angle = (us.bearing);
    	double angle2 = TurnToBall.Turner(us, ball);
    	double angle3 = TurnToBall.findBearing(us, ball);
    	
    	try {
			gotoball.approach(world, rc);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	angle  = TurnToBallSim.AngleTurner(us,
				ball.x, ball.y);
		//rc.rotate(angle);
		//rc.stop();
		
    	/**while (true) {
    		
    		//rc.rotate(TurnToBallSim.Turner(us,
    		//		ball));
    	//	System.out.println("bearing: "+ Math.toDegrees(us.bearing));
    		//System.out.println("x: "+ us.x + "y: " + us.y);
    		
    	System.out.println(TurnToBallSim.AngleTurner(us,
    				ball.x, ball.y));
    		
    		
    		
    	}
    	*/

	}

}

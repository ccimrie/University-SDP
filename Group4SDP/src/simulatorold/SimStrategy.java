package simulatorold;


public class SimStrategy extends Thread{
	
	private static final float distanceFromBallToStop = 8.0f ;
	
	SimWorld world;
	SimServer rc;
	
	public SimStrategy(SimWorld world, SimServer rc)
	{
		this.world = world;
		this.rc = rc;
	}
	
	public void update() {
		/**
		// First we turn to the ball
    	Robot us = world.ourRobot;
    	Ball ball = world.ball;
    	
    	// Plan:
    	// 0. Get bearings
    	// 1. Turn to face ball
    	// 2. Move forwards
		double distance = DistanceToBall.Distance(us.x, us.y, ball.x, ball.y);
        System.out.println(String.format("Distance to ball is %f", distance));
		double angle = TurnToBall.Turner(us, ball);
        System.out.println(String.format("Angle of ball to robot is %f", angle));
        
        if(rc.isTurning) {
        	// This is to simulate turning "blocking"
        	return;
        }
        
		if(Math.abs(angle) > 30) {
			// Stop everything and turn
			System.out.println("Stop and turn");
			rc.stop();
			rc.rotate(-angle);
			// We don't want to carry on after this command!
			// This also removes the need for that else block
			return;
		}
		
		if(distance > distanceFromBallToStop) {
			System.out.println("Forward");
			rc.forward();
			return;
			// Let's not arc for this milestone as it's too complicated
			/*if(Math.abs(angle) > 10) {
				//TODO: Perfect this with different values for the arc radius (maybe relate it to distance / angle)
				System.out.println("Arcing");
				int direction;
				if (angle > 0) {
					direction = 1;
				} else {
					direction = -1;
				}
				rc.arcForward(direction * 0.25);
			} else {
				System.out.println("Forward");
				rc.forward();
				
			}
			return;
			
			
		}
		
	//	System.out.println("Stop");
	//	rc.stop();
		*/
	}
	
	public void run()
	{
		System.out.println("started strategy");
		System.out.println(world.ball.x);
		
		rc.setDefaultRotateSpeed(2.0d);
		dummy_rotate.dummy(world, rc);
		
		//rc.rotate(Math.PI);
/**		while(true)
		{
			//update();
			//GoToPointSim.goToPoint(world, rc, world.ball.getPosition(), AvoidanceStrategy.AvoidingBall);
			//dummy_rotate.goToPoint(world, rc);
	
			

	
		
			try {
				
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
		}*/
	}

}

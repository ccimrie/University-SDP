package strategy.planning;

import geometry.Vector;

import java.util.Observable;
import java.util.Observer;

import strategy.calculations.IsRobotFacingPoint;
import strategy.calculations.Possession;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import strategy.movement.TurnToBall;
import world.state.Ball;
import world.state.Robot;
import world.state.RobotType;

public class ScoreFromAnywhere extends Strategy implements Observer {
	
	public int step = 0;
	Offensive o = new Offensive();
	double speed = 0.7;
	double delta = 0.015;
	
	private final double edgeSafeDist = 25;
	public final float pitchL = 600; //625;//600;
    public final float pitchW = 330; //315;//330;
    int rotateBackCounter = 0;
    
    private int stuckCounter = 10;
	private int goBackCounter = 0;
	
	@Override
	public void update(Observable obj, Object arg) {
	
		Robot us = world.ourRobot;
    	Ball ball = world.ball;
    	
    	System.out.println("step: " + step);
    	
    	System.out.println("gobackcounter: " + goBackCounter);
    	System.out.println("stuckCounter : " + stuckCounter);

    	// When we are stuck
    	
    	// go back counter is 0 by default and is being
    	// set to 7 in case we are stuck for 10 frames
    	if(goBackCounter > 0)
		{
    		// we move backwards
			rc.travel(-0.3d, 0.3d);
			goBackCounter --;
		}
		else if(stuckCounter <= 0)
		{
			// if we are stuck for 10 frames, reset
			// stuck counter and set go back counter
			stuckCounter = 10;
			goBackCounter = 7;
		}
		else if (rc.isTouchPressed()) {
			//rc.travel(0.3d, -1d);
			stuckCounter--;
			System.out.println("touch");
	    	System.out.println("stuckCounter : " + stuckCounter);

		}
		else
		{
    	
	    	if(step == -1)
		    {
	    		//debugging
	    		//System.out.println("Ball: x = " + ball.x + "\ty = " + ball.y);
	    		//System.out.println("Possession: " + Possession.hasPossession(world, RobotType.Us));
				//double distUsBall1 = Math.sqrt((us.x - ball.x)*(us.x - ball.x) + (us.y - ball.y)*(us.y - ball.y));
		    	//System.out.println("dist: " + distUsBall1 );
		    	System.out.println("angle: " + TurnToBall.Turner(us, ball) );
	    	}
	    	
	    	if(step == 0)
	    	{
	    		//rotateBackCounter = 0;
	    		
	    		speed = 0.5;
		    	double targetDistFromBall = 100.0d;
		    	
		    	
		    	rc.setDefaultRotateSpeed(60.0);
		    	rc.setDefaultTravelSpeed(speed);
		    	
		    	double ax = world.getTheirGoal().getX();
				double ay = world.getTheirGoal().getY();
				
				double bx = ball.x;
				double by = ball.y;
				
				double distAB = Math.sqrt((bx-ax)*(bx-ax) + (by-ay)*(by-ay));
		    	
		    	double destX = bx + targetDistFromBall*(bx-ax)/distAB;
		    	double destY = by + targetDistFromBall*(by-ay)/distAB;
		    	
		    	//"cut" the coordinates so that we don't crash into the wall
		    	
		    	if (destX < 39 + edgeSafeDist)
		    		destX = 39 + edgeSafeDist;
		    	else if (destX > 39 + pitchL - edgeSafeDist)
		    		destX = 39 + pitchL - edgeSafeDist;
		    	
		    	if (destY < 46 + edgeSafeDist)
		    		destY = 46 + edgeSafeDist;
		    	else if (destY > 46 + pitchW - edgeSafeDist)
		    		destY = 46 + pitchW - edgeSafeDist;
		    	
		    	double distanceSqr = (us.x - destX)*(us.x - destX) + (us.y - destY)*(us.y - destY);
		    	
		    	double distUsBall = Math.sqrt((us.x - bx)*(us.x - bx) + (us.y - by)*(us.y - by));
		    	System.out.println("dist: " + distUsBall );
		    	if(distanceSqr > 30*30)
		    		GoToPoint.goToPoint(world, rc, new Vector(destX, destY), AvoidanceStrategy.AvoidingBall);
		    	else
		    	{
		    		step = 1;
					//rc.stop();
					//stop();
		    	}
	    	
	    	}
	    	else if(step == 1)
	    	{
	    		double angle = TurnToBall.Turner(us, ball);
	    		if ((Math.abs(angle) > 5))// && (rotateBackCounter != 0))
	    		{
	    			//double angSign = Math.signum(angle);
	    			System.out.println("angle diff: " + angle);
	    			rc.rotate(-angle);
	    			//rc.rotate(-200*angSign);
	    		}
	    		else
	    		{
	    			if(rotateBackCounter>0)
	    			{
	    				double angSign = Math.signum(angle);
	    				rc.rotate(5.0*angSign);
	    				System.out.println("##################Trying to trun back...##########################");
	    				rotateBackCounter--;
	    			}
	    			else
	    			{
	    				step = 2;
	    				rc.stop();
	    			}
					//stop();
	    		}
	    	}
	    	
	    	else if(step == 2)
	    	{
	    		double distUsBall1 = Math.sqrt((us.x - ball.x)*(us.x - ball.x) + (us.y - ball.y)*(us.y - ball.y));
	    		System.out.println("dist: " + distUsBall1 );
		    	System.out.println("bearing: " + TurnToBall.findBearing(us, ball));
	    		
	    		if(Possession.hasPossession(world, RobotType.Us))
	    		{
	    			step = 3;
	    			//GoToPoint.goToPoint(world, rc, world.getTheirGoal().getX(), world.getTheirGoal().getY(), 2);
	    		}
	    		else
	    		{
	    			speed = 0.15;
	        		rc.setDefaultTravelSpeed(speed);
	    			GoToPoint.goToPoint(world, rc, ball.getPosition(), AvoidanceStrategy.IgnoringBall);
	    		}
	    	}
	    	else if(step == 3)
	    	{
	    		//if (!Possession.hasPossession(world, RobotType.Us)) {
	    		//	step = 0;
	    		//	return;
	    		//}
	    		//rc.kick();
	    		//o.offensive(world, rc);
	    		//rc.setDefaultTravelSpeed(0.4);
	    		speed += delta;
	    		rc.setDefaultTravelSpeed(speed);
	    		GoToPoint.goToPoint(world, rc, world.getTheirGoal(), AvoidanceStrategy.Aggressive);
	    		
	    		if(!((us.x < world.getMidPoint() && world.ourHalfLeft()) 
	    				|| (us.x >= world.getMidPoint() && !world.ourHalfLeft())))
	    			{
		    			double angle = IsRobotFacingPoint.Turner(us, world.getTheirGoal().getX(), world.getTheirGoal().getY());
		    			System.out.println(Math.abs(angle));
		    			if(Math.abs(angle) < 10)
		    			{
		    				rc.kick();
		    				step = 0;
		    			}
	    			}    		
	    	}
		}
	}
}
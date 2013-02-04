package strategy.planning;

import java.util.Observable;

import strategy.movement.FollowBall;
import world.state.PossessionType;
import world.state.World;

public class FollowFASTball extends Strategy{

	@Override
	public void update(Observable arg, Object obj) {
		
		//rc.setDefaultRotateSpeed(50.0);
    	rc.setDefaultTravelSpeed(0.45);
    	double val = 1.63;
		
		double tolerance = 2.0; //number of pixels that counts as the same 
//		System.out.println(Math.abs(world.ball.x-world.prevBall.x));
		if (world.hasPossession == PossessionType.Us){
			this.stopSmoothly();
			ScoreFromAnywhere s = new ScoreFromAnywhere();
			s.step = 3;
			s.execute();
		}
		else {
			if (Math.abs(world.ball.x-world.prevBall.x) > tolerance ||  
	    			Math.abs(world.ball.y-world.prevBall.y) > tolerance) {
	    			FollowBall follow = new FollowBall();
		    		follow.followBall(world, rc,val);
	    		
	    	}
		}
		
		
	}
	
}

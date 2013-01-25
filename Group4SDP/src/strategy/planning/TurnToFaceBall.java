package strategy.planning;

import java.util.Observable;
import strategy.movement.TurnToBall;

public class TurnToFaceBall extends Strategy {
	
	boolean finished = false;

	@Override
	public void update(Observable arg0, Object arg1) {
		rc.setDefaultRotateSpeed(50.0);
    	rc.setDefaultTravelSpeed(0.3);
    	rc.setAcceleration(0.05);
    	double angle = TurnToBall.Turner(world.ourRobot, world.ball);
    	if (!finished)
    		rc.rotate(-angle, 10.0);
    	
		finished = true;
		
    	/*
		if ((Math.abs(angle) > 15)) {
			rc.rotate(-angle, 10.0);

			return;
		}
		*/
		
	}

}

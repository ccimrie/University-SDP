package strategy.planning;

import java.util.Observable;

public class ShowTouchSensor extends Strategy {

	private boolean collisionInProgress;
	
	@Override
	public void update(Observable arg0, Object arg1) {
		rc.setDefaultRotateSpeed(50.0);
    	rc.setDefaultTravelSpeed(0.3);
    	
    	// OBSTACLE DETECTION STRATEGY
    	// When touch sensor is pressed, go back no matter what.
    	if (collisionInProgress) {
    		if (rc.isMoving()) {
    			return;
    		} else {
    			collisionInProgress = false;
    			return;
    		}
    	}
    	if (rc.isTouchPressed()) {
    		System.out.println("Touch sensor pressed");
			rc.travel(-0.3d, 0.3d);
			collisionInProgress = true;
			return;
		}
    	rc.forward();
	}

}

package strategy.planning;

import java.util.Observable;

public class DefensiveTest extends Strategy{

	@Override
	public void update(Observable arg, Object obj) {
		
		rc.setDefaultRotateSpeed(50.0);
    	rc.setDefaultTravelSpeed(0.3);
		
		Defensive d = new Defensive();
		
		d.defensive(world, rc);
		
	}
	
}

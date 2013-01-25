package strategy.planning;

import java.util.Observable;

public class OffensiveTest extends Strategy{

	@Override
	public void update(Observable arg, Object obj) {
		
		rc.setDefaultRotateSpeed(50.0);
    	rc.setDefaultTravelSpeed(0.3);
		
		Offensive o = new Offensive();
		
		o.offensive(world, rc);
		
	}
	
}

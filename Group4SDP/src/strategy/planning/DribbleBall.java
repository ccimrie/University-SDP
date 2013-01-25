
package strategy.planning;

import java.util.Observable;

public class DribbleBall extends Strategy {
	
	boolean isMoving = false;

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		/*if (isMoving) {
			stop();
		}
		
		if (Possession.hasPossession(world, RobotType.Us)) {
			rc.travel(0.3,0.3);
			isMoving = true;
			return;
		}*/
		if (isMoving) {
			stop();
			return;
		}
		rc.travel(0.5d,1d);
		isMoving = true;
	}

}

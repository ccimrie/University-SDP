package strategy.planning;

import java.util.Observable;
import java.util.Random;


public class PenaltyAttack extends Strategy {
	
	boolean turned = false;
	boolean moved = false;
	boolean kicked = false;
	
	public void update(Observable arg, Object obj) {
		
		Random generator = new Random();
		int kick = generator.nextInt();
		
		if (!turned) {
			if (kick <= 0) {
				rc.rotate(315.0);
				turned = true;
			} else {
				rc.rotate(-315.0);
				turned = true;
			}
		}
		
		if (!rc.isMoving() && turned && !moved && !kicked) {
			rc.travel(0.1d,0.1d);
			moved = true;
		}
		
		if (!rc.isMoving() && turned && moved && !kicked) {
			rc.kick();
			kicked = true;
		}
		
		if (kicked) {
			this.stop();
			Strategy mp = new MainPlanner();
			mp.execute();
		}
	}
	
}

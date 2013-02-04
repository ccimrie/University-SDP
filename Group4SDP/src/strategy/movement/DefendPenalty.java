package strategy.movement;

import java.util.Random;

public class DefendPenalty {
	/* This class exists to randomly pick which part of the goal to defend in the event of a penalty.
	 * Since we don't know the penalty strategy our opponent will employ, it seems as good a method as any.
	 */
	public static void guessPenalty() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Random generator = new Random();
		int randomInt = generator.nextInt(3);
		if (randomInt == 0) {
			//forward();
		} else if (randomInt == 1) {
			//backward();
		} else {
			//stop();
		}

		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

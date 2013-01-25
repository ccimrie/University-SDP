package strategy.movement;

import java.util.Random;

import comms.control.ServerInterface;

public class RandomKick {

	public static void LuckyKick(ServerInterface rc, double angle) {
		Random generator = new Random();
		int kick = generator.nextInt();
		if (kick <= 0) {
			rc.rotate(-angle);
		} else {
			rc.rotate(angle);
		}
		rc.kick();
	}
	
}

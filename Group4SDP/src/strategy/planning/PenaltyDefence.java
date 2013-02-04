package strategy.planning;

import java.util.Observable;

import world.state.World;

public class PenaltyDefence extends Strategy{
	
	World world = World.getInstance();
	
	double theirOriginal = Math.toDegrees(world.theirRobot.bearing);
	
	int counter = 0;
	
	public void update(Observable arg, Object obj) {
		
		System.out.println(Math.toDegrees(world.theirRobot.bearing) + " " + theirOriginal);
		
		if(Math.toDegrees(world.theirRobot.bearing) > theirOriginal + 5 && counter > -1) {
			System.out.println("reversing");
			rc.travel(-0.1d, 1d);
			theirOriginal = Math.toDegrees(world.theirRobot.bearing);
			counter--;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		if(Math.toDegrees(world.theirRobot.bearing) < theirOriginal - 5 && counter < 1) {
			System.out.println("going forwards");
			rc.travel(0.1d, 1d);
			theirOriginal = Math.toDegrees(world.theirRobot.bearing);
			counter++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		System.out.println(counter);
		
	}

}

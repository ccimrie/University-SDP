package strategy.planning;

import java.util.Observable;

import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

public class PenaltyDefence extends StrategyInterface implements Runnable{
	

	
	public PenaltyDefence(WorldState world, Robot us, Robot them,
			RobotController rc) {
		super(world, us, them, rc);
		// TODO Auto-generated constructor stub
	}


	WorldState world;
	
	
	public void run() {
		
		double theirOriginal = Math.toDegrees(world.theirRobot.bearing);
		
		int counter = 0;
		
		System.out.println(Math.toDegrees(world.theirRobot.bearing) + " " + theirOriginal);
		
		if(Math.toDegrees(world.theirRobot.bearing) > theirOriginal + 5 && counter > -1) {
			System.out.println("go backwards");
			rc.move(0, -100);
			try {
				rc.wait(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			rc.stop();
			
			//rc.travel(-0.1d, 1d);
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
			
			rc.move(0, -100);
			try {
				rc.wait(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			rc.stop();
			//rc.travel(0.1d, 1d);
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

package strategy.planning;

import movement.RobotMover;
import communication.BluetoothRobot;

import world.state.WorldState;

public class Strategy implements Runnable {
	public WorldState world;
	public RobotMover mover;
	public static boolean alldie = false;

	public Strategy(WorldState world, RobotMover mover) {
		this.world = world;
		this.mover = mover;
	}

	public void run() {
		// Add this instance as an observer to the world to be notified of frame
		// updates
		System.out.println("[Strategy] Are we blue? " + world.areWeBlue());
		System.out.println("[Strategy] Are we on the left side? "
				+ world.areWeOnLeft());
		System.out.println("[Strategy] Are we on the main pitch? "
				+ world.isMainPitch());
		Thread plan = new Thread(new MainPlanner(world, mover),
				"Planning Thread");
		plan.start();
	}

	public static void stop() {
		alldie = true;
	}

	public static void reset() {
		alldie = false;
	}
}
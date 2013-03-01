package strategy.planning;

import strategy.movement.Movement;
import vision.WorldState;
import world.state.RobotController;

public class Strategy implements Runnable {
	public WorldState world;
	public RobotController robot;
	public Movement mover;
	public static boolean alldie = false;

	public Strategy(WorldState world, RobotController robot, Movement mover) {
		this.world = world;
		this.robot = robot;
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
		Thread plan = new Thread(new MainPlanner(world, robot, mover),
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
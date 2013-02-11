package strategy.planning;

import world.state.Robot;
import world.state.RobotController;
import world.state.RobotType;
import vision.WorldState;


public class Strategy {
	public static Robot us;
	public static Robot them;
	public static RobotController robot;
	public static WorldState world;
	public static boolean shouldIdie = false; //Use this as a control variable if the
	//threads should die.

	public void execute(WorldState world, RobotController robot) {
		Strategy.world = world;
		Strategy.us = world.ourRobot;
		Strategy.them = world.theirRobot;
		Strategy.robot = robot;
		// Add this instance as an observer to the world to be notified of frame updates
		System.out.println("[Strategy] Are we blue? " + world.areWeBlue());
		System.out.println("[Strategy] Are we on the left side? " + world.areWeOnLeft());
		System.out.println("[Strategy] Are we on the main pitch? " + world.isMainPitch());
		Thread strat = new Thread(new MainPlanner(world, us, them, robot), "Planning Thread");
		strat.run();
	}

	public void stop() throws InterruptedException{
		shouldIdie = true;
		Thread.sleep(2000); //Wait for the thread to notice it needs to die
		robot.stop();
	}


}
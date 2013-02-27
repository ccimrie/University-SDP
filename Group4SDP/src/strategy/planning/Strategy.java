package strategy.planning;

import world.state.Robot;
import world.state.RobotController;
import strategy.calculations.GoalInfo;
import vision.WorldState;


public class Strategy implements Runnable {
	public static Robot us;
	public static Robot them;
	public static RobotController robot;
	public static WorldState world;
	public static boolean alldie = false;
	
	public Strategy(WorldState world, RobotController robot){
		Strategy.world = world;
		Strategy.us = world.ourRobot;
		Strategy.them = world.theirRobot;
		Strategy.robot = robot;
	}

	public void run() {
		// Add this instance as an observer to the world to be notified of frame updates
		System.out.println("[Strategy] Are we blue? " + world.areWeBlue());
		System.out.println("[Strategy] Are we on the left side? " + world.areWeOnLeft());
		System.out.println("[Strategy] Are we on the main pitch? " + world.isMainPitch());
		Thread plan = new Thread(new MainPlanner(world, us, them, robot), "Planning Thread");
		plan.start();
	}

	public static void stop() throws InterruptedException{
		alldie = true;
		Thread.sleep(1000); //Wait for all threads to notice this and terminate.
		robot.stop();
	}
}
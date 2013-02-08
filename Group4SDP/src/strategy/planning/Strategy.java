package strategy.planning;

import world.state.RobotController;
import world.state.RobotType;
import vision.WorldState;


public abstract class Strategy {
		RobotController rc = new RobotController(RobotType.Us);
		//IMPORTANT! We get our worldstate from the GUI for now. We need to decide where
		//to take it from.
		public static WorldState world = computer.ControlGUI2.worldState;
        public static boolean shouldIdie = false; //Use this as a control variable if the
        //threads should die.
        
        public void execute() {
   
        		// Add this instance as an observer to the world to be notified of frame updates
                System.out.println("[Strategy] Are we blue? " + world.areWeBlue());
                System.out.println("[Strategy] Are we on the left side? " + world.areWeOnLeft());
                System.out.println("[Strategy] Are we on the main pitch? " + world.isMainPitch());
                Thread strat = new Thread(new MainPlanner2(), "Planning Thread");
                strat.run();
        }
        
        public void stop() throws InterruptedException{
        		shouldIdie = true;
        		Thread.sleep(2000); //Wait for the thread to notice it needs to die
                rc.stop();
        }
   

}
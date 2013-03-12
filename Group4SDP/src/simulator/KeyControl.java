package simulator;

import java.awt.event.KeyEvent;

import strategy.calculations.GoalInfo;
import vision.PitchConstants;
import world.state.RobotType;
import world.state.WorldState;


public class KeyControl {


	
	   public void keyControl(KeyEvent e,  WorldState world) {
		 
		   SimulatorTestbed simTest = new SimulatorTestbed(world);
			Simulator simulator = new Simulator(simTest);
			SimulatorRobot theirRobot = new SimulatorRobot(RobotType.Them, simTest.simTheirRobot);
		   
	        if (e.getKeyChar() == 's') {
	        	
	        	theirRobot.move(0, -100);
	          //  getModel().getKeys()['s'] = false;
	        }
	        if (e.getKeyChar()  == 'w') {
	        	theirRobot.move(0, 100);
	       //     getModel().getKeys()['w'] = false;
	        }
	        if (e.getKeyChar()  == 'a') {
	        	theirRobot.rotate(-10);
	        	
	         //   getModel().getKeys()['a'] = false;
	        }
	        if (e.getKeyChar() == 'd') {
	        	theirRobot.rotate(10);
	        	
	           // getModel().getKeys()['d'] = false;
	        }
	        if (e.getKeyChar() == 'b') {
	        	theirRobot.kick();
	            //getModel().getKeys()['b'] = false;
	        }
	        
	  /**      if (argKeyChar == 'c') {
	        	if(!control)
	        	{
	        		control = true;
	        		rc = new SimServer(this);
	        		
	        		strategy = new SimStrategy(world,rc);
	        		
	        		strategy.start();
	        		
	        		
	        		//robotControl = new RobotControl();
	        		//robotControl.start();
	        		
	        	}
	            getModel().getKeys()['c'] = false;
	        }
	        */

	    }



}

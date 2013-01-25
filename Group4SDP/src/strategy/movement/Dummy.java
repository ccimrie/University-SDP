package strategy.movement;

import comms.control.Server;

public class Dummy {
	
	public static void main(String[] args)
	{
    
		Server rc = Server.getInstance();
		
		//boolean running = true;
	
	    rc.travel(5d,100d);
	
	    //Wait for half a second before continuing
	
	    try {
	        Thread.sleep(500);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }

	}

}

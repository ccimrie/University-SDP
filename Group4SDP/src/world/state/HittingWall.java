package world.state;
import vision.*;


public class HittingWall {
//The purpose of this class is to check whether we are hitting a wall or not

	/**
	 * We define 6 walls
	 * 
	 * south wall
	 * 
	 * South West wall next  to the goal
	 * 
	 * South East wall next  to the goal
	 * 	
	 * 
	 * North Wall
	 * 
	 * North East Wall next  to the goal 
	 * 
	 * North West next  to the goal
	 * 
	 * 
	 */
	
	
	public boolean nearSouth(Robot robot){
				
		if (robot.y>394)
			
		
			return true;
		
		else return false;
		
		
		
	}
	
	
	public boolean nearTop(Robot robot){
		
		if (robot.y<98)
			
		
			return true;
		
		else return false;
		
		
		
	}
	
	
	
}

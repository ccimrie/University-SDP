package strategy.calculations;

import geometry.Vector;

import world.state.Robot;
import world.state.World;


public class BlockGoal {

	/*test
	public static void main(String[] args) {
        double x = Blockx(9, 6);
        double y = Blocky(9, 6);
        System.out.println("Coordinates to go to are: (" + x + ", " + y + ")");
    }*/
	
	//Tells the robot to go to the appropriate coordinates to block the goal 
	
	public static double Blockx(double ballx, double bally){
	
		//width of front of robot
		double width = 55;
		
		World world = World.getInstance();
		
		//coordinates of our goal edge
		
		double goal1x;
		double goal1y;
		double goal2x;
		double goal2y;

		if(world.ourHalfLeft()){
			goal1x = 38;
			goal1y = 104;
			
			goal2x = 39;
			goal2y = 275;
			
		} else {
			
			goal1x = 650;
			goal1y = 108;
			
			goal2x = 647;
			goal2y = 273;
		}

		
		//calculations to find optimal point of block
		
		double angle1 = (Math.sin(Math.toRadians(AngleBetweenTwoVectors.angle(goal1x, goal1y, ballx, bally, goal2x, goal2y))/2));
		
		double length1 = ((width/2)/angle1);
		
		double modulus = Math.sqrt(Math.pow(goal1x - ballx, 2)+ Math.pow(goal1y - bally, 2));		
		double midway1x = ballx + ((length1 * (goal1x - ballx))/(modulus));
				
		double modulus2 = (Math.sqrt((Math.pow(goal2x - ballx, 2))+(Math.pow(goal2y - bally, 2))));		
		double midway2x = ballx + ((length1 * (goal2x - ballx))/(modulus2));
				
		double blockx = ((midway1x + midway2x)/2);
		
		return Math.round(blockx);

	}
	
	public static double Blocky(double ballx, double bally){
		
		//width of front of robot
		double width = 55;
		
		World world = World.getInstance();
		
		//coordinates of our goal edge
		
		double goal1x;
		double goal1y;
		double goal2x;
		double goal2y;
		
		Vector goal = world.getOurGoal();


		goal1x = goal.getX() + 82;
		goal1y = goal.getY() + 82;
			
		goal2x = goal.getX() - 82;
		goal2y = goal.getY() - 82;

		
		double angle1 = (Math.sin(Math.toRadians(AngleBetweenTwoVectors.angle(goal1x, goal1y, ballx, bally, goal2x, goal2y)/2)));
		
		double length1 = ((width/2)/angle1);
		
		double modulus1 = (Math.sqrt((Math.pow(goal1x - ballx, 2))+(Math.pow(goal1y - bally, 2))));	
		double midway1y = bally + ((length1 * (goal1y - bally))/modulus1);
		
		double modulus2 = (Math.sqrt((Math.pow(goal2x - ballx, 2))+(Math.pow(goal2y - bally, 2))));
		double midway2y = bally + ((length1 * (goal2y - bally))/modulus2);
				
		double blocky = ((midway1y + midway2y)/2);
		
		return Math.round(blocky);

	}
}
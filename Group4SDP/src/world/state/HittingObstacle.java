package world.state;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;

import geometry.Vector;
import vision.*;


public class HittingObstacle {
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
	
	Vector northWestPole = PitchInfo.getLeftGoalTop();
	Vector southWestPole = PitchInfo.getLeftGoalBottom();
	
	
	Vector northWestCorner =  new Vector(35, 94);
	Vector southWestCorner =  new Vector(35, 392);
	
	
	Vector northEastPole = PitchInfo.getRightGoalTop();
	Vector southEastPole = PitchInfo.getRightGoalBottom();
	
	
	Vector northEastCorner =  new Vector(593, 92);
	Vector southEastCorner =  new Vector(593, 392);
	
	
public boolean nearSouth(WorldState world){
				
		if (world.getOurRobot().y>394)
			
		
			return true;
		
		else return false;
		
		
		
	}
	
	
public boolean nearTop(WorldState world){
		
		if (world.getOurRobot().y<98)
			
		
			return true;
		
		else return false;
		
		
		
	}
	
public boolean nearNorthWest(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
		
		if (pointToLineDistance(northWestCorner,northWestPole, positionOurRobot ) <5){
			
			return true;
		}
			
		
			
		
		else return false;
		
		
		
	}
	

public boolean nearSouthWest(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
		
		if (pointToLineDistance(southWestCorner,southWestPole, positionOurRobot ) <5){
			
			return true;
		}
			
		
			
		
		else return false;
		
		
		
	}



public boolean nearNorthEast(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
		
		if (pointToLineDistance(northEastCorner,northEastPole, positionOurRobot ) <5){
			
			return true;
		}
			
		
			
		
		else return false;
		
		
		
	}


public boolean nearSouthEast(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
		
		if (pointToLineDistance(southEastCorner,southEastPole, positionOurRobot ) <5){
			
			return true;
		}
			
		
			
		
		else return false;
		
		
		
	}






	
public boolean notHittingWall(WorldState world){
	
	if((nearSouth(world) ||  nearTop(world) || nearNorthWest(world) || nearSouthWest(world) || 
			nearNorthEast(world) || nearSouthEast(world)) == false )
		return true;
	
	
	else return false;
	
	
	
}
	
	

	
	
	/*
	 * Then we also have the  opponent 
	 * robot
	 * 
	 * 
	 * so from the center of the robot 
	 * we extend it to incorporate 4 boundaries
	 * 
	 */
	
	
	public boolean nearObstacleEnnemy(WorldState world){
		
		
		Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
		
		Vector northEast =  new Vector(world.getTheirRobot().x+39, world.getTheirRobot().y-44);
		
		Vector northWest =  new Vector(world.getTheirRobot().x-37, world.getTheirRobot().y-44);
		
		Vector southWest =  new Vector(world.getTheirRobot().x-36, world.getTheirRobot().y+32);
		
		Vector southEast =  new Vector(world.getTheirRobot().x+37, world.getTheirRobot().y+30);
		
	
		
		if(pointToLineDistance(northWest,southEast, positionOurRobot )< 5 ||  pointToLineDistance(northWest,northEast, positionOurRobot )< 5   ||  pointToLineDistance(southWest,southEast, positionOurRobot )< 5  
				||  pointToLineDistance(northEast,southEast, positionOurRobot )< 5 )
		{
			
			return true;
			
			
		}
		
		else return false;
		
		
	
		
		
		
		
		
	}

	 public double pointToLineDistance(Vector A, Vector  B, Vector P) {
		    double normalLength = Math.sqrt((B.getX()-A.getX())*(B.getY()-A.getX())+(B.getY()-A.getY())*(B.getY()-A.getY()));
		    return Math.abs((P.getX()-A.getX())*(B.getY()-A.getY())-(P.getY()-A.getY())*(B.getX()-A.getX()))/normalLength;
		  }

	
	
}

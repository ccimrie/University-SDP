package strategy.movement;

import strategy.calculations.Prediction;
import strategy.planning.MoveToBall;
import strategy.planning.MoveToPoint;
import world.state.PossessionType;
import world.state.Robot;
import world.state.RobotType;
import world.state.WorldState;

import communication.BluetoothRobot;


public class Inteception  {

	public WorldState world;
	public Robot us = new Robot(RobotType.Us);
	public Robot them = new Robot(RobotType.Them);
	
	PossessionType pt;
	MoveToPoint m = new MoveToPoint();
	MoveToBall  mb = new MoveToBall();
	public void tackle (WorldState world, BluetoothRobot rc) throws InterruptedException{
	
		this.world = world;
		// This is a tackle method that we shall split in 3 cases :
		// case 1 : we have the ball
		//case 2 : they have it
		// case 3 : nobody has it
		
		
		// extra condition
		// if enemy robot aiming in our goal, aproach to the ball with orientation 
		// 45 (-45) 135 (-135) degree, if enemy kicked the ball kick it back or catch
		// if possible
		
		
		// Case 1 :
		if (world.getPosession() == PossessionType.Us){
		
			
			// if we have the ball we decided to do nothing
			// job is finished
			return;
		}
		
		
		
		
		// Case 2 :
		if (world.getPosession() == PossessionType.Them){
		
			
			// This shall be the main case 
			
			int threshold = 200;
				
			//The strategy to tackle the ball will depend 
			//on the distance, we have a case where the dist is bigger than the threshold and the other one
			
			
			if (DistanceToBall.Distance(RobotType.Us, world)> threshold){
				Prediction p = new Prediction();
				
				double[] predictionVector = p.predictball(world);
				m.moveToPoint( world, rc, predictionVector[1],  predictionVector[2]);
				
			}
			else{
				MoveToBall.approach(world, rc);
			}
			
		}
		if (world.getPosession() == PossessionType.Both){
		
		}
		if (world.getPosession() == PossessionType.Nobody){
			MoveToBall.approach(world, rc);
		}
		
		
		
		
	}
	
	public void podkat(){
		
		if (world.getPosession() == PossessionType.Both){
			
		}
	}

	

}

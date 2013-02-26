package strategy.planning;
import world.state.*;
import balle.strategy.Interception;
import geometry.Vector;

import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import strategy.movement.Inteception;
import world.state.PossessionType;
import world.state.Robot;
import world.state.RobotController;
import vision.WorldState;


import strategy.planning.*;
import world.state.*;
import vision.*;
import strategy.calculations.*;
public class Offensive extends StrategyInterface implements Runnable{
	Inteception take = new Inteception();
	MoveToBall mb = new MoveToBall();
	public Offensive(WorldState world, Robot us, Robot them, RobotController rc){
		super(world, us, them, rc);
	}

	public void run(){
		
		while (!shouldidie && !Strategy.alldie){		
			Vector theirGoal = world.getTheirGoal();

			if((us.x < world.getMidPoint() && world.ourHalfLeft()) 
					|| (us.x >= world.getMidPoint() && !world.ourHalfLeft())){

				System.out.println("OUR HALF YEAH!");
				//GoToPoint.goToPoint(world, server, theirGoal, AvoidanceStrategy.Aggressive);
				
			} else {
				if (world.getPosession() == PossessionType.Us){
					System.out.println("We got ball");
					
					PlainScoring killthemALL = new PlainScoring();
					try {
						killthemALL.domination(world, us, them, rc);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else{
					try {
						System.out.println("Going to the ball");
						MoveToBall.approach(world, rc);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				

			}
		
		rc.stop();
	}
}
}
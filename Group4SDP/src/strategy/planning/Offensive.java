package strategy.planning;
import world.state.*;
import balle.strategy.Interception;
import geometry.Vector;

import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import strategy.movement.Inteception;
import strategy.movement.Movement;
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
	Thread movthread;
	public Offensive(WorldState world, Robot us, Robot them, RobotController rc){
		super(world, us, them, rc);
		
	}

	public void run(){
		
		while (!shouldidie && !Strategy.alldie){		
			
				if (world.getPosession() == PossessionType.Us){
					System.out.println("We got ball");
					
					PlainScoring killthemALL = new PlainScoring();
					
						//killthemALL.domination(world, us, them, rc);
						killthemALL.faceGoal();
				
					
				} else{
					
						System.out.println("Going to the ball");
						//Movement mv = new Movement();
						
					/**	Movement mover = new Movement(world, rc, world.getBallX(), world.getBallY(), 0,0,0.0,3);
						
						if ( !movthread.isAlive()){
						movthread = new Thread(mover, "Movement Thread");
						movthread.start();
						}
						**/
						
					
				}
				

			
		
		rc.stop();
	}
}
}
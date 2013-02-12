package strategy.planning;

import geometry.Vector;

import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import world.state.Robot;
import world.state.RobotController;
import vision.WorldState;


public class Offensive extends StrategyInterface implements Runnable{


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

				double angle = IsRobotFacingPoint.Turner(us, theirGoal.getX(), theirGoal.getY());
				System.out.println(Math.abs(angle));
				if(Math.abs(angle) > 40){

					System.out.println("Their half wrong angle");
					//Go to point not implemented.
					//	GoToPoint.goToPoint(world, server, theirGoal, AvoidanceStrategy.Aggressive);

				} else {

					System.out.println("Kicking!");
					rc.kick();
				}

			}
		}
		rc.stop();
	}
}
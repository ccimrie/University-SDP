package strategy.planning;

import world.state.*;
import balle.strategy.Interception;
import geometry.Vector;

import strategy.calculations.DistanceCalculator;
import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import strategy.movement.Inteception;
import strategy.movement.Movement;
import strategy.movement.TurnToBall;
import world.state.PossessionType;
import world.state.Robot;
import world.state.RobotController;
import vision.WorldState;

import strategy.planning.*;
import world.state.*;
import vision.*;
import strategy.calculations.*;

public class Offensive extends StrategyInterface implements Runnable {
	Inteception take = new Inteception();
	MoveToBall mb = new MoveToBall();
	Thread movthread;

	public Offensive(WorldState world, Robot us, Robot them, RobotController rc) {
		super(world, us, them, rc);

	}

	public void run() {

		while (!shouldidie && !Strategy.alldie) {

			System.out.println("Going to the ball");
			Everything eve = new Everything(world, rc);
			

//			while (DistanceCalculator.Distance(us.x, us.y, world.getBallX(), world.getBallX()) > 80) {			
//				eve.moveTowardsPoint(world.getBallX(), world.getBallY());
//				try {
//					Thread.sleep(42);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			double angle = TurnToBall.Turner(us, world.ball);
//			System.out.println(angle);
//			rc.rotate((int) angle);
			
			try {
				MoveToBall.approach(world, rc);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			PlainScoring killthemALL = new PlainScoring();

			try {
				killthemALL.domination(world, us, them, rc);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rc.stop();

		}

		rc.stop();
	}
}

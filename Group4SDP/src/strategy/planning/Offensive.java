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

	public Offensive(WorldState world, Movement mover) {
		super(world, mover);
	}

	@Override
	public void run() {

		while (!shouldidie && !Strategy.alldie) {

			System.out.println("Going to the ball");

			synchronized (mover) {
				mover.moveToAndStop(world.ball.x, world.ball.y);
				try {
					mover.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			PlainScoring killthemALL = new PlainScoring();

			try {
				killthemALL.domination(world, mover);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (mover) {
				mover.stopRobot();
				try {
					mover.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		mover.stopRobot();
	}
}

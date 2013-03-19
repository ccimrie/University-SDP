package strategy.planning;

import movement.RobotMover;
import strategy.calculations.AngleCalculator;
import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import vision.Position;
import world.state.Ball;
import world.state.HittingObstacle;
import world.state.Robot;
import world.state.WorldState;
import utility.SafeSleep;
import strategy.movement.Dribbler;

public class Offense3 extends StrategyInterface {

	public Ball ball;
	HittingObstacle wall = new HittingObstacle();
	MoveToPoint mpoint = new MoveToPoint();
	Position theirGoal;
	Robot them;
	Robot us;
	AngleCalculator angg = new AngleCalculator(world);
	Position a = world.getTheirGoal();

	// Preconditions for activation of the strategy:
	// Assume that we have the ball and we're facing the enemy goal.

	// The way this strategy works is that it checks if the other robot
	// is in the way. If it isn't it tries to score. Else it tries to side
	// step it and then score.

	public Offense3(WorldState world, RobotMover mover) {
		super(world, mover);
		this.ball = world.ball;
		this.theirGoal = world.getTheirGoal();
		this.them = world.theirRobot;
		this.us = world.ourRobot;
	}

	public void run() {
		
		double angle;
		System.out.println("[Offense] started 3!");
		System.out.println("[offence] bool 1" + shouldidie + " bool 2 " + Strategy.alldie);
		while (!shouldidie && !Strategy.alldie) {
			// moving near wall and pushing ball into goal
			if(world.areWeOnLeft()){
				 angle = angg.AngleTurner(us.x + 50, us.y);
				mover.rotate(Math.toRadians(angle));
				try {
					mover.waitForCompletion();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				 angle = angg.AngleTurner(us.x - 50, us.y);
				mover.rotate(Math.toRadians(angle));
				try {
					mover.waitForCompletion();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			}	
			mover.move(0,50);
			try {
				SafeSleep.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
			}
			if (them.y < world.goalInfo.getMid().getY()) {
				System.out.println("[offence] world.angleToTheirGoal() > 0");
				 angle = angg.AngleTurner(us.x, us.y - 50);
				if (world.areWeOnLeft()){
					mover.move(60, 90);
				}else {
					mover.move(-60,90);
				}
			} else {
				System.out.println("[offence] world.angleToTheirGoal() <= 0");
				 angle = angg.AngleTurner(us.x, us.y + 50);
				if (world.areWeOnLeft()){
					mover.move(-60, 90);
				}else {
					mover.move(60,90);
				}
			}

			while (!shouldidie && !Strategy.alldie && wall.notHittingWall(world)) {
				System.out.println("[offence] Waiting to hit the wall.");
				try {
					SafeSleep.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			mover.move(0,100);
			
			while (!shouldidie && !Strategy.alldie && wall.inCorner(world)){
				System.out.println("[offence] Waiting to hit corner.");
				try {
					SafeSleep.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("[offence] Move to goal.");
			if (us.y > 124){
				mover.move(80,0);
			}else{
				mover.move(-80,0);
			}
			
			try {
				SafeSleep.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("[offence] Fatality.");
			mover.kick();
			mover.stopRobot();
			

		}
	}
}

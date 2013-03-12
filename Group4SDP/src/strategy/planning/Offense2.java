package strategy.planning;

import movement.RobotMover;
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

public class Offense2 extends StrategyInterface {

	public Ball ball;
	HittingObstacle wall = new HittingObstacle();
	MoveToPoint mpoint = new MoveToPoint();
	Position theirGoal;
	Robot them;
	Robot us;

	//Preconditions for activation of the strategy:
	//Assume that we have the ball and we're facing the enemy goal.
	
	//The way this strategy works is that it checks if the other robot
	//is in the way. If it isn't it tries to score. Else it tries to side
	//step it and then score.

	public Offense2(WorldState world, RobotMover mover) {
		super(world, mover);
		this.ball = world.ball;
		this.theirGoal = world.getTheirGoal();
		this.them = world.theirRobot;
		this.us = world.ourRobot;
	}

	public void run() {
		System.out.println("Offense started!");
		while (!shouldidie && !Strategy.alldie){
			if (world.distanceThemToTheirgoal() > 
			world.distanceUsToTheirgoal()){
				//In case we are in front of them
				try {
					faceGoal();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mover.move(0, 80);
				while (world.distanceUsToTheirgoal() >300);
				try {
					SafeSleep.sleep(600);
					if (!shouldidie && !Strategy.alldie){
						return;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mover.kick();
				mover.stopRobot();
			} else {
				// Their robot is in front of us.
				// Face the goal first.
				try {
					faceGoal();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// In case they are in our way we should sidestep them.
				if (Math.abs(them.y - us.y) < 30){
					mover.move(0.80);
					//Sidestep in the direction where there is more space.
					if (world.angleToEnemy() > 0) {
						Dribbler.dribble(mover, 2);
					} else {
						Dribbler.dribble(mover, 1);
					}
					//After we start sidestepping them, sleep until
					//we have nearly passed them or if we are too close to the wall.
					while (Math.abs(us.x - them.x) < 30 ||
							(us.y - world.goalInfo.pitchConst.getLeftBuffer() < 40)
							|| (us.y - world.goalInfo.pitchConst.getRightBuffer() < 40)){
						try {
							SafeSleep.sleep(600);
							if (!shouldidie && !Strategy.alldie){
								return;
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//Face the goal. This will stop the sidemovement
					try {
						faceGoal();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mover.move(0,80);
					try {
						SafeSleep.sleep(600);
						if (!shouldidie && !Strategy.alldie){
							return;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//Else we can dribble and score and don't bother with them.
				while (world.distanceUsToTheirgoal() > 200){
					try {
						SafeSleep.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mover.kick();
				mover.stopRobot();
			}
		}
	}
	public void faceGoal() throws InterruptedException {
		Position a = world.getTheirGoal();
		System.out.println(a.getX() + ", " + a.getY());

		double angle = (TurnToBall.AngleTurner(world.getOurRobot(), a.getX(),
				a.getY()));

		System.out.println(angle);

		mover.rotate(Math.toRadians(angle));
		mover.waitForCompletion();
	}

}

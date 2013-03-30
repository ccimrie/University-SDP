package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import world.state.WorldState;
import world.state.Ball;
import world.state.PossessionType;

public class OffenseDribble extends StrategyInterface {
	private Ball ball;
	
	public OffenseDribble(WorldState world, RobotMover mover) {
		super(world, mover);
		ball = world.ball;
	}
	
	public void run(){
		while (!(shouldidie || Strategy.alldie)){
			if (world.getPosession() == PossessionType.Us){
				double theirx = world.getTheirGoal().getX();
				double theiry = world.getTheirGoal().getY();
				mover.dribble(1);//Turn on the dribbler in possession mode.
				try {
					mover.waitForCompletion();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				mover.moveToAStar(theirx, theiry, false, true);
				//Wait for the movement to complete while still checking that we have the ball.
				while (!(shouldidie || Strategy.alldie) && (world.distanceUsToTheirgoal() > 70)
						&& world.getPosession() == PossessionType.Us){
					try {
						SafeSleep.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//If we still have the ball we should "Kick it in the enemy goal.
				//Otherwise we should stop and try to get the ball again.
				if (world.getPosession() == PossessionType.Us){
					//When this is executed in theory A* should still be navigating the
					//robot, presumably to the goal so we should be kicking the ball whilst
					//moving forward.
					mover.dribble(2);
				}else{
					continue; //Else try to go and get the ball
				}
			}else{
				//We don't have the ball! We need to go get it first.
				//Since we rely on A* to move us, we should possition our robot
				//Straight on the ball. Use the code from DribbleBall5 for that purpose
				if (world.areWeOnLeft()) {
					double slope = (242 - ball.y) / (610 - ball.x);
					if (ball.y > 240){
						slope = -slope;
					}
					mover.moveToAStar(ball.x - 40, ball.y + slope * 70, true, true);
					mover.delay(50);
					RobotMover.distanceThreshold = 10;
					mover.moveToAndStop(ball.x - 40, ball.y + slope * 60);
					try {
						mover.waitForCompletion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					double slope = (244 - ball.y) / (28 - ball.x);
					if (ball.y < 240){
						slope = -slope;
					}
					mover.moveToAStar(ball.x + 70, ball.y + 70 * slope, true, true);
					mover.delay(50);
					RobotMover.distanceThreshold = 10;
					mover.moveToAndStop(ball.x + 60, ball.y + slope * 60);
					try {
						mover.waitForCompletion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}

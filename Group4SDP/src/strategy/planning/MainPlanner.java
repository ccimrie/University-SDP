package strategy.planning;

import geometry.Vector;

import java.awt.geom.Point2D;

import strategy.calculations.DistanceCalculator;
import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.AvoidanceStrategy;
import strategy.movement.GoToPoint;
import strategy.movement.LineupStrategies;
import strategy.movement.TurnToBall;
import vision.WorldState;
import world.state.PossessionType;
import world.state.Robot;
import world.state.RobotController;
import strategy.planning.Strategy;

public class MainPlanner extends StrategyInterface implements Runnable {
	
	int angleToTurnToBall = 15;
	boolean collisionInProgress = false;
	boolean isTurning = false;

	enum state {
		Offensive,
		Defensive,
		StartOfGame,
		MoveToBall,
		EndOfGame
	}
	state currentState = state.StartOfGame;
	state newState = currentState;

	public MainPlanner(WorldState world, Robot us, Robot them, RobotController rc){
		super(world, us,them, rc);
	}
	
	@Override
	//Run the strategy from here.
	public void run() {
		Thread strategy;
		// TODO change this to StartOfGame strategy
		strategy = new Thread(new Defensive(world, us, them, rc), "Defense Thread");
		strategy.start();
		
		/*The while loop will execute the strategy until it is broken or we're told to die*/
		while (!Strategy.alldie) {
			// Get state
			int we; // 1 if we are blue and 2 if we are yellow
			if (world.areWeBlue())
				we = 1;
			else
				we = 2;
			if (world.whoHasTheBall() == -1) {
				// Nobody has the ball
				newState = state.StartOfGame;
			} else if (world.whoHasTheBall() == we) {
				// We have the ball
				newState = state.Offensive;
			} else {
				// They have the ball
				newState = state.Defensive;
			}
			if (world.ballIsInGoal())
				newState = state.EndOfGame;
			// If state did not change, do nothing
			// Otherwise kill old strategy and start new one
			if (currentState != newState) {
				currentState = newState;
				strategy = null;
				if (currentState == state.StartOfGame) {
					// TODO change to StartOfGame strategy type, when implemented
					strategy = new Thread(new Defensive(world, us, them, rc), "Defense Thread");
				} else if (currentState == state.Defensive) {
					// TODO change to Defensive strategy type, when implemented
					strategy = new Thread(new Defensive(world, us, them, rc), "Defense Thread");
				} else if (currentState == state.Offensive) {
					// TODO change to Offensive strategy type, when implemented
					strategy = new Thread(new Defensive(world, us, them, rc), "Defense Thread");
				} else if (currentState == state.EndOfGame) {
					// TODO change to EndOfGame strategy type, when implemented
					strategy = new Thread(new Defensive(world, us, them, rc), "Defense Thread");
				}
			}
			
//			if (currentState != newState) {
//				System.out.println("State changed to " + currentState.toString());
//				newState = currentState;
//			}

			// OBSTACLE DETECTION STRATEGY
			// WE MUST IMPLEMENT THAT OURSELVES
			// When touch sensor is pressed, go back no matter what.
			/*if (collisionInProgress) {
    		if (rc.isMoving()) {
    			return;
    		} else {
    			collisionInProgress = false;
        		System.out.println("Finished Collision");
    			return;
    		}
    	}
    	if (rc.isTouchPressed()) {
    		System.out.println("Collision in Progress");
			rc.travel(-0.3d, 0.3d);
			currentState = state.GetBehindBall;
			collisionInProgress = true;
			return;
		}
			 */

			// BLOCK TURNS
			//NO IDEA WHY THIS CODE IS NEEDED.
			/*if (isTurning) {
    		if (rc.isMoving()) {
    			return;
    		} else {
        		System.out.println("Finished Turn");
    			isTurning = false;
    		}
    	}
			 */
			// TODO: ROBOT AVOIDANCE STRATEGY
			// We should avoid other robots like the plague to stop our vision from
			// breaking.

			// END OF PLAY STRATEGY
			// If the ball is in the goal then stop playing.
//			if(world.ballIsInGoal()) {
//				rc.stop();
//				return;
//			}


//			if (currentState == state.StartOfMatch) {
//				currentState = state.GetBehindBall;
//				return;
//			}

//			if (currentState == state.GetBehindBall) {
//				// STEP 1
//				// ======
//				// First we want to get behind the ball
//				Point2D pointBehindBall = LineupStrategies.getPointBehindBall(world);
//				// Get the square distance between us and the destination
//				double distanceBetweenUsAndPointBehindBall = 
//					DistanceCalculator.Distance(world.ourRobot.x, world.ourRobot.y,
//							pointBehindBall.getX(), pointBehindBall.getY());
//
//				// Radius in which to stop trying to get behind ball
//				int pointRadius = 30;
//				// If we are further away than this then go to the point
//				if(distanceBetweenUsAndPointBehindBall > pointRadius) {
//					GoToPoint.goToPoint(
//							world,
//							rc,
//							new Vector(pointBehindBall.getX(),
//									pointBehindBall.getY()),
//									AvoidanceStrategy.AvoidingBall
//					);
//					return;
//				} else {
//					currentState = state.TurnToBall;
//					return;
//				}
//			}
//
//
//			if (currentState == state.TurnToBall) {
//				// STEP 2
//				// ======
//				// Now turn to the ball.
//
//				// If they have possession at this state, go back to one
//				if (world.hasPossession == PossessionType.Them){
//					currentState = state.GetBehindBall;
//				}
//
//				double angle = TurnToBall.Turner(world.ourRobot, world.ball);
//				if ((Math.abs(angle) > angleToTurnToBall)) {
//					rc.rotate((int)-angle);
//					isTurning = true;
//					System.out.println("Turn in Progress");
//					return;
//				} else {
//					currentState = state.TakePossesion;
//					return;
//				}
//			}
//
//			if (currentState == state.TakePossesion) {
//				// STEP 3
//				// ======
//				// Having turned to the ball, we may now slowly try to take posession.
//
//				// If they have possession at this state, go back to one
//				if (world.hasPossession == PossessionType.Them){
//					currentState = state.GetBehindBall;
//					return;
//				}
//				if (world.hasPossession == PossessionType.Us) {
//					currentState = state.DriveToGoalWithBall;
//					return;
//				}
//				// Otherwise we can get it
//				if (world.hasPossession == PossessionType.Nobody) {
//					GoToPoint.goToPoint(world, rc, world.ball.getPosition(),
//							AvoidanceStrategy.IgnoringBall);
//					return;
//				}
//			}
//
//			if (currentState == state.DriveToGoalWithBall) {
//				// STEP 4
//				// ======
//				// Having obtained posession, we may now drive towards the enemy goal.
//				if (world.hasPossession == PossessionType.Us) {
//					GoToPoint.goToPoint(world, rc,
//							world.getTheirGoal(),
//							AvoidanceStrategy.IgnoringBall);
//					currentState = state.ShootAndScore;
//				} else {
//					currentState = state.GetBehindBall;
//					return;
//				}
//			}
//
//			if (currentState == state.ShootAndScore) {
//				// STEP 5
//				// ======
//				// We are heading towards the enemy goal. Can we kick and score?
//				if (world.hasPossession != PossessionType.Us) {
//					currentState = state.GetBehindBall;
//				}
//				if (world.areWeInOurHalf() != true) {
//					// We can score!
//					double scoringAngle = IsRobotFacingPoint.Turner(
//							world.ourRobot,
//							world.getTheirGoal().getX(),
//							world.getTheirGoal().getY());
//					if(Math.abs(scoringAngle) < 15) {
//						rc.kick();
//					}
//					return;
//				}
//			}
		
		}
	}
}

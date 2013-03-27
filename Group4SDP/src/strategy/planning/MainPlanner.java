package strategy.planning;

import movement.RobotMover;
import world.state.WorldState;

public class MainPlanner extends StrategyInterface {

	int angleToTurnToBall = 15;
	double startTime;
	boolean collisionInProgress = false;
	boolean isTurning = false;

	enum state {
		Offensive, Defensive, MoveToBall, EndOfGame
	}

	state currentState = state.MoveToBall;
	state newState = currentState;

	public MainPlanner(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	// Run the strategy from here.
	@Override
	public void run() {
		System.out.println("[MainPlanner] Are we blue? " + world.areWeBlue());
		System.out.println("[MainPlanner] Are we on the left side? "
				+ world.areWeOnLeft());
		System.out.println("[MainPlanner] Are we on the main pitch? "
				+ world.isMainPitch());

		StrategyInterface activeStrat;
		Thread strategyThread;
		activeStrat = new InterceptBall(world, mover);
		strategyThread = new Thread((InterceptBall) activeStrat,
				"Move to ball Thread");
		strategyThread.start();
		startTime = System.currentTimeMillis();

		/*
		 * The while loop will execute the strategy until it is broken or we're
		 * told to die
		 */
		while (!Strategy.alldie && !shouldidie) {
			// Get state
			int ourRobot; // 1 if we are blue and 2 if we are yellow
			int theirRobot;
			int theirVelocity;

			if (world.areWeBlue()) {
				ourRobot = 1;
				theirRobot = 2;
				theirVelocity = (int) Math.sqrt(world.getYellowXVelocity()
						* world.getYellowXVelocity()
						+ world.getYellowXVelocity()
						* world.getYellowXVelocity());
			} else {
				ourRobot = 2;
				theirRobot = 1;
				theirVelocity = (int) Math.sqrt(world.getBlueXVelocity()
						* world.getBlueXVelocity() + world.getBlueXVelocity()
						* world.getBlueXVelocity());
			}

			// if (currentState == state.MoveToBall && theirVelocity >= 50 &&
			// (System.currentTimeMillis() - startTime) >= 3000) {
			// newState = state.Defensive;
			if (world.whoHasTheBall() == -1) {
				newState = state.MoveToBall;
			} else if (world.whoHasTheBall() == ourRobot) {
				newState = state.Offensive;
			} else if (/*currentState == state.Defensive
					&& */world.whoHasTheBall() == ourRobot) {
				newState = state.Offensive;
			} else if (/*currentState == state.Offensive
					&& */world.whoHasTheBall() == theirRobot) {
				newState = state.Defensive;
			} else if (world.ballIsInGoal()) {
				newState = state.EndOfGame;
			} else {
				newState = state.MoveToBall;
			}

			// If state did not change, do nothing
			// Otherwise kill old strategy and start new one
			if (currentState != newState) {
				currentState = newState;
				if (strategyThread.isAlive())
					activeStrat.kill();
				if (currentState == state.MoveToBall) {
					activeStrat = new InterceptBall(world, mover);
					strategyThread = new Thread(activeStrat,
							"Move to ball Thread");
					System.out.println("[MainPlanner] Move To Ball Started");
				} else if (currentState == state.Defensive) {
					activeStrat = new Defensive(world, mover);
					strategyThread = new Thread(activeStrat, "Defense Thread");
					System.out.println("[MainPlanner] Defense thread started.");
				} else if (currentState == state.Offensive) {
					activeStrat = new Offense2(world, mover);
					strategyThread = new Thread(activeStrat, "Offense Thread");
				} else if (currentState == state.EndOfGame) {
					System.out.println("[MainPlanner] End of game.");
					activeStrat = new EndOfGame(world, mover);
					strategyThread = new Thread((EndOfGame) activeStrat,
							"End of Game Thread");
				}
				strategyThread.start();
			}
		}
	}
}

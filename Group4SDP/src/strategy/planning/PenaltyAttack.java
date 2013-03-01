package strategy.planning;

import java.util.Random;

import strategy.movement.Movement;
import strategy.movement.TurnToBall;
import vision.Position;
import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

public class PenaltyAttack extends StrategyInterface implements Runnable {
	public PenaltyAttack(WorldState world, RobotController rc, Movement mover) {
		super(world, rc, mover);
	}

	@Override
	public void run() {
		Position target = world.getTheirGoal();
		double targetAim = 0.0;
		boolean aimAbove = false;
		// If their robot is off to one side of the goal, aim for the side
		// they're not on
		if (Math.abs(world.theirRobot.y - target.getY()) > 5) {
			aimAbove = world.theirRobot.y > target.getY();
		}
		// Otherwise pick a side at random
		else {
			Random gen = new Random();
			aimAbove = gen.nextBoolean();
		}
		if (aimAbove)
			targetAim = world.areWeOnLeft() ? -18 : 18;
		else
			targetAim = world.areWeOnLeft() ? 18 : -18;

		System.out.println("Turn angle: " + targetAim);

		synchronized (mover) {
			mover.rotate(Math.toRadians(targetAim));
			try {
				mover.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mover.kick();
		}
	}
}

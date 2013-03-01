package strategy.planning;

import java.util.Random;

import strategy.movement.Movement;
import strategy.movement.TurnToBall;
import vision.Position;
import vision.WorldState;
import world.state.Robot;
import world.state.RobotController;

public class PenaltyAttack extends StrategyInterface implements Runnable {
	private Movement move;

	public PenaltyAttack(WorldState world, Robot us, Robot them,
			RobotController rc) {
		super(world, us, them, rc);
	}

	@Override
	public void kill() {
		super.kill();
		if (move != null && move.isAlive())
			move.die();
	}

	@Override
	public void run() {
		Position target = world.getTheirGoal();
		double targetAim = 0.0;
		boolean aimAbove = false;
		// If their robot is off to one side of the goal, aim for the side
		// they're not on
		if (Math.abs(them.y - target.getY()) > 5) {
			aimAbove = them.y > target.getY();
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

		move = new Movement(world, rc, 0, 0, 0, 0,
				Math.toRadians(targetAim), 6);
		move.run();

		rc.kick();
	}
}

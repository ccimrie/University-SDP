package strategy.planning;

import movement.MovingPoint;
import movement.RobotMover;
import utility.SafeSleep;
import vision.Position;
import world.state.WorldState;

public class MovingPointTest extends StrategyInterface {

	public MovingPointTest(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		double angle = 0.0;
		Position pitchCenter = world.goalInfo.getMid();
		final double radius = 100.0;
		final MovingPoint p = new MovingPoint(pitchCenter.getX() + radius,
				pitchCenter.getY());

		try {
			mover.moveTo(p);
			SafeSleep.sleep(42);
			while (!shouldidie && !Strategy.alldie && mover.isRunning()) {
				p.set(radius * Math.cos(angle) + pitchCenter.getX(), radius
						* Math.sin(angle) + pitchCenter.getY());
				SafeSleep.sleep(42);
				angle += Math.PI / 144.0;
				System.out.println("Angle: " + Math.toDegrees(angle));
			}
			mover.stopRobot();
		} catch (InterruptedException ignore) {
		}
	}
}

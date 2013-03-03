package strategy.calculations;

import world.state.Ball;
import world.state.WorldState;

public class Prediction {
	private static final Ball Ball = null;

	// just empty prediction that will be filled up later on

	public double[] predictball(WorldState world) throws InterruptedException {

		int q = world.getBallX();
		int w = world.getBallY();
		wait(1000);
		int x = world.getBallX();
		int y = world.getBallY();
		int dx = x - q;
		int dy = y - w;
		double[] r = new double[] { dx, dy };
		return r;

	}

}

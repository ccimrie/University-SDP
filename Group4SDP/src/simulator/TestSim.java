package simulator;

import javax.swing.UIManager;

import strategy.calculations.GoalInfo;
import utility.SafeSleep;
import vision.PitchConstants;
import world.state.WorldState;

public class TestSim {

	public static float x, y, ballx, bally, oppx, oppy, bearing, oppBearing;

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ignore) {
		}
		final PitchConstants pitchConstants = new PitchConstants(0);
		final GoalInfo goalInfo = new GoalInfo(pitchConstants);
		final WorldState worldState = new WorldState(goalInfo);

		final Simulator sim = new Simulator(worldState);

		while (true) {
			try {
				SafeSleep.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Ball: (" + worldState.getBallX() + ", "
					+ worldState.getBallY() + ")");
			System.out.println("Blue: (" + worldState.getBlueX() + ", "
					+ worldState.getBlueY() + ")");
			System.out.println("Yellow (" + worldState.getYellowX() + ", "
					+ worldState.getYellowY() + ")");
			System.out.println("Blue Orientation: "
					+ Math.toDegrees(worldState.getBlueOrientation()));
			System.out.println("Yellow Orientation: "
					+ Math.toDegrees(worldState.getYellowOrientation()));
			System.out.println();
		}
	}

}

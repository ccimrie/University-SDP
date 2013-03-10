package simulator;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jbox2d.testbed.framework.TestList;
import org.jbox2d.testbed.framework.TestbedFrame;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;

import strategy.calculations.GoalInfo;
import utility.SafeSleep;
import vision.PitchConstants;
import world.state.WorldState;

public class TestSim {

	public static float x, y, ballx, bally, oppx, oppy, bearing, oppBearing;

	public static void main(String[] args) {
		final PitchConstants pitchConstants = new PitchConstants(0);
		final GoalInfo goalInfo = new GoalInfo(pitchConstants);
		final WorldState worldState = new WorldState(goalInfo);

		Thread thread1 = new Thread() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {

				}
				// Instantiate model where all the tests reside.
				TestbedModel model = new TestbedModel();
				TestbedPanel panel = new TestPanelJ2D(model);

				// Instantiate new custom test.
				Updateworld matchTest = new Updateworld(worldState);

				// Add test to the model
				model.addCategory("Robots");
				model.addTest(matchTest);

				// Add the default tests which were included in JBox2D distro.
				TestList.populateModel(model);
				JFrame testbed = new TestbedFrame(model, panel);
				testbed.setVisible(true);
				testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			}
		};

		Thread thread2 = new Thread() {
			public void run() {
				while (true) {
					try {
						SafeSleep.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//System.out.println("Ball x :" + worldState.getBallX()
					//		+ " y :" + worldState.getBallY());
				//	System.out.println("Blue x :" + worldState.getBlueX()
				//			+ " y :" + worldState.getBlueY());
				//	System.out.println("Yellow x :" + worldState.getYellowX()
				//			+ " y :" + worldState.getYellowY());
					// System.out.println("Blue Orientation :" +
					// worldState.getBlueOrientation());
					 System.out.println("Yellow Orientation :" +
					 Math.toDegrees(worldState.getYellowOrientation()));

				}

			}

		};

		thread1.start();
		thread2.start();
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {

		}
	}

}

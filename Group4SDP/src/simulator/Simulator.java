package simulator;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jbox2d.dynamics.Body;
import org.jbox2d.testbed.framework.TestList;
import org.jbox2d.testbed.framework.TestbedFrame;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;

import world.state.WorldState;

/**
 * 
 * @author Alex Adams (s1046358)
 */
public class Simulator extends Thread {
	private final WorldState worldState;

	private final JFrame testbed;

	private boolean die = false;

	private Body ourRobot;
	private Body enemyRobot;

	public Simulator(final WorldState worldState) {
		super("simulator");
		this.worldState = worldState;

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
		}
		// Instantiate model where all the tests reside.
		TestbedModel model = new TestbedModel();
		TestbedPanel panel = new TestPanelJ2D(model);

		// Instantiate new custom test.
		RoboFootball matchTest = new RoboFootball();
		// Add test to the model
		model.addCategory("Robots");
		model.addTest(matchTest);

		testbed = new TestbedFrame(model, panel);
		testbed.setVisible(true);
		testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void run() {
		while (!die) {

		}
	}
}

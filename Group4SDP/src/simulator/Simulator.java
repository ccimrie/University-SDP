package simulator;

import javax.swing.JFrame;

import org.jbox2d.testbed.framework.TestbedFrame;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;

/**
 * A class for wrapping a SimulatorTestbed object in a JBox2D simulation
 * 
 * @author Alex Adams (s1046358)
 */
public class Simulator {

	private final JFrame testbed;

	public Simulator(final SimulatorTestbed simTest) {
		// Instantiate model where all the tests reside.
		TestbedModel model = new TestbedModel();
		TestbedPanel panel = new TestPanelJ2D(model);

		// Add test to the model
		model.addCategory("SDP");
		model.addTest(simTest);

		testbed = new TestbedFrame(model, panel);
		testbed.setVisible(true);
		testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

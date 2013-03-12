package simulator;

import javax.swing.JFrame;

import org.jbox2d.common.Vec2;
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
	private static final float scaleX = 578.0f / 2.445f;
	private static final float scaleY = 312.0f / 1.225f; 

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

	/**
	 * Converts angles from JBox2D's coordinate system to the same system used
	 * by vision & strategy
	 * 
	 * @param jboxAngle
	 *            The angle in JBox2D's coordinate system (in radians)
	 * @return The angle in our coordinate system (in radians)
	 */
	public static double convertAngle(double jboxAngle) {
		double x = Math.cos(jboxAngle);
		double y = Math.sin(jboxAngle);

		double angle = Math.acos(y);
		if (angle < 0)
			angle += 2.0 * Math.PI;
		if (x < 0)
			angle = 2.0 * Math.PI - angle;

		return angle;
	}

	/**
	 * TODO: proper implementation <br/>
	 * Converts coordinates from the simulator's coordinate system to the
	 * vision's
	 * 
	 * @param simCoords
	 *            The simulator coordinates to be converted
	 * @return The coordinates after conversion
	 */
	public static Vec2 convertCoordsFromSim(Vec2 simCoords) {
		return new Vec2(30.0f + simCoords.x * scaleX, 390.0f - simCoords.y * scaleY);
	}
}

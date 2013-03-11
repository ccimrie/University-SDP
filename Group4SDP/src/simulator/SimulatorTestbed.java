package simulator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

import simulator.objects.Ball;
import simulator.objects.Pitch;
import simulator.objects.Robot;
import world.state.RobotType;
import world.state.WorldState;

/**
 * 
 * @author Alex Adams (s1046358)
 * @author Bachir
 */
public class SimulatorTestbed extends TestbedTest {
	private final WorldState worldState;

	private Pitch pitch;
	private Ball simBall;
	public final Robot simOurRobot;
	public final Robot simTheirRobot;

	public SimulatorTestbed(final WorldState worldState) {
		super();
		this.worldState = worldState;
		simOurRobot = new Robot();
		simTheirRobot = new Robot();
	}

	@Override
	public String getTestName() {
		return "Group 4 Simulator";
	}

	@Override
	public void initTest(boolean arg0) {
		System.out.println("Initializing Simulator");

		World world = getWorld();
		this.pitch = new Pitch(world);
		this.simBall = new Ball(world);

		this.simOurRobot.init(world, true);
		this.simTheirRobot.init(world, false);

		getWorld().setGravity(new Vec2(0.0f, 0.0f));

		setCamera(new Vec2(Pitch.length / 2, Pitch.width / 2).mul(Pitch.scale),
				0.5f * Pitch.scale);
	}

	@Override
	public boolean isSaveLoadEnabled() {
		return true;
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

	public double getRobotOrientation(RobotType robot) {
		double result = 0;
		switch (robot) {
		case Us:
			result = convertAngle(simOurRobot.body.getAngle());
			break;
		case Them:
			result = convertAngle(simTheirRobot.body.getAngle());
			break;
		default:
			System.out
					.println("DERP! Invalid robot type passed to SimulatorTestbed.getRobotOrientation()");
		}
		return result;
	}

	@Override
	public void step(TestbedSettings settings) {
		assert (simBall != null && simOurRobot != null && simTheirRobot != null) : "SimulatorTestbed stepped before being initialized";

		try {
			simOurRobot.beforeStep();
			simTheirRobot.beforeStep();

			// Step the simulation
			super.step(settings);

			simOurRobot.afterStep();
			simTheirRobot.afterStep();

			// Update the world state
			worldState
					.setBallX((int) (simBall.body.getWorldCenter().x * Pitch.scale));
			worldState
					.setBallY((int) (simBall.body.getWorldCenter().y * Pitch.scale));

			worldState
					.setBlueX((int) (simOurRobot.body.getWorldCenter().x * Pitch.scale));
			worldState
					.setBlueY((int) (simOurRobot.body.getWorldCenter().y * Pitch.scale));

			worldState
					.setYellowX((int) (simTheirRobot.body.getWorldCenter().x * Pitch.scale));
			worldState
					.setYellowY((int) (simTheirRobot.body.getWorldCenter().y * Pitch.scale));

			worldState.setBlueOrientation(convertAngle(simOurRobot.body
					.getAngle()));

			worldState.setYellowOrientation(convertAngle(simTheirRobot.body
					.getAngle()));
		} catch (InterruptedException e) {
			System.out.println("Simulator interrupted");
		}
	}
}

package simulator;

import org.jbox2d.common.MathUtils;
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
 * The main simulator class
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
		try {

			World world = getWorld();
			this.pitch = new Pitch(world);
			this.simBall = new Ball(world, new Vec2(pitch.length / 2.0f,
					pitch.width / 2.0f));

			this.simOurRobot.init(world, new Vec2(pitch.length * 0.1f,
					pitch.width / 2.0f), 0.0f);
			this.simTheirRobot.init(world, new Vec2(pitch.length * 0.9f,
					pitch.width / 2.0f), MathUtils.PI);

			getWorld().setGravity(new Vec2(0.0f, 0.0f));

			setCamera(new Vec2(pitch.length / 2, pitch.width / 2), 200.0f);

		} catch (InterruptedException ignore) {
		}
	}

	@Override
	public boolean isSaveLoadEnabled() {
		return true;
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
			Vec2 ball = Simulator.convertCoordsFromSim(simBall.body
					.getWorldCenter());
			worldState.setBallX((int) ball.x);
			worldState.setBallY((int) ball.y);

			Vec2 ourRobot = Simulator.convertCoordsFromSim(simOurRobot.body
					.getWorldCenter());
			double ourRobotAngle = Simulator.convertAngle(simOurRobot.body
					.getAngle());
			Vec2 theirRobot = Simulator.convertCoordsFromSim(simTheirRobot.body
					.getWorldCenter());
			double theirRobotAngle = Simulator.convertAngle(simTheirRobot.body
					.getAngle());

			if (worldState.areWeBlue()) {
				worldState.setBlueX((int) ourRobot.x);
				worldState.setBlueY((int) ourRobot.y);
				worldState.setBlueOrientation(ourRobotAngle);

				worldState.setYellowX((int) theirRobot.x);
				worldState.setYellowY((int) theirRobot.y);
				worldState.setYellowOrientation(theirRobotAngle);
			} else {
				worldState.setBlueX((int) theirRobot.x);
				worldState.setBlueY((int) theirRobot.y);
				worldState.setBlueOrientation(theirRobotAngle);

				worldState.setYellowX((int) ourRobot.x);
				worldState.setYellowY((int) ourRobot.y);
				worldState.setYellowOrientation(ourRobotAngle);
			}

			worldState.update();

			worldState.setOurRobot();
			worldState.setTheirRobot();
			worldState.setBall();
			worldState.updatePossesion();

		} catch (InterruptedException e) {
			System.out.println("Simulator interrupted");
		}
	}
	
}

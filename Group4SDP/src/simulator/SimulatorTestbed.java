package simulator;

import java.util.concurrent.locks.ReentrantLock;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
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

	private Vec2 ourRobotSpeed = new Vec2();
	private float ourRobotRotSpeed = 0f;
	private Vec2 theirRobotSpeed = new Vec2();
	private float theirRobotRotSpeed = 0f;
	private final ReentrantLock ourRobotLock = new ReentrantLock(true);
	private final ReentrantLock theirRobotLock = new ReentrantLock(true);

	Pitch pitch;
	Ball simBall;
	Robot simOurRobot, simTheirRobot;

	public SimulatorTestbed(final WorldState worldState) {
		super();
		this.worldState = worldState;
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

		this.simOurRobot = new Robot(world, true);
		this.simTheirRobot = new Robot(world, false);
		
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
	private double convertAngle(double jboxAngle) {
		double x = Math.cos(jboxAngle);
		double y = Math.sin(jboxAngle);

		double angle = Math.acos(y);
		if (angle < 0)
			angle += 2.0 * Math.PI;
		if (x < 0)
			angle = 2.0 * Math.PI - angle;

		return angle;
	}

	private void setOurRobotSpeed(double speedX, double speedY) {
		ourRobotLock.lock();
		// Simulator coordinates are different to the ones we use.
		ourRobotSpeed = new Vec2((float) speedY, (float) speedX);
		ourRobotLock.unlock();
	}

	private void setEnemyRobotSpeed(double speedX, double speedY) {
		theirRobotLock.lock();
		// Simulator coordinates are different to the ones we use.
		theirRobotSpeed = new Vec2((float) speedY, (float) speedX);
		theirRobotLock.unlock();
	}

	public synchronized void setRobotSpeed(RobotType robot, double speedX,
			double speedY) {
		switch (robot) {
		case Us:
			setOurRobotSpeed(speedX, speedY);
			break;
		case Them:
			setEnemyRobotSpeed(speedX, speedY);
			break;
		default:
			System.out
					.println("DERP! Invalid robot type passed to SimulatorTestbed.setRobotSpeed()");
		}
	}

	private void setOurRobotRotationSpeed(double rotationSpeed) {
		ourRobotLock.lock();
		ourRobotRotSpeed = (float) rotationSpeed;
		ourRobotLock.unlock();
	}

	private void setEnemyRobotRotationSpeed(double rotationSpeed) {
		theirRobotLock.lock();
		theirRobotRotSpeed = (float) rotationSpeed;
		theirRobotLock.unlock();
	}

	public synchronized void setRobotRotationSpeed(RobotType robot,
			double rotationSpeed) {
		switch (robot) {
		case Us:
			setOurRobotRotationSpeed(rotationSpeed);
			break;
		case Them:
			setEnemyRobotRotationSpeed(rotationSpeed);
			break;
		default:
			System.out
					.println("DERP! Invalid robot type passed to SimulatorTest.setRobotRotationSpeed()");
		}
	}

	@Override
	public void step(TestbedSettings settings) {

		// Apply force vectors and torque
		Body ourRobot = simOurRobot.body;
		Body theirRobot = simTheirRobot.body;

		ourRobotLock.lock();
		ourRobot.applyForce(ourRobot.getWorldVector(ourRobotSpeed),
				ourRobot.getWorldPoint(ourRobot.getLocalCenter()));
		ourRobot.applyTorque(ourRobotRotSpeed);
		ourRobotLock.unlock();

		theirRobotLock.lock();
		theirRobot.applyForce(theirRobot.getWorldVector(theirRobotSpeed),
				theirRobot.getWorldPoint(ourRobot.getLocalCenter()));
		theirRobot.applyTorque(theirRobotRotSpeed);
		theirRobotLock.unlock();

		// Step the simulation
		super.step(settings);

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

		worldState
				.setBlueOrientation(convertAngle(simOurRobot.body.getAngle()));

		worldState.setYellowOrientation(convertAngle(simTheirRobot.body
				.getAngle()));
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
}

package simulator;

import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.testbed.framework.TestbedFrame;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;

import world.state.RobotType;
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
	private Vec2 ourRobotForce;
	private float ourRobotTorque;
	private Body enemyRobot;
	private Vec2 enemyRobotForce;
	private float enemyRobotTorque;
	private final ReentrantLock ourRobotLock = new ReentrantLock(true);
	private final ReentrantLock enemyRobotLock = new ReentrantLock(true);

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

	public void kill() {
		die = true;
	}

	@Override
	public void run() {
		while (!die) {
			// Apply force vectors and torque;
			ourRobot.applyForce(ourRobot.getWorldVector(ourRobotForce),
					ourRobot.getWorldPoint(ourRobot.getLocalCenter()));
			ourRobot.applyTorque(ourRobotTorque);

			enemyRobot.applyForce(enemyRobot.getWorldVector(enemyRobotForce),
					enemyRobot.getWorldPoint(ourRobot.getLocalCenter()));
			enemyRobot.applyTorque(enemyRobotTorque);
		}
	}

	private void setOurRobotSpeed(double speedX, double speedY) {
		ourRobotLock.lock();
		// Simulator coordinates are different to the ones we use.
		ourRobotForce = new Vec2((float) speedY, (float) speedX);
		ourRobotLock.unlock();
	}

	private void setEnemyRobotSpeed(double speedX, double speedY) {
		enemyRobotLock.lock();
		// Simulator coordinates are different to the ones we use.
		enemyRobotForce = new Vec2((float) speedY, (float) speedX);
		enemyRobotLock.unlock();
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
					.println("DERP! Invalid robot type passed to Simulator.setRobotSpeed()");
		}
	}

	private void setOurRobotRotationSpeed(double rotationSpeed) {
		ourRobotLock.lock();
		ourRobotTorque = (float) rotationSpeed;
		ourRobotLock.unlock();
	}

	private void setEnemyRobotRotationSpeed(double rotationSpeed) {
		enemyRobotLock.lock();
		enemyRobotTorque = (float) rotationSpeed;
		enemyRobotLock.unlock();
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
					.println("DERP! Invalid robot type passed to Simulator.setRobotSpeed()");
		}
	}

	public double getRobotOrientation(RobotType robot) {
		double result = 0;
		switch (robot) {
		case Us:
			result = ourRobot.getAngle();
			break;
		case Them:
			result = enemyRobot.getAngle();
			break;
		default:
			System.out
					.println("DERP! Invalid robot type passed to Simulator.setRobotSpeed()");
		}
		return result;
	}
}

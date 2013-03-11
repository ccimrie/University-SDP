package simulator.objects;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;

import simulator.SimulatorTestbed;

/**
 * A class representing a robot in the simulator, which is also responsible for
 * managing things like speed and rotation for that robot
 * 
 * @author Alex Adams (s1046358)
 */
public class Robot {
	/** The robot's body in the simulator */
	public Body body;
	/** A controllable part representing the robot's kicker */
	private PrismaticJoint kicker;

	/**
	 * The robot's speed vector, where x is the forward/back speed, y is
	 * left/right, both from -100 to 100.
	 */
	private Vec2 speed = new Vec2();
	/** The rotation speed for the robot in radians per second */
	private float rotSpeed = 0f;

	/**
	 * The threshold for rotating, within which the rotation should stop.
	 */
	private static final double rotateThreshold = Math.toRadians(2);

	/** Flag to enable kicking */
	private boolean kickActive = false;
	/**
	 * State variable for the kicker to spread out the kicker's motion over a
	 * few steps
	 */
	private int kickStep = 0;
	/**
	 * Used to make the kick() caller wait until the kick finishes, micking the
	 * real robot finishing the kick before returning confirmation
	 */
	private Semaphore kickSem = new Semaphore(0, true);

	/** Flag to enable rotation */
	private boolean rotateActive = false;
	/** The direction vector to rotate to */
	private Vec2 targetOrient;
	/**
	 * Used to make the rotate() caller wait until the rotate finishes,
	 * mimicking the real robot finishing the rotate before returning
	 * confirmation
	 */
	private Semaphore rotSem = new Semaphore(0, true);

	/**
	 * Recursive mutex to prevent variables being read/written by multiple
	 * threads simultaneously
	 */
	private ReentrantLock lock = new ReentrantLock(true);

	/**
	 * (Re-)initializes the robot. This should only be called during a call to
	 * initTest() in SimulatorTestbed.
	 * 
	 * @param world
	 *            The simulator's world
	 * @param isOurRobo
	 *            A flag to say whether this robot is ours or not
	 */
	public void init(World world, boolean isOurRobo) {
		float scale = 20.0f;

		// Create the robot with its properties
		PolygonShape robotShape = new PolygonShape();
		robotShape.setAsBox(0.09f * scale, 0.09f * scale);

		FixtureDef robotFixDef = new FixtureDef();
		robotFixDef.shape = robotShape;
		robotFixDef.density = 10.0f;
		robotFixDef.friction = 0.3f;

		BodyDef robotBodyDef = new BodyDef();
		robotBodyDef.type = BodyType.DYNAMIC;
		robotBodyDef.angularDamping = 4.0f;
		robotBodyDef.linearDamping = 3.0f;
		robotBodyDef.allowSleep = false;

		// TODO: move this so we can choose where the robot starts
		if (isOurRobo) {
			robotBodyDef.position.set(0.1f * scale, Pitch.width * scale / 2);

			robotBodyDef.angle = 0;
		} else {

			robotBodyDef.position.set(Pitch.length * scale - 0.1f * scale,
					Pitch.width * scale / 2);
			robotBodyDef.angle = MathUtils.PI;
		}
		body = world.createBody(robotBodyDef);
		body.createFixture(robotFixDef);

		// Create a kicker
		PolygonShape kickerShape = new PolygonShape();
		kickerShape.setAsBox(0.001f * scale, 0.09f * scale);

		FixtureDef kickerFixDef = new FixtureDef();
		kickerFixDef.shape = kickerShape;
		kickerFixDef.density = 3.0f;
		kickerFixDef.friction = 0.3f;

		BodyDef kickerBodyDef = new BodyDef();
		kickerBodyDef.type = BodyType.DYNAMIC;
		if (isOurRobo) {
			kickerBodyDef.position.set(body.getWorldCenter().x + 0.09f * scale
					+ 0.002f * scale, body.getWorldCenter().y);
			kickerBodyDef.angle = 0;
		} else {
			kickerBodyDef.position.set(body.getWorldCenter().x - 0.09f * scale
					- 0.002f * scale, body.getWorldCenter().y);
			kickerBodyDef.angle = MathUtils.PI;
		}
		kickerBodyDef.allowSleep = false;
		Body kickerBody = world.createBody(kickerBodyDef);
		kickerBody.createFixture(kickerFixDef);

		// Create a mechanical joint between robot and kicker
		PrismaticJointDef kickerJointDef = new PrismaticJointDef();
		if (isOurRobo) {
			kickerJointDef.localAxis1.set(1.0f, 0.0f);
		} else {
			kickerJointDef.localAxis1.set(-1.0f, 0.0f);
		}
		kickerJointDef.localAxis1.normalize();
		kickerJointDef.localAnchorA.set(body.getLocalCenter());
		kickerJointDef.localAnchorB.set(0.0f, 0.09f * scale);

		kickerJointDef.initialize(body, kickerBody, body.getWorldCenter(),
				kickerJointDef.localAxis1);

		// TODO: calibrate
		if (isOurRobo) {
			kickerJointDef.motorSpeed = 200.0f;
		} else {
			kickerJointDef.motorSpeed = -200.0f;
		}
		kickerJointDef.maxMotorForce = 400.0f;
		kickerJointDef.enableMotor = true;
		kickerJointDef.lowerTranslation = 0.0f;
		kickerJointDef.upperTranslation = 0.7f;
		kickerJointDef.enableLimit = true;

		this.kicker = (PrismaticJoint) world.createJoint(kickerJointDef);
	}

	/**
	 * TODO: calibrate<br/>
	 * Step pre-processing method which processes the forces to be applied for
	 * this step
	 * 
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public void beforeStep() throws InterruptedException {
		lock.lockInterruptibly();
		// Apply forward/back/left/right speed
		body.applyLinearImpulse(body.getWorldVector(speed),
				body.getWorldPoint(body.getLocalCenter()));

		// If we're doing a rotation, apply the angular motion
		if (rotateActive)
			body.applyAngularImpulse(body.getMass() * rotSpeed);

		if (kickActive) {
			// TODO: calibrate
			// Slows the kickers motion over 5 steps
			if (kickStep < 5) {
				kicker.setMotorSpeed(100.0f);
				++kickStep;
			} else {
				kicker.setMotorSpeed(-100.0f);
				kickStep = 0;
			}
		} else {
			// If we're not kicking, make sure the kicker stays retracted
			kicker.setMotorSpeed(-100.0f);
		}
		lock.unlock();
	}

	/**
	 * Step post-processing method which is responsible for waking up threads
	 * waiting for rotation or kicking to finish
	 * 
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public void afterStep() throws InterruptedException {
		if (rotateActive) {
			lock.lockInterruptibly();
			float angle = body.getAngle();
			Vec2 orient = new Vec2((float) Math.cos(angle),
					(float) Math.sin(angle));
			if (Math.acos(Vec2.dot(orient, targetOrient)) < rotateThreshold) {
				rotSem.release();
				rotateActive = false;
			}
			lock.unlock();
		}
		if (kickActive && kickStep == 0) {
			lock.lockInterruptibly();
			kickActive = false;
			kickSem.release();
			lock.unlock();
		}
	}

	/**
	 * Sets the speed vector for the robot
	 * 
	 * @param speedX
	 *            The speed in the left/right axis relative to the robot
	 * @param speedY
	 *            The speed in the forward/back axis relative to the robot
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public void setSpeed(double speedX, double speedY)
			throws InterruptedException {
		lock.lockInterruptibly();
		// Simulator coordinates are different to the ones we use.
		speed.set((float) speedY, (float) -speedX);
		lock.unlock();
	}

	/**
	 * Sets the rotation speed for the robot
	 * 
	 * @param rotSpeed
	 *            The rotation speed for the robot in radians
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public void setRotSpeed(double rotSpeed) throws InterruptedException {
		lock.lockInterruptibly();
		this.rotSpeed = -(float) rotSpeed;
		lock.unlock();
	}

	/**
	 * Gets the bearing for the robot in the coordinate system used by vision &
	 * strategy
	 * 
	 * @return The clockwise bearing relative to north in radians
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public double getOrientation() throws InterruptedException {
		lock.lockInterruptibly();
		double result = SimulatorTestbed.convertAngle(body.getAngle());
		lock.unlock();
		return result;
	}

	/**
	 * Performs a rotation for the robot and waits for it to complete before
	 * returning, to mimick waiting for the confirmation code from the real
	 * robot
	 * 
	 * @param angleRad
	 *            The angle the robot should rotate by, in radians
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public void rotate(double angleRad) throws InterruptedException {
		// Don't bother with angles that're too small.
		if (Math.abs(angleRad) < rotateThreshold)
			return;
		lock.lockInterruptibly();
		// TODO: calibrate
		double rotSpeed = Math.PI / 10;
		if (angleRad < 0)
			rotSpeed = -rotSpeed;
		setRotSpeed(rotSpeed);

		float angle = body.getAngle() - (float) angleRad;
		targetOrient = new Vec2((float) Math.cos(angle),
				(float) Math.sin(angle));
		rotateActive = true;
		lock.unlock();

		// Wait for the rotation to complete
		rotSem.acquire();
	}

	/**
	 * Performs a kick for the robot and waits for it to complete before
	 * returning, to mimick waiting for the confirmation code from the real
	 * robot
	 * 
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public void kick() throws InterruptedException {
		lock.lockInterruptibly();
		kickActive = true;
		kickStep = 0;
		lock.unlock();

		// Wait for the kick to complete
		kickSem.acquire();
	}
}

package simulator.objects;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import simulator.Simulator;
import simulator.objects.kickers.SimpleDribbler;
import simulator.objects.kickers.SimpleKicker;

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
	private SimpleKicker kicker;
	private SimpleDribbler dribbler;

	/**
	 * The robot's speed vector, where x is the forward/back speed, y is
	 * left/right, both from -100 to 100.
	 */
	private Vec2 speed = new Vec2();
	/** The rotation speed for the robot in radians per second */
	private float rotSpeed = 0f;

	private float power = 1;

	/**
	 * The threshold for rotating, within which the rotation should stop.
	 */
	private static final double rotateThreshold = Math.toRadians(2);

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
	 * @throws InterruptedException
	 *             If the thread is interrupted
	 */
	public void init(final World world, final Vec2 initialPos,
			final float initialAngle) throws InterruptedException {
		lock.lockInterruptibly();

		// Create the robot with its properties
		PolygonShape robotShape = new PolygonShape();
		robotShape.setAsBox(0.09f, 0.09f);

		FixtureDef robotFixDef = new FixtureDef();
		robotFixDef.shape = robotShape;
		// Realistic density - water is 1000.
		robotFixDef.density = 600.0f;
		robotFixDef.friction = 0.3f;

		BodyDef robotBodyDef = new BodyDef();
		robotBodyDef.type = BodyType.DYNAMIC;
		robotBodyDef.angularDamping = 5.0f;
		robotBodyDef.linearDamping = 5.0f;
		robotBodyDef.allowSleep = false;

		robotBodyDef.position.set(initialPos);
		System.out.println("Robot position: "
				+ robotBodyDef.position.toString());
		robotBodyDef.angle = initialAngle;

		body = world.createBody(robotBodyDef);
		body.createFixture(robotFixDef);

		// Create a kicker
		PolygonShape kickerShape = new PolygonShape();
		kickerShape.setAsBox(0.001f, 0.09f);

		Vec2 kickerPosition = (new Vec2(0.095f * (float) Math
				.cos(initialAngle), 0.095f * (float) Math.sin(initialAngle)))
				.add(body.getWorldCenter());
		
		kicker = SimpleKicker.createKicker(world, body, kickerPosition, initialAngle, kickerShape);
//		dribbler = SimpleDribbler.createDribbler(world, body, kickerPosition, initialAngle);
		lock.unlock();
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
		body.applyLinearImpulse(body.getWorldVector(new Vec2(speed.x / 144.0f,
				speed.y / 120.0f)), body.getWorldPoint(body.getLocalCenter()));

		// If we're doing a rotation, apply the angular motion
		if (rotateActive)
			body.applyAngularImpulse(rotSpeed / 30.0f);
		
		kicker.beforeStep();
//		dribbler.beforeStep();
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
		lock.lockInterruptibly();
		if (rotateActive) {
			float angle = body.getAngle();
			Vec2 orient = new Vec2((float) Math.cos(angle),
					(float) Math.sin(angle));
			if (Math.acos(Vec2.dot(orient, targetOrient)) < rotateThreshold) {
				rotSem.release();
				rotateActive = false;
			}
		}
		kicker.afterStep();
//		dribbler.afterStep();
		lock.unlock();
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
		speed.set((float) speedY * power, (float) -speedX * power);
		lock.unlock();
	}

	public void setPower(float powerInput) {
		power = powerInput;
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
		double result = Simulator.convertAngle(body.getAngle());
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
		kicker.setUpKick();
		lock.unlock();
		
		kicker.waitForKickCompletion();
	}
	
	public void activateDribbler() {
		dribbler.activate();
	}
	
	public void deactivateDribbler() {
		dribbler.deactivate();
	}
}

package simulator.objects.kickers;

import java.util.concurrent.Semaphore;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;

public class SimpleKicker extends KickerPart {
	protected PrismaticJoint joint;

	/** Flag to enable kicking */
	protected boolean kickActive = false;
	/**
	 * State variable for the kicker to spread out the kicker's motion over a
	 * few steps
	 */
	protected int kickStep = 0;
	/**
	 * Used to make the kick() caller wait until the kick finishes, micking the
	 * real robot finishing the kick before returning confirmation
	 */
	protected Semaphore kickSem = new Semaphore(0, true);

	protected SimpleKicker(World world, BodyDef kickerBodyDef,
			FixtureDef kickerFixDef) {
		super(world, kickerBodyDef, kickerFixDef);
	}

	/**
	 * Factory method to create a SimpleKicker object
	 * 
	 * @param world
	 *            The simulator world
	 * @param robotBody
	 *            The Body object for the robot the kicker is to be attached to
	 * @param initialPos
	 *            The initial position of the kicker, relative to the world
	 * @param initialAngle
	 *            The initial angle of the kicker, relative to the world
	 * @param kickerShape
	 *            The shape of the kicker
	 * @return A SimpleKicker object initialised according to the parameters.
	 */
	public static SimpleKicker createKicker(World world, Body robotBody,
			Vec2 initialPos, float initialAngle, PolygonShape kickerShape) {
		FixtureDef kickerFixDef = new FixtureDef();
		kickerFixDef.shape = kickerShape;
		kickerFixDef.density = 600.0f;
		kickerFixDef.friction = 0.3f;

		BodyDef kickerBodyDef = new BodyDef();
		kickerBodyDef.type = BodyType.DYNAMIC;
		kickerBodyDef.position.set(initialPos);
		kickerBodyDef.angle = initialAngle;
		kickerBodyDef.allowSleep = false;

		SimpleKicker kicker = new SimpleKicker(world, kickerBodyDef,
				kickerFixDef);

		PrismaticJointDef kickerJointDef = new PrismaticJointDef();
		kickerJointDef.localAxis1.set((float) Math.cos(initialAngle),
				(float) Math.sin(initialAngle));
		kickerJointDef.localAxis1.normalize();
		kickerJointDef.localAnchorA.set(robotBody.getLocalCenter());
		kickerJointDef.localAnchorB.set(0.0f, 0.09f);

		kickerJointDef.initialize(robotBody, kicker.body,
				robotBody.getWorldCenter(), kickerJointDef.localAxis1);

		// TODO: calibrate / GUI controls for adjusting
		kickerJointDef.motorSpeed = 2.0f;
		kickerJointDef.maxMotorForce = 4.0f;
		kickerJointDef.enableMotor = true;
		kickerJointDef.lowerTranslation = 0.0f;
		kickerJointDef.upperTranslation = 0.1f;
		kickerJointDef.enableLimit = true;

		kicker.joint = (PrismaticJoint) world.createJoint(kickerJointDef);

		return kicker;
	}

	@Override
	public void beforeStep() {
		if (kickActive) {
			// TODO: calibrate / GUI controls for adjusting
			// Slows the kickers motion over 5 steps
			if (kickStep < 5) {
				joint.setMotorSpeed(2.0f);
				++kickStep;
			} else {
				joint.setMotorSpeed(-2.0f);
				kickStep = 0;
			}
		} else {
			// If we're not kicking, make sure the kicker stays retracted
			joint.setMotorSpeed(-2.0f);
		}
	}

	@Override
	public void afterStep() {
		if (kickActive && kickStep == 0) {
			kickActive = false;
			if (kickSem.hasQueuedThreads())
				kickSem.release();
		}
	}

	/**
	 * Triggers a kick to start
	 */
	public void setUpKick() {
		kickActive = true;
		kickStep = 0;
	}

	/**
	 * Blocks the calling thread until a kick has completed
	 * 
	 * @throws InterruptedException
	 *             If the calling thread is interrupted before the kick is
	 *             completed
	 */
	public void waitForKickCompletion() throws InterruptedException {
		kickSem.acquire();
	}
}

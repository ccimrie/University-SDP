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

public class Robot {
	public final Body body;
	private PrismaticJoint joint;

	private Vec2 speed = new Vec2();
	private float rotSpeed = 0f;

	private static final double rotateThreshold = Math.toRadians(2);

	private boolean kickActive = false;
	private int kickStep = 0;
	private Semaphore kickSem = new Semaphore(0, true);

	private boolean rotateActive = false;
	private Vec2 targetOrient;
	private Semaphore rotSem = new Semaphore(0, true);

	private ReentrantLock lock = new ReentrantLock(true);

	public Robot(World world, boolean isOurRobo) {
		float scale = 20.0f;
		// Updateworld.scale = scale;
		// create robot's body
		PolygonShape roboShape = new PolygonShape();
		roboShape.setAsBox(0.09f * scale, 0.09f * scale);

		FixtureDef fd = new FixtureDef();
		fd.shape = roboShape;
		fd.density = 10.0f;
		fd.friction = 0.3f;

		BodyDef bdr = new BodyDef();
		bdr.type = BodyType.DYNAMIC;
		bdr.angularDamping = 4.0f;
		bdr.linearDamping = 3.0f;
		bdr.allowSleep = false;

		if (isOurRobo) {
			bdr.position.set(0.1f * scale, Pitch.width * scale / 2);

			bdr.angle = 0;
		} else {

			bdr.position.set(Pitch.length * scale - 0.1f * scale, Pitch.width
					* scale / 2);
			bdr.angle = MathUtils.PI;
		}
		body = world.createBody(bdr);
		body.createFixture(fd);

		// Create a bumper mechanism imitating flap in 2 dimensions
		PolygonShape shapeFlap = new PolygonShape();
		shapeFlap.setAsBox(0.001f * scale, 0.09f * scale);

		FixtureDef flapfx = new FixtureDef();
		flapfx.shape = shapeFlap;
		flapfx.density = 3.0f;
		flapfx.friction = 0.3f;

		BodyDef bdFlap = new BodyDef();
		bdFlap.type = BodyType.DYNAMIC;
		if (isOurRobo) {
			bdFlap.position.set(body.getWorldCenter().x + 0.09f * scale
					+ 0.002f * scale, body.getWorldCenter().y);
			bdFlap.angle = 0;
		} else {
			bdFlap.position.set(body.getWorldCenter().x - 0.09f * scale
					- 0.002f * scale, body.getWorldCenter().y);
			bdFlap.angle = MathUtils.PI;
		}
		bdFlap.allowSleep = false;
		Body flap = world.createBody(bdFlap);
		flap.createFixture(flapfx);

		// Create a mechanical joint between robot and flap/bumper
		PrismaticJointDef pjd = new PrismaticJointDef();
		if (isOurRobo) {
			pjd.localAxis1.set(1.0f, 0.0f);
		} else {
			pjd.localAxis1.set(-1.0f, 0.0f);
		}
		pjd.localAxis1.normalize();
		pjd.localAnchorA.set(body.getLocalCenter());
		pjd.localAnchorB.set(0.0f, 0.09f * scale);

		pjd.initialize(body, flap, body.getWorldCenter(), pjd.localAxis1);

		if (isOurRobo) {
			pjd.motorSpeed = 500.0f;
		} else {
			pjd.motorSpeed = -500.0f;
		}
		pjd.maxMotorForce = 800.0f;
		pjd.enableMotor = true;
		pjd.lowerTranslation = 0.0f;
		pjd.upperTranslation = 0.8f;
		pjd.enableLimit = true;

		joint = (PrismaticJoint) world.createJoint(pjd);
	}

	public void beforeStep() throws InterruptedException {
		lock.lockInterruptibly();
		body.applyForce(body.getWorldVector(speed),
				body.getWorldPoint(body.getLocalCenter()));
		body.applyTorque(rotSpeed);

		if (kickActive) {
			if (kickStep < 5) {
				joint.setMotorSpeed(100.0f);
				++kickStep;
			} else {
				joint.setMotorSpeed(-100.0f);
				kickStep = 0;
			}
		} else {
			joint.setMotorSpeed(-100.0f);
		}
		lock.unlock();
	}

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
			kickSem.release();
			kickActive = false;
			lock.unlock();
		}
	}

	public void setSpeed(double speedX, double speedY)
			throws InterruptedException {
		lock.lockInterruptibly();
		// Simulator coordinates are different to the ones we use.
		speed.set((float) speedY, (float) speedX);
		lock.unlock();
	}

	public void setRotSpeed(double rotSpeed) throws InterruptedException {
		lock.lockInterruptibly();
		rotSpeed = (float) rotSpeed;
		lock.unlock();
	}

	public double getOrientation() throws InterruptedException {
		lock.lockInterruptibly();
		double result = SimulatorTestbed.convertAngle(body.getAngle());
		lock.unlock();
		return result;
	}

	public void rotate(double angleRad) throws InterruptedException {
		// Don't bother with angles that're too small.
		if (angleRad < rotateThreshold)
			return;
		lock.lockInterruptibly();
		setRotSpeed(Math.PI / 10);
		float angle = body.getAngle() + (float) angleRad;
		targetOrient = new Vec2((float) Math.cos(angle),
				(float) Math.sin(angle));
		rotateActive = true;
		lock.unlock();

		// Wait for the rotation to complete
		rotSem.acquire();
	}

	public void kick() throws InterruptedException {
		lock.lockInterruptibly();
		kickActive = true;
		kickStep = 0;
		lock.unlock();
		
		// Wait for the kick to complete
		kickSem.acquire();
	}
}

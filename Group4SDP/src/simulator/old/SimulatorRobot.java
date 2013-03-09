package simulator.old;

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

import static simulator.old.RoboFootball.pitchL;
import static simulator.old.RoboFootball.pitchW;
import static simulator.old.RoboFootball.scale;

public class SimulatorRobot {

	public Body robot;
	public PrismaticJoint joint;
	public long kickStep = 0;
	public boolean isKicking = false;

	public SimulatorRobot(boolean isOurRobo, World world) {

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
			bdr.position.set(0.1f * scale, pitchW * scale / 2);
			bdr.angle = 0;
		} else {
			bdr.position.set(pitchL * scale - 0.1f * scale, pitchW * scale / 2);
			bdr.angle = MathUtils.PI;
		}
		robot = world.createBody(bdr);
		robot.createFixture(fd);

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
			bdFlap.position.set(robot.getWorldCenter().x + 0.09f * scale
					+ 0.002f * scale, robot.getWorldCenter().y);
			bdFlap.angle = 0;
		} else {
			bdFlap.position.set(robot.getWorldCenter().x - 0.09f * scale
					- 0.002f * scale, robot.getWorldCenter().y);
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
		pjd.localAnchorA.set(robot.getLocalCenter());
		pjd.localAnchorB.set(0.0f, 0.09f * scale);
		pjd.initialize(robot, flap, robot.getWorldCenter(), pjd.localAxis1);

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

	public void kick() {
		if (!isKicking) {
			isKicking = !isKicking;
		}
	}

	public void goForward() {
		Vec2 f = robot.getWorldVector(new Vec2(40.0f * robot.getMass(),
				0.0f * scale));
		Vec2 p = robot.getWorldPoint(robot.getLocalCenter());
		robot.applyForce(f, p);
	}

	public void goBackwards() {
		Vec2 f = robot.getWorldVector(new Vec2(-40.0f * robot.getMass(),
				0.0f * scale));
		Vec2 p = robot.getWorldPoint(robot.getLocalCenter());
		robot.applyForce(f, p);
	}

	public void turnLeft() {
		robot.applyTorque(15.0f * robot.getMass());
	}

	public void turnRight() {
		robot.applyTorque(-15.0f * robot.getMass());
	}
}

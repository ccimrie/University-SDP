package simulator.objects.kickers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;

public class SimpleDribbler extends KickerPart {
	protected PrismaticJoint joint;

	protected final AtomicBoolean active = new AtomicBoolean(false);

	protected SimpleDribbler(World world, BodyDef kickerBodyDef,
			FixtureDef kickerFixDef) {
		super(world, kickerBodyDef, kickerFixDef);
	}

	public static SimpleDribbler createDribbler(World world, Body robotBody,
			Vec2 initialPos, float initialAngle) {
		PolygonShape dribblerShape = new PolygonShape();
		dribblerShape.setAsBox(0.001f, 0.06f);

		FixtureDef dribblerFixDef = new FixtureDef();
		dribblerFixDef.shape = dribblerShape;
		dribblerFixDef.density = 600.0f;
		dribblerFixDef.friction = 0.3f;

		BodyDef dribblerBodyDef = new BodyDef();
		dribblerBodyDef.type = BodyType.DYNAMIC;
		dribblerBodyDef.position.set(initialPos);
		dribblerBodyDef.angle = initialAngle;
		dribblerBodyDef.allowSleep = false;

		SimpleDribbler dribbler = new SimpleDribbler(world, dribblerBodyDef,
				dribblerFixDef);

		PrismaticJointDef dribblerJointDef = new PrismaticJointDef();
		dribblerJointDef.localAxis1.set((float) Math.cos(initialAngle),
				(float) Math.sin(initialAngle));
		dribblerJointDef.localAxis1.normalize();
		dribblerJointDef.localAnchorA.set(robotBody.getLocalCenter());
		dribblerJointDef.localAnchorB.set(0.0f, 0.09f);

		dribblerJointDef.initialize(robotBody, dribbler.body,
				robotBody.getWorldCenter(), dribblerJointDef.localAxis1);

		dribblerJointDef.lowerTranslation = 0.0f;
		dribblerJointDef.upperTranslation = 0.0f;
		dribblerJointDef.enableLimit = true;

		dribbler.joint = (PrismaticJoint) world.createJoint(dribblerJointDef);

		return dribbler;
	}

	@Override
	public void beforeStep() {
		if (active.get()) {
			System.out.println("SimpleDribbler.beforeStep()");
			ContactEdge contacts = body.getContactList();
			Body ball = null;
			// Find the ball object if it's touching the dribbler
			while (contacts != null) {
				System.out.println("Iterating over contact points");
				Contact contact = contacts.contact;
				if (contact.getFixtureA().getShape() instanceof CircleShape) {
					ball = contact.getFixtureA().getBody();
					break;
				} else if (contact.getFixtureB().getShape() instanceof CircleShape) {
					ball = contact.getFixtureB().getBody();
					break;
				}
				contacts = contacts.next;
			}
			// If the ball isn't touching the dribbler, nothing to do.
			if (ball == null)
				return;
			Vec2 ballPos = ball.getPosition();
			Vec2 dribblerForce = body.getPosition().sub(ballPos);
			dribblerForce.normalize();
			dribblerForce = dribblerForce.mul(0.005f);
			ball.applyForce(dribblerForce, ball.getPosition());
			System.out
					.println("Dribbler in contact with ball, tugging with force: ("
							+ dribblerForce.x + ", " + dribblerForce.y + ")");
		}
	}

	@Override
	public void afterStep() {
		// Do nothing
	}

	public boolean isActive() {
		return active.get();
	}

	public void activate() {
		System.out.println("SimpleDribbler.activate()");
		active.set(true);
	}

	public void deactivate() {
		System.out.println("SimpleDribbler.deactivate()");
		active.set(false);
	}
}

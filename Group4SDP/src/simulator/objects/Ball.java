package simulator.objects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Ball {
	public final Body body;

	public Ball(final World simWorld) {
		CircleShape ballshape = new CircleShape();
		ballshape.m_radius = 0.025f * Pitch.scale;

		FixtureDef fdb = new FixtureDef();
		fdb.shape = ballshape;
		fdb.density = 1.0f;
		fdb.friction = 0.01f;

		BodyDef ballbd = new BodyDef();
		ballbd.type = BodyType.DYNAMIC;
		ballbd.angularDamping = 1.0f;
		ballbd.linearDamping = 0.5f;
		ballbd.bullet = true;
		ballbd.position.set(Pitch.length * Pitch.scale / 2, Pitch.width
				* Pitch.scale / 2);
		
		body = simWorld.createBody(ballbd);
		body.createFixture(fdb);
	}
}

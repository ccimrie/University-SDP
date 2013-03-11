package simulator.objects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * A class representing the ball in the simulator
 * 
 * @author Alex Adams (s1046358)
 */
public class Ball {
	public final Body body;

	/**
	 * Creates a ball object within the simulator's world
	 * 
	 * @param simWorld
	 *            The simulator's world the ball is to be created in
	 */
	public Ball(final World simWorld) {
		CircleShape ballshape = new CircleShape();
		ballshape.m_radius = 0.025f * Pitch.scale;

		FixtureDef fdb = new FixtureDef();
		fdb.shape = ballshape;
		fdb.density = 1.0f;
		fdb.friction = 0.15f;

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

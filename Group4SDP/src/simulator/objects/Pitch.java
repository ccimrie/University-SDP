package simulator.objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * A class representing the pitch in the simulator
 * 
 * @author Alex Adams (s1046358)
 */
public class Pitch {
	private final Body body;

	public static final float length = 2.4384f;
	public static final float width = 1.2192f;
	public static final float goalWidth = 0.6f;

	public static final float scale = 20.0f;

	/**
	 * Constructs a pitch object within the simulator's world
	 * 
	 * @param simWorld
	 *            The simulator's world the pitch is to be constructed in
	 */
	public Pitch(final World simWorld) {
		BodyDef bd = new BodyDef();
		body = simWorld.createBody(bd);
		PolygonShape shape = new PolygonShape();

		// Vertices for the pitch boundary, starting from the bottom left corner
		// and forming a loop
		Vec2[] pitchVerts = new Vec2[12];

		pitchVerts[0] = new Vec2(0.0f, 0.0f);
		pitchVerts[1] = new Vec2(length, 0.0f);
		pitchVerts[2] = new Vec2(length, (width - goalWidth) / 2);
		pitchVerts[3] = new Vec2(length + 0.06f, (width - goalWidth) / 2);
		pitchVerts[4] = new Vec2(length + 0.06f, (width + goalWidth) / 2);
		pitchVerts[5] = new Vec2(length, (width + goalWidth) / 2);
		pitchVerts[6] = new Vec2(length, width);
		pitchVerts[7] = new Vec2(0.0f, width);
		pitchVerts[8] = new Vec2(0.0f, (width + goalWidth) / 2);
		pitchVerts[9] = new Vec2(-0.06f, (width + goalWidth) / 2);
		pitchVerts[10] = new Vec2(-0.06f, (width - goalWidth) / 2);
		pitchVerts[11] = new Vec2(0.0f, (width - goalWidth) / 2.0f);

		// Scale the vertices
		for (int i = 0; i < pitchVerts.length; ++i)
			pitchVerts[i] = pitchVerts[i].mul(scale);

		// Create the pitch as a physical object in the simulator
		for (int i = 0; i < pitchVerts.length; ++i) {
			shape.setAsEdge(pitchVerts[i], pitchVerts[(i + 1)
					% pitchVerts.length]);
			body.createFixture(shape, 0.0f);
		}
	}
}

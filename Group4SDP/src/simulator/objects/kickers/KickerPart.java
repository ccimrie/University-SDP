package simulator.objects.kickers;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * A base class for all the different types of kicker / dribbler
 * 
 * @author Alex Adams (s1046358)
 */
public abstract class KickerPart {
	/** The body object for the kicker / dribbler */
	protected final Body body;

	/**
	 * Creates a body for the kicker or dribbler in the simulator world
	 * 
	 * @param world
	 *            The simulator world
	 * @param kickerBodyDef
	 *            A BodyDef object describing the kicker's body
	 * @param kickerFixDef
	 *            A FixtureDef object describing the shape, density, etc. of the
	 *            kicker. Set this to null if the kicker shouldn't have a shape.
	 */
	public KickerPart(final World world, BodyDef kickerBodyDef,
			FixtureDef kickerFixDef) {
		assert kickerBodyDef != null : "BodyDef for kicker is null";
		this.body = world.createBody(kickerBodyDef);
		if (kickerFixDef != null) {
			this.body.createFixture(kickerFixDef);
		}
	}

	/**
	 * Performs any processing necessary before a physics step of the
	 * simulation. Examples include: applying forces, setting motor speeds, etc.
	 */
	public abstract void beforeStep();

	/**
	 * Performs any processing necessary after a physics step of the simulation.
	 * Example: waking up something waiting for a kick to complete, setting
	 * flags, etc.
	 */
	public abstract void afterStep();
}

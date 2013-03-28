package simulator.objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class SimpleKicker extends KickerPart {

	public SimpleKicker(World world, Vec2 initialPos, float initialAngle, PolygonShape kickerShape) {
		super(world, kickerBodyDef, kickerJointDef);
		
		FixtureDef kickerFixDef = new FixtureDef();
		kickerFixDef.shape = kickerShape;
		kickerFixDef.density = 600.0f;
		kickerFixDef.friction = 0.3f;
		kickerBodyDef.position.set(initialPos);
		kickerBodyDef.angle = initialAngle;
	}

	@Override
	public void beforeStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterStep() {
		// TODO Auto-generated method stub

	}

}

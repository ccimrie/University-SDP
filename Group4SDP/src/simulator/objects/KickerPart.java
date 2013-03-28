package simulator.objects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

public abstract class KickerPart {
	private final Body body;
	private final Joint joint;
	
	public KickerPart(final World world, BodyDef kickerBodyDef, JointDef kickerJointDef) {		
		this.body = world.createBody(kickerBodyDef);
		this.joint = world.createJoint(kickerJointDef);
	}

	public abstract void beforeStep();
	
	public abstract void afterStep();
}

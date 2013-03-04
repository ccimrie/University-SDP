package simulator;




import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;
import world.state.*; 

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
/**
 * 
 * This class will serve the purpose of updating the worldState
 * 
 * work in progress...
 * 
 * Bach
 * @author s1142191
 *
 */
public class Updateworld extends TestbedTest {
    public static final float pitchL = 2.4384f;
    public static final float pitchW = 1.2192f;
    public static final float goalW = 0.6f;

    public static float scale = 20.0f;

    boolean firstTime = true;
    boolean simulatorMode = false;
    public SimDisplayRobot ourownRobot, theEnemyRobot;

    
   

    WorldState worldstate;
    
   
		Robot ourRobot = new Robot(RobotType.Us);
		Robot theirRobot = new Robot(RobotType.Them);
		Ball ball = new Ball();
	
 

    Body pitch;   

 //   public Body ball;
//    public SimulatorRobot ourRobo, enemyRobo;

    public boolean isSaveLoadEnabled() {
        return true;
    }


    public void initialisePitch() {
        BodyDef bd = new BodyDef();
        this.pitch = getWorld().createBody(bd);
        getWorld().setGravity(new Vec2(0.0f, 0.0f));

        PolygonShape shape = new PolygonShape();
        //Bottom wall
        shape.setAsEdge(new Vec2(0.0f * scale, 0.0f * scale), new Vec2(pitchL * scale, 0.0f * scale));
        this.pitch.createFixture(shape, 0.0f);
        //Top wall
        shape.setAsEdge(new Vec2(0.0f * scale, pitchW * scale), new Vec2(pitchL * scale, pitchW * scale));
        this.pitch.createFixture(shape, 0.0f);
        //Left wall
        shape.setAsEdge(new Vec2(0.0f * scale, 0.0f * scale), new Vec2(0.0f * scale, pitchW * scale / 2 - goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
        shape.setAsEdge(new Vec2(0.0f * scale, pitchW * scale / 2 + goalW * scale / 2), new Vec2(0.0f * scale, pitchW * scale));
        this.pitch.createFixture(shape, 0.0f);
        //Left goal
        shape.setAsEdge(new Vec2(0.0f * scale, pitchW * scale / 2 + goalW * scale / 2),
                new Vec2(-0.06f * scale, pitchW * scale / 2 + goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
        shape.setAsEdge(new Vec2(0.0f * scale, pitchW * scale / 2 - goalW * scale / 2),
                new Vec2(-0.06f * scale, pitchW * scale / 2 - goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
        shape.setAsEdge(new Vec2(-0.06f * scale, pitchW * scale / 2 + goalW * scale / 2),
                new Vec2(-0.06f * scale, pitchW * scale / 2 - goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
        //Right wall
        shape.setAsEdge(new Vec2(pitchL * scale, 0.0f * scale), new Vec2(pitchL * scale, pitchW * scale / 2 - goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
        shape.setAsEdge(new Vec2(pitchL * scale, pitchW * scale / 2 + goalW * scale / 2), new Vec2(pitchL * scale, pitchW * scale));
        this.pitch.createFixture(shape, 0.0f);
        //Right goal
        shape.setAsEdge(new Vec2(pitchL * scale, pitchW * scale / 2 + goalW * scale / 2),
                new Vec2(pitchL * scale + 0.06f * scale, pitchW * scale / 2 + goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
        shape.setAsEdge(new Vec2(pitchL * scale, pitchW * scale / 2 - goalW * scale / 2),
                new Vec2(pitchL * scale + 0.06f * scale, pitchW * scale / 2 - goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
        shape.setAsEdge(new Vec2(pitchL * scale + 0.06f * scale, pitchW * scale / 2 + goalW * scale / 2),
                new Vec2(pitchL * scale + 0.06f * scale, pitchW * scale / 2 - goalW * scale / 2));
        this.pitch.createFixture(shape, 0.0f);
    }

    public void createBall() {
        CircleShape ballshape = new CircleShape();
        ballshape.m_radius = 0.025f * scale;

        FixtureDef fdb = new FixtureDef();
        fdb.shape = ballshape;
        fdb.density = 1.0f;
        fdb.friction = 0.01f;

        BodyDef ballbd = new BodyDef();
        ballbd.type = BodyType.DYNAMIC;
        ballbd.angularDamping = 1.0f;
        ballbd.linearDamping = 0.5f;
        ballbd.bullet = true;
        ballbd.position.set(pitchL * scale / 2, pitchW * scale / 2);


    }

 

    public synchronized void step(TestbedSettings settings) {
        super.step(settings);
        
        
        
        float timestep = 1.0f/getModel().getCalculatedFps();
        
       // ourownRobot, 
     
       
        ourRobot.x =  ourownRobot.robot.getWorldCenter().x/scale;
		ourRobot.y = ourownRobot.robot.getWorldCenter().y/scale;
		ourRobot.bearing = ourownRobot.robot.getAngle() + Math.PI/2.0f;
		
		theirRobot.x = theEnemyRobot.robot.getWorldCenter().x/scale;
		theirRobot.y = theEnemyRobot.robot.getWorldCenter().y/scale;
		theirRobot.bearing = theEnemyRobot.robot.getAngle() + Math.PI/2.0f;
		
		//ball.x = ball.getWorldCenter().x/scale,X;
		//ball.y = ball.getWorldCenter().y/scale);
        
        
       

  
        
    }


	@Override
	public String getTestName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void initTest(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

    






    //Input code ends here
}

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
import vision.*;

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
import strategy.calculations.*;
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
    public SimDisplayRobot ourRobotSim, theirRobotSim;
    boolean argDeserialized = true;
    
    public Body ballSim;

    WorldState worldState;
   
    
    
    

	Robot ourRobot = new Robot(RobotType.Us);
	Robot theirRobot = new Robot(RobotType.Them);
	
	Ball ball = new Ball();
	
 

    Body pitch;   
	public Updateworld(WorldState worldState) {
		// Set the state fields.
		this.worldState = worldState;
	
	}


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
    public WorldState getTheRealWorld(){
    	
    	return worldState;
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
        this.ballSim = getWorld().createBody(ballbd);
        this.ballSim.createFixture(fdb);

    }

 

    public synchronized void step(TestbedSettings settings) {
        super.step(settings);
        
        
        
        float timestep = 1.0f/getModel().getCalculatedFps();
       
        
       // ourownRobot, 
        worldState.setBallX( (int)(Math.round((ballSim.getWorldCenter().x/scale))));
        worldState.setBallY((int)Math.round(ballSim.getWorldCenter().y/scale));
        
        worldState.setBlueX((int)(Math.round((ourRobotSim.robot.getWorldCenter().x/scale))));
        worldState.setBallY((int)Math.round(ourRobotSim.robot.getWorldCenter().y/scale));
        
        worldState.setYellowX((int)(theirRobotSim.robot.getWorldCenter().x/scale));
        worldState.setYellowY((int)(theirRobotSim.robot.getWorldCenter().y/scale));
        
       worldState.setBlueOrientation(Math.toDegrees(Math.abs((ourRobotSim.robot.getAngle()-Math.PI/2.0f)%(2*Math.PI))) );
        
       
        worldState.setYellowOrientation(Math.toDegrees(Math.abs((theirRobotSim.robot.getAngle()-Math.PI/2.0f)%(2*Math.PI))) );
        
       
        
        
       /** 


        Thread thread1 = new Thread () {
        	public void run () {
                worldstate.setBallX( (int)(Math.round((ballSim.getWorldCenter().x/scale))));
                worldstate.setBallY((int)Math.round(ballSim.getWorldCenter().y/scale));
        		}
        			};

       
       thread1.start();
       
       */
         /**
          * 
          *The part that need some tweaking.
          * 
          *Bach 
          */
    /**    
        ourRobot.x =  ourRobotSim.robot.getWorldCenter().x/scale;
		ourRobot.y = ourRobotSim.robot.getWorldCenter().y/scale;
		ourRobot.bearing = ourRobotSim.robot.getAngle() + Math.PI/2.0f;
		
		theirRobot.x = theirRobotSim.robot.getWorldCenter().x/scale;
		theirRobot.y = theirRobotSim.robot.getWorldCenter().y/scale;
		theirRobot.bearing = theirRobotSim.robot.getAngle() + Math.PI/2.0f;
		
	ball.x = ballSim.getWorldCenter().x/scale;
	ball.y = ballSim.getWorldCenter().y/scale;
        */
        
       

  
        
    }


	@Override
	public String getTestName() {
		// TODO Auto-generated method stub
		 return "Synergy Team simulator";
	}


	@Override
	public void initTest(boolean argDeserialized) {
        if (this.firstTime) {
            setCamera(new Vec2(pitchL * scale / 2, pitchW * scale / 2), 0.5f * scale);
            this.firstTime = false;
        }
        if (argDeserialized) {
            return;
        }
        {
            initialisePitch();
            createBall();
           
            this.ourRobotSim = new SimDisplayRobot(true, getWorld());
            this.theirRobotSim = new SimDisplayRobot(false, getWorld());
            
          //  this.world = new SimWorld();
        }
		
	}

    






    //Input code ends here
}

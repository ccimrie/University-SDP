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
import comms.control.Protocol;

public class RoboFootball extends TestbedTest {
    public static final float pitchL = 2.4384f;
    public static final float pitchW = 1.2192f;
    public static final float goalW = 0.6f;

    public static final float scale = 20.0f;

    boolean firstTime = false;
    boolean simulatorMode = true;
    boolean ourRoboKick = false, ourRoboTurnLeft = false, ourRoboTurnRight = false,
            ourRoboGoForward = false, ourRoboGoBackwards = false;
    long kickStep = 0;
    
    public Thread robotControl;
    public boolean control = false;
    
    public int commandCount = 0;
    
    Thread strategy;
    SimServer rc;
    SimWorld world;
    
    float distToGo = 0;
    float angleToGo = 0;
    float timestep = 0;
    float radius = 0;
    
    float speed = 0.5f;
    float angVel = 0.2f;
    
    boolean isMoving = false;
    boolean isRotating = false;
    boolean isArcMoving = false;
    boolean rotatingRight = false;
    boolean fixedDistance = false;

    Body pitch;
    public Body ball;
    public SimulatorRobot ourRobo, enemyRobo;

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

        this.ball = getWorld().createBody(ballbd);
        this.ball.createFixture(fdb);
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

            this.ourRobo = new SimulatorRobot(true, getWorld());
            this.enemyRobo = new SimulatorRobot(false, getWorld());
            
            this.world = new SimWorld();
        }

    }

    public synchronized void step(TestbedSettings settings) {
        super.step(settings);
        
        //update time step
        
        timestep = 1.0f/getModel().getCalculatedFps();
        
        //float vel = enemyRobo.robot.getAngularVelocity();
        
        //update the speed
        if(rc != null)
        {
	        speed = rc.travelSpeed;
	        angVel = rc.rotateSpeed;
        }

        //Check our robot for kicking
        if (this.ourRobo.isKicking) {
            if (this.ourRobo.kickStep < 5) {
                this.ourRobo.joint.setMotorSpeed(100.0f);
                this.ourRobo.kickStep++;
            } else {
                this.ourRobo.joint.setMotorSpeed(-100.0f);
                this.ourRobo.isKicking = false;
                this.ourRobo.kickStep = 0;
            }
        } else {
            this.ourRobo.joint.setMotorSpeed(-100.0f);
        }

        //Check enemy robot for kicking
        if (this.enemyRobo.isKicking) {
            if (this.enemyRobo.kickStep < 5) {
            	this.enemyRobo.joint.setMotorSpeed(100.0f);
            	this.enemyRobo.kickStep++;
            } else {
            	this.enemyRobo.joint.setMotorSpeed(-100.0f);
            	this.enemyRobo.isKicking = false;
            	this.enemyRobo.kickStep = 0;
            }
        } else {
        	this.enemyRobo.joint.setMotorSpeed(-100.0f);
        }

        //Moving robot according to key input
        if (this.ourRoboGoBackwards) {
        	this.ourRobo.goBackwards();
        }
        if (this.ourRoboGoForward) {
        	this.ourRobo.goForward();
        }
        if (this.ourRoboTurnLeft) {
        	this.ourRobo.turnLeft();
        }
        if (this.ourRoboTurnRight) {
        	this.ourRobo.turnRight();
        }
        if (this.ourRoboKick) {
        	this.ourRobo.kick();
        }
        
        world.update(enemyRobo.robot.getWorldCenter().x/scale,
        		enemyRobo.robot.getWorldCenter().y/scale,
        		enemyRobo.robot.getAngle(),
        		ourRobo.robot.getWorldCenter().x/scale,
        		ourRobo.robot.getWorldCenter().y/scale,
        		ourRobo.robot.getAngle(),
        		ball.getWorldCenter().x/scale,
        		ball.getWorldCenter().y/scale);
        
        //executeCommand();
        
        applyCommands();
        
    }
    
    public void executeCommand(byte newCommand, float arg)
    {	
    	//RobotControl rc = (RobotControl) robotControl;
    	
    	//if(rc == null)
    		//return;
    	
    	//int newCommand = rc.command;
    	//int newCount = rc.commandCount;
    	
    	//if(newCount == commandCount)
    		//no new command- do nothing
    	//	return;
    	
    	//commandCount = newCount;
    	
    	if(newCommand == Protocol.KICK)
    		enemyRobo.isKicking = true;
    	
    	if(newCommand == Protocol.TRAVEL)
    	{
    		enemyRobo.robot.applyAngularImpulse(0.0f);
    		
    		isMoving = true;
    		isRotating = false;
    		isArcMoving = false;
    		fixedDistance = true;
    		distToGo = arg;
    	}
    	
    	if(newCommand == Protocol.FORWARD)
    	{
    		enemyRobo.robot.applyAngularImpulse(0.0f);
    		
    		isMoving = true;
    		isRotating = false;
    		isArcMoving = false;
    		fixedDistance = false;
    	}
    	
    	if(newCommand == Protocol.STOP)
    	{
    		System.out.println("stop command");
    		
    		isMoving = false;
    		isRotating = false;
    		isArcMoving = false;
    		rc.isTurning = false;
    		
    		Vec2 i = enemyRobo.robot.getWorldVector(new Vec2(00.0f, 0.0f));
    		Vec2 p = enemyRobo.robot.getWorldPoint(enemyRobo.robot.getLocalCenter());
    		
    		enemyRobo.robot.applyLinearImpulse(i, p);
    		
    		enemyRobo.robot.applyAngularImpulse(0.0f);
    	}
    	
    	if(newCommand == Protocol.ROTATE)
    	{
    		
    		Vec2 i = enemyRobo.robot.getWorldVector(new Vec2(00.0f, 0.0f));
    		Vec2 p = enemyRobo.robot.getWorldPoint(enemyRobo.robot.getLocalCenter());
    		
    		enemyRobo.robot.applyLinearImpulse(i, p);
    		
    		isMoving = false;
    		isRotating = true;
    		isArcMoving = false;
    		
    		angleToGo = arg;
    		
    		if(angleToGo>0)
    			rotatingRight = true;
    		else
    			rotatingRight = false;
    		
    		angleToGo = Math.abs(angleToGo);
    		
    	}
    	
    	if(newCommand == Protocol.ARC_FORWARD)
    	{
    		isMoving = false;
    		isRotating = false;
    		isArcMoving = true;
    		
    		radius = arg;
    	}
    	
    	
    }
    
    public void applyCommands()
    {
    	if(isMoving)
    	{
    		if(fixedDistance)
    		{
	    		if(distToGo > 0)
	    		{
	    			Vec2 i = enemyRobo.robot.getWorldVector(new Vec2(speed*enemyRobo.robot.getMass(), 0.0f));
	        		Vec2 p = enemyRobo.robot.getWorldPoint(enemyRobo.robot.getLocalCenter());
	        		
	        		enemyRobo.robot.applyLinearImpulse(i, p);
	        		distToGo -= speed*timestep;
	        		System.out.println("To go: " + distToGo);
	    		}
	    		else
	    		{
	    			isMoving = false;
	    			distToGo = 0;
	    			
	    			Vec2 i = enemyRobo.robot.getWorldVector(new Vec2(0.0f, 0.0f));
	        		Vec2 p = enemyRobo.robot.getWorldPoint(enemyRobo.robot.getLocalCenter());
	        		
	        		enemyRobo.robot.applyLinearImpulse(i, p);
	    		}
    		}
    		else
    		{
    			Vec2 i = enemyRobo.robot.getWorldVector(new Vec2(speed*enemyRobo.robot.getMass(), 0.0f));
        		Vec2 p = enemyRobo.robot.getWorldPoint(enemyRobo.robot.getLocalCenter());
        		
        		enemyRobo.robot.applyLinearImpulse(i, p);
    		}
    	}
    	
    	if(isArcMoving)
    	{
    		Vec2 i = enemyRobo.robot.getWorldVector(new Vec2(speed*enemyRobo.robot.getMass(), 0.0f));
    		Vec2 p = enemyRobo.robot.getWorldPoint(enemyRobo.robot.getLocalCenter());
    		
    		enemyRobo.robot.applyLinearImpulse(i, p);
    		
    		float correctionFactor = 1.0f/14.0f;
			
    		enemyRobo.robot.applyAngularImpulse(-(speed/radius)*enemyRobo.robot.getInertia()*correctionFactor);
    		
    		
    	}
    	
    	if(isRotating&&rotatingRight)
    	{
    		if(angleToGo > 0)
    		{
    			
    			float correctionFactor = 1.0f/14.0f;
    			
        		enemyRobo.robot.applyAngularImpulse(-angVel*enemyRobo.robot.getInertia()*correctionFactor);
        		
        		angleToGo -= angVel*timestep;
    		}
    		else
    		{
    			isRotating = false;
    			rc.isTurning = false;
    			angleToGo = 0;
    			enemyRobo.robot.applyAngularImpulse(0.0f);
    		}
    	}
    	else if(isRotating&&!rotatingRight)
    	{
    		if(angleToGo > 0)
    		{
    			
    			float correctionFactor = 1.0f/14.0f;
    			
        		enemyRobo.robot.applyAngularImpulse(angVel*enemyRobo.robot.getInertia()*correctionFactor);
        		
        		angleToGo -= angVel*timestep;
    		}
    		else
    		{
    			isRotating = false;
    			rc.isTurning = false;
    			angleToGo = 0;
    			enemyRobo.robot.applyAngularImpulse(0.0f);
    		}
    	}
    	
    }

    @Override
    public String getTestName() {
        return "Match";
    }

    //Input - methods listening to button presses and releases
    public void keyPressed(char argKeyChar, int argKeyCode) {
        if (argKeyChar == 's') {
        	this.ourRoboGoBackwards = true;
            getModel().getKeys()['s'] = false;
        }
        if (argKeyChar == 'w') {
        	this.ourRoboGoForward = true;
            getModel().getKeys()['w'] = false;
        }
        if (argKeyChar == 'a') {
        	this.ourRoboTurnLeft = true;
            getModel().getKeys()['a'] = false;
        }
        if (argKeyChar == 'd') {
        	this.ourRoboTurnRight = true;
            getModel().getKeys()['d'] = false;
        }
        if (argKeyChar == 'b') {
        	this.ourRoboKick = true;
            getModel().getKeys()['b'] = false;
        }
        
        if (argKeyChar == 'c') {
        	if(!control)
        	{
        		control = true;
        		rc = new SimServer(this);
        		
        		strategy = new SimStrategy(world,rc);
        		
        		strategy.start();
        		
        		
        		//robotControl = new RobotControl();
        		//robotControl.start();
        		
        	}
            getModel().getKeys()['c'] = false;
        }

    }

    public void keyReleased(char argKeyChar, int argKeyCode) {
        if (argKeyChar == 's') {
        	this.ourRoboGoBackwards = false;
            getModel().getKeys()['s'] = true;
        }
        if (argKeyChar == 'w') {
        	this.ourRoboGoForward = false;
            getModel().getKeys()['w'] = true;
        }
        if (argKeyChar == 'a') {
        	this.ourRoboTurnLeft = false;
            getModel().getKeys()['a'] = true;
        }
        if (argKeyChar == 'd') {
        	this.ourRoboTurnRight = false;
            getModel().getKeys()['d'] = true;
        }
        if (argKeyChar == 'b') {
        	this.ourRoboKick = false;
            getModel().getKeys()['b'] = true;
        }
        if (argKeyChar == 'c') {
            getModel().getKeys()['c'] = true;
        }
    }
    //Input code ends here
}

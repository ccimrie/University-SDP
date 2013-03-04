package world.state;

import geometry.Vector;
import strategy.calculations.AngleCalculator;
import strategy.calculations.DistanceCalculator;
import strategy.calculations.GoalInfo;
import world.state.WorldState;

import vision.Position;

/**
 * A class storing the world state from vision, used by strategy
 * 
 * TODO: clean up duplicated functionality from integrating vision and strategy
 * (will be easier via eclipse refactoring tools)
 */
public class WorldState {
	/** The number of frames used to calculate velocity */
	private static final int NUM_FRAMES = 5;
	AngleCalculator a = new AngleCalculator(this);
	private long counter;
	DistanceCalculator dist = new DistanceCalculator();
	private int direction; // 0 = right, 1 = left.
	private int colour; // 0 = yellow, 1 = blue
	private int pitch; // 0 = main, 1 = side room
	WorldState world = this;
	private int greenX;
	private int greenY;

	private int currentFrame = 0;
	// Buffers for smoothing positions/angles and calculating velocities
	private int[] blueXBuf = new int[NUM_FRAMES];
	private int[] blueYBuf = new int[NUM_FRAMES];
	private double[] blueOrientBuf = new double[NUM_FRAMES];

	private int[] yellowXBuf = new int[NUM_FRAMES];
	private int[] yellowYBuf = new int[NUM_FRAMES];
	private double[] yellowOrientBuf = new double[NUM_FRAMES];

	private int[] ballXBuf = new int[NUM_FRAMES];
	private int[] ballYBuf = new int[NUM_FRAMES];

	// Smoothed positions/angles
	private int blueX;
	private int blueY;
	private double blueOrient;

	private int yellowX;
	private int yellowY;
	private double yellowOrient;

	private int ballX;
	private int ballY;

	// Velocities
	private double blueXVel;
	private double blueYVel;

	private double yellowXVel;
	private double yellowYVel;

	private double ballXVel;
	private double ballYVel;

	private boolean weAreBlue = true;
	private boolean weAreOnLeft = true;
	private boolean mainPitch = true;
	private boolean blueHasBall = false;
	private boolean yellowHasBall = false;

	private PossessionManager pm = new PossessionManager();

	public final GoalInfo goalInfo;

	public int frame;
	public Robot theirRobot = new Robot(RobotType.Them);
	public Robot ourRobot = new Robot(RobotType.Us);
	public Ball ball = new Ball();
	public Ball prevBall = new Ball();
	public PossessionType hasPossession = PossessionType.Nobody;

	public WorldState(GoalInfo goalInfo) {
		// control properties
		this.direction = 0;
		this.colour = 0;
		this.pitch = 0;
		this.goalInfo = goalInfo;
	}

	public void setBlueX(int blueX) {
		this.blueX = blueX;
		this.blueXBuf[currentFrame] = blueX;
	}

	public int getBlueX() {
		return blueX;
	}

	public double getBlueXVelocity() {
		return blueXVel;
	}

	public void setBlueY(int blueY) {
		this.blueY = blueY;
		this.blueYBuf[currentFrame] = blueY;
	}

	public int getBlueY() {
		return blueY;
	}

	public double getBlueYVelocity() {
		return blueYVel;
	}

	public void setBlueOrientation(double blueOrientation) {
		this.blueOrientBuf[currentFrame] = blueOrientation;
	}

	public double getBlueOrientation() {
		return blueOrient;
	}

	public void setYellowX(int yellowX) {
		this.yellowX = yellowX;
		this.yellowXBuf[currentFrame] = yellowX;
	}

	public int getYellowX() {
		return yellowX;
	}

	public double getYellowXVelocity() {
		return yellowXVel;
	}

	public void setYellowY(int yellowY) {
		this.yellowY = yellowY;
		this.yellowYBuf[currentFrame] = yellowY;
	}

	public int getYellowY() {
		return yellowY;
	}

	public double getYellowYVelocity() {
		return yellowYVel;
	}

	public void setYellowOrientation(double yellowOrientation) {
		this.yellowOrientBuf[currentFrame] = yellowOrientation;
	}

	public double getYellowOrientation() {
		return yellowOrient;
	}

	public void setGreenX(int greenX) {
		this.greenX = greenX;
	}

	public int getGreenX() {
		return greenX;
	}

	public void setGreenY(int greenY) {
		this.greenY = greenY;
	}

	public int getGreenY() {
		return greenY;
	}

	public void setBallX(int ballX) {
		this.ballX = ballX;
		this.ballXBuf[currentFrame] = ballX;
	}

	public int getBallX() {
		return ballX;
	}

	public double getBallXVelocity() {
		return ballXVel;
	}

	public void setBallY(int ballY) {
		this.ballY = ballY;
		this.ballYBuf[currentFrame] = ballY;
	}

	public int getBallY() {
		return ballY;
	}

	public double getBallYVelocity() {
		return ballYVel;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public int getColour() {
		return colour;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getPitch() {
		return pitch;
	}

	public long getCounter() {
		return this.counter;
	}

	/**
	 * Triggers calculation of smoothed angles and velocities
	 */
	public void update() {
		++counter;

		// Reinitialise all positions/angles/velocities to 0
		blueXVel = 0;
		blueYVel = 0;
		blueOrient = 0;

		yellowXVel = 0;
		yellowYVel = 0;
		yellowOrient = 0;

		ballXVel = 0;
		ballYVel = 0;

		// Smooth angles
		for (int i = 0; i < NUM_FRAMES; ++i) {
			blueOrient += blueOrientBuf[i];

			yellowOrient += yellowOrientBuf[i];
		}
		blueOrient /= NUM_FRAMES;
		yellowOrient /= NUM_FRAMES;

		// Calculate velocities
		int prevFrame;
		// Calculate +ve modulus - % operator doesn't have the desired effect.
		prevFrame = 2 + currentFrame - NUM_FRAMES;
		if (prevFrame < 0)
			prevFrame += NUM_FRAMES;

		blueXVel += blueXBuf[currentFrame] - blueXBuf[prevFrame];
		blueYVel += blueYBuf[currentFrame] - blueYBuf[prevFrame];

		yellowXVel += yellowXBuf[currentFrame] - yellowXBuf[prevFrame];
		yellowYVel += yellowYBuf[currentFrame] - yellowYBuf[prevFrame];

		ballXVel += ballXBuf[currentFrame] - ballXBuf[prevFrame];
		ballYVel += ballYBuf[currentFrame] - ballYBuf[prevFrame];

		blueXVel /= NUM_FRAMES;
		blueYVel /= NUM_FRAMES;

		yellowXVel /= NUM_FRAMES;
		yellowYVel /= NUM_FRAMES;

		ballXVel /= NUM_FRAMES;
		ballYVel /= NUM_FRAMES;

		++currentFrame;
		if (currentFrame >= NUM_FRAMES)
			currentFrame = 0;
	}

	// ///////////////////////
	public Robot getOurRobot() {
		return this.ourRobot;
	}

	public Robot getTheirRobot() {
		return this.theirRobot;
	}

	public Ball getBall() {
		return this.ball;
	}

	// //////////////////////////
	public void setOurRobot() {
		if (areWeBlue()) {
			this.ourRobot.x = blueX;
			this.ourRobot.y = blueY;
			this.ourRobot.setPosition(new Vector(ourRobot.x, ourRobot.y));
			this.ourRobot.bearing = getBlueOrientation();
		} else {
			this.ourRobot.x = yellowX;
			this.ourRobot.y = yellowY;
			this.ourRobot.setPosition(new Vector(ourRobot.x, ourRobot.y));
			this.ourRobot.bearing = getYellowOrientation();
		}
	}

	public void setTheirRobot() {
		if (areWeBlue()) {
			// If we are blue, the other robot is yellow.
			// Please correct me if I am wrong, the code is obscure...
			this.theirRobot.x = yellowX;
			this.theirRobot.y = yellowY;
			this.theirRobot.setPosition(new Vector(theirRobot.x, theirRobot.y));
			this.theirRobot.bearing = getYellowOrientation();
		} else {
			this.theirRobot.x = blueX;
			this.theirRobot.y = blueY;
			this.theirRobot.setPosition(new Vector(theirRobot.x, theirRobot.y));
			this.theirRobot.bearing = getBlueOrientation();
		}
	}

	public void setBall() {
		this.ball.x = ballX;
		this.ball.y = ballY;
	}

	public void setWeAreOnLeft(boolean weAreOnLeft) {
		this.weAreOnLeft = weAreOnLeft;
	}

	public boolean areWeOnLeft() {
		return weAreOnLeft;
	}

	public boolean areWeBlue() {
		return weAreBlue;
	}

	public void setWeAreBlue(boolean areWeBlue) {
		weAreBlue = areWeBlue;
	}

	public boolean isMainPitch() {
		return mainPitch;
	}

	public void setMainPitch(boolean onMainPitch) {
		mainPitch = onMainPitch;
	}

	public Position getOurGoal() {
		if (weAreOnLeft) {
			return goalInfo.getLeftGoalCenter();
		} else {
			return goalInfo.getRightGoalCenter();
		}
	}

	public Position getTheirGoal() {
		if (weAreOnLeft) {
			return goalInfo.getLeftGoalCenter();
		} else {
			return goalInfo.getRightGoalCenter();
		}
	}

	public int getMidLine() {
		return (goalInfo.pitchConst.getLeftBuffer() + (640 - goalInfo.pitchConst
				.getRightBuffer())) / 2;
	}

	public double distanceBetweenUsAndBall() {
		return DistanceCalculator.Distance(this.ourRobot.x, this.ourRobot.y,
				this.ball.x, this.ball.y);
	}

	public boolean areWeInOurHalf() {
		int midLine = getMidLine();

		if (weAreOnLeft) {
			return this.ourRobot.x < midLine;
		} else {
			return this.ourRobot.x > midLine;
		}
	}

	public boolean ballIsInGoal() {
		if (this.ball.x < goalInfo.getLeftGoalCenter().getX()
				|| this.ball.x > goalInfo.getRightGoalCenter().getX())
			return true;
		else
			return false;
	}

	public int whoHasTheBall() {
		if (blueHasBall) {
			return 1;
		} else if (yellowHasBall) {
			return 2;
		} else
			return -1;
	}

	public void updatePossesion() {
		hasPossession = pm.setPossession(this);
		if (weAreBlue) {
			if (hasPossession == PossessionType.Us) {
				blueHasBall = true;
				yellowHasBall = false;
			} else if (hasPossession == PossessionType.Them) {
				blueHasBall = false;
				yellowHasBall = true;
			} else {
				blueHasBall = false;
				yellowHasBall = false;
			}
		} else {
			if (hasPossession == PossessionType.Us) {
				blueHasBall = false;
				yellowHasBall = true;
			} else if (hasPossession == PossessionType.Them) {
				blueHasBall = true;
				yellowHasBall = false;
			} else {
				blueHasBall = false;
				yellowHasBall = false;
			}
		}
	}

	public PossessionType getPosession() {
		return hasPossession;
	}
	public boolean weFaceTheirGoal(){
		
		double gpt;
		double gpb;
		double x;
		if (weAreOnLeft){
			 gpt = goalInfo.getRightGoalTop().getY();
			 gpb = goalInfo.getRightGoalBottom().getY();
			 x = goalInfo.getRightGoalBottom().getX();
		}else {
			 gpt = goalInfo.getLeftGoalTop().getY();
			 gpb = goalInfo.getLeftGoalBottom().getY();
			 x = goalInfo.getLeftGoalBottom().getX();
		}
		if ((AngleCalculator.AngleTurner(x, gpt) > 0 && AngleCalculator.AngleTurner(x, gpt) < 20 )||
				(AngleCalculator.AngleTurner(x, gpb) < 0 && AngleCalculator.AngleTurner(x, gpb) > (-20) )){
			return true;
		}else return false;
	}
	public boolean enemyIsClose(){
		if(distanceToRobot() < 200) return true;
		else return false;
	}
	public boolean enemyInFront(){
		double ang = a.angleToEnemy();
		if((ang > (-15)) && (ang < 15)) return true;
		else return false;
	}
	public Position getTheirGoalTop(){
		if (weAreOnLeft) return goalInfo.getRightGoalTop();
		else return goalInfo.getLeftGoalTop();
	}
	public Position getTheirGoalBot(){
		if (weAreOnLeft) return goalInfo.getRightGoalBottom();
		else return goalInfo.getLeftGoalBottom();
	}
	public Position getOurGoalTop(){
		if (weAreOnLeft) return goalInfo.getLeftGoalTop();
		else return goalInfo.getRightGoalTop();
	}
	public Position getOurGoalBot(){
		if (weAreOnLeft) return goalInfo.getLeftGoalBottom();
		else return goalInfo.getRightGoalBottom();
	}
	
	public double distanceToBall() {

		
        return DistanceCalculator.Distance(ourRobot.x,ourRobot.y,this.ball.x,this.ball.y);

    }
	public double distanceToRobot() {

		
        return DistanceCalculator.Distance(ourRobot.x,ourRobot.y,theirRobot.x, theirRobot.y);

    }
	public double distanceUsToTheirgoal(){
		return DistanceCalculator.Distance(ourRobot.x,ourRobot.y,world.getTheirGoal().getX(), world.getTheirGoal().getY());
	}
	public double distanceThemToTheirgoal(){
		return DistanceCalculator.Distance(theirRobot.x,theirRobot.y,world.getTheirGoal().getX(), world.getTheirGoal().getY());
	}
	public double distanceUsToOurgoal(){
		return DistanceCalculator.Distance(ourRobot.x,ourRobot.y,world.getOurGoal().getX(), world.getOurGoal().getY());
	}
	public double distanceThemToOurgoal(){
		return DistanceCalculator.Distance(theirRobot.x,theirRobot.y,world.getOurGoal().getX(), world.getOurGoal().getY());
	}
	public double angleToEnemy(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.getTheirRobot().x, world.getTheirRobot().y);
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	public double angleToBall(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.ball.x, world.ball.y);
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	public double angleToTheirGoal(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.getTheirGoal().getX(), world.getTheirGoal().getY());
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	public double angleToOurGoal(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.getOurGoal().getX(), world.getOurGoal().getY());
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToTheirGoalTop(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.getTheirGoalTop().getX(), world.getTheirGoalTop().getY());
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	public double angleToTheirGoalBot(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.getTheirGoalBot().getX(), world.getTheirGoalBot().getY());
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	public double angleToOurGoalTop(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.getOurGoalTop().getX(), world.getOurGoalTop().getY());
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	public double angleToOurGoalBot(){
		double pointBearing = AngleCalculator.findPointBearing(ourRobot, world.getOurGoalTop().getX(), world.getOurGoalBot().getY());
		double angle = AngleCalculator.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	
}

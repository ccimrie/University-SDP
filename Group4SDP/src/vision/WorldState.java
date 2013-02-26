package vision;

import geometry.Vector;
import strategy.calculations.DistanceCalculator;
import world.state.Ball;
import world.state.PitchInfo;
import world.state.PossessionManager;
import world.state.PossessionType;
import world.state.Robot;
import world.state.RobotType;

import vision.WorldState;

/**
 * A class storing the world state from vision, used by strategy
 * 
 * TODO: clean up duplicated functionality from integrating vision and strategy
 * (will be easier via eclipse refactoring tools)
 */
public class WorldState {
	/** The number of frames used to calculate velocity */
	private static final int NUM_FRAMES = 3;
	
	private int direction; // 0 = right, 1 = left.
	private int colour; // 0 = yellow, 1 = blue
	private int pitch; // 0 = main, 1 = side room
	
	private int currentFrame = 0;
	private int[] blueX = new int[NUM_FRAMES];
	private int[] blueY = new int[NUM_FRAMES];
	private int[] yellowX = new int[NUM_FRAMES];
	private int[] yellowY = new int[NUM_FRAMES];
	private int[] ballX = new int[NUM_FRAMES];
	private int[] ballY = new int[NUM_FRAMES];
	private int[] greenX = new int[NUM_FRAMES];
	private int[] greenY = new int[NUM_FRAMES];
	
	private double[] blueOrientation = new double[NUM_FRAMES];
	private double[] yellowOrientation = new double[NUM_FRAMES];
	private long counter;

	// Used to filter out invalid bearings
	// TODO: not used
	// private Double[] ourBearings = new Double[3];
	// private Double[] theirBearings = new Double[3];

	private boolean weAreBlue = true;
	private boolean weAreOnLeft = true;
	private boolean mainPitch = true;
	private boolean blueHasBall = false;
	private boolean yellowHasBall = false;
	
	private PossessionManager pm = new PossessionManager();

	public int frame;
	public Robot theirRobot = new Robot(RobotType.Them);
	public Robot ourRobot = new Robot(RobotType.Us);
	public Ball ball = new Ball();
	public Ball prevBall = new Ball();
	public PossessionType hasPossession = PossessionType.Nobody;

	public WorldState() {
		// control properties
		this.direction = 0;
		this.colour = 0;
		this.pitch = 0;
	}
	
	public void setGreenX(int greenX) {
		this.greenX[currentFrame] = greenX;
	}

	public int getGreenX() {
		return greenX[currentFrame];
	}
	
	public void setGreenY(int greenY) {
		this.greenY[currentFrame] = greenY;
	}

	public int getGreenY() {
		return greenY[currentFrame];
	}

	public void setBlueX(int blueX) {
		this.blueX[currentFrame] = blueX;
	}

	public int getBlueX() {
		return blueX[currentFrame];
	}

	public int getBlueY() {
		return blueY[currentFrame];

	}

	public void setBlueY(int blueY) {
		this.blueY[currentFrame] = blueY;

	}

	public int getYellowX() {
		return yellowX[currentFrame];
	}

	public void setYellowX(int yellowX) {
		this.yellowX = yellowX[currentFrame];

	}

	public int getYellowY() {
		return yellowY[currentFrame];
	}

	public void setYellowY(int yellowY) {
		this.yellowY[currentFrame] = yellowY;

	}

	public int getBallX() {
		return ballX[currentFrame];
	}

	public void setBallX(int ballX) {
		this.ballX[currentFrame] = ballX;

	}

	public int getBallY() {
		return ballY[currentFrame];
	}

	public void setBallY(int ballY) {
		this.ballY[currentFrame] = ballY;

	}

	public double getBlueOrientation() {
		return blueOrientation[currentFrame];
	}

	public void setBlueOrientation(double blueOrientation) {
		this.blueOrientation[currentFrame] = blueOrientation;

	}

	public double getYellowOrientation() {
		return yellowOrientation[currentFrame];
	}

	public void setYellowOrientation(double yellowOrientation) {
		this.yellowOrientation[currentFrame] = yellowOrientation;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public void updateCounter() {
		this.counter++;
	}

	public long getCounter() {
		return this.counter;
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

	static Vector leftGoalCentre;
	static Vector rightGoalCentre;
	public static double midPoint;

	public Vector getOurGoal() {
		if (weAreOnLeft && !mainPitch) {
			leftGoalCentre = PitchInfo.getLeftGoalCentreSide();
			return leftGoalCentre;
		} else if (!weAreOnLeft && !mainPitch) {
			rightGoalCentre = PitchInfo.getRightGoalCentreSide();
			return rightGoalCentre;
		} else if (weAreOnLeft && mainPitch) {
			leftGoalCentre = PitchInfo.getLeftGoalCentreMain();
			return leftGoalCentre;
		} else {
			rightGoalCentre = PitchInfo.getRightGoalCentreMain();
			return rightGoalCentre;
		}
	}

	public Vector getTheirGoal() {
		if (weAreOnLeft && !mainPitch) {
			rightGoalCentre = PitchInfo.getRightGoalCentreSide();
			return rightGoalCentre;
		} else if (!weAreOnLeft && !mainPitch) {
			leftGoalCentre = PitchInfo.getLeftGoalCentreSide();
			return leftGoalCentre;
		} else if (weAreOnLeft && mainPitch) {
			rightGoalCentre = PitchInfo.getRightGoalCentreMain();
			return rightGoalCentre;
		} else {
			leftGoalCentre = PitchInfo.getLeftGoalCentreMain();
			return leftGoalCentre;
		}
	}

	public double getMidPoint() {
		if (mainPitch) {
			midPoint = PitchInfo.midPointMain;
			return midPoint;
		} else {
			midPoint = PitchInfo.midPointSide;
			return midPoint;
		}
	}

	public boolean ourHalfLeft() {
		if (weAreOnLeft)
			return true;
		else
			return false;
	}

	public double distanceBetweenUsAndBall() {
		return DistanceCalculator.Distance(this.ourRobot.x, this.ourRobot.y,
				this.ball.x, this.ball.y);
	}

	public boolean areWeInOurHalf() {
		if (!((this.ourRobot.x < this.getMidPoint() && this.ourHalfLeft()) || (this.ourRobot.x >= this
				.getMidPoint() && !this.ourHalfLeft())))
			return false;
		else
			return true;
	}

	public boolean ballIsInGoal() {
		if (this.areWeOnLeft()) {
			if (this.ball.x < this.getOurGoal().getX()
					|| this.ball.x > this.getTheirGoal().getX()) {
				return true;
			}
		} else {
			if (this.ball.x > this.getOurGoal().getX()
					|| this.ball.x < this.getTheirGoal().getX()) {
				return true;
			}
		}
		return false;
	}
	
	public int whoHasTheBall(){
		if (blueHasBall){
			return 1;
		}
		if (yellowHasBall){
			return 2;
		}
		return -1;
	}
	
	public void updatePossesion(){
		hasPossession = pm.setPossession(this);
		if (weAreBlue){
			if (hasPossession == PossessionType.Us){
				blueHasBall = true;
				yellowHasBall = false;
			} else if (hasPossession == PossessionType.Them){
				blueHasBall = false;
				yellowHasBall = true;
			}else{
				blueHasBall = false;
				yellowHasBall = false;
			}
		}else {
			if (hasPossession == PossessionType.Us){
				blueHasBall = false;
				yellowHasBall = true;
			} else if (hasPossession == PossessionType.Them){
				blueHasBall = true;
				yellowHasBall = false;
			}else{
				blueHasBall = false;
				yellowHasBall = false;
			}
		}
	}
	public PossessionType getPosession(){
		return hasPossession;
	}
}

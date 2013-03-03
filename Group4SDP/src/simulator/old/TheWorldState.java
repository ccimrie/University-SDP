package simulator.old;

import geometry.Vector;
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
public class TheWorldState {
	/** The number of frames used to calculate velocity */
	private static final int NUM_FRAMES = 5;

	private long counter;

	private int direction; // 0 = right, 1 = left.
	private int colour; // 0 = yellow, 1 = blue
	private int pitch; // 0 = main, 1 = side room

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

	

	public int frame;




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
/**	public Robot getOurRobot() {
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
*/
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








}

package vision;
import geometry.Vector;
import strategy.calculations.DistanceCalculator;
import world.state.Ball;
import world.state.PitchInfo;
import world.state.PossessionManager;
import world.state.PossessionType;
import world.state.Robot;
import world.state.RobotType;
import world.state.World;

import geometry.Vector;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import strategy.calculations.DistanceCalculator;
import vision.Vision;
import vision.WorldState;
import world.state.PitchInfo;

public class WorldState {

	private int direction; // 0 = right, 1 = left.
	private int colour; // 0 = yellow, 1 = blue
	private int pitch; // 0 = main, 1 = side room
	private int blueX;
	private int blueY;
	private int yellowX;
	private int yellowY;
	private int ballX;
	private int ballY;
	private double blueOrientation;
	private double yellowOrientation;
	private long counter;
	
	
	
	private Double[] ourBearings = new Double[3]; // To filter out invalid bearings 
    private Double[] theirBearings = new Double[3]; // To filter out invalid bearings 

    private boolean weAreBlue = true;
    private boolean weAreOnLeft = true;
    private boolean mainPitch = true;
    
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

		// object properties
		this.blueX = 0;
		this.blueY = 0;
		this.yellowX = 0;
		this.yellowY = 0;
		this.ballX = 0;
		this.ballY = 0;
		this.blueOrientation = 0;
		this.yellowOrientation = 0;
		for (int i = 0; i < 3; i++){
    		ourBearings[i] = 0.0;
    		theirBearings[i] = 0.0;
    	}
	}

	public int getBlueX() {
		return blueX;
	}
	public void setBlueX(int blueX) {
		this.blueX = blueX;
		
		
	}
	public int getBlueY() {
		return blueY;
		
	}
	public void setBlueY(int blueY) {
		this.blueY = blueY;
		
	}
	public int getYellowX() {
		return yellowX;
	}
	public void setYellowX(int yellowX) {
		this.yellowX = yellowX;
		
		
	}
	public int getYellowY() {
		return yellowY;
	}
	public void setYellowY(int yellowY) {
		this.yellowY = yellowY;
		
	}
	public int getBallX() {
		return ballX;
	}
	public void setBallX(int ballX) {
		this.ballX = ballX;
		
	}
	
	public int getBallY() {
		return ballY;
	}
	
	public void setBallY(int ballY) {
		this.ballY = ballY;
		
	}

	public double getBlueOrientation() {
		return blueOrientation;
	}

	public void setBlueOrientation(double blueOrientation) {
		this.blueOrientation = blueOrientation;
		
	}

	public double getYellowOrientation() {
		return yellowOrientation;
	}

	public void setYellowOrientation(double yellowOrientation) {
		this.yellowOrientation = yellowOrientation;
		
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
	/////////////////////////
	public Robot getOurRobot()
    {
    	return this.ourRobot;
    }
    
    public Robot getTheirRobot()
    {
    	return this.theirRobot;
    }
    
    public Ball getBall()
    {
    	return this.ball;
    }
    ////////////////////////////
    public void setOurRobot(){
    	if (areWeBlue()){
    		this.ourRobot.x = blueX;
    		this.ourRobot.y = blueY;
    		this.ourRobot.setPosition(new Vector(ourRobot.x, ourRobot.y));
    		this.ourRobot.bearing = getBlueOrientation();  
    	}
    	else {
    		this.ourRobot.x = yellowX;
    		this.ourRobot.y = yellowY;
    		this.ourRobot.setPosition(new Vector(ourRobot.x, ourRobot.y));
    		this.ourRobot.bearing = getYellowOrientation();  
    	}
    }
    public void setTheirRobot(){
    	if (areWeBlue()){
    		//If we are blue, the other robot is yellow.
    		//Please correct me if I am wrong, the code is obscure...
    		this.theirRobot.x = yellowX;
    		this.theirRobot.y = yellowY;
    		this.theirRobot.setPosition(new Vector(theirRobot.x, theirRobot.y));
    		this.theirRobot.bearing = getYellowOrientation();
    	}
    	else {
    		this.theirRobot.x = blueX;
    		this.theirRobot.y = blueY;
    		this.theirRobot.setPosition(new Vector(theirRobot.x, theirRobot.y));
    		this.theirRobot.bearing = getBlueOrientation(); 
    	}
    }
    public void setBall(){
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
	
	public Vector getOurGoal(){
		if(weAreOnLeft && !mainPitch){
			leftGoalCentre = PitchInfo.getLeftGoalCentreSide();
			return leftGoalCentre;
		} else if (!weAreOnLeft && !mainPitch){
			rightGoalCentre = PitchInfo.getRightGoalCentreSide();
			return rightGoalCentre;
		} else if (weAreOnLeft && mainPitch){
			leftGoalCentre = PitchInfo.getLeftGoalCentreMain();
			return leftGoalCentre;
		} else {
			rightGoalCentre = PitchInfo.getRightGoalCentreMain();
			return rightGoalCentre;
		}
	}
	
	public Vector getTheirGoal(){
		if(weAreOnLeft && !mainPitch){
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
	
	public double getMidPoint(){
		if(mainPitch){
			midPoint = PitchInfo.midPointMain;
			return midPoint;
		} else {
			midPoint = PitchInfo.midPointSide;
			return midPoint;
		}
	}
	
	public boolean ourHalfLeft(){
		if(weAreOnLeft) return true;
		else return false; 
	}
	
	public double distanceBetweenUsAndBall() {
		return DistanceCalculator.Distance(
    			this.ourRobot.x, this.ourRobot.y,
    			this.ball.x, this.ball.y);
	}
	
	
	public boolean areWeInOurHalf() {
		if(!((this.ourRobot.x < this.getMidPoint() && this.ourHalfLeft()) 
				|| (this.ourRobot.x >= this.getMidPoint() && !this.ourHalfLeft())))
			return false;
		else
			return true;
	}
	
	public boolean ballIsInGoal() {
    	if(this.areWeOnLeft()) {
	    	if (this.ball.x < this.getOurGoal().getX() || this.ball.x > this.getTheirGoal().getX()) {
	    		return true;
	    	}
    	} else {
	    	if (this.ball.x > this.getOurGoal().getX() || this.ball.x < this.getTheirGoal().getX()) {
	    		return true;
	    	}
    	}
    	return false;
	}

}

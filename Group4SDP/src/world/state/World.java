package world.state;

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

public class World extends Observable implements Runnable, WorldInterface {
	/*
	 * TODO: Document this class!
	 * It is an observable singleton running in it's own thread.
	 * This is probably abusing design patterns.
	 */
    private static final World instance = new World();
	private Vision vision;
	private static boolean isConnected = false;
	private static Socket visionSocket;
	private static PrintWriter out;
	private static BufferedReader in;
	
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
    
    //TODO: Get these data from files depending on pitch info from GUI

    private World() {
    	//TODO: write function to read in and set variables according to pitch and side info from GUI
    	//readPitchInfo(mainPitch,weAreOnLeft);
    	System.out.println("World constructed");
    	Thread t = new Thread(this);
    	t.start();
    	// Initialize our bearings
    	for (int i = 0; i < 3; i++){
    		ourBearings[i] = 0.0;
    		theirBearings[i] = 0.0;
    	}
    }
    
    public void run() {
    	this.connectVision();
    }

    public static World getInstance() {
    	int connectionAttempts = 5;
    	int delay = 500;
    	for (int i = 0; i < connectionAttempts; i++) {
	    	if (isConnected) {
	        	// Set up the possession manager
	        	instance.pm = new PossessionManager();
	    		return instance;
	    	}
			try {
				System.out.println("Server not connected, retrying...");
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        System.err.println("Is the vision server on?");
        System.exit(1);
		return null;
    }

    public static void main(String[] args) {
    	// This starts an infinite loop for the server
    }
    
    public void connectVision() {
    	//this.frame = Integer.parseInt(splitArray[0]);
    	WorldState worldState = vision.getWorldState();
        this.ourRobot.x = worldState.getBlueX();
		this.ourRobot.y = worldState.getBlueY();
		this.ourRobot.setPosition(new Vector(worldState.getBlueX(), worldState.getBlueY()));
		this.ourRobot.bearing = worldState.getBlueOrientation();    
        this.theirRobot.x = worldState.getYellowX();
        this.theirRobot.y = worldState.getYellowY();
		this.theirRobot.setPosition(new Vector(worldState.getYellowX(), worldState.getYellowY()));
		this.theirRobot.bearing = worldState.getYellowOrientation();
		
		this.prevBall.x = this.ball.x;
    	this.prevBall.y = this.ball.y;
    	this.prevBall.setPosition(this.ball.getPosition());
    	// Ball
        this.ball.x = worldState.getBallX();
        this.ball.y = worldState.getBallY();
        this.ball.setPosition(new Vector(worldState.getBallX(), worldState.getBallY()));
		System.out.println("Coordinates were parsed succesfully");
/*<<<<<<< HEAD
		this.hasPossession = this.pm.setPossession(this);
	    //setChanged();
	    //notifyObservers(this.frame);
		
=======
>>>>>>> c800884a33bd94b64b9f05ac77826f5d160b59cc*/

    }
    
    public void parseLine(String input) {

    	String delimiter = ";";
        String[] splitArray;
        splitArray = input.split(delimiter);

        this.frame = Integer.parseInt(splitArray[0]);

        String delimiter2 = ",";
        Double x, y;
        for (int i = 1; i < splitArray.length; i++) {
        	// Format is yellow, blue, ball
            String[] temp = splitArray[i].split(delimiter2);
            if ((i == 1 && !weAreBlue) || (i == 2 && weAreBlue)) {
            	x = Double.parseDouble(temp[0]);
            	y = Double.parseDouble(temp[1]);
        		this.ourRobot.x = x;
        		this.ourRobot.y = y;
        		this.ourRobot.setPosition(new Vector(x, y));
        		this.ourRobot.bearing = Double.parseDouble(temp[2]);
            } else if ((i == 2 && !weAreBlue) || (i == 1 && weAreBlue)) {
            	x = Double.parseDouble(temp[0]);
            	y = Double.parseDouble(temp[1]);
                this.theirRobot.x = x;
                this.theirRobot.y = y;
        		this.theirRobot.setPosition(new Vector(x, y));
        		this.theirRobot.bearing = Double.parseDouble(temp[2]);
            } else {
            	x = Double.parseDouble(temp[0]);
            	y = Double.parseDouble(temp[1]);
            	//previous position
            	this.prevBall.x = this.ball.x;
            	this.prevBall.y = this.ball.y;
            	this.prevBall.setPosition(this.ball.getPosition());
            	// Ball
                this.ball.x = x;
                this.ball.y = y;
                this.ball.setPosition(new Vector(x, y));
            }
        }
        /* Some kind of logging going on here
        System.out.printf("%f\t%f\t%f\t\t%f\t%f\n",
        ourBearings[0],ourBearings[1],ourBearings[2],tmp,this.ourRobot.bearing);
        */

        // Tell our observers that we've updated
        // Update who has possession
        this.hasPossession = this.pm.setPossession(this);
	    setChanged();
	    notifyObservers(this.frame);
    }
    
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
			leftGoalCentre = PitchInfo.leftGoalCentreSide;
			return leftGoalCentre;
		} else if (!weAreOnLeft && !mainPitch){
			rightGoalCentre = PitchInfo.rightGoalCentreSide;
			return rightGoalCentre;
		} else if (weAreOnLeft && mainPitch){
			leftGoalCentre = PitchInfo.leftGoalCentreMain;
			return leftGoalCentre;
		} else {
			rightGoalCentre = PitchInfo.rightGoalCentreMain;
			return rightGoalCentre;
		}
	}
	
	public Vector getTheirGoal(){
		if(weAreOnLeft && !mainPitch){
			rightGoalCentre = PitchInfo.rightGoalCentreSide;
			return rightGoalCentre;
		} else if (!weAreOnLeft && !mainPitch) {
			leftGoalCentre = PitchInfo.leftGoalCentreSide;
			return leftGoalCentre;
		} else if (weAreOnLeft && mainPitch) {
			rightGoalCentre = PitchInfo.rightGoalCentreMain;
			return rightGoalCentre;
		} else {
			leftGoalCentre = PitchInfo.leftGoalCentreMain;
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

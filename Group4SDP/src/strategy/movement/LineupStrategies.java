package strategy.movement;

import java.awt.geom.Point2D;

import world.state.PitchInfo;
import vision.WorldState;

public class LineupStrategies {
		
	// TODO: MOVE THESE TO PITCHINFO!!!111
	public static final float pitchL = 625;//600;
    public static final float pitchW = 315;//330;
    public static final float arbitraryX = 39;
    public static final float arbitraryY = 46;
    
	public static Point2D getPointBehindBall(WorldState world){

    	double targetDistFromBall = 100.0d;
    	
    	double ax = world.getTheirGoal().getX();
		double ay = world.getTheirGoal().getY();
		
		double bx = world.getBall().x;
		double by = world.getBall().y;
		
		double distAB = Math.sqrt((bx-ax)*(bx-ax) + (by-ay)*(by-ay));
    	
    	double destX = bx + targetDistFromBall*(bx-ax)/distAB;
    	double destY = by + targetDistFromBall*(by-ay)/distAB;
    	
    	//"cut" the coordinates so that we don't crash into the wall
    	if (destX < arbitraryX + PitchInfo.safeDistanceFromWall)
    		destX = arbitraryX + PitchInfo.safeDistanceFromWall;
    	else if (destX > arbitraryX + pitchL - PitchInfo.safeDistanceFromWall)
    		destX = arbitraryX + pitchL - PitchInfo.safeDistanceFromWall;
    	
    	if (destY < arbitraryY + PitchInfo.safeDistanceFromWall)
    		destY = arbitraryY + PitchInfo.safeDistanceFromWall;
    	else if (destY > arbitraryY + pitchW - PitchInfo.safeDistanceFromWall)
    		destY = arbitraryY + pitchW - PitchInfo.safeDistanceFromWall;
    	
    	Point2D behindball = new Point2D.Double(destX, destY);
    	
    	return behindball;
		
	}
	
}

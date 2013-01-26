package world.state;

import geometry.Vector;

public class PitchInfo {

	//side pitch
	/* Total= 608, 324
	 * LC = 40, 193
	 * RC = 644, 193
	 * RT = 650, 108
	 * RB = 647, 273
	 * LT = 38, 104
	 * LB = 39, 275
	 * C = 348, 189
	 * TL = 36, 18
	 * BR = 658, 344
	 */
	
	//main pitch
	/* Total = 643, 312
	 * LC = 38, 204
	 * RC = 672, 204
	 * M = 
	 */
	private final static double edgeSafeDist = 15;
	    
	static Vector leftGoalCentreSide = new Vector(40.0, 160.0);
	static Vector rightGoalCentreSide = new Vector(644.0, 193.0);
	
	public static final Vector lowerBoundSide = new Vector(36.0,18.0);
	public static final Vector upperBoundSide = new Vector(658.0,344.0);
	
	public static final Vector safeLowerBoundSide = new Vector(lowerBoundSide.getX() + edgeSafeDist, lowerBoundSide.getY() + edgeSafeDist);
	public static final Vector safeUpperBoundSide = new Vector(upperBoundSide.getX() + edgeSafeDist, upperBoundSide.getY() + edgeSafeDist);
	
	public static double midPointSide = 348.0;
	
	static Vector leftGoalCentreMain = new Vector(38.0, 204.0);
	static Vector rightGoalCentreMain = new Vector(672.0, 204.0);
	//check this!
	static Vector lowerBoundMain = new Vector(39.0,46.0);
	static Vector upperBoundMain = new Vector(664.0,370.0);

	public static double midPointMain = 355.0;
	
	public static double safeDistanceFromWall = 25;
	
	//public static double moveToX = 0.0;
	//public static double moveToY = 0.0;
	
	private final static float pitchL = 600; //625;//600;
    private final static float pitchW = 330;

	public static Vector outOfBoundsSide(Vector point){
		
		double returnX = point.getX();
		double returnY = point.getY();
		
		if (point.getX() < lowerBoundSide.getX() + edgeSafeDist)
			returnX = lowerBoundSide.getX() + edgeSafeDist;
		else if (point.getX() > upperBoundSide.getX() - edgeSafeDist)
			returnX = upperBoundSide.getX() - edgeSafeDist;

		if (point.getY() < lowerBoundSide.getY() + edgeSafeDist)
			returnY = lowerBoundSide.getY() + edgeSafeDist;
		else if (point.getY() > upperBoundSide.getY() - edgeSafeDist)
			returnY = upperBoundSide.getY() - edgeSafeDist;
		
		Vector returnPoint = new Vector(returnX, returnY);
		
		return returnPoint;
	}
	public static Vector outOfBoundsMain(Vector point){
			
			double returnX = point.getX();
			double returnY = point.getY();
			
			if (point.getX() < lowerBoundMain.getX() + edgeSafeDist)
				returnX = lowerBoundMain.getX() + edgeSafeDist;
			else if (point.getX() > upperBoundMain.getX() - edgeSafeDist)
				returnX = upperBoundMain.getX() - edgeSafeDist;
	
			if (point.getY() < lowerBoundMain.getY() + edgeSafeDist)
				returnY = lowerBoundMain.getY() + edgeSafeDist;
			else if (point.getY() > upperBoundMain.getY() - edgeSafeDist)
				returnY = upperBoundMain.getY() - edgeSafeDist;
			
			Vector returnPoint = new Vector(returnX, returnY);
			
			return returnPoint;
		}
}
package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import vision.Position;
import world.state.WorldState;

/**
 * Defensive Strategy. CASE 1.
 * 
 * Should work when the combination on the field is: 1) OUR GOAL - US -THEM 2)
 * They have possession of the ball or are about to have it (ie. they are
 * advancing quickly to the ball) 3) Everything is happening on our side of the
 * pitch
 * 
 * What the Strategy does: 1) We retreat to the middle of our goal While loop:
 * 2) Turn to face the opponent 3) Move accordingly on the y axis - when their
 * rotation angle has stabilized and as long as the turn is not too small.
 * 4)Adjust according to the movement of the robot - angle and up-down y axis
 * (while loop iterations).
 * 
 * The defence should be off if the situation changes to: 1) Ball & other robot
 * not on our side of the pitch. 2) Ball on our side of the pitch, but the other
 * robot has no possession of it and is not attempting to get it. 3) We have
 * caught the ball in defence or diverted it from out goal 4) They scored a goal
 * 5) The combination on the pitch is OUR GOAL - THEM - US, as in this case we
 * can't get there in time for this defence to work
 * 
 * @autor Simona Petrova & Marija Pinkute. 2013.
 * 
 * */
public class Defensive extends StrategyInterface {

	private Position ourGoalCenter;
	private Position ourGoalDefendPosition;
	private Position ourGoalTop;
	private Position ourGoalBottom;
	private double previousTheirBearing = 0;
	private final int threshold = 30;

	public Defensive(WorldState world, RobotMover mover) {
		super(world, mover);
	}

	@Override
	public void run() {
		try {
			System.out.println("Defensive strategy activated");

			// Set the goal dimension variables, so that we we know which goal
			// to protect.
			setGoalVariables();

			// Initial step of defence. Move to the center of our goal
			mover.moveToAndStop(ourGoalDefendPosition.getX(),
					ourGoalDefendPosition.getY());
			mover.waitForCompletion();
			if (shouldidie)
				return;

			while (!Strategy.alldie && !shouldidie) {

				double turningAngle = calcAngleToTrunToFaceOtherRobot();

				// Turn only if the turning angle is more than 7 degrees.
				if (Math.abs(turningAngle) > 7) {
					mover.rotate(Math.toRadians(turningAngle));
					mover.waitForCompletion();
					if (shouldidie)
						return;
				}

				// Move on the Y axis if needed to face the other robot
				double destY = calcDistYAxisAcordingToEnemy();
				if (destY != -1) {
					mover.moveToAndStop(ourGoalDefendPosition.getX(), destY);
					mover.waitForCompletion();
					if (shouldidie)
						return;
				} else
					SafeSleep.sleep(50);

			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Determine which goal is ours and set the goal constants
	 * 
	 * @return ourGoalDefendPosition, the Position object to which our robot
	 *         should go to defend the center of our goal
	 */
	public Position setGoalVariables() {

		int PITCH = world.getPitch();

		if (world.areWeOnLeft()) {
			ourGoalTop = world.goalInfo.getLeftGoalTop(PITCH);
			ourGoalBottom = world.goalInfo.getLeftGoalBottom(PITCH);
			ourGoalCenter = world.goalInfo.getLeftGoalCenter(PITCH);
			ourGoalDefendPosition = new Position(ourGoalCenter.getX()
					+ threshold, ourGoalCenter.getY());
		} else {
			ourGoalTop = world.goalInfo.getRightGoalTop(PITCH);
			ourGoalBottom = world.goalInfo.getRightGoalBottom(PITCH);
			ourGoalCenter = world.goalInfo.getRightGoalCenter(PITCH);
			ourGoalDefendPosition = new Position(ourGoalCenter.getX()
					- threshold, ourGoalCenter.getY());
		}
		return ourGoalDefendPosition;
	}

	/**
	 * Calculate the angle at which our robot should face the other robot. In
	 * this case we assume that the other robot has possession of the ball, thus
	 * we have to face it in order to catch.
	 * 
	 * @return angle, the angle at which our robot should turn to face the other
	 *         robot
	 */
	public double calcAngleToTrunToFaceOtherRobot() {
		double angle = 0;

		// Finding the angle to which we should turn
		if (Math.toDegrees(world.theirRobot.bearing) > 180)
			angle = Math.toDegrees(world.theirRobot.bearing - Math.PI);
		else
			angle = Math.toDegrees(world.theirRobot.bearing + Math.PI);

		// Finding the angle our robot should turn to get to the destination
		// angle
		angle -= Math.toDegrees(world.ourRobot.bearing);

		// If the turning angle is more than 180 degrees turn anti-clockwise as
		// smaller turn
		if (angle > 180)
			angle -= 360;

		return angle;
	}

	/**
	 * Calculating the kick line of the attacking robot and returning where on
	 * the X axis we should stand.
	 * 
	 * The robot should move back to the center of the goal into our defence
	 * position if the enemy is not facing us. The robot should move on the Y
	 * axis if: ~ it is the first turn ~ if the change in their bearing is
	 * larger than 5 degrees, as it may have changed due to fluctuation ~ if the
	 * change in their bearing is smaller than 10 degrees, because we want to
	 * turn only when the angle has stabilized
	 * 
	 * @return destY, the destination on the Y axis our robot should move, if no
	 *         move is to be made return -1.
	 */
	public double calcDistYAxisAcordingToEnemy() {

		double angle, ythreshold;
		double destY = -1;
		double theirBearing = Math.toDegrees(world.theirRobot.bearing);
		System.out.println(theirBearing);

		double diffTheirsBearings = Math.abs(previousTheirBearing
				- theirBearing);

		// They are not facing us, remain in the middle of the goal
		if (theirBearing < 180 && world.areWeOnLeft())
			return ourGoalDefendPosition.getY();

		if (theirBearing > 180 && !world.areWeOnLeft())
			return ourGoalDefendPosition.getY();

		// They are facing us.
		if ((previousTheirBearing == 0)
				|| ((diffTheirsBearings > 5) && (diffTheirsBearings < 10))) {

			if (world.areWeOnLeft()) {
				angle = Math.abs(270 - theirBearing);
				ythreshold = (world.theirRobot.x - ourGoalCenter.getX())
						* Math.tan(Math.toRadians(angle));
				System.out.println("ythershold " + ythreshold);
			} else {
				angle = Math.abs(90 - theirBearing);
				ythreshold = (ourGoalCenter.getX() - world.theirRobot.x)
						* Math.tan(Math.toRadians(angle));
			}

			
			 ythreshold -= Math.abs(world.theirRobot.y - world.ourRobot.y);
			 
			  System.out.println( "ythershold - world.theirRobot.y - world.ourRobot.y " + ythreshold); 
			  // Lower half of the field 
			  if (world.theirRobot.y < ourGoalCenter.getY()) 
				  destY = world.ourRobot.y + ythreshold; 
			  else
			  // Upper half of the field 
				  destY = world.ourRobot.y - ythreshold;
			
			
			
			// If we have to move on the Y axis to the point that our robot does
			// not cover the goal, cover the corner of the goal, as a ricochet
			// is expected
			if (destY > ourGoalTop.getY())
				destY = ourGoalTop.getY() - threshold;
			else if (destY < ourGoalBottom.getY())
				destY = ourGoalBottom.getY() + threshold;

			previousTheirBearing = theirBearing;
		}

		System.out.println(destY);
		return destY;

//		
//		if (world.theirRobot.y < ourGoalCenter.getY())
//			// Lower half of the field
//			destY = ourGoalCenter.getY() + ythreshold;
//		else
//			// Upper half of the field
//			destY = ourGoalCenter.getY() - ythreshold;
	}

	public void setPrevTheirBearing(double angle) {
		previousTheirBearing = angle;
	}

}

/*
 * Copyright (c) 2012. University of Edinburgh
 */

package strategy.movement;

import world.state.Ball;
import world.state.Robot;

public class TurnToBall {

	/*
	 * findBearing takes 2 coordinates (the ball and our robot) and calculates
	 * the bearing of the ball relative to the robot (using the robot as the
	 * origin and "north" as being up on the camera feed)
	 */

	public static double findBearing(Robot us, Ball ball) {
		return findPointBearing(us, ball.x, ball.y);
	}

	/*
	 * turnAngle takes the ballBearing (the output from findBearing) and
	 * botBearing (the angle the robot is facing from north, which will be
	 * supplied from vision) and calculates the difference between these and
	 * selects the smallest turn to take. Clockwise turns are a positive angle
	 * and anti-clockwise are negative.
	 */

	public static double turnAngle(double botBearing, double toBallBearing) {
		botBearing = Math.toDegrees(botBearing);
		double turnAngle = toBallBearing - botBearing;
		if (turnAngle > 180.0)
			turnAngle -= 360.0;
		return turnAngle;
	}

	public static double Turner(Robot us, Ball ball) {
		double ballBearing = findBearing(us, ball);
		double angle = turnAngle(us.bearing, ballBearing);
		return angle;
	}

	public static double findPointBearing(Robot us, double x, double y) {
		// calculates the bearing of a point (x,y) relative to the robot (using
		// the robot as the origin and "north"
		// as being up on the camera feed)
		// calculates the angle depending on which side of the robot the ball is
		// on
		double bearing = 0;
		double xDiff = x - us.x;
		double yDiff = y - us.y;

		// Use the dot product formula to determine the angle between the vector
		// (0, -1) (up to the camera) and the vector (xDiff, yDiff)
		bearing = Math.acos(-yDiff / Math.sqrt(xDiff * xDiff + yDiff * yDiff));

		// Correct for the case when the angle calculated above is the
		// counterclockwise bearing instead of clockwise
		if (xDiff < 0)
			bearing = 2.0 * Math.PI - bearing;

		return Math.toDegrees(bearing);
	}

	// This method calculates the angle between the robot and a point (x,y)
	public static double AngleTurner(Robot us, double x, double y) {
		double pointBearing = findPointBearing(us, x, y);
		double angle = turnAngle(us.bearing, pointBearing);
		return angle;
	}
}
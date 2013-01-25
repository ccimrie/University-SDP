package strategy.planning;

import java.util.Observable;

import world.state.PitchInfo;
import geometry.Vector;

public class ArcTest extends Strategy {
	@Override
	public void update(Observable arg0, Object arg1) {
		rc.setDefaultRotateSpeed(30.0);
		rc.setDefaultTravelSpeed(0.3);
		rc.setAcceleration(1.2);

		Vector lowBound = normalise(PitchInfo.safeLowerBoundSide);
		Vector upperBound = normalise(PitchInfo.safeUpperBoundSide);

		Vector ballPos = normalise(world.ball.getPosition());
		Vector robotPos = normalise(world.ourRobot.getPosition());
		Vector robotDirection = new Vector(world.ourRobot.bearing);

		// Find the intersection point with each wall if the robot were to continue forwards
		// TODO: adjust for pitch safe distance
		Vector intTop, intBottom, intLeft, intRight;
		intTop =	robotPos.intersectY(robotDirection, lowBound.getY());
		intBottom = robotPos.intersectY(robotDirection, upperBound.getY());
		intLeft =	robotPos.intersectX(robotDirection, lowBound.getX());
		intRight =	robotPos.intersectX(robotDirection, upperBound.getX());

		//System.out.println(String.format("Upper bound: %s Lower bound: %s", upperBound, lowBound));

		// Get the minimum distance to an intersection point (null returns mean no intersection was possible)
		double intersectionDist = Double.POSITIVE_INFINITY;
		if (intTop != null) intersectionDist = Math.min(intersectionDist, robotPos.distance(intTop));
		if (intBottom != null) intersectionDist = Math.min(intersectionDist, robotPos.distance(intBottom));
		if (intLeft != null) intersectionDist = Math.min(intersectionDist, robotPos.distance(intLeft));
		if (intRight != null) intersectionDist = Math.min(intersectionDist, robotPos.distance(intRight));

		double turnAngle = Vector.angleBetweenPoints(robotPos, ballPos) - world.ourRobot.bearing;
		double arcRadius = Math.min(intersectionDist, robotPos.distance(ballPos)) / (2 * Math.sin(turnAngle));
		rc.arcForward(arcRadius);
	}

	private Vector normalise(Vector v) {
		double x = 2.4384 * (v.getX() - PitchInfo.lowerBoundSide.getX()) / (PitchInfo.upperBoundSide.getX());
		double y = 1.2192 * (v.getY() - PitchInfo.lowerBoundSide.getY()) / (PitchInfo.upperBoundSide.getY());
		return new Vector(x, y);
	}
}

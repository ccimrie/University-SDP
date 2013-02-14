package strategy.movement.test;

import static org.junit.Assert.*;

import org.junit.Test;

import strategy.movement.TurnToBall;

import world.state.Ball;
import world.state.Robot;
import world.state.RobotType;

public class TestBearing {
	/** The error threshold used when asserting two doubles are equal */
	private static final double DOUBLE_THRESHOLD = 1.0e-12;

	/**
	 * Create a Ball object given its position for testing purposes
	 * 
	 * @param x
	 *            The x-coordinate of the ball
	 * @param y
	 *            The y-coordinate of the ball
	 * @return A Ball object representing the ball
	 */
	private Ball makeBall(double x, double y) {
		Ball result = new Ball();
		result.x = x;
		result.y = y;

		return result;
	}

	/**
	 * Create a Robot object given its type, position, and bearing for testing
	 * purposes
	 * 
	 * @param type
	 *            The type of the robot
	 * @param x
	 *            The x-coordinate of the robot
	 * @param y
	 *            The y-coordinate of the robot
	 * @param bearing
	 *            The bearing of the robot
	 * @return A Robot object representing the robot
	 */
	private Robot makeRobot(RobotType type, double x, double y, double bearing) {
		Robot result = new Robot(type);
		result.x = x;
		result.y = y;
		result.bearing = bearing;

		return result;
	}

	@Test
	public void testFindBearing() {
		// Tests the TurnToBall.findBearing method with robots at every 5 pixels
		// on both the x and y axes, and the ball at distances increasing in
		// steps of 50, and at angles increasing in steps of 1 degree.
		for (int x = 0; x < 640; x += 5) {
			for (int y = 0; y < 480; y += 5) {
				Robot robot = makeRobot(RobotType.Us, x, y, 0.0);
				for (int r = 50; r < 500; r += 50) {
					for (double angle = 0.0; angle < 360.0; angle += 1.0) {
						Ball ball = makeBall(
								(double) r * Math.sin(Math.toRadians(angle))
										+ x,
								(double) -r * Math.cos(Math.toRadians(angle))
										+ y);
						assertEquals(angle,
								TurnToBall.findBearing(robot, ball),
								DOUBLE_THRESHOLD);
					}
				}
			}
		}
	}

	@Test
	public void testTurnAngle() {
		// Yes, one parameter is in radians and the other is in degrees...

		// Tests the TurnToBall.turnAngle method for every combination of
		// angles, discretised to 0.1 degree
		for (double angle1 = 0.0; angle1 < Math.PI; angle1 += Math.PI / 1800.0) {
			double angle2Min = Math.toDegrees(angle1);
			double angle2Max = Math.toDegrees(angle1 + Math.PI);
			for (double angle2 = angle2Min; angle2 < angle2Max; angle2 += 0.1) {
				assertEquals(angle2 - Math.toDegrees(angle1),
						TurnToBall.turnAngle(angle1, angle2), DOUBLE_THRESHOLD);
				assertEquals(
						Math.toDegrees(angle1) - angle2,
						TurnToBall.turnAngle(Math.toRadians(angle2),
								Math.toDegrees(angle1)), DOUBLE_THRESHOLD);
			}
		}
	}

	@Test
	public void testFindPointBearing() {
		// Essentially the same as testFindBearing, but applied to robots and
		// points rather than balls (intended to make sure both methods work
		// independently of each other)
		for (int x = 0; x < 640; x += 5) {
			for (int y = 0; y < 480; y += 5) {
				Robot robot = makeRobot(RobotType.Us, x, y, 0.0);
				for (int r = 50; r < 500; r += 50) {
					for (double angle = 0.0; angle < 360.0; angle += 1.0) {
						assertEquals(angle, TurnToBall.findPointBearing(robot,
								(double) r * Math.sin(Math.toRadians(angle))
										+ x,
								(double) -r * Math.cos(Math.toRadians(angle))
										+ y), DOUBLE_THRESHOLD);
					}
				}
			}
		}
	}
}

package strategy.movement.test;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import strategy.movement.TurnToBall;

import world.state.Ball;
import world.state.Robot;
import world.state.RobotType;

public class TestBearing {
	private static double FP_THRESHOLD = 0.000000000001;

	/**
	 * Create a Ball object given its position for testing purposes
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Ball makeBall(double x, double y) {
		Ball result = new Ball();
		result.x = x;
		result.y = y;

		return result;
	}

	private Robot makeRobot(RobotType type, double x, double y, double bearing) {
		Robot result = new Robot(type);
		result.x = x;
		result.y = y;
		result.bearing = bearing;

		return result;
	}

	/**
	 * Tests findBearing over a wide range of realistic values
	 */
	@Test
	public void testFindBearing() {
		for (int x = 0; x < 640; x += 5) {
			for (int y = 0; y < 480; y += 5) {
				Robot robot = makeRobot(RobotType.Us, x, y, 0.0);
				for (int r = 50; r < 500; r += 50) {
					for (double angle = 0.0; angle < 360.0; angle += 5.0) {
						Ball ball = makeBall(
								(double) r * Math.sin(Math.toRadians(angle))
										+ x,
								(double) -r * Math.cos(Math.toRadians(angle))
										+ y);
						assertEquals(angle,
								TurnToBall.findBearing(robot, ball),
								FP_THRESHOLD);
					}
				}
			}
		}
	}

	/**
	 * Yes, one parameter is in radians and the other is in degrees. I did not
	 * write the method being tested! (nor any that depend on it)
	 */
	@Test
	public void testTurnAngle() {
		for (double angle1 = 0.0; angle1 < Math.PI; angle1 += Math.PI / 180.0) {
			double angle2Min = Math.toDegrees(angle1);
			double angle2Max = Math.toDegrees(angle1 + Math.PI);
			for (double angle2 = angle2Min; angle2 < angle2Max; angle2 += 1.0) {
				assertEquals(angle2 - Math.toDegrees(angle1),
						TurnToBall.turnAngle(angle1, angle2),
						FP_THRESHOLD);
				assertEquals(Math.toDegrees(angle1) - angle2,
						TurnToBall.turnAngle(Math.toRadians(angle2), Math.toDegrees(angle1)),
						FP_THRESHOLD);
			}
		}
	}

	@Test
	public void testFindPointBearing() {
		for (int x = 0; x < 640; x += 5) {
			for (int y = 0; y < 480; y += 5) {
				Robot robot = makeRobot(RobotType.Us, x, y, 0.0);
				for (int r = 50; r < 500; r += 50) {
					for (double angle = 0.0; angle < 360.0; angle += 5.0) {
						assertEquals(angle, TurnToBall.findPointBearing(robot,
								(double) r * Math.sin(Math.toRadians(angle)) + x,
								(double) -r * Math.cos(Math.toRadians(angle)) + y),
								FP_THRESHOLD);
					}
				}
			}
		}
	}
}

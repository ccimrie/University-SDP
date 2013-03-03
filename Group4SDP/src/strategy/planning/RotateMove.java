package strategy.planning;

import communication.BluetoothRobot;

import strategy.movement.DistanceToBall;
import world.state.Robot;
import world.state.WorldState;

public class RotateMove {

	public void rotateMove(WorldState worldState, BluetoothRobot robot,
			double moveToX, double moveToY) throws InterruptedException {

		worldState.setOurRobot();
		Robot us = worldState.ourRobot;

		double angleToRotate = 90;
		// convert to mm
		double distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY) * 3.6;
		// mm/s
		double speed = 150;
		double time = distance / speed;
		double angle = angleToRotate / time;
		System.out.println(distance);
		while (distance > 50) {
			double x = (moveToX - us.x) / 3.6;
			double y = (moveToY - us.y) / 3.6;
			double xd = x * Math.cos(us.bearing) - y * Math.sin(us.bearing);
			double yd = x * Math.sin(us.bearing) + y * Math.cos(us.bearing);
			robot.rotateMove((int) xd, (int) yd, (int) angle);
			Thread.sleep(40);
			distance = DistanceToBall.Distance(us.x, us.y, moveToX, moveToY) / 3.6;
		}
	}

}

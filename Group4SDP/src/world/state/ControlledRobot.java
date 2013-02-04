package world.state;

import comms.control.Server;
import comms.control.ServerInterface;

public class ControlledRobot extends Robot implements ServerInterface {
	public ControlledRobot(RobotType type) {
		super(type);
	}

	private Server rc;
	
	private enum CurrentMovement {
		
	}
	
	public void setServer(Server rc) {
		this.rc = rc;
	}

	@Override
	public boolean quit() {
		return rc.quit();
	}

	@Override
	public boolean stop() {
		return rc.stop();
	}

	@Override
	public void setDefaultTravelSpeed(Double speed) {
		rc.setDefaultTravelSpeed(speed);
	}

	@Override
	public boolean travel(Double distance) {
		return rc.travel(distance);
	}

	@Override
	public boolean travel(Double distance, Double speed) {
		return rc.travel(distance, speed);
	}

	@Override
	public boolean kick() {
		return rc.kick();
	}

	@Override
	public void setDefaultRotateSpeed(Double speed) {
		rc.setDefaultRotateSpeed(speed);
	}

	@Override
	public boolean rotate(Double angle) {
		return rc.rotate(angle);
	}

	@Override
	public boolean rotate(Double angle, Double speed) {
		return rc.rotate(angle, speed);
	}

	@Override
	public boolean forward() {
		return rc.forward();
	}

	@Override
	public boolean forward(Double speed) {
		return rc.forward(speed);
	}

	@Override
	public boolean arcForward(Double radius) {
		return rc.arcForward(radius);
	}

	@Override
	public boolean arcForward(Double radius, Double speed) {
		return rc.arcForward(radius, speed);
	}

	@Override
	public boolean isMoving() {
		return rc.isMoving();
	}

	@Override
	public boolean isStalled() {
		return rc.isStalled();
	}

	@Override
	public boolean isTouchPressed() {
		return rc.isTouchPressed();
	}

}

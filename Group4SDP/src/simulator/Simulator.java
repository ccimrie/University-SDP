package simulator;

import org.jbox2d.dynamics.Body;

import world.state.WorldState;

public class Simulator {
	private final WorldState worldState;

	/**
	 * Index for the wheel on the left of the robot (forward / backward motion)
	 */
	public static final int LEFT_WHEEL = 0;
	/**
	 * Index for the wheel on the right of the robot (forward / backward motion)
	 */
	public static final int RIGHT_WHEEL = 1;
	/**
	 * Index for the wheel on the front of the robot (left / right motion)
	 */
	public static final int FRONT_WHEEL = 2;
	/**
	 * Index for the wheel on the back of the robot (left / right motion)
	 */
	public static final int BACK_WHEEL = 3;

	
	public Simulator(final WorldState worldState) {
		this.worldState = worldState;
	}

	public void setRobotWheelCount(int count) {
		// TODO: implement
	}

	public int getRobotWheelCount() {
		// TODO: implement
		return 0;
	}

	public void setRobotWheelSpeed(int wheel, int speed) {
		// TODO: implement
	}

	public int getRobotWheelSpeed() {
		// TODO: implement
		return 0;
	}
}

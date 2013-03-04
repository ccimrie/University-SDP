package simulator;

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

	/**
	 * Sets the number of wheels for a robot
	 * 
	 * @param count
	 *            The number of wheels the robot should have
	 */
	public void setRobotWheelCount(int count) {
		// TODO: implement
	}

	/**
	 * Gets the number of wheels for a robot
	 * 
	 * @return The number of wheels the robot has
	 */
	public int getRobotWheelCount() {
		// TODO: implement
		return 0;
	}

	/**
	 * Sets the angular velocity for one wheel on the robot
	 * 
	 * @param wheel
	 *            An index to determine which wheel to set the speed for
	 * @param speed
	 *            The speed in degrees per second to set the wheel to
	 */
	public void setRobotWheelSpeed(int wheel, int speed) {
		// TODO: implement
	}

	/**
	 * Gets the angular velocity for one wheel on the robot
	 * 
	 * @param wheel
	 *            An index to determine which wheel to get the speed for
	 * @return The speed in degrees per second the wheel is set to
	 */
	public int getRobotWheelSpeed(int wheel) {
		// TODO: implement
		return 0;
	}

	/**
	 * Simulates a rotation by the specified angle before returning
	 * 
	 * @param angRad
	 *            The angle to rotate by, in radians
	 */
	public void doRotate(double angRad) {

	}
}

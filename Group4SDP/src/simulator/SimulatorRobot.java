package simulator;

import world.state.Robot;
import world.state.RobotType;

import communication.RobotController;

/**
 * A class to simulate control of the robot, as if there was a bluetooth
 * connection to the real robot <br/>
 * TODO: implement random (infrequent & toggleable) failures
 * 
 * @author Alex Adams (s1046358)
 */
public class SimulatorRobot extends Robot implements RobotController {
	/**
	 * A boolean value representing whether a simulated connection is active
	 */
	private boolean connected = false;
	
	private final Simulator sim;

	public SimulatorRobot(RobotType type, final Simulator simulator) {
		super(type);
		
		sim = simulator;
	}

	/**
	 * Simulates creating a connection to the robot
	 * 
	 * @throws Exception
	 *             occasionally to simulate a connection failure
	 */
	@Override
	public void connect() throws Exception {
		connected = true;
	}

	/**
	 * Simulates whether the robot has an active connection
	 * 
	 * @return true if there's an active connection, false otherwise
	 */
	@Override
	public boolean isConnected() {
		return connected;
	}

	/**
	 * TODO: add a small delay between connected and ready becoming true<br/>
	 * Simulates whether the robot is ready to receive commands
	 * 
	 * @return true if the robot's ready, false otherwise.
	 */
	@Override
	public boolean isReady() {
		return connected;
	}

	/**
	 * Simulates a disconnect from the robot
	 */
	@Override
	public void disconnect() {
		connected = false;
	}

	/**
	 * Simulates a stop command for the robot
	 */
	@Override
	public int stop() {

		return 0;
	}

	/**
	 * Simulates a kick command for the robot
	 */
	@Override
	public int kick() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Simulates a move command for the robot
	 */
	@Override
	public int move(int speedX, int speedY) {
		
		return 0;
	}

	/**
	 * Simulates a rotate command for the robot
	 */
	@Override
	public int rotate(int angleDeg) {
		return 0;
	}

	/**
	 * Simulates a rotateMove command for the robot
	 */
	@Override
	public int rotateMove(int speedX, int speedY, int rotSpeed) {
		// TODO implement to mimic Brick.java implementation for the real robot
		return 0;
	}

	@Override
	public void clearBuff() {
		// Not required for simulated robot
	}
}

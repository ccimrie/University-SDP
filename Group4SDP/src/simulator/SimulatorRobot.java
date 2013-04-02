package simulator;

import world.state.Robot;
import world.state.RobotType;

import utility.SafeSleep;

import communication.RobotController;

/**
 * A class to simulate control of the robot, as if there was a bluetooth
 * connection to the real robot <br/>
 * TODO: implement random (infrequent & toggleable) failures <br/>
 * TODO: implement simulated bluetooth delays
 * 
 * @author Alex Adams (s1046358)
 */
public class SimulatorRobot extends Robot implements RobotController {
	/**
	 * A boolean value representing whether a simulated connection is active
	 */
	private boolean connected = false;

	private final simulator.objects.Robot simRobot;

	public SimulatorRobot(RobotType type, final simulator.objects.Robot simRobot) {
		super(type);

		this.simRobot = simRobot;
	}

	public void setPower(float powerInput) {
		simRobot.setPower(powerInput);
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
		try {
			SafeSleep.sleep(25);
			simRobot.setSpeed(0, 0);
			SafeSleep.sleep(25);
			return 0;
		} catch (InterruptedException e) {
			return -2;
		}
	}

	/**
	 * Simulates a kick command for the robot
	 */
	@Override
	public int kick() {
		try {
			SafeSleep.sleep(25);
			simRobot.kick();
			SafeSleep.sleep(25);
			return 0;
		} catch (InterruptedException e) {
			return -2;
		}
	}

	/**
	 * Simulates a move command for the robot
	 */
	@Override
	public int move(int speedX, int speedY) {
		try {
			SafeSleep.sleep(25);
			simRobot.setSpeed(speedX, speedY);
			SafeSleep.sleep(25);
			return 0;
		} catch (InterruptedException e) {
			return -2;
		}
	}

	/**
	 * Simulates a rotate command for the robot
	 */
	@Override
	public int rotate(int angleDeg) {
		try {
			SafeSleep.sleep(25);
			simRobot.rotate(Math.toRadians(angleDeg));
			SafeSleep.sleep(25);
			return 0;
		} catch (InterruptedException e) {
			return -2;
		}
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

	@Override
	public void forcequit() {
		connected = false;
	}

	@Override
	public int dribble(int direction) {
		simRobot.activateDribbler();
		return 0;
	}

	@Override
	public int stopdribble() {
		simRobot.deactivateDribbler();
		return 0;
	}

	@Override
	public int arc(int l, int r) {
		// TODO Auto-generated method stub
		return 0;
	}
}

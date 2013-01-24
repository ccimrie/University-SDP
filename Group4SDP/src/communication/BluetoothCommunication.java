package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * Provides communication between PC and Robot
 * 
 * @author Sarun Gulyanon
 * @author Richard Kenyon
 * @author Nikolay Bogoychev
 */
public class BluetoothCommunication {

	public static final int[] ROBOT_READY = { 0, 0, 0, 0 };
	private InputStream in;
	private OutputStream out;
	private NXTComm nxtComm;
	private NXTInfo nxtInfo;
	private boolean isRobotReady = false;
	private boolean isConnected = false;

	/**
	 * @param deviceName
	 *            The name of the Bluetooth device
	 * @param deviceMACAddress
	 *            The MAC address of the Bluetooth device
	 */
	public BluetoothCommunication(String deviceName, String deviceMACAddress) {
		nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, deviceName,
				deviceMACAddress);
	}

	/**
	 * Returns true if the server is connected to the robot, returns false
	 * otherwise
	 * 
	 * @return a boolean indicating whether the server is connected to the robot
	 *         or not
	 */
	public boolean hasConnection() {
		return isConnected;
	}

	/**
	 * Receive a byte from the robot
	 * 
	 * @return An integer containing the byte we received from the robot
	 * 
	 * @throws IOException
	 *             when fail to receive a byte from robot
	 */
	public int[] receiveFromRobot() throws IOException {
		byte[] res = new byte[4];
		in.read(res);
		int[] ret = { (int) (res[0]), (int) (res[1]), (int) (res[2]),
				(int) (res[3]) };
		return ret;
	}

	/**
	 * Returns whether the robot is ready to receive data or not. Always check
	 * that the robot is ready before sending any commands.
	 */
	public boolean isRobotReady() {
		return isRobotReady;
	}

	/**
	 * Send 4 byte commands to the robot.
	 * 
	 * @param comm
	 *            - int [] with 4 elements, first element is the opcode the rest
	 *            are options that can be passed.
	 * @throws IOException
	 *             when fail to send command to robot
	 */

	public void sendToRobot(int[] comm) throws IOException {

		byte[] command = { (byte) comm[0], (byte) comm[1], (byte) comm[2],
				(byte) comm[3] };

		out.write(command);
		out.flush();
	}

	/**
	 * Opens a new Bluetooth connection and connects the input and output
	 * streams to this new Bluetooth connection
	 * 
	 * @throws IOException
	 *             when we fail to open the Bluetooth connection
	 */
	public void openBluetoothConnection() throws IOException {
		try {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		} catch (NXTCommException e) {
			System.err.println("Could not create connection: " + e.toString());
		}

		System.out.println("Attempting to connect to robot...");

		try {
			nxtComm.open(nxtInfo);
			in = nxtComm.getInputStream();
			out = nxtComm.getOutputStream();

			while (true) {
				int[] res = receiveFromRobot();
				boolean equals = true;
				for (int i = 0; i < 4; i++) { // wait for ready signal
					if (res[i] != ROBOT_READY[i]) {
						equals = false;
						break;
					}
				}
				if (equals) {
					break;
				} else {
					Thread.sleep(10); // Prevent 100% cpuusage
				}
			}

			isRobotReady = true;
			System.out.println("Robot is ready!");
			isConnected = true;
		} catch (NXTCommException e) { // (NXTCommException |
										// InterruptedException e) {
			throw new IOException("Failed to connect " + e.toString());

		} catch (InterruptedException e) { 
			throw new IOException("Failed to connect " + e.toString());
		}
	}

	/**
	 * Closes the Bluetooth connection and closes the input and output streams
	 */
	public void closeBluetoothConnection() {
		try {
			isConnected = false;
			in.close();
			out.close();
			nxtComm.close();
		} catch (IOException e) {
			System.err.println("Couldn't close Bluetooth connection: "
					+ e.toString());
		}
	}
}

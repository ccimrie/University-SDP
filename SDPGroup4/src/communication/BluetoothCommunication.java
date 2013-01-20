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
 */
public class BluetoothCommunication {

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
	public int receiveByteFromRobot() throws IOException {
		return in.read();
	}

	/**
	 * Returns whether the robot is ready to receive data or not. Always check
	 * that the robot is ready before sending any commands.
	 */
	public boolean isRobotReady() {
		return isRobotReady;
	}

	/**
	 * Send fixed-sized byte command to robot. Array of bytes consist of opcode
	 * and its parameter
	 * 
	 * @param command
	 *            - opcode concatenate to parameter
	 * 
	 * @throws IOException
	 *             when fail to send command to robot
	 */
	public void sendToRobot(byte[] command) throws IOException,
			IllegalArgumentException {
		if (command.length != Constants.COMMAND_SIZE) {
			throw new IllegalArgumentException("Command has wrong length "
					+ "(Expected " + Constants.COMMAND_SIZE + " byte command)");
		}

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

			while ((receiveByteFromRobot()) != Constants.ROBOT_READY) {
				; // wait for ready signal
			}

			isRobotReady = true;
			System.out.println("Robot is ready!");
			isConnected = true;
		} catch (NXTCommException e) {
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
			System.err.println("Couldn't close Bluetooth connection: " +
					e.toString());
		}
	}
}

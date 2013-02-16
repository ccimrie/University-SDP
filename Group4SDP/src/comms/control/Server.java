package comms.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTInfo;

import comms.control.Protocol;
import comms.control.Protocol.CommandPacket;

public class Server implements ServerInterface
{
	private static final String robotName = "GROUP11";
	private static final String robotMAC = "00:16:53:08:E0:69";
	private static final Server instance = new Server();
	
	private Server() {

	}

	private boolean isConnected = false;
	public boolean isConnected() { return isConnected; }

	// To deal with delay in commands to/from the robot:
	// This will be set true from within this class when a movement command is sent
	// It will only be set false when a status update comes from the robot
	private boolean isMoving = false;
	private boolean isStalled = false;
	private boolean isTouchPressed = false;
	
	private NXTComm nxtComm;
	private OutputStream os;
	private InputStream is;
	private Double defaultTravelSpeed;
	private Double defaultRotateSpeed;

	public static Server getInstance() {
		return instance;
	}

    public static void main(String[] args) {
    	// This starts an infinite loop for the server
    }

	public void connect() {
		if (isConnected) return;
		
		new Thread() {
			public void run() {
				NXTInfo ni = new NXTInfo(NXTCommFactory.BLUETOOTH, robotName, robotMAC);
				try {
					nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
					nxtComm.open(ni);
				} catch (NXTCommException e) {
					e.printStackTrace();
					// TODO: Stop exiting if we can't connect
					System.exit(1);
				}

				os = nxtComm.getOutputStream();
				is = nxtComm.getInputStream();

				setDefaultTravelSpeed(0.5d);
				setDefaultRotateSpeed(180d);

				System.out.println("Connected to robot!");
				isConnected = true;

				try {
					while (isConnected) {
						byte[] buffer = new byte[Protocol.MAX_PACKET_LENGTH];
						int readCount = is.read(buffer);
						if (readCount > 0) {
							CommandPacket packet = new CommandPacket(buffer);
							processCommand(packet);
						} else if (readCount < 0) {
							switch (readCount) {
							case -1:
								break;
							case -2:
								// TODO: DATA LOST!
								System.out.println("Read: -2");
								break;
							case -3:
								// TODO: Other (?)
								System.out.println("Read: -3");
								break;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void disconnect() {
		if (isConnected) {
			isConnected = false;
			try {
				nxtComm.close();
				System.out.println("Disconnected from robot.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void processCommand(CommandPacket packet) {
		switch (packet.command) {
		case Protocol.STATUS_MOVING:
			isMoving = (Boolean)packet.arguments[0];
			break;
		case Protocol.STATUS_STALLED:
			isStalled = (Boolean)packet.arguments[0];
			break;
		case Protocol.STATUS_TOUCH_PRESSED:
			isTouchPressed = (Boolean)packet.arguments[0];
			break;
		default:
			// TODO: Unknown command
		}
	}

	public boolean quit() {
		return sendToRobot(Protocol.QUIT);
	}
	
	public boolean stop() {
		return sendToRobot(Protocol.STOP);
	}
	
	public void setDefaultTravelSpeed(Double speed) {
		this.defaultTravelSpeed = speed;
	}
	
	public boolean setTravelSpeed(Double speed) {
		return sendToRobot(Protocol.SET_TRAVEL_SPEED, convertUnits(speed));
	}

	public boolean travel(Double distance) {
		return travel(distance, defaultTravelSpeed);
	}
	
	public boolean travel(Double distance, Double speed) {
		if (!setTravelSpeed(speed))
			return false;
		
		isMoving = true;
		return sendToRobot(Protocol.TRAVEL, convertUnits(distance));
	}
	
	public boolean kick() {
		return sendToRobot(Protocol.KICK);
	}
	
	public void setDefaultRotateSpeed(Double speed) {
		defaultRotateSpeed = speed;
	}
	
	public boolean rotate(Double angle) {
		return rotate(angle, defaultRotateSpeed);
	}
	
	public boolean rotate(Double angle, Double speed) {
		if (!sendToRobot(Protocol.SET_ROTATE_SPEED, speed))
			return false;

		isMoving = true;
		return sendToRobot(Protocol.ROTATE, angle);
	}
	
	public boolean forward() {
		return forward(defaultTravelSpeed);
	}
	
	public boolean forward(Double speed) {
		if (!setTravelSpeed(speed))
			return false;

		isMoving = true;
		return sendToRobot(Protocol.FORWARD);
	}

	public boolean backward() {
		return backward(defaultTravelSpeed);
	}

	public boolean backward(Double speed) {
		if (!setTravelSpeed(speed))
			return false;

		isMoving = true;
		return sendToRobot(Protocol.BACKWARD);
	}

	public boolean arcForward(Double radius) {
		return arcForward(radius, defaultTravelSpeed);
	}

	// Positive radius will arc right, negative arcs left
	public boolean arcForward(Double radius, Double speed) {
		if (!setTravelSpeed(speed))
			return false;

		isMoving = true;
		return sendToRobot(Protocol.ARC_FORWARD, convertUnits(radius));
	}
	
	public boolean arcBackward(Double radius) {
		return arcBackward(radius, defaultTravelSpeed);
	}

	// Positive radius will arc right, negative arcs left
	public boolean arcBackward(Double radius, Double speed) {
		if (!setTravelSpeed(speed))
			return false;

		isMoving = true;
		return sendToRobot(Protocol.ARC_BACKWARD, convertUnits(radius));
	}

	public boolean setAcceleration(Double acceleration) {
		return sendToRobot(Protocol.SET_ACCELERATION, convertUnits(acceleration));
	}
	
	private double convertUnits(Double value) {
		// Convert metres to millimetres
		return value * 1000f;
	}

	private boolean sendToRobot(byte command, Object ... arguments) {
		if (!isConnected)
			return false;
		try {
			os.write(new Protocol.CommandPacket(command, arguments).toByteArray());
			os.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean isMoving() {
		return this.isMoving;
	}
	
	public boolean isStalled() {
		return this.isStalled;
	}
	
	public boolean isTouchPressed() {
		return this.isTouchPressed;
	}


}

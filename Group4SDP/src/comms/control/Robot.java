package comms.control;

import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.navigation.*;

import comms.control.Protocol;
import comms.control.Protocol.CommandPacket;

public class Robot
{
	private boolean running = false;
	private boolean connected = false;
	private boolean isMoving = false;
	private boolean isStalled = false;
	private boolean isTouchPressed = false;

	private NXTConnection connection;
	private DifferentialPilot pilot;
	private TouchSensor touchSensor = new TouchSensor(SensorPort.S2);

	public static void main(String[] args)
	{
		Robot robot = new Robot();
		robot.init();
		robot.run();
	}

	private void init() {
		// Wheel size: 81.6mm (diameter) x 15mm (width)
		// Track width: 103mm
		// All leJOS arguments should use millimetres as units
		pilot = new DifferentialPilot(81.6f, 116.0f, Motor.B, Motor.C, true);
        pilot.setTravelSpeed(0.5f);
	}

	private void run() {
		running = true;
		while (running) {
			if (Button.ESCAPE.isPressed()) {
				running = false;
				break;
			}

			if (!connected) {
				LCD.drawString("Waiting...", 0, 2);
				LCD.drawString("Please connect", 0, 3);

				// TODO: Would be nice to thread this (or find a non-blocking option)
				//       so we can press escape while waiting for a connection
				connection = Bluetooth.waitForConnection();
				connected = true;

				showString("Connected!");
			}

			// Receive command packets
			byte[] buffer = new byte[Protocol.MAX_PACKET_LENGTH];
			int readCount = connection.readPacket(buffer, Protocol.MAX_PACKET_LENGTH);
			if (readCount > 0) {
				CommandPacket packet = new CommandPacket(buffer);
				processCommand(packet);
			} else if (readCount < 0) {
				switch (readCount) {
				case -1:
					connected = false;
					pilot.stop();
					break;
				case -2:
					// TODO: DATA LOST!
					break;
				case -3:
					// TODO: Other (?)
					break;
				}
			}

			// Check our state and update the server if necessary
			if (isMoving != pilot.isMoving()) {
				sendCommand(new CommandPacket(Protocol.STATUS_MOVING, pilot.isMoving()));
				isMoving = pilot.isMoving();
			}
			
			if (isStalled != pilot.isStalled()) {
				sendCommand(new CommandPacket(Protocol.STATUS_STALLED, pilot.isStalled()));
				isStalled = pilot.isStalled();
			}

			if (isTouchPressed != touchSensor.isPressed()) {
				sendCommand(new CommandPacket(Protocol.STATUS_TOUCH_PRESSED, touchSensor.isPressed()));
				isTouchPressed = touchSensor.isPressed();
			}
		}
	}

	private void processCommand(CommandPacket packet) {
		switch (packet.command) {
		case Protocol.QUIT:
			running = false;
			break;

		case Protocol.STOP:
			showString("STOP");
			pilot.stop();
			break;

		case Protocol.TRAVEL:
			showString("TRAVEL " + packet.arguments[0]);
			pilot.travel((Double)packet.arguments[0], true);
			break;

		case Protocol.ROTATE:
			showString("ROTATE " + packet.arguments[0]);
			// Reversed for leJOS. A positive angle should rotate clockwise
			pilot.rotate(-1 * (Double)packet.arguments[0], true);
			break;

		case Protocol.KICK:
			showString("KICK");
			Motor.A.setSpeed(720);
			Motor.A.rotate(60);
			Motor.A.rotate(-60);
			Motor.A.stop();
			break;

		case Protocol.SET_TRAVEL_SPEED:
			showString("SET_TRAVEL_SPEED " + packet.arguments[0]);
			pilot.setTravelSpeed((Double)packet.arguments[0]);
			break;

		case Protocol.SET_ROTATE_SPEED:
			showString("SET_ROTATE_SPEED " + packet.arguments[0]);
			pilot.setRotateSpeed((Double)packet.arguments[0]);
			break;

		case Protocol.FORWARD:
			showString("FORWARD");
			pilot.forward();
			break;

		case Protocol.BACKWARD:
			showString("BACKWARD");
			pilot.backward();
			break;

		case Protocol.ARC_FORWARD:
			showString("ARC_FORWARD " + packet.arguments[0]);
			pilot.arcForward((Double)packet.arguments[0]);
			break;

		case Protocol.ARC_BACKWARD:
			showString("ARC_BACKWARD " + packet.arguments[0]);
			pilot.arcBackward((Double)packet.arguments[0]);
			break;

		case Protocol.SET_ACCELERATION:
			showString("SET_ACCEL");
			pilot.setAcceleration(((Double)packet.arguments[0]).intValue());
			break;

		default:
			showString("Received unrecognised command");
		}
	}

	private void sendCommand(CommandPacket packet) {
		byte[] buf = packet.toByteArray();
		connection.sendPacket(buf, buf.length);
	}

	private static void showString(String str) {
		LCD.clear();
		LCD.drawString(str, 0, 2);
	}
}

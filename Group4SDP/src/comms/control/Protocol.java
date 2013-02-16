package comms.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Protocol {
	public static final int MAX_PACKET_LENGTH = 255;
	private static final int ARG_DOUBLE =		1;
	private static final int ARG_BOOL =			2;
	// Input commands
	public static final byte QUIT =				1;
	public static final byte STOP =				2;
	public static final byte TRAVEL =			3;
	public static final byte ROTATE =			4;
	public static final byte KICK =				5;
	public static final byte SET_TRAVEL_SPEED =	6;
	public static final byte SET_ROTATE_SPEED =	7;
	public static final byte FORWARD =			8;
	public static final byte ARC_FORWARD =		9;
	public static final byte SET_ACCELERATION = 10;
	public static final byte BACKWARD =			11;
	public static final byte ARC_BACKWARD =		12;
	// Output commands
	public static final byte STATUS_MOVING =	100;
	public static final byte STATUS_STALLED =	101;
	public static final byte STATUS_TOUCH_PRESSED = 102;

	
	public static class CommandPacket {
		public byte command;
		public Object[] arguments;

		public CommandPacket(byte command, Object ... arguments) {
			for (Object arg : arguments) {
				if (!supportedArgument(arg)) {
					throw new IllegalArgumentException();
				}
			}
			this.command = command;
			this.arguments = arguments;
		}

		public CommandPacket(byte[] buffer) {
			try {
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buffer));
				command = dis.readByte();
				byte argCount = dis.readByte();
				this.arguments = new Object[argCount];
				for (int i = 0; i < argCount; i++) {
					byte argType = dis.readByte();
					switch (argType) {
					case ARG_DOUBLE:
						arguments[i] = dis.readDouble();
						break;
					case ARG_BOOL:
						arguments[i] = dis.readBoolean();
						break;
					default:
						throw new IllegalArgumentException();
					}
				}
			} catch (IOException e) {
				throw new IllegalArgumentException();
			}
		}

		public byte[] toByteArray() {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);

			try {
				dos.writeByte(command);
				dos.writeByte((byte)arguments.length);
				for (Object argument : arguments) {
					if (argument instanceof Double) {
						dos.writeByte(ARG_DOUBLE);
						dos.writeDouble((Double)argument);
					} else if (argument instanceof Boolean) {
						dos.writeByte(ARG_BOOL);
						dos.writeBoolean((Boolean)argument);
					} else {
						// TODO: Error
					}
				}
				dos.flush();
			} catch (IOException e) {
				return null;
			}

			return bos.toByteArray();
		}

		public boolean supportedArgument(Object arg) {
			if (arg instanceof Double) return true;
			if (arg instanceof Boolean) return true;
			return false;
		}
	}
}

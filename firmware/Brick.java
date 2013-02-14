import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/**
 * Provides possible movement actions for the robot
 * 
 * @author Nikolay Bogoychev
 * @author Jakov Smelkin
 * 
 */
public class Brick {

	// IO control
	private static InputStream is;
	private static OutputStream os;

	// Command opcodes
	private final static int DO_NOTHING = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDS = 2;
	private final static int LEFT = 10;
	private final static int RIGHT = 11;
	private final static int MOVE = 12;
	private final static int SLOWMOVE = 13;
	private final static int STOP = 3;
	private final static int KICK = 4;
	private final static int QUIT = 5;
	private final static int ROTATE = 6;
	private final static int ROTATEMOVE = 7;
	private final static int TEST = 66;

	// I2C motor board
	private static Mux chip = new Mux(SensorPort.S1);

	// Motors
	private final static int FRONTMOTOR = 1;
	private final static int BACKMOTOR = 2;

	// Renaming motors for easier recognition and change implementation.
	private static NXTRegulatedMotor kicker = Motor.A;
	private static NXTRegulatedMotor leftMotor = Motor.B;
	private static NXTRegulatedMotor rightMotor = Motor.C;

	public static void main(String[] args) throws Exception {

		// Waiting for a connection and opening streams on success
		LCD.clear();
		LCD.drawString("Waiting for", 0, 2);
		LCD.drawString("Bluetooth", 0, 3);
		NXTConnection connection = Bluetooth.waitForConnection();
		is = connection.openInputStream();
		os = connection.openOutputStream();
		LCD.clear();
		LCD.drawString("Connected!", 0, 2);
		byte[] robotready = { 0, 0, 0, 0 };
		os.write(robotready);
		os.flush();

		// Begin reading commands
		int opcode = DO_NOTHING;
		int option1, option2, option3;

		while ((opcode != QUIT) && !(Button.ESCAPE.isDown())) {

			// Get the next command from the inputstream
			byte[] byteBuffer = new byte[4];
			is.read(byteBuffer);

			// We send 4 different numbers, use as options
			opcode = byteBuffer[0];
			option1 = byteBuffer[1];
			option2 = byteBuffer[2];
			option3 = byteBuffer[3];

			if (opcode > 0)
				LCD.drawString("opcode = " + opcode, 0, 2);
			switch (opcode) {

			case FORWARDS:
				LCD.clear();
				LCD.drawString("Forward!", 0, 2);
				LCD.refresh();
				move(0, option1);
				replytopc(opcode,os);
				break;

			case BACKWARDS:
				LCD.clear();
				LCD.drawString("Backward!", 0, 2);
				LCD.refresh();
				move(0, -option1);
				replytopc(opcode,os);
				break;

			case LEFT:
				LCD.clear();
				LCD.drawString("Left!", 0, 2);
				LCD.refresh();
				move(-option1, 0);
				replytopc(opcode,os);
				break;

			case RIGHT:
				LCD.clear();
				LCD.drawString("Right!", 0, 2);
				LCD.refresh();
				move(option1, 0);
				replytopc(opcode,os);
				break;

			case STOP:
				LCD.clear();
				LCD.drawString("Stopping!", 0, 2);
				LCD.refresh();
				stop();
				replytopc(opcode,os);
				break;

			case ROTATE:
				LCD.clear();
				LCD.drawString("Rotate!", 0, 2);
				LCD.refresh();
				// The angle is calculated by having option2*10 + option3
				rotate(option1, option2 * 10 + option3);
				replytopc(opcode,os);
				break;

			case KICK:
				LCD.clear();
				LCD.drawString("Kicking!", 0, 2);
				LCD.refresh();
				kick();
				replytopc(opcode,os);
				break;

			case MOVE:
				LCD.clear();
				LCD.drawString("Moving at an angle!", 0, 2);
				LCD.refresh();
				move(option1, option2);
				replytopc(opcode,os);
				break;
				
			case SLOWMOVE:
				LCD.clear();
				LCD.drawString("Moving at an angle!", 0, 2);
				LCD.refresh();
				slowmove(option1, option2);
				replytopc(opcode,os);
				break;
				
			case ROTATEMOVE:
				LCD.clear();
				LCD.drawString("Moving at an angle!", 0, 2);
				LCD.refresh();
				rotateMove(option1, option2, option3);
				replytopc(opcode,os);
				break;

			case TEST:
				boolean receiveTrue = ((opcode == 66) && (option1 == 0) && (option2 == 0) && (option3 == 66));
				String tmp = "TEST! " + receiveTrue;
				LCD.drawString(tmp, 0, 2);
				byte[] testres = new byte[] { 66, 77, 88, 99 };
				os.write(testres);
				os.flush();
				break;
			case QUIT: // Exit the loop, close connection
				// Sound.twoBeeps();
				break;
			}
		}

		// Closing streams and connection
		is.close();
		os.close();
		Thread.sleep(100); // Waiting for data to drain
		LCD.clear();
		LCD.drawString("Closing", 0, 2);
		LCD.refresh();
		connection.close();
		LCD.clear();
	}
	/**Replies to pc that we have received a package and finished its
	 * execution
	 * @param opcode the opcode we received.
	 * @param os - the Output stream for the brick.
	 * @throws IOException 
	 */
	
	public static void replytopc(int opcode, OutputStream os) throws IOException{
		byte [] reply = {111, (byte)opcode, 0,0};
		os.write(reply);
		os.flush();
	}
	
	/**
	 * Rotate at a direction
	 * 
	 * @param: dir 1 counterclockwise (left), 2 clockwise (right)
	 * 
	 */
	public static void rotate(int dir, int angle) throws InterruptedException {
		leftMotor.setAcceleration(2000);
		rightMotor.setAcceleration(2000);
		leftMotor.setSpeed(250);
		rightMotor.setSpeed(250);
		angle = (int) (angle * 2);
		chip.move(1, DO_NOTHING, 0);
		chip.move(2, DO_NOTHING, 0);

		switch (dir) {
		case 1:
			leftMotor.rotate(-angle, true);
			rightMotor.rotate(angle);

			break;
		case 2:
			leftMotor.rotate(angle, true);
			rightMotor.rotate(-angle);
			break;
		}
		stop();
	}

	public static void siderotate(int dir, int angle) throws InterruptedException {
		// Some code here to transform angle into time needed to rotate
		int sleeptime = 1000;

		switch (dir) {
		case 1:
			chip.move(1, FORWARDS, 230);
			chip.move(2, BACKWARDS, 230);
			break;
		case 2:
			chip.move(1, BACKWARDS, 230);
			chip.move(2, FORWARDS, 230);
			break;
		}
		Thread.sleep(sleeptime);
		chip.stop();
	}

	/**
	 * Move in a direction relative to the robot
	 * 
	 * @param x
	 *            The X coordinate. Positive is right, negative is left.
	 * @param y
	 *            The Y coordinate. Positive is forward, negative is backward.
	 * @throws InterruptedException
	 *             When sleeping of a thread is interrupted
	 */
	private static void move(int x, int y) throws InterruptedException {
		leftMotor.setAcceleration(2000);
		rightMotor.setAcceleration(2000);
		// Multiplying by 2, since byte only allows upto 127
		y = y * 6;
		x = x * 2;
		if (y > 0) {
			leftMotor.setSpeed(y);
			rightMotor.setSpeed(y);
			leftMotor.forward();
			rightMotor.forward();
		} else if (y < 0) {
			leftMotor.setSpeed(-y);
			rightMotor.setSpeed(-y);
			leftMotor.backward();
			rightMotor.backward();
		} else {
			leftMotor.flt();
			rightMotor.flt();
		}

		if (x > 0) {
			chip.move(FRONTMOTOR, 2, x);
			chip.move(BACKMOTOR, 1, x);
		} else if (x < 0) {
			chip.move(FRONTMOTOR, 1, -x);
			chip.move(BACKMOTOR, 2, -x);
		} else {
			chip.move(1, DO_NOTHING, 0);
			chip.move(2, DO_NOTHING, 0);
			chip.stop();
		}
	}
	
	/**
	 * Move slowly, same as the above method, except very slow
	 * in order to use it when we're near to the ball.
	 * 
	 * @param x
	 *            The X coordinate. Positive is right, negative is left.
	 * @param y
	 *            The Y coordinate. Positive is forward, negative is backward.
	 * @throws InterruptedException
	 *             When sleeping of a thread is interrupted
	 */
	private static void slowmove(int x, int y) throws InterruptedException {
		leftMotor.setAcceleration(2000);
		rightMotor.setAcceleration(2000);
		if (y > 0) {
			leftMotor.setSpeed(y + 250);
			rightMotor.setSpeed(y + 250);
			leftMotor.forward();
			rightMotor.forward();
		} else if (y < 0) {
			leftMotor.setSpeed(-y + 250);
			rightMotor.setSpeed(-y + 250);
			leftMotor.backward();
			rightMotor.backward();
		} else {
			leftMotor.flt();
			rightMotor.flt();
		}

		if (x > 0) {
			chip.move(FRONTMOTOR, 2, x);
			chip.move(BACKMOTOR, 1, x);
		} else if (x < 0) {
			chip.move(FRONTMOTOR, 1, -x);
			chip.move(BACKMOTOR, 2, -x);
		} else {
			chip.move(1, DO_NOTHING, 0);
			chip.move(2, DO_NOTHING, 0);
			chip.stop();
		}
	}

	/**
	 * Stops all motors, makes them float afterwards.
	 * 
	 * @throws InterruptedException
	 *             When sleeping of a thread is interrupted
	 */
	private static void stop() throws InterruptedException {
		leftMotor.stop(true);
		rightMotor.stop(true);
		chip.stop();
		Thread.sleep(10);
		leftMotor.flt(true);
		rightMotor.flt(true);
	}

	/**
	 * A simple kick. Brings back the kicker to initial position after the kick.
	 */
	private static void kick() {
		kicker.setSpeed(900);
		kicker.rotateTo(60);
		kicker.setSpeed(250);
		kicker.rotateTo(0);
		kicker.flt();
	}

	/**
	 * Rotates a certain angle while moving a certain distance.
	 * Wheel diameter: 64mm, Radius: 32mm
	 * Wheel distance from centre: 61mm
	 * Motor speeds:
	 * 28 = 502;
	 * 200 = 580;
	 * 2*pi*32=201.1~201mm;
	 * 360/201~=1.79
	 * (580-502) / (200-28)=0.45;
	 * setSpeed is in deg/s
	 * w is clockwise
	 * 
	 * @param vtx
	 *            speed in millimetres/second to the robots right (positive) or left (negative)
	 * @param vty
	 *            speed in millimetres/second forwards (positive) or backwards (negative)
	 * @param w
	 *            angular rotation speed (in degrees/second) clockwise (positive) or counterclockwise (negative)
	 */
	private static void rotateMove(int vtx, int vty, int w) {
		byte r = 61;
		// Front wheel
		int vf = (int) Math.rint((vtx + w * r) * 0.8);
		// Back wheel
		int vb = (int) Math.rint((vtx - w * r) * 0.8);
		// Left wheel
		int vl = (int) Math.rint((vty + w * r) * 1.79);
		// Right wheel
		int vr = (int) Math.rint((vty - w * r) * 1.79);

		if (vf > 0)
			chip.move(1, 2, vf);
		else
			chip.move(1, 1, vf);

		if (vb > 0)
			chip.move(2, 1, vf);
		else
			chip.move(2, 2, vf);

		leftMotor.flt();
		leftMotor.setSpeed(Math.abs(vl));
		rightMotor.flt();
		rightMotor.setSpeed(Math.abs(vl));
		if (vl > 0)
			leftMotor.forward();
		else
			leftMotor.backward();
		if (vr > 0)
			rightMotor.forward();
		else
			rightMotor.backward();

	}
}
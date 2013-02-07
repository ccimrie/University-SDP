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
	private static final int MOVE = 12;
	private final static int STOP = 3;
	private final static int KICK = 4;
	private final static int QUIT = 5;
	private final static int ROTATE = 6;
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
				move(0, 255);
				break;

			case BACKWARDS:
				LCD.clear();
				LCD.drawString("Backward!", 0, 2);
				LCD.refresh();
				move(0, -255);
				break;

			case LEFT:
				LCD.clear();
				LCD.drawString("Left!", 0, 2);
				LCD.refresh();
				move(-255, 0);
				break;

			case RIGHT:
				LCD.clear();
				LCD.drawString("Right!", 0, 2);
				LCD.refresh();
				move(255, 0);
				break;

			case STOP:
				LCD.clear();
				LCD.drawString("Stopping!", 0, 2);
				LCD.refresh();
				stop();
				break;

			case ROTATE:
				LCD.clear();
				LCD.drawString("Rotate!", 0, 2);
				LCD.refresh();
				//The angle is calculated by having option2*10 + option3
				rotate(option1, option2*10 + option3);
				break;

			case KICK:
				LCD.clear();
				LCD.drawString("Kicking!", 0, 2);
				LCD.refresh();
				kick();
				break;

			case MOVE:
				LCD.clear();
				LCD.drawString("Moving at an angle!", 0, 2);
				LCD.refresh();
				move(option1, option2);
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
	/**
	 * Rotate at a direction
	 * 
	 * @param: dir 1 counterclockwise (left), 2 clockwise (right)
	 * 
	 */
	public static void rotate(int dir, int angle) throws InterruptedException {
		leftMotor.setAcceleration(2000);
		rightMotor.setAcceleration(2000);
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		angle= (int) (angle * 2);
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
	
	public static void siderotate(int dir, int angle) throws InterruptedException{
		//Some code here to transform angle into time needed to rotate
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
		if (y > 0) {
			leftMotor.setSpeed(y + 500);
			rightMotor.setSpeed(y + 500);
			leftMotor.forward();
			rightMotor.forward();
		} else if (y < 0) {
			leftMotor.setSpeed(-y + 500);
			rightMotor.setSpeed(-y + 500);
			leftMotor.backward();
			rightMotor.backward();
		} else {
			leftMotor.flt();
			rightMotor.flt();
		}

		if (x > 0) {
			x = x*2; //Get the maximum speed out of the mux board
			chip.move(FRONTMOTOR, 2, x);
			chip.move(BACKMOTOR, 1, x);
		} else if (x < 0) {
			x = x*2; //Get the maximum speed out of the mux board
			chip.move(FRONTMOTOR, 1, -x);
			chip.move(BACKMOTOR, 2, -x);
		} else {
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

}
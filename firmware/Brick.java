import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class Brick {

	private static InputStream is;
	private static OutputStream os;
	private static volatile boolean blocking = false;
	private static volatile boolean kicking = false;

	// State(Direction): 0 - float, 1 - Forward, 2 - Backwards.
	private static int stateMM1 = 0;
	private static int stateMM2 = 0;

	// Command decoding. Most are not implemented yet
	private final static int DO_NOTHING = 0;

	private final static int FORWARDS = 1;
	private final static int BACKWARDS = 2;
	private final static int LEFT = 10;
	private final static int RIGHT = 11;	
	private static final int ANGLEMOVE = 12;

	private final static int STOP = 3;
	private final static int KICK = 4;
	private final static int QUIT = 5;
	private final static int ROTATE = 6;
	private final static int TRAVEL_BACKWARDS_SLIGHTLY = 7;
	private final static int TRAVEL_ARC = 8;
	private final static int ACCELERATE = 9;
	private final static int TEST = 66;
	private static Mux chip = new Mux(SensorPort.S1); // i2c motor board

	public static void main(String[] args) throws Exception {

		// Wait for a connection and open streams on success
		LCD.clear();
		LCD.drawString("Waiting...", 0, 2);
		LCD.drawString("Please connect", 0, 3);
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

				case TEST:
					boolean receiveTrue = ((opcode == 66) && (option1 == 0) && (option2 == 0) && (option3 == 66));
					String tmp = "TEST! " + receiveTrue;
					LCD.drawString(tmp, 0, 2);
					byte[] testres = new byte[] { 66, 77, 88, 99 };
					os.write(testres);
					os.flush();
					break;

				case FORWARDS:
					LCD.clear();
					LCD.drawString("Forward!", 0, 2);
					LCD.refresh();
					//int speed1 = option1*10;
					//int speed2 = option2*10;
					mainMotor1(FORWARDS,5);
					mainMotor2(FORWARDS,5);
					Thread.sleep(10);
					mainMotor1(FORWARDS, 700 + option1); //Left motor
					mainMotor2(FORWARDS, 700 + option2);
					break;

				case BACKWARDS:
					LCD.clear();
					LCD.drawString("Backward!", 0, 2);
					LCD.refresh();
					//while(something)
					mainMotor1(BACKWARDS, 600);
					mainMotor2(BACKWARDS, 600);
					break;

				case LEFT:
					LCD.clear();
					LCD.drawString("Left!", 0, 2);
					LCD.refresh();
					chip.sideMotor1(1, 255);
					chip.sideMotor2(2, 255);
					break;

				case RIGHT:
					LCD.clear();
					LCD.drawString("Right!", 0, 2);
					LCD.refresh();
					chip.sideMotor1(2, 255);
					chip.sideMotor2(1, 255);
					break;

				case STOP:
					stopMainMotor2();
					stopMainMotor1();
					chip.sm1stop();
					chip.sm2stop();
					Thread.sleep(10);
					Motor.A.flt(); //Make the motors not waste battery
					Motor.B.flt();//After they stop moving.
					break;

				case ROTATE:
					LCD.clear();
					LCD.drawString("Rotate!", 0, 2);
					LCD.refresh(); //We get the angle by adding option1+option2 If it is greater than 0 we rotate clockwise
					//Otherwise we rotate counterclockwise.
					rotate(option1+option2);
					break;

				case KICK:
					LCD.clear();
					LCD.drawString("Kicking", 0, 2);
					LCD.refresh();					
					Motor.C.setSpeed(900);
					Motor.C.rotateTo(60);
					Motor.C.setSpeed(250);
					Motor.C.rotateTo(0);
					break;
				case ANGLEMOVE:
					LCD.clear();
					LCD.drawString("Moving at an angle", 0, 2);
					LCD.refresh();
					byte dirMain= (byte)(((option3 & 2) > 0) ? FORWARDS : BACKWARDS);
					byte dirSide= (byte)(((option3 & 1) > 0) ? FORWARDS : BACKWARDS);
					mainMotor1(dirMain, 700 + option1); //Left motor
					mainMotor2(dirMain, 700 + option1);
					chip.sideMotor1(dirSide, option2);
					chip.sideMotor2((dirSide == FORWARDS) ? BACKWARDS : FORWARDS, option2);
					break;

				case QUIT: // close connection
					// Sound.twoBeeps();
					break;
			}
		}
		// close streams and connection
		is.close();
		os.close();
		Thread.sleep(100); // wait for data to drain
		LCD.clear();
		LCD.drawString("closing", 0, 2);
		LCD.refresh();
		connection.close();
		LCD.clear();
	}

	// Methods to activate each of the main motors individually
	public static void mainMotor1(int direction, int speed) {
		Motor.A.setSpeed(speed);
		switch (direction) {
			case FORWARDS:
				Motor.A.forward();
				stateMM1 = 1;
				break;
			case BACKWARDS:
				Motor.A.backward();
				stateMM1 = 2;
				break;
		}
	}

	public static void mainMotor2(int direction, int speed) {
		Motor.B.setSpeed(speed);
		switch (direction) {
			case FORWARDS:
				Motor.B.forward();
				stateMM2 = 1;
				break;
			case BACKWARDS:
				Motor.B.backward();
				stateMM2 = 1;
				break;
		}
	}

	// Methods for stopping each of the main motors individually
	public static void stopMainMotor1() throws InterruptedException {
		if (stateMM1 != 0) {
			Motor.A.stop(true);
			stateMM1 = 0;
		}
	}

	public static void stopMainMotor2() throws InterruptedException {
		if (stateMM2 != 0) {
			Motor.B.stop(true);
			stateMM2 = 0;
		}
	}
	
	public static void rotate(int angle) {
		if (angle>0){ //Rotate clockwise
			chip.sideMotor1(1, 255);
			chip.sideMotor2(1, 255);
			Motor.A.setSpeed(700);
			Motor.B.setSpeed(700);
			Motor.A.rotate(angle+15, true);
			Motor.B.rotate(-angle-15);
			chip.sideMotor1(0,0);
			chip.sideMotor2(0,0);
			Motor.A.flt(); //Make the motors not waste battery
			Motor.B.flt();
		} else { //Rotate couterclockwise
			chip.sideMotor1(2, 255);
			chip.sideMotor2(2, 255);
			Motor.A.setSpeed(700);
			Motor.B.setSpeed(700);
			Motor.A.rotate(angle-15, true);
			Motor.B.rotate(-angle+15);
			chip.sideMotor1(0,0);
			chip.sideMotor2(0,0);
			Motor.A.flt(); //Make the motors not waste battery
			Motor.B.flt();
		}
	}
	
	public static void maintainspeed(int option1, int option2) throws InterruptedException{
		while (true){
			Thread.sleep(20);
			if (Motor.A.getTachoCount() == Motor.B.getTachoCount()){
				mainMotor1(FORWARDS, 700 + option1); //Left motor
				mainMotor2(FORWARDS, 700 + option2);
			}else if(Motor.A.getTachoCount() > Motor.B.getTachoCount()){
				mainMotor1(FORWARDS, 700 + option1); //Left motor
				mainMotor2(FORWARDS, 700 + option2+5);
			}else{
				mainMotor1(FORWARDS, 700 + option1-5); //Left motor
				mainMotor2(FORWARDS, 700 + option2);
			}
		}
	}
}

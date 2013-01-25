
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class Brick {
	// class variables

	private static InputStream is;
	private static OutputStream os;
	private static volatile boolean blocking = false;
	private static volatile boolean kicking = false;
	//Variable to keep track of moving motors. 0 - not moving
	//1 forwards, 2 backwards
	private static int ismovingmm1 = 0;
	private static int ismovingmm2 = 0;

	// Command decoding. Most are not implemented yet

	private final static int DO_NOTHING = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDS=2;
	private final static int LEFT=10;
	private final static int RIGHT=11;
	private final static int STOP = 3;
	private final static int KICK = 4;
	private final static int QUIT = 5;
	private final static int FORWARDS_TRAVEL = 6;
	private final static int TRAVEL_BACKWARDS_SLIGHRLY = 7;
	private final static int TRAVEL_ARC = 8;
	private final static int ACCELERATE = 9;
	private final static int TEST = 66;

	public static void main(String[] args) throws Exception {
		Mux chip = new Mux(SensorPort.S1); //i2c motor board

		// wait for a connection and open streams
		LCD.clear();
		LCD.drawString("Waiting...", 0, 2);
		LCD.drawString("Please connect", 0, 3);
		NXTConnection connection = Bluetooth.waitForConnection();
		is = connection.openInputStream();
		os = connection.openOutputStream();
		LCD.clear();
		LCD.drawString("Connected!", 0, 2);
		byte [] robotready = {0,0,0,0};
		os.write(robotready);
		os.flush();

		// begin reading commands
		int opcode = DO_NOTHING;
		int option1, option2, option3;

		while (opcode != QUIT) {
			// get the next command from the inputstream
			byte[] byteBuffer = new byte[4];
			is.read(byteBuffer);
			// We send 4 different numbers, use as options
			opcode = (int)byteBuffer[0];
			option1 = (int)byteBuffer[1];
			option2 = (int)byteBuffer[2];
			option3 = (int)byteBuffer[3];

			if (opcode > 0)
				LCD.drawString("opcode = " + opcode, 0, 2);
			switch (opcode) {

			case TEST:
				boolean receivetrue = ((opcode == 66) && (option1 == 0) && (option2 == 0) && (option3 == 66));
				String tmp = "TEST! " + receivetrue;
				LCD.drawString(tmp, 0, 2);
				byte [] testres = new byte [] {66,77,88,99};
				os.write(testres);
				os.flush();
				break;

			case FORWARDS:
				LCD.clear();
				LCD.drawString("Forward!", 0, 2);
				LCD.refresh();
				mainmotor1(FORWARDS, 600);
				mainmotor2(FORWARDS, 600);
				break;

			case BACKWARDS:
				LCD.clear();
				LCD.drawString("Backward!", 0, 2);
				LCD.refresh();
				mainmotor1(BACKWARDS, 600);
				mainmotor2(BACKWARDS, 600);
				break;

			case LEFT:
				LCD.clear();
				LCD.drawString("Left!", 0, 2);
				LCD.refresh();
				chip.sidemotor2(1,255);
				chip.sidemotor1(1,255);
				break;

			case RIGHT:
				LCD.clear();
				LCD.drawString("Right!", 0, 2);
				LCD.refresh();
				chip.sidemotor2(2,255);
				chip.sidemotor1(2,255);
				break;

			case STOP:
				stopmainmotor2();
				stopmainmotor1();
				chip.sm1stop();
				chip.sm2stop();
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
				
			case ROTATE:
				//TODO
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
	//Methods to activate each of the main motors individually
	public static void mainmotor1(int direction, int speed){
		Motor.A.setSpeed(speed);
		switch (direction){
		case FORWARDS:
			Motor.A.forward();
			ismovingmm1 = 1;
			break;
		case BACKWARDS:
			Motor.A.backward();
			ismovingmm1 = 2;
			break;
		}
	}
	public static void mainmotor2(int direction, int speed){
		Motor.B.setSpeed(speed);
		switch (direction){
		case FORWARDS:
			Motor.B.forward();
			ismovingmm2 = 1;
			break;
		case BACKWARDS:
			Motor.B.backward();
			ismovingmm2 = 1;
			break;
		} 
	}
	//Methods for stopping each of the main motors individually
	public static void stopmainmotor1(){
		if (ismovingmm1 != 0) {
			Motor.A.stop(true);
			ismovingmm1 = 0;
		}
	}
	public static void stopmainmotor2(){
		if (ismovingmm2 != 0){
			Motor.B.stop(true);
			ismovingmm2 = 0;
		}
	}
}

//package firmware;

import java.io.InputStream;
import java.io.OutputStream;
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

	// Command decoding. Most are not implemented yet

	private final static int DO_NOTHING = 0X00;
	private final static int FORWARDS = 0X01;
	private final static int BACKWARDS=0x02;
	private final static int STOP = 0X03;
	private final static int KICK = 0X04;
	private final static int QUIT = 0X05;
	private final static int FORWARDS_TRAVEL=0X06;
	private final static int TRAVEL_BACKWARDS_SLIGHRLY=0X07;
	private final static int TRAVEL_ARC=0X08;
	private final static int ACCELERATE=0X09;


	private final static int ROTATE = 0X0A; 
	private final static int EACH_WHEEL_SPEED=0X0B;
	private final static int STEER =0X0C;

	public static void main(String[] args) throws Exception {
		while (true) {

			// wait for a connection and open streams
			LCD.clear();
			LCD.drawString("Waiting...", 0, 2);
			LCD.drawString("Please connect", 0, 3);
			NXTConnection connection = Bluetooth.waitForConnection();
			is = connection.openInputStream();
			os = connection.openOutputStream();
			LCD.clear();
			LCD.drawString("Connected!", 0, 2);

			// begin reading commands
			int n = DO_NOTHING;

			while (n != QUIT) {
				// get the next command from the inputstream
				byte[] byteBuffer = new byte[4];
				is.read(byteBuffer);
				// We send 4 different numbers, use as options
				int opcode = (int)byteBuffer[0];
				int option1 = (int)byteBuffer[1];
				int option2 = (int)byteBuffer[2];
				int option3 = (int)byteBuffer[3];
				
				//n = byteArrayToInt(byteBuffer);
				//int opcode = ((n << 24) >> 24);
				//if it doesn't work try with the method byteArrayToInt
				//that group 5 had and the hack above
				
				if (opcode > 0)
					LCD.drawString("opcode = " + opcode, 0, 2);
				switch (opcode) {

				case FORWARDS:
					int speedForward = n>>8;
					LCD.clear();
					LCD.drawString("move forwards", 0, 2);
					//LCD.drawInt((int) pilot.getMaxTravelSpeed(), 0,4);
					LCD.refresh();
					Motor.A.setSpeed(600);
					Motor.B.setSpeed(600);
					Motor.A.forward();
					Motor.B.forward();
					Thread.sleep (1000);
					Motor.A.stop();
					Motor.B.stop();
					break;

				/*case FORWARDS_TRAVEL:
					int speedForwards = n>>8;
					int travelDistance= n >>8;
					LCD.clear();
					LCD.drawString("move forwards whith speed", 0, 2);
					//LCD.drawInt((int) pilot.getMaxTravelSpeed(), 0,4);
					LCD.refresh();
					pilot.setTravelSpeed(speedForwards);
					pilot.travel(travelDistance);
					break;*/

				/*case TRAVEL_BACKWARDS_SLIGHRLY:	
					LCD.clear();
					LCD.drawString("travel back a little bit", 0, 2);
					//LCD.drawInt((int) pilot.getMaxTravelSpeed(), 0,4);
					LCD.refresh();
					pilot.travel(-10);
					break;*/

				/*case BACKWARDS:
					int speedBackward = n>>8 ;
					LCD.clear();
					LCD.drawString("move backwards", 0, 2);
					//LCD.drawInt((int) pilot.getMaxTravelSpeed(), 0,4);
					LCD.refresh();
					pilot.backward();
					pilot.setTravelSpeed(speedBackward);
					break;*/
					
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
			break;
		}		
	}
	// Code for decoding the byte array, taken from stackoverflow
	// http://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java
	public static int byteArrayToInt(byte[] b) 
	{
	    return  (b[3] & 0xFF) |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
}

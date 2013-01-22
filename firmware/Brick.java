
import java.io.InputStream;
import java.io.OutputStream;

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

	// Command decoding. Most are not implemented yet

	private final static int DO_NOTHING = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDS=2;
	private final static int STOP = 3;
	private final static int KICK = 4;
	private final static int QUIT = 5;
	private final static int FORWARDS_TRAVEL = 6;
	private final static int TRAVEL_BACKWARDS_SLIGHRLY = 7;
	private final static int TRAVEL_ARC = 8;
	private final static int ACCELERATE = 9;
	private final static int SIDEWAYS=10;
	private final static int TEST = 66;

	public static void main(String[] args) throws Exception {
		//SensorPort.S1.i2cEnable(1);
		Mux chip = new Mux(SensorPort.S1);
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
			byte [] robotready = {0,0,0,0};
			os.write(robotready);
			os.flush();

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
				
				case TEST:
					boolean receivetrue = ((opcode == 66) && (option1 == 0) && (option2 == 0) && (option3 == 66));
					String tmp = "TEST! " + receivetrue;
					LCD.drawString(tmp, 0, 2);
					byte [] testres = new byte [] {66,77,88,99};
					os.write(testres);
					os.flush();
					break;

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
					Thread.sleep (2000);
					Motor.A.stop();
					Motor.B.stop();
					break;
					
				case SIDEWAYS:
					LCD.clear();
					LCD.drawString("Sideways", 0, 2);
					LCD.refresh();
					chip.sidemotor1(1,127);
					chip.sidemotor2(1,127);
					Thread.sleep(2000);
					chip.sidemotor1(0,0);
					chip.sidemotor2(0, 0);
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
}

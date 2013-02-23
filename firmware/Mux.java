import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

/**
 * Provides possible movement actions for the robot
 * 
 * @author Jakov Smelkin
 * @author Nikolay Bogoychev
 */
public class Mux extends I2CSensor {

	private static final byte FLOAT = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDS = 2;
	// Stopping time (in miliseconds)
	private static final int STOPTIME = 40;
	private I2CMotor backMotor;
	private I2CMotor frontMotor;
	private I2CMotor kickerLeftMotor;
	private I2CMotor kickerRightMotor;

	@SuppressWarnings("deprecation")
	public Mux(I2CPort port) {
		super(port);
		// Read the morse code for the i2c board. Ours happens to be 0xB4.
		// setAddress() function is deprecated. Perhaps investigate into the future for
		// something better but for now it is enough.
		setAddress(0xB4);
		frontMotor = new I2CMotor(0x01, 0x02, FLOAT, 0);
		backMotor = new I2CMotor(0x03, 0x04, FLOAT, 0);
		kickerRightMotor = new I2CMotor(0x05, 0x06, FLOAT, 0);
		kickerLeftMotor = new I2CMotor(0x07, 0x08, FLOAT, 0);
	}

	// Spins a specific motor
	public void move(int motor, int direction, int speed) {
		switch (motor) {

		case 1:
			frontMotor.move(direction, speed);
			break;

		case 2:
			backMotor.move(direction, speed);
			break;
		}
	}
	
	public void kick() throws InterruptedException{
		kickerRightMotor.move(BACKWARDS, 255);
		kickerLeftMotor.move(BACKWARDS, 255);
		Thread.sleep(100);
		kickerRightMotor.move(FORWARDS, 150);
		kickerLeftMotor.move(FORWARDS, 150);
		Thread.sleep(50);
		kickerRightMotor.move(FLOAT, 0);
		kickerLeftMotor.move(FLOAT, 0);
		
	}

	// Stops both motors
	public void stop() throws InterruptedException {
		backMotor.stop();
		frontMotor.stop();
	}

	private class I2CMotor {
		// A class to control a side motor
		private byte directionAddr;
		private byte speedAddr;
		private int state; // State(Direction): 0 - float, 1 - Forward, 2 - Backwards 3 - Break.
		private int speed; // Speed from 0 to 255.

		public I2CMotor(int directionAddr, int speedAddr, int state, int speed) {
			super();
			this.directionAddr = (byte) directionAddr;
			this.speedAddr = (byte) speedAddr;
			this.state = (byte) state;
			this.speed = (byte) speed;
		}

		// Makes the motor spin in a specified direction
		public void move(int dir, int spd) {
			sendData(directionAddr, (byte) dir);
			sendData(speedAddr, (byte) (spd - 256));
			speed = spd;
			state = dir;
		}
		
		// Stops the motor if not stopped already
		public void stop() throws InterruptedException {
			switch (state) {
			case FLOAT: // Motor is not moving, do nothing
				break;
			case FORWARDS: // Motor is moving forward, stop it
				move(BACKWARDS, speed);
				break;
			case BACKWARDS: // Motor is moving backwards, stop it
				move(FORWARDS, speed);
				break;
			}
			Thread.sleep(STOPTIME);
			move(FLOAT, 0);
		}

	}
}
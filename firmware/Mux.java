import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

public class Mux extends I2CSensor {

	// Speeds of the side motors 0 to 255.
	private int speed1 = 0;
	private int speed2 = 0;

	// State(Direction): 0 - float, 1 - Forward, 2 - Backwards 3 - Break.
	private int stateSM1 = 0;
	private int stateSM2 = 0;

	// Stopping time (in miliseconds)
	private final int stopTime = 10;

	public Mux(I2CPort port) {
		super(port);
		// Read the morse code for the i2c board. Ours happens to be 0xB4.
		// setAddress() function is deprecated. Perhaps investigate into the future for
		// something better but for now it is enough.
		setAddress(0xB4);
	}

	// Controlling the state (direction) of the motor and its speed.
	// Since Java uses singed byte value and the motors take an unsinged one,
	// we convert a
	public void sideMotor1(int direction, int speed) {
		speed1 = speed;
		sendData((byte) 0x01, (byte) direction);
		sendData((byte) 0x02, (byte) (speed - 256));
		stateSM1 = direction;
	}

	public void sideMotor2(int direction, int speed) {
		speed2 = speed;
		sendData((byte) 0x03, (byte) direction);
		sendData((byte) 0x04, (byte) (speed - 256));
		stateSM2 = direction;
	}

	// Since breaking signal has potential of breaking the mux,
	// what we do instead is run the motor in the opposite direction for a small time,
	// effectively cancelling the momentum that we currently have.

	public void sm1stop() throws InterruptedException {
		switch (stateSM1) {
			case 0:
				// Motor is not moving, do nothing
				break;
			case 1:
				// Motor is moving forward, stop it
				sideMotor1(2, speed1);
				Thread.sleep(stopTime);
				sideMotor1(0, 0);
				stateSM1 = 0;
				break;
			case 2:
				// Motor is moving backwards, stop it
				sideMotor1(1, speed1);
				Thread.sleep(stopTime);
				sideMotor1(0, 0);
				stateSM1 = 0;
				break;
		}
	}

	public void sm2stop() throws InterruptedException {
		switch (stateSM2) {
			case 0:
				// Motor is not moving, do nothing
				break;
			case 1:
				// Motor is moving forward, stop it
				sideMotor2(2, speed2);
				Thread.sleep(stopTime);
				sideMotor2(0, 0);
				stateSM2 = 0;
				break;
			case 2:
				// Motor is moving backwards, stop it
				sideMotor2(1, speed2);
				Thread.sleep(stopTime);
				sideMotor2(0, 0);
				stateSM2 = 0;
				break;
		}
	}

}
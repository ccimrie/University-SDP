import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.Motor;

public class Mux extends I2CSensor{
	private int speed1 = 0;
	private int speed2 = 0;
	private int ismovingsm1 = 0;
	private int ismovingsm2 = 0;
	private final int stopvalue = 10; //Stop value in miliseconds
	public Mux(I2CPort port){
		super(port);
		setAddress(0xB4);
		//Read the morse code for the i2c board.
		//That function is deprecated perhaps investigate into the future for
		//something better but for now it is enough.
	}

	//Direction: 0 - float, 1 = Forward, 2 - Backwards 3 break

	public void sidemotor1(int direction, int speed){
		speed1 = speed;
		sendData((byte)0x01,(byte)direction); 
		sendData((byte)0x02,(byte)(speed-256));
		ismovingsm1 = direction;
	}

	public void sidemotor2(int direction, int speed){
		speed2 = speed;
		sendData((byte)0x03,(byte)direction); 
		sendData((byte)0x04,(byte)(speed-256));
		ismovingsm2 = direction;
	}

	//Since breaking is not allowed, what we do instead is run the motor
	//in the opposite direction for 1, effectively cancelling the momentum
	//that we currently have.

	public void sm1stop() throws InterruptedException{
		switch (ismovingsm1){
		case 0:
			//Motor is not moving, do nothing
			break;
		case 1:
			//Motor is moving forward, stop it
			sidemotor1(2,speed1);
			Thread.sleep(stopvalue);
			sidemotor1(0,0);
			ismovingsm1 = 0;
			break;
		case 2:
			//Motor is moving backwards, stop it
			sidemotor1(1,speed1);
			Thread.sleep(stopvalue);
			sidemotor1(0,0);
			ismovingsm1 = 0;
			break;
		} 
	}

	public void sm2stop() throws InterruptedException{
		switch (ismovingsm2){
		case 0:
			//Motor is not moving, do nothing
			break;
		case 1:
			//Motor is moving forward, stop it
			sidemotor2(2,speed2);
			Thread.sleep(stopvalue);
			sidemotor2(0,0);
			ismovingsm2 = 0;
			break;
		case 2:
			//Motor is moving backwards, stop it
			sidemotor2(1,speed2);
			Thread.sleep(stopvalue);
			sidemotor2(0,0);
			ismovingsm2 = 0;
			break;
		} 
	}

	//Byte array conversion from stack overflow.
}
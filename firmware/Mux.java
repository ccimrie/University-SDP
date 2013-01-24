import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.Motor;

public class Mux extends I2CSensor{
	int ismovingsm1 = 0;
	int ismovingsm2 = 0;
	public Mux(I2CPort port){
		super(port);
		setAddress(0xB4);
		//Read the morse code for the i2c board.
	//That function is deprecated perhaps investigate into the future for
		//something better but for now it is enough.
	}
	
	//Direction: 0 - float, 1 = Forward, 2 - Backwards 3 break
	
	public void sidemotor1(int direction, int speed){
		sendData((byte)0x01,(byte)direction); 
		sendData((byte)0x02,(byte)speed);
		ismovingsm1 = direction;
	}
	
	public void sidemotor2(int direction, int speed){
		sendData((byte)0x03,(byte)direction); 
		sendData((byte)0x04,(byte)speed);
		ismovingsm1 = direction;
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
			sidemotor1(0,0);
			sidemotor1(2,127);
			Thread.sleep(1);
			sidemotor1(0,0);
			ismovingsm1 = 0;
			break;
		case 2:
			//Motor is moving backwards, stop it
			sidemotor1(0,0);
			sidemotor1(1,127);
			Thread.sleep(1);
			sidemotor1(2,0);
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
			sidemotor2(0,0);
			sidemotor2(2,127);
			Thread.sleep(1);
			sidemotor2(0,0);
			ismovingsm2 = 0;
			break;
		case 2:
			//Motor is moving backwards, stop it
			sidemotor2(0,0);
			sidemotor2(1,127);
			Thread.sleep(1);
			sidemotor2(2,0);
			ismovingsm2 = 0;
			break;
		} 
	}
	
}


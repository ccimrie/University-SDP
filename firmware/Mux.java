import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

public class Mux extends I2CSensor{

	public Mux(I2CPort port){
		super(port);
		setAddress(0xB4); //Read the morse code for the i2c board.
	//That function is deprecated perhaps investigate into the future for
		//something better but for now it is enough.
	}
	
	public void sidemotor1(int direction, int speed){
		sendData((byte)0x01,(byte)direction); 
		sendData((byte)0x02,(byte)speed);
	}
	
	public void sidemotor2(int direction, int speed){
		sendData((byte)0x03,(byte)direction); 
		sendData((byte)0x04,(byte)speed);
	}
	
}


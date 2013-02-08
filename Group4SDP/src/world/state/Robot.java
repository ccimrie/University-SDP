package world.state;

import communication.BluetoothCommunication;

public class Robot extends Entity {
	
	@Deprecated public double x;
	@Deprecated public double y;
	public double bearing;
	public RobotType type;
	protected BluetoothCommunication comms;
	
	public Robot(RobotType type, BluetoothCommunication comms) {
		this.type = type;
		this.comms = comms;
	}
	

	public String name() {
		return "Robot (" + type.toString() + ")";
	}
	
	public String position() {
		return String.format("%s: %s bearing %f",
				this.type, this.getPosition(), this.bearing);
	}
	
}

package communication;

import java.io.IOException;

public class DummyComms extends BluetoothCommunication {

	public DummyComms(String deviceName, String deviceMACAddress) {
		super(deviceName, deviceMACAddress);
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}
	
	@Override
	public int[] receiveFromRobot() throws IOException {
		return null;
	}
	
	@Override
	public boolean isRobotReady() {
		return true;
	}
	
	@Override
	public void sendToRobotSimple(int[] comm) throws IOException {
	}
	@Override
	public int sendToRobot(int[] comm) throws IOException {
		return 0;
	}
	@Override
	public void openBluetoothConnection() throws IOException {
	}
	@Override
	public void closeBluetoothConnection() {
	}
}

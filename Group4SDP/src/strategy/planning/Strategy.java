package strategy.planning;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import world.state.World;
import communication.*;


public abstract class Strategy implements Observer {
	
	World world = World.getInstance();
	BluetoothCommunication connection = new BluetoothCommunication(DeviceInfo.NXT_NAME, DeviceInfo.NXT_MAC_ADDRESS);
	//Server rc = Server.getInstance();
	
	public void execute() {
		// Add this instance as an observer to the world to be notified of frame updates
		System.out.println("[Strategy] Are we blue? " + world.areWeBlue());
		System.out.println("[Strategy] Are we on the left side? " + world.areWeOnLeft());
		System.out.println("[Strategy] Are we on the main pitch? " + world.isMainPitch());
		world.addObserver(this);
	}
	
	public void stop() {
		world.deleteObservers();
		int[] stop_command = {Commands.STOP,0,0,0};
		try {
			connection.sendToRobot(stop_command);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Stop command not sent - check bluetooth connection");
		}
	}
	
	public void stopSmoothly() {
		world.deleteObservers();
	}
	
	public void setTeam(boolean WeAreBlue) {
		
	}
	
	@Override
	public abstract void update(Observable arg0, Object arg1);

}

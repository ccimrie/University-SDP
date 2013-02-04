package strategy.planning;

import java.util.Observable;
import java.util.Observer;

import world.state.RobotController;
import world.state.RobotType;
import world.state.World;

import communication.BluetoothCommunication;
import communication.DeviceInfo;

public abstract class Strategy implements Observer {
        
        World world = World.getInstance();
        BluetoothCommunication comms = new BluetoothCommunication(DeviceInfo.NXT_NAME, DeviceInfo.NXT_MAC_ADDRESS);
        RobotController rc = new RobotController(RobotType.Us);
        
        // Returns new Server() empty constructor - same as our BluetoothConnection.java
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
                rc.setComms(comms);
                rc.stop();
        }
        
        public void stopSmoothly() {
                world.deleteObservers();
        }
        
        public void setTeam(boolean WeAreBlue) {
                
        }
        
        @Override
        public abstract void update(Observable arg0, Object arg1);

}
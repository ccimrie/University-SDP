package world.state;

import java.io.IOException;
import java.util.Timer;

import communication.*;
import strategy.planning.Commands;

public class RobotController extends Robot {
	
	private BluetoothCommunication comms = new BluetoothCommunication(DeviceInfo.NXT_NAME, DeviceInfo.NXT_MAC_ADDRESS);
	
	public RobotController(RobotType type) {
		super(type);
	}

	private enum CurrentMovement {
		
	}

	//Opens Bluetooth communication between computer and robot
	public void setComms(BluetoothCommunication comms) {
		this.comms = comms;
		try {
			comms.openBluetoothConnection();
		} catch (IOException e) {
			System.out.println("Could not connect");
			e.printStackTrace();
		}
	}

	public void quit() {
		int[] command = {Commands.QUIT, 0, 0, 0};
		try {
			comms.sendToRobot(command);
		}
		catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Quit...");
		System.exit(0);
	}
	
	
	public void stop(Timer timer) {
		int[] command = {Commands.STOP, 0, 0, 0};
		try {
			comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		timer.cancel();
		System.out.println("Stop...");
	} 
	public void stop() {
		int[] command = {Commands.STOP, 0, 0, 0};
		try {
			comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		
		System.out.println("Stop...");
	} 

	public void kick() {
		int[] command = {Commands.KICK, 0, 0, 0};
		try {
			comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Kick");
	}
	
	public void rotate() {
		int[] command = {Commands.ROTATE, -120, -30, 0};//Angle is the sum of option1 + option2
		try {
			comms.sendToRobot(command);
		}
		catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Rotate...");
	}
	public void rotate(int input ) {
		int dir =0;
		if (input <0 && input > -180) {
			input = -input;
			dir = 1;
		}
		
		int op1 = input%10;
		int op2 = input/10;
		int[] command = {Commands.ROTATE, dir, op2, op1};//Angle is the sum of option1 + option2
		try {
			comms.sendToRobot(command);
		}
		catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Rotate...");
	}

	public void forward() {
		int[] command = {Commands.FORWARDS, 100, 100, 0};
		try {
			comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving forward...");
	}
	
	public void backward(){
		int[] command = {Commands.BACKWARDS, 0, 0, 0};
		try {
			comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving backwards...");
	}
	
	public void left(){
		int[] command = {Commands.LEFT, 0, 0, 0};
		try {
			comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving leftside...");
	}
	
	public void right(){
		int[] command = {Commands.RIGHT, 0, 0, 0};
		try {
			comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving rightside...");
	}
	
	//TODO Add anglemove method
	
	/*
	public boolean forward(Double speed) {
		return rc.forward(speed);
	} */

	/*
	public boolean arcForward(Double radius) {
		return rc.arcForward(radius);
	}


	public boolean arcForward(Double radius, Double speed) {
		return rc.arcForward(radius, speed);
	}
	*/
	/*
	public boolean isMoving() {
		return rc.isMoving();
	} */

	/*
	public boolean isStalled() {
		return rc.isStalled();
	} */

	// TODO - Implement when sensors have been added
	/*
	public boolean isTouchPressed() {
		return rc.isTouchPressed();
	} */
	
	/*
	public boolean rotate(Double angle, Double speed) {
		return rc.rotate(angle, speed);
	}
	*/
	
	/*
	public void setDefaultRotateSpeed(Double speed) {
		rc.setDefaultRotateSpeed(speed);
	} */

	/*
	public boolean rotate(Double angle) {
		return rc.rotate(angle);
	} */
	//TODO - Make it rotate to an angle eventually please :)

	/*
	public void setDefaultTravelSpeed(Double speed) {
		rc.setDefaultTravelSpeed(speed);
	} */

	/*
	public boolean travel(Double distance) {
		return rc.travel(distance);
	} */

	/*
	public boolean travel(Double distance, Double speed) {
		return rc.travel(distance, speed);
	} */
}

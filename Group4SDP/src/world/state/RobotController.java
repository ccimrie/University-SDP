package world.state;

import java.io.IOException;


import communication.*;
import strategy.planning.Commands;

public class RobotController extends Robot {
	
	private BluetoothCommunication comms = computer.ControlGUI2.comms;
	public RobotController(RobotType type) {
		super(type);
	}


	//Opens Bluetooth communication between computer and robot

	public void quit() {
		int[] command = {Commands.QUIT, 0, 0, 0};
		try {
			comms.sendToRobotSimple(command);
		}
		catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Quit...");
		System.exit(0);
	}
	
 
	public int stop() {
		int[] command = {Commands.STOP, 0, 0, 0};
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Stop...");
		return confirmation;
	} 

	public int kick() {
		int[] command = {Commands.KICK, 0, 0, 0};
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Kick");
		return confirmation;
	}
	
	public int rotate(int input ) {
		int dir =2;
		int confirmation = 0;
		if (input <0 && input > -180) {
			input = -input;
			dir = 1;
		}
		
		int op1 = input%10;
		int op2 = input/10;
		int[] command = {Commands.ROTATE, dir, op2, op1};//Angle is the sum of option1 + option2
		try {
			System.out.println("Str " + dir + " " + op2 + " " + op1);
			confirmation = comms.sendToRobot(command);
		}
		catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Rotate...");
		return confirmation;
	}

	public int move(int op1, int op2) {
		int[] command = {Commands.ANGLEMOVE, op1, op2, 0};
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving forward...");
		return confirmation;
	}
	
	public int forward(int op1, int op2) {
		int[] command = {Commands.FORWARDS, op1, op2, 0};
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving forward...");
		return confirmation;
	}
	
	public int backward(int op1, int op2){
		int[] command = {Commands.BACKWARDS, op1, op2, 0};
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving backwards...");
		return confirmation;
	}
	
	public int left(int op1, int op2){
		int[] command = {Commands.LEFT, op1, op2, 0};
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving leftside...");
		return confirmation;
	}
	
	public int right(int op1, int op2){
		int[] command = {Commands.RIGHT, op1, op2, 0};
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving rightside...");
		return confirmation;
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

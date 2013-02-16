package simulator;

import comms.control.Protocol;
import comms.control.ServerInterface;

public class SimServer implements ServerInterface {
	
	//private static final SimServer instance = new SimServer(rf);
	
	public float x, y, ballx, bally, oppx, oppy, bearing, oppBearing;
	
	public int command = -1;
	public float arg = 0.0f;
	public int commandCount = 0;
	
	float travelSpeed = 0.3f;
	float rotateSpeed = 0.3f;
	
	float defaultTravelSpeed = 0.3f;
	float defaultRotateSpeed = 0.3f;
	
	RoboFootball rf;
	
	public boolean isTurning = false;
	public boolean isMoving = false;
	public boolean isStalled = false;
	public boolean isTouchPressed = false;

	
	public SimServer(RoboFootball rf)
	{
		this.rf = rf;
	}
	
	/*
	public static SimServer getInstance() {
		return instance;
	}
	*/
	
	public boolean sendCommand(byte command, float arg)
	{
		if(rf==null)
			return false;
		rf.executeCommand(command, arg);
		//this.command = command;
		//this.arg = arg;
		//commandCount++;
		return true;
	}

	@Override
	public boolean quit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop() {
		return sendCommand(Protocol.STOP, 0);
	}

	@Override
	public void setDefaultTravelSpeed(Double speed) {
		defaultTravelSpeed = speed.floatValue();

	}

	@Override
	public boolean travel(Double distance) {
		return travel(distance, (double)defaultTravelSpeed);
	}

	@Override
	public boolean travel(Double distance, Double speed) {
		travelSpeed = speed.floatValue();
		return sendCommand(Protocol.TRAVEL, distance.floatValue());
	}

	@Override
	public boolean kick() {
		return sendCommand(Protocol.KICK, 0);
	}

	@Override
	public void setDefaultRotateSpeed(Double speed) {
		defaultRotateSpeed = speed.floatValue();

	}

	@Override
	public boolean rotate(Double angle) {
		return rotate(angle, (double)defaultRotateSpeed);
	}

	@Override
	public boolean rotate(Double angle, Double speed) {
		rotateSpeed = speed.floatValue();
		isTurning = true;
		return sendCommand(Protocol.ROTATE, (float) Math.toRadians( angle));
	}

	@Override
	public boolean forward() {
		return forward((double)defaultTravelSpeed);
	}

	@Override
	public boolean forward(Double speed) {
		travelSpeed = speed.floatValue();
		return sendCommand(Protocol.FORWARD, 0);
	}
	
	public boolean arcForward(Double radius) {
		return arcForward(radius, (double)defaultTravelSpeed);
	}
	
	// Positive radius will arc right, negative arcs left
	public boolean arcForward(Double radius, Double speed) {
		travelSpeed = speed.floatValue();
		return sendCommand(Protocol.ARC_FORWARD, radius.floatValue());
	}

	@Override
	public boolean isMoving() {
		// TODO Auto-generated method stub
		return this.isMoving;
	}

	@Override
	public boolean isStalled() {
		// TODO Auto-generated method stub
		return this.isStalled;
	}

	@Override
	public boolean isTouchPressed() {
		// TODO Auto-generated method stub
		return this.isTouchPressed;
	}
	
}

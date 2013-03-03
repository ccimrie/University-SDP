package communication;

import simulator.Simulator;
import world.state.Robot;
import world.state.RobotType;

public class SimulatorRobot extends Robot implements RobotController {
	
	private boolean connected = false;
	private final Simulator sim;
	
	public SimulatorRobot(RobotType type, final Simulator simulator) {
		super(type);
		sim = simulator;
	}

	@Override
	public void connect() throws Exception {
		connected = true;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}
	
	@Override
	public boolean isReady() {
		// TODO: add a small delay between connected and ready becoming true
		return connected;
	}

	@Override
	public void disconnect() {
		connected = false;
	}

	@Override
	public int stop() {
		// Assumes 4 wheels
		assert (sim.getRobotWheelCount() == 4) : "DERP! robot wheel count is not 4";
		
		sim.setRobotWheelSpeed(Simulator.LEFT_WHEEL, 0);
		sim.setRobotWheelSpeed(Simulator.RIGHT_WHEEL, 0);
		sim.setRobotWheelSpeed(Simulator.FRONT_WHEEL, 0);
		sim.setRobotWheelSpeed(Simulator.BACK_WHEEL, 0);
		
		return 0;
	}

	@Override
	public int kick() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int move(int speedX, int speedY) {
		// Assumes 4 wheels
		assert (sim.getRobotWheelCount() == 4) : "DERP! robot wheel count is not 4";
		
		sim.setRobotWheelSpeed(Simulator.LEFT_WHEEL, speedY);
		sim.setRobotWheelSpeed(Simulator.RIGHT_WHEEL, speedY);
		sim.setRobotWheelSpeed(Simulator.FRONT_WHEEL, speedX);
		sim.setRobotWheelSpeed(Simulator.BACK_WHEEL, speedX);
		return 0;
	}

	@Override
	public int rotate(int angleDeg) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rotateMove(int speedX, int speedY, int rotSpeed) {
		// TODO implement to mimic Brick.java implementation for the real robot
		return 0;
	}

	@Override
	public void clearBuff() {
		// Not required for simulated robot
	}
}

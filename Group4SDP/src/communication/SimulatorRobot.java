package communication;

import simulator.Simulator;
import world.state.Robot;
import world.state.RobotType;

public class SimulatorRobot extends Robot implements RobotController {
	
	private boolean connected = false;
	private final int simIndex;
	private final Simulator sim;
	
	public SimulatorRobot(RobotType type, final int simIndex, final Simulator simulator) {
		super(type);
		sim = simulator;
		this.simIndex = simIndex;
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
		// TODO: add a delay between connected and ready becoming true
		return connected;
	}

	@Override
	public void disconnect() {
		connected = false;
	}

	@Override
	public int stop() {
		int wheelCount = sim.getRobotWheelCount();
		
		return 0;
	}

	@Override
	public int kick() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int move(int speedX, int speedY) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rotate(int angleDeg) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rotateMove(int speedX, int speedY, int rotSpeed) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clearBuff() {
		// TODO Auto-generated method stub

	}
}

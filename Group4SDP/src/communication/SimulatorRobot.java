package communication;

import simulator.Simulator;
import world.state.Robot;
import world.state.RobotType;

public class SimulatorRobot extends Robot implements RobotController {
	
	private final Simulator sim;
	
	public SimulatorRobot(RobotType type, final Simulator simulator) {
		super(type);
		sim = simulator;
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

	}

	@Override
	public int stop() {
		// TODO Auto-generated method stub
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

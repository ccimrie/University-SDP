package simulator;

import world.state.Ball;
import world.state.Robot;

public interface WorldInterface {

	Robot getOurRobot();

	Robot getTheirRobot();

	Ball getBall();

}

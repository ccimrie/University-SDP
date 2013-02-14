package simulator;

import world.state.Ball;
import world.state.Robot;
import world.state.RobotType;
import simulator.WorldInterface;

public class SimWorld implements WorldInterface {
	public Robot ourRobot, theirRobot;
	public Ball ball;
	
	public SimWorld()
	{
		ourRobot = new Robot(RobotType.Us);
		theirRobot = new Robot(RobotType.Them);
		ball = new Ball();
	}

	public void update(float ourX, float ourY, float ourBearing,
			float theirX, float theirY, float theirBearing,
			float ballX, float ballY)
	{
		ourRobot.x = ourX;
		ourRobot.y = ourY;
		ourRobot.bearing = ourBearing + Math.PI/2.0f;
		
		theirRobot.x = theirX;
		theirRobot.y = theirY;
		theirRobot.bearing = theirBearing + Math.PI/2.0f;
		
		ball.x = ballX;
		ball.y = ballY;
	}
	
	@Override
	public Robot getOurRobot() {
		return ourRobot;
	}

	@Override
	public Robot getTheirRobot() {
		return theirRobot;
	}

	@Override
	public Ball getBall() {
		return ball;
	}

}

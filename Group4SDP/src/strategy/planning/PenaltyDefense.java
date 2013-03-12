package strategy.planning;

import movement.RobotMover;
import world.state.WorldState;
import utility.SafeSleep;
import world.state.Ball;

public class PenaltyDefense extends StrategyInterface {
	double originalx;
	double originaly;
	Ball ball;
	
	private Thread strategyThread;
	private StrategyInterface strategy;
	
	public PenaltyDefense(WorldState world, RobotMover mover) {
		super(world, mover);
		//Original position of the ball. Use this to determine
		this.ball = world.ball;
		this.originalx = ball.x;
		this.originaly = ball.y;
	}

	@Override
	public void run() {
		double theirOriginal = Math.toDegrees(world.theirRobot.bearing);

		int counter = 0;
		while ((!shouldidie && !Strategy.alldie) &&
				(Math.abs(ball.x - originalx) < 10) && 
				((Math.abs(ball.y - originaly)) < 10)){
			System.out.println(Math.toDegrees(world.theirRobot.bearing) + " "
					+ theirOriginal);

			if (Math.toDegrees(world.theirRobot.bearing) > theirOriginal + 5
					&& counter > -1) {
				System.out.println("go backwards");
				mover.move(0, -100);
				try {
					SafeSleep.sleep(600);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mover.stopRobot();

				theirOriginal = Math.toDegrees(world.theirRobot.bearing);
				counter--;
				try {
					SafeSleep.sleep(600);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (Math.toDegrees(world.theirRobot.bearing) < theirOriginal - 5
					&& counter < 1) {
				System.out.println("going forwards");

				mover.move(0, 100);
				try {
					SafeSleep.sleep(600);;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mover.stopRobot();

				theirOriginal = Math.toDegrees(world.theirRobot.bearing);
				counter++;
				try {
					SafeSleep.sleep(600);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(counter);
		}
		strategy = new MainPlanner(world, mover);
		strategyThread = new Thread(strategy);
		strategyThread.start();
	}
}

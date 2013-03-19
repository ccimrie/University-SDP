package strategy.planning;

import movement.RobotMover;
import utility.SafeSleep;
import vision.Position;
import world.state.WorldState;
import world.state.Ball;
import world.state.Robot;
import strategy.movement.TurnToBall;
import strategy.planning.DribbleBall5;

public class InterceptBall extends StrategyInterface {
	private static final double distanceThreshold = 50;
	private static final double angleThreshold = Math.toRadians(15);
	private Ball ball;
	private final double initx;
	private final double inity;
	private Robot us;
	private int ourRobot = 2;
	private DribbleBall5 dribble;

	public InterceptBall(WorldState world, RobotMover mover) {
		super(world, mover);
		this.ball = world.ball;
		initx = ball.x;
		inity = ball.y;
		us = world.getOurRobot();
		//For use with the possession manager.
		if(world.areWeBlue()){
			ourRobot = 1;
		}
	}

	@Override
	public void run() {
		System.out.println("InterceptBall started");
		/* This while loop waits until the other robot kicks the ball,
		 * before going into intercept mode. Comment it out if you don't
		 * want to use it.
		 */
		/*double change = Math.abs(initx-ball.x) + Math.abs(inity-ball.y);
		while (change < 10){
			try {
				SafeSleep.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			change = Math.abs(initx-ball.x) + Math.abs(inity-ball.y);
		}
		System.out.println("BALL MOVED! Intercept it!");
		 */
		double ballTurnAngle;

		try {
			System.out.println("shouldidie: " + shouldidie);
			System.out.println("Strategy.alldie: " + Strategy.alldie);
			while (!shouldidie && !Strategy.alldie) {
				Position projBallPos = world.projectedBallPos();

				if (world.distanceBetweenUsAndBall() > distanceThreshold) {
					System.out.println("Projected ball pos: (" + projBallPos.getX() + ", " + projBallPos.getY() + ")");
					double targetX = (world.areWeOnLeft()) ? 60 : 560;
					mover.moveTowards(targetX, projBallPos.getY());
				}
				SafeSleep.sleep(50);
			}
			//In case we don't intercept the ball in time, ie it hits us on
			//the side, start dribbleball5 from the previous milestone
			//immediately.

			//Check if the ball is closer to our goal then us.
			if(Math.abs(ball.x - world.getOurGoal().getX()) >
			Math.abs(us.x - world.getOurGoal().getX())){
				dribble.dribbleBall(world, mover);
			}else{

				// After we are on the trajectory, rotate to face the ball.
				// and start moving towards it.
				ballTurnAngle = Math.toRadians(TurnToBall.Turner(us, ball));
				mover.rotate(ballTurnAngle);
				mover.waitForCompletion();
				mover.move(0,100);
				while(world.whoHasTheBall() != ourRobot &&
						!shouldidie && !Strategy.alldie){
					SafeSleep.sleep(50);
				}
				mover.stopRobot();
				dribble.dribbleBall(world, mover);
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
		System.out.println("InterceptBall ended");
	}
}

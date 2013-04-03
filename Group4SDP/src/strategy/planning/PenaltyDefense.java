package strategy.planning;

import movement.RobotMover;
import world.state.Robot;
import world.state.WorldState;
import utility.SafeSleep;
import world.state.Ball;

public class PenaltyDefense extends StrategyInterface {
	double originalx;
	double originaly;
	
	enum state {
	    top, bot, mid;
	}
	
	state curstate = state.mid;
	
	Robot them;
	Robot us;
	Ball ball;
	
	int goalmidx;
	int goalmidy;
	
	int goaltopx;
	int goaltopy;
	
	int goalbotx;
	int goalboty;
	
	private Thread strategyThread;
	
	public PenaltyDefense(WorldState world, RobotMover mover) {
		super(world, mover);
		//Original position of the ball. Use this to determine
		this.ball = world.ball;
		this.originalx = ball.x;
		this.originaly = ball.y;
		them = world.theirRobot;
		us = world.ourRobot;

		if (world.areWeOnLeft()){
			goalmidx = (int) us.x;
			goalmidy = 218;
			
			goaltopx = (int) us.x;
			goaltopy = 184;// - 30;
			
			goalbotx = (int) us.x;
			goalboty = 265;// + 30;
		}else{
			goalmidx = (int) us.x;
			goalmidy = 234;
			
			goaltopx = (int) us.x;
			goaltopy = 177;// - 30;
			
			goalbotx = (int) us.x;;
			goalboty = 300;// + 30;
		}
		System.out.printf("Topy %d, midy %d, boty %d\n", goaltopy, goalmidy, goalboty);
	}
	
	/**
	 * Sets up the desired strategy thread to be started after the ball is kicked.
	 * @param strategy any sort of strategy thread.
	 */
	
	public void setStrat(Thread strategy){
		this.strategyThread = strategy;
	}
	/**
	 * calculate the angle to the mid, top and bottom of our goal and move to that point.
	 */
	@Override
	public void run() {
		
		while ((!shouldidie && !Strategy.alldie) &&
				(Math.abs(ball.x - originalx) < 10) && 
				((Math.abs(ball.y - originaly)) < 10)){
			
			long sleeptime = 0; //Sleep time after movement
			state prevstate = curstate;
			
			double anglemid = Math.abs(mover.angleCalculator(them.x, them.y,
					goalmidx, goalmidy, them.bearing));
			
			double angletop = Math.abs(mover.angleCalculator(them.x, them.y,
					goaltopx, goaltopy, them.bearing));
			
			double anglebot = Math.abs(mover.angleCalculator(them.x, them.y,
					goalbotx, goalboty, them.bearing));
			
			if ((Math.min(Math.min(anglemid, angletop), anglebot)) == anglemid){
				curstate = state.mid;
			}else if ((Math.min(Math.min(anglemid, angletop), anglebot)) == anglebot){
				curstate = state.bot;
			}else{
				curstate = state.top;
			}
			if (curstate != prevstate){
				switch (curstate){
				
				case mid:	mover.moveTowards(goalmidx, goalmidy);
							sleeptime = 600;
							break;
				
				case bot:	mover.moveTowards(goalbotx, goalboty);
							if (prevstate == state.mid){
								sleeptime = 600;
							}else{
								sleeptime = 1200;
							}
							break;
						
				case top:	mover.moveTowards(goaltopx, goaltopy);
							if (prevstate == state.mid){
								sleeptime = 600;
							}else{
								sleeptime = 1200;
							}
							break;		
				
				}
				try {
					SafeSleep.sleep(sleeptime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mover.stopRobot();
				try {
					mover.waitForCompletion();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				SafeSleep.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//Start a strategy if you have one.
		if (strategyThread != null){
			strategyThread.start();
		}
	}
}

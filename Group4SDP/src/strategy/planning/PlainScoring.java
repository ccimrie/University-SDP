package strategy.planning;

import balle.strategy.pFStrategy.Vector;
import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.DistanceToBall;
import strategy.movement.TurnToBall;
import vision.WorldState;
import world.state.*;
import geometry.*;
import java.math.*;
import strategy.*;

public class PlainScoring{
	
	
	public WorldState world; Robot us; Robot them;
	RobotController rc;
	public Ball ball = world.ball;
	
	
	public void domination(WorldState world, Robot us, Robot them,
			RobotController rc) throws InterruptedException {
		geometry.Vector a = world.getTheirGoal();
		
		//Case where we have the ball and the opponent is behind us and 
		double angle = IsRobotFacingPoint.Turner(us, a.getX(), a.getY());
		double angleT = IsRobotFacingPoint.Turner(us, them.x, them.y);
		double angleDeg = Math.toDegrees(angle);
		if(us.x-them.x>0 && (angleDeg   < 15 || angleDeg  > 345)){
			faceGoal();
			rc.move(0, 100);
			wait(2000);
			rc.kick();
			rc.stop();
			return;
			
		}
		if(us.x-them.x>0 && (angleDeg   > 15 || angleDeg  < 345)){
			faceGoal();
			rc.move(0, 100);
			wait(2000);
			rc.kick();
			rc.stop();
			return;
			
		}
		if ((angleDeg   < 20 || angleDeg  > 340)&&
				(DistanceToBall.Distance(us.x, us.y, a.getX(), a.getY()) < 200)){
			// need more details
			rc.rotate(5);
			rc.kick();
			return;
		}
		if (them.x > us.x && angleT < 20 && angleT > 340){
			double mid = them.x - us.x;
			double nu = them.y +20 ;
			//TODO : fix this so we're sure we don't hit the wall#
			MoveToPoint m = new MoveToPoint();
			m.moveToPoint(world, rc, mid, nu);
			faceGoal();
			rc.move(0, 100);
			wait(1000);
			rc.kick();
			rc.stop();
			return;
		}
			
		
		
	}
	public void faceGoal() {
		double angle = TurnToBall.Turner(us, ball);
		double angleDeg = Math.toDegrees(angle);
		if((Math.abs(angleDeg) > 15) && (Math.abs(angleDeg) < 40) ) {
			//Stop everything and turn
			System.out.println("The final angle is " + angleDeg);
			rc.stop();
			rc.rotate((int)(angleDeg/2));
		}else if (Math.abs(angleDeg) > 40){
			rc.stop();
			rc.rotate((int)angleDeg);
		}
		
	}
	
	
}

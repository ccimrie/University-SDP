package strategy.planning;

import balle.strategy.pFStrategy.Vector;
import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.DistanceToBall;
import strategy.movement.Movement;
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
	HittingObstacle wall = new HittingObstacle();
	MoveToPoint mpoint = new MoveToPoint();
	PitchInfo info = new PitchInfo();
	
	
	
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
		if (them.x > us.x && (angleT < 20 || angleT > 340)){
			double mid = them.x - us.x;
			double nu = them.y +20 ;
			if (DistanceToBall.Distance(us.x, us.y, a.getX(), a.getY()) > 200){
				double goalX = world.getTheirGoal().getX();
				double goalY = world.getTheirGoal().getY();
				double gpt = 165;
				double gpb = 310;
				double d = DistanceToBall.Distance(us.x, us.y,goalX, goalY);
				double newAngle = 0;
				if (gpb - us.y < us.y - gpt){
					 newAngle = Math.asin((gpb - us.y)/d);
				}
				else  newAngle = Math.asin((gpt - us.y)/d);
				
				rc.rotate((int) newAngle);
				rc.kick();
			
			
		}else {
			double mang = 0;
			if (us.x - 104  > 395 - us.x){
				mang = 45;
			}
			else mang = -45;
			Movement m = new Movement(world, rc);
			int t = 0;
			m.move(Math.toRadians(mang));
			
			while (us.x < them.x || t < 3 || wall.notHittingWall(world)){
			
			Thread.sleep(1000);
			t += 1;
			
			
			}
			rc.stop();
			return;
			
			
			
		}
		}
		if (wall.inCorner(world) && (them.x > us.x && (angleT < 20 || angleT > 340))){
			int p1 = 0;
			int p2 = 0;
			if (us.x > 300)
			{
				p1 = 412;
				p2 = 232;
			}
			else{
				p1 = 222;
				p2 = 235;
			}
			mpoint.moveToPoint(world, rc, p1, p2);
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

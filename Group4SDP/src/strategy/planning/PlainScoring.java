package strategy.planning;

import balle.strategy.pFStrategy.Vector;
import strategy.calculations.IsRobotFacingPoint;
import strategy.movement.DistanceToBall;
import strategy.movement.Movement;
import strategy.movement.TurnToBall;
import vision.Position;
import vision.WorldState;
import world.state.*;
import geometry.*;
import java.math.*;
import strategy.*;

public class PlainScoring{
	
	
	
	public Ball ball;
	HittingObstacle wall = new HittingObstacle();
	MoveToPoint mpoint = new MoveToPoint();
	Position a ;
	public WorldState world;
	public RobotController rc;
	public Robot us;
	
	public void domination(WorldState world, Robot us, Robot them,
			RobotController rc) throws InterruptedException {
			this.ball = world.ball;
			this.a = world.getTheirGoal();
			this.world = world;
			this.rc = rc;
			this.us = us;
			
		System.out.println( "Domination is started");
		//Case where we have the ball and the opponent is behind us and 
		double angle = IsRobotFacingPoint.Turner(us, a.getX(), a.getY());
		double angleT = IsRobotFacingPoint.Turner(us, them.x, them.y);
	//	double angleDeg = Math.toDegrees(angle);
		double realangle = TurnToBall.AngleTurner(world.getOurRobot(), a.getX(), a.getY());
		double  angleDeg = Math.abs(realangle);
		
		
		if(us.x-them.x>0 && (angleDeg   < 15 || angleDeg  > 345)){
			System.out.println("ennemy is behind, straight punch!");
			faceGoal();
			rc.move(0, 100);
			Thread.sleep(1500);
			rc.kick();
			rc.stop();
			return;
			
		}
		if(us.x-them.x>0 && (angleDeg   > 15 || angleDeg  < 345)){
			System.out.println("ennemy is behind, navigate and punch ");
			faceGoal();
			Thread.sleep(1500);
			rc.move(0, 100);
			
			rc.kick();
			rc.stop();
			return;
			
		}
		if ((angleDeg   < 20 || angleDeg  > 340)&&
				(DistanceToBall.Distance(us.x, us.y, a.getX(), a.getY()) < 200)){
			
			System.out.println("Blind punch to side");
			rc.rotate(5);
			rc.kick();
			return;
		}
		if (them.x > us.x && (angleT < 20 || angleT > 340)){
			System.out.println("ennemy is really ahead of us ");
			double mid = them.x - us.x;
			double nu = them.y +20 ;
			if (DistanceToBall.Distance(us.x, us.y, them.x, them.y) > 200){
				System.out.println("try to score ");
				double goalX = world.getTheirGoal().getX();
				double goalY = world.getTheirGoal().getY();
				double gpt = 165;
				double gpb = 310;
				double d = DistanceToBall.Distance(us.x, us.y,goalX, goalY);
				double newAngle = 0;
				if (Math.abs(gpb - us.y) < Math.abs(gpt - us.y)){
					 newAngle = Math.asin(Math.abs((gpb - us.y)/d));
				}
				else  newAngle = - Math.asin(Math.abs((gpt - us.y)/d));
				
				rc.rotate((int) newAngle);
				rc.kick();
			
			
		}else {
			System.out.println("atempt to dodge");
			double mang = 0;
			if (us.x - 104  > 395 - us.x){
				mang = 45;
			}
			else mang = -45;
			Movement m = new Movement(world, rc, 0, 0, 0, 0, mang, 0);
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
			System.out.println("run away from corner");
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
		
	/*	double angle = (TurnToBall.AngleTurner(us, a.getX(), a.getY()))%360;
		double angleDeg = Math.toDegrees(angle);
		System.out.println("The radangle " + angleDeg);
		System.out.println("The final angle is " + angleDeg);
		if((Math.abs(angleDeg) < 40) ) {
			
			
			rc.stop();
			rc.rotate((int)(angleDeg/1.5));
		}else if (Math.abs(angleDeg) > 40){
			//System.out.println("The final angle is " + angleDeg);
			rc.stop();
			rc.rotate((int) (angleDeg/1.08));
		}
		*/
		
		
		Position a = world.getTheirGoal();
		System.out.println(a.getX() + ", " + a.getY());
		
		
		double angle = (TurnToBall.AngleTurner(world.getOurRobot(), a.getX(), a.getY()));

		
	
		System.out.println(angle);
		rc.rotate((int)angle);
	}
	
	
}

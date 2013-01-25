package strategy.movement;

import world.state.WorldInterface;
import comms.control.ServerInterface;

public class OldGoToPoint {
	
	private static double usX, usY, themX, themY, destX, destY;
	private static double dxUsThem, dyUsThem, dxUsDest, dyUsDest, dxThemDest, dyThemDest;
	private static double distUsThem, distUsDest, distThemDest;
	
	public static double getMagn()
	{
		//get the magnitude of the repulsive vector

	    double area = Math.abs(dxUsThem*dyThemDest - dxThemDest*dyUsThem);

	    double distFromLine = area/distUsDest;
	    
	    
	    //if(distUsThem > 1)
	    	//return 0;

	    if(distFromLine > 0.3)
	        return 0;

	    return 0.8;
	    
	    
	    //return 0.3/distUsThem;
	}
	
	public static void goToPoint(WorldInterface world, ServerInterface rc, double x, double y)
	{
		usX = world.getOurRobot().x;
		usY = world.getOurRobot().y;
		themX = world.getTheirRobot().x;
		themY = world.getTheirRobot().y;
		destX = x;
		destY = y;
		
		dxUsDest = destX - usX;
		dyUsDest = destY - usY;
		distUsDest = Math.sqrt(dxUsDest*dxUsDest + dyUsDest*dyUsDest);
			
		double destVectX = dxUsDest/distUsDest;
		double destVectY = dyUsDest/distUsDest;
		
		dxUsThem = themX - usX;
		dyUsThem = themY - usY;
		
		distUsThem = Math.sqrt(dxUsThem*dxUsThem + dyUsThem*dyUsThem);
			
		double repVectEnemyX = -dxUsThem/distUsThem;
		double repVectEnemyY = -dyUsThem/distUsThem;
			
		dxThemDest = destX - themX;
		dyThemDest = destY - themY;
		distThemDest = Math.sqrt(dxThemDest*dxThemDest + dyThemDest*dyThemDest);
		
		double magn = getMagn();
		
		double vxDir = destVectX + magn*repVectEnemyX;
	    double vyDir = destVectY + magn*repVectEnemyY;

	    //double vDirMagn = Math.sqrt(vxDir*vxDir + vyDir*vyDir);
	    System.out.println("dist: " + distUsThem + " magn: " + magn);
		
		FollowVector.followVector(world, rc, vxDir, vyDir);
			
	}

}

package strategy.calculations;

public class AngleBetweenTwoVectors {

	/*find the angle between two lines
	 * the middle point (x2, y2) of the three points taken
	 * as input is the meeting point of the two lines
	 */
	
	public static double angle(double x1, double y1, double x2, double y2, double x3, double y3) {

       double vect1x = x1 - x2;
       double vect1y = y1 - y2;
       
       double vect2x = x3 - x2;
       double vect2y = y3 - y2;
       
       double dotProduct = ((vect1x * vect2x) + (vect1y * vect2y));
       
       double norm1 = Math.sqrt((Math.pow(vect1x, 2)) + (Math.pow(vect1y, 2)));
       double norm2 = Math.sqrt((Math.pow(vect2x, 2)) + (Math.pow(vect2y, 2)));
       
       double normFinal = norm1 * norm2;
       
       double angle = Math.toDegrees(Math.acos(dotProduct/normFinal));
       
       return angle;

    }

}

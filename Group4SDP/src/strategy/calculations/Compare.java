package strategy.calculations;

public class Compare {

	public static boolean compareDouble(double x1, double x2, double diff) {
		// This function simply compares two coordinates, and returns true if they are more than the given distance apart
		// It seemed simpler to use than incredibly long and unwieldy comparisons within if statements
		if (Math.abs(x1-x2) > diff) {
			return false;
		} else {
			return true;
		}
		
		
	}

	public static boolean compareCoord(double yell_b, double findBearing, int i) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

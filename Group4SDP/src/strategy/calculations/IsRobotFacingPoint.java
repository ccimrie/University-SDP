package strategy.calculations;

import world.state.Robot;

public class IsRobotFacingPoint {
	
	public static double findBearing(Robot us, double x, double y) {
    	
    	double bearing = 0;

        //checks if the ball is in line horizontally with the ball

        if (us.x == x) {
            if (us.y > y) {
                bearing = Math.toDegrees(Math.PI);
            } else {
                bearing = 0;
            }
            return bearing;

            //checks if the ball is in line vertically with the ball

        } else if (us.y == y) {
            if (us.x > x) {
                bearing = Math.toDegrees(Math.PI * 1.5);
            } else {
                bearing = Math.toDegrees(Math.PI / 2);
            }
            return bearing;

        } else {

            //calculates the angle depending on which quadrant around the robot the ball is in

            if (us.x > x && us.y > y) {
                bearing = 360 - Math.toDegrees(Math.atan((us.x - x) / (us.y - y)));

            } else if (us.x < x && us.y > y) {
                bearing = Math.toDegrees(Math.atan((x - us.x) / (us.y - y)));

            } else if (us.x < x && us.y < y) {
                bearing = 180 - Math.toDegrees(Math.atan((x - us.x) / (y - us.y)));

            } else {
                bearing = 180 + Math.toDegrees(Math.atan((us.x - x) / (y - us.y)));
            }
            return bearing;
        }
    }
	
	public static double turnAngle(double botBearing, double toBearing) {
    	
    	botBearing = Math.toDegrees(botBearing);
        if (botBearing >= toBearing) {
            double turnAngle = botBearing - toBearing;
            if (turnAngle > 180) {
                return 360 - turnAngle;
            } else {
                return 0 - turnAngle;
            }
        } else {
            double turnAngle = toBearing - botBearing;
            if (turnAngle > 180) {
                return turnAngle - 360;
            } else {
                return turnAngle;
            }
        }
    }
	
	public static double Turner(Robot us, double x, double y) {
    	double toBearing = findBearing(us, x, y);
        double angle = turnAngle(us.bearing, toBearing);
        return angle;
    }

}

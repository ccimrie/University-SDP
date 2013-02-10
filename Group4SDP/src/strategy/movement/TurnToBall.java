/*
 * Copyright (c) 2012. University of Edinburgh
 */

package strategy.movement;
import world.state.Ball;
import world.state.Robot;
//import comms.control.Server;

public class TurnToBall {

    /*public static void main(String[] args) {
        double angle = findBearing(80, 20, 100, 10);
        System.out.println("Bearing to ball is: " + angle + " degrees.");
        double turnAngle = turnAngle(240, angle);
        System.out.println("Amount to turn is: " + turnAngle + " degrees.");
    }*/

    /*findBearing takes 2 coordinates (the ball and our robot) and calculates the bearing
      * of the ball relative to the robot (using the robot as the origin and "north"
      * as being up on the camera feed)
     */

    public static double findBearing(Robot us, Ball ball) {
    	
    	double ballBearing = 0;

        //checks if the ball is in line horizontally with the ball

//        if (us.x == ball.x) {
//            if (us.y > ball.y) {
//                ballBearing = Math.toDegrees(Math.PI);
//            } else {
//                ballBearing = 0;
//            }
//            return ballBearing;
//
//            //checks if the ball is in line vertically with the ball
//
//        } else if (us.y == ball.y) {
//            if (us.x > ball.x) {
//                ballBearing = Math.toDegrees(Math.PI * 1.5);
//            } else {
//                ballBearing = Math.toDegrees(Math.PI / 2);
//            }
//            return ballBearing;
//
//        } else {

            //calculates the angle depending on which quadrant around the robot the ball is in
        	double xDiff = ball.x - us.x;
        	double yDiff = ball.y - us.y;
        	
            if (xDiff > 0) {
            	if(yDiff > 0){
            		ballBearing = 90 + Math.toDegrees(Math.atan2(Math.abs(yDiff), Math.abs(xDiff)));
            	}
            	else {
            		ballBearing = 90 - Math.toDegrees(Math.atan2(Math.abs(yDiff), Math.abs(xDiff)));
            	}
            }
            else {
            	if(yDiff > 0){
            		ballBearing = 270 - Math.toDegrees(Math.atan2(Math.abs(yDiff), Math.abs(xDiff)));
            	}
            	else {
            		ballBearing = 270 + Math.toDegrees(Math.atan2(Math.abs(yDiff), Math.abs(xDiff)));
            	}
            }
        //}
            return ballBearing;
    }

    /*turnAngle takes the ballBearing (the output from findBearing) and botBearing
      * (the angle the robot is facing from north, which will be supplied from vision)
      * and calculates the difference between these and selects the smallest turn to take.
      * Clockwise turns are a positive angle and anti-clockwise are negative.
     */

    public static double turnAngle(double botBearing, double toBallBearing) {
    	
    	botBearing = Math.toDegrees(botBearing);
        double turnAngle = toBallBearing - botBearing;
        if (turnAngle > 180.0) turnAngle = -360.0 + turnAngle;
        return turnAngle;
    }

    public static double Turner(Robot us, Ball ball) {
    	double ballBearing = findBearing(us, ball);
        double angle = turnAngle(us.bearing, ballBearing);
        return angle;
    }

}
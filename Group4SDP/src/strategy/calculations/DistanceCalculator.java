/*
 * Copyright (c) 2012. University of Edinburgh
 */

package strategy.calculations;

//calculates distance between two points

public class DistanceCalculator {

    public static double Distance(double x1, double y1, double x2, double y2) {

        double xDistance = Math.abs(x1 - x2);
        double yDistance = Math.abs(y1 - y2);

        double distance = Math.sqrt(Math.pow(yDistance, 2) + (Math.pow(xDistance, 2)));

        return distance;

    }

}

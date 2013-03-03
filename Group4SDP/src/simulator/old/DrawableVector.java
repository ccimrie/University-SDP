package simulator.old;

import java.awt.Color;


public class DrawableVector extends DrawableLine {

    /**
     * Create new drawable vector
     * 
     * @param position
     *            position to draw the vector from
     * @param vector
     *            the vector to draw
     * @param colour
     *            colour you want the vector to be drawn
     */
    public DrawableVector(Coord position, Coord vector, Color colour) {
        super(new Line(position, position.add(vector)), colour);
    }
}

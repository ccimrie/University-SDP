package vision.edgevision;

import java.awt.Point;

/**
 * A pair of pixels which mark
 * @author Alex Adams (s1046358)
 */
public class Edge {
	private final Point pix1;
	private final Point pix2;

	public Edge (Point pix1, Point pix2) {
		this.pix1 = pix1;
		this.pix2 = pix2;
	}

	public Point getFirst() {
		return pix1;
	}

	public Point getSecond() {
		return pix2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!this.getClass().equals(obj.getClass())) return false;
		Edge e = (Edge) obj;
		return (this.pix1.equals(e.pix1) && this.pix2.equals(e.pix2));
	}
}

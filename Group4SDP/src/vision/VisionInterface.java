package vision;

import java.awt.Point;
import java.awt.image.BufferedImage;

public interface VisionInterface {
	public void sendFrame(BufferedImage frame, int fps, Point ball,
			Point blueRobot, double blueOrientation,
			Point yellowRobot, double yellowOrientation);
}

package JavaVision;

import java.awt.image.BufferedImage;

public interface VideoReceiver {
	void sendNextFrame(BufferedImage frame, int frameRate, int frameCounter);
}

package vision.interfaces;

import java.awt.image.BufferedImage;

public interface VideoReceiver {
	void sendFrame(BufferedImage frame, int frameRate, int frameCounter);
}

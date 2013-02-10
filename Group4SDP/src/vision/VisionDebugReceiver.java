package vision;

import java.awt.image.BufferedImage;

public interface VisionDebugReceiver {
	public void sendDebugOverlay(BufferedImage debug);
}

package vision;

import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.ArrayList;

import vision.interfaces.VideoReceiver;

/**
 * Class to remove barrel distortion
 * 
 * @author Rado
 * @author James Hulme
 */
public class DistortionFix implements VideoReceiver {
	private static int width = 640;
	private static int height = 480;
	public static double barrelCorrectionX = -0.01;
	public static double barrelCorrectionY = -0.055;

	private ArrayList<VideoReceiver> videoReceivers = new ArrayList<VideoReceiver>();
	private boolean active = true;

	private final PitchConstants pitchConstants;

	public DistortionFix(final PitchConstants pitchConstants) {
		this.pitchConstants = pitchConstants;
	}

	/**
	 * Determines whether the barrel distortion correction is currently active,
	 * i.e. whether the distortion effect is applied to the video stream
	 * 
	 * @return true if the correction is active, false otherwise
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Enables or disables the barrel distortion correction being applied to the
	 * video stream
	 * 
	 * @param active
	 *            true to enable, false to disable
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Remove barrel distortion on whole image
	 * 
	 * Buffers used so we only correct the pitch area not the useless background
	 * area
	 * 
	 * TODO: find an efficient way to merge pixels based on rounding up/down to
	 * avoid "duplicate" pixels - mostly aesthetic
	 * 
	 * @param image
	 *            Frame to correct
	 * @param left
	 *            Left buffer
	 * @param right
	 *            Right buffer
	 * @param top
	 *            Top buffer
	 * @param bottom
	 *            Bottom buffer
	 * @return A new image with no barrel distortion
	 */
	public BufferedImage removeBarrelDistortion(BufferedImage image, int left,
			int right, int top, int bottom) {

		BufferedImage newImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Point p;
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				p = invBarrelCorrect(new Point(i, j));

				if (0 <= p.x && p.x < width && 0 <= p.y && p.y < height) {
					newImage.setRGB(i, j, image.getRGB(p.x, p.y));
				}
			}
		}

		return newImage;
	}

	/**
	 * Barrel correction for single points
	 * 
	 * Used to correct for the distortion of individual points.
	 * 
	 * @see {@link #invBarrelCorrect(Point)} for correcting an image
	 * 
	 * @param p
	 *            Point to fix
	 * @return Fixed Point
	 */
	public static Point barrelCorrect(Point p) {
		// first normalise pixel
		double px = (2 * p.x - width) / (double) width;
		double py = (2 * p.y - height) / (double) height;

		// then compute the radius of the pixel you are working with
		double rad = px * px + py * py;

		// then compute new pixel
		double px1 = px * (1 - barrelCorrectionX * rad);
		double py1 = py * (1 - barrelCorrectionY * rad);

		// then convert back
		int pixi = (int) ((px1 + 1) * width / 2);
		int pixj = (int) ((py1 + 1) * height / 2);

		return new Point(pixi, pixj);
	}

	/**
	 * Inverse barrel correction for single points
	 * 
	 * Used to correct the distortion in an image without producing an odd
	 * grid-like visual artifact
	 * 
	 * @see {@link #invBarrelCorrect(Point)} for correcting an image
	 * 
	 * @param p
	 *            Point to "unfix"
	 */
	public static Point invBarrelCorrect(Point p) {
		// first normalise pixel
		double px = (2 * p.x - width) / (double) width;
		double py = (2 * p.y - height) / (double) height;

		// then compute the radius of the pixel you are working with
		double rad = px * px + py * py;

		// then compute new pixel
		double px1 = px * (1 + barrelCorrectionX * rad);
		double py1 = py * (1 + barrelCorrectionY * rad);

		// then convert back
		int pixi = (int) ((px1 + 1) * width / 2);
		int pixj = (int) ((py1 + 1) * height / 2);
		return new Point(pixi, pixj);
	}

	/**
	 * Registers an object to receive frames from the distortion fix
	 * 
	 * @param receiver
	 *            The object being registered
	 */
	public void addReceiver(VideoReceiver receiver) {
		this.videoReceivers.add(receiver);
	}

	/**
	 * Used to send a frame to the distortion fix
	 * 
	 * @param frame
	 *            The frame being sent
	 * @param frameRate
	 *            The current frame rate
	 * @param frameCounter
	 *            The current frame index
	 */
	@Override
	public void sendFrame(BufferedImage frame, int frameRate, int frameCounter) {
		BufferedImage processedFrame;

		// If the distortion overlay is active, apply it
		if (isActive()) {
			int topBuffer = pitchConstants.getTopBuffer();
			int bottomBuffer = frame.getHeight()
					- pitchConstants.getBottomBuffer();
			int leftBuffer = pitchConstants.getLeftBuffer();
			int rightBuffer = frame.getWidth()
					- pitchConstants.getRightBuffer();

			processedFrame = removeBarrelDistortion(frame, leftBuffer,
					rightBuffer, topBuffer, bottomBuffer);
		}
		// Otherwise just forward the frame as-is
		else
			processedFrame = frame;

		for (VideoReceiver receiver : videoReceivers)
			receiver.sendFrame(processedFrame, frameRate, frameCounter);
	}
}

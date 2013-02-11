package vision;

import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.ArrayList;

import vision.interfaces.VideoReceiver;

/**
 * Class to remove barrel distortion from bufferedimages
 * 
 * @author Rado
 * @author James Hulme
 * 
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
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Remove barrel distortion on whole image
	 * 
	 * Buffers used so we only correct the pitch area not the useless background area
	 * 
	 * @param image Frame to correct
	 * @param left Left buffer
	 * @param right Right buffer
	 * @param top Top buffer
	 * @param bottom Bottom buffer
	 * @return A new image with no barrel distortion
	 */
    public BufferedImage removeBarrelDistortion(BufferedImage image, int left, int right, int top, int bottom){

    	BufferedImage newImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    	
    	Point p;
    	
    	/* Old version
    	for (int i = left; i < right; i++) {
    		for (int j = top; j < bottom; j++) {
    			p = barrelCorrected(new Point(i,j));
    			
    			if(left <= p.x && p.x < right && top <=  p.y&& p.y < bottom ){
    				newImage.setRGB(p.x,p.y, image.getRGB(i,j));
    		    }
			}
		}*/
    	
    	// New version - does not produce the grid artifact
    	// TODO: find an efficient way to merge pixels based on rounding up/down
    	// to avoid "duplicate" pixels - mostly aesthetic, might somewhat affect
    	// vision system accuracy
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
     * Correct for single points
     * 
     * Called by the above function, but also called when overlay is turned off
     * 
     * @param p Point to fix
     * @return Fixed Point
     */
    public static Point barrelCorrected(Point p) {
    	// System.out.println("Pixel: (" + x + ", " + y + ")");
    	// first normalise pixel
    	double px = (2 * p.x - width) / (double) width;
    	double py = (2 * p.y - height) / (double) height;

    	// System.out.println("Norm Pixel: (" + px + ", " + py + ")");
    	// then compute the radius of the pixel you are working with
    	double rad = px * px + py * py;

    	// then compute new pixel
    	double px1 = px * (1 - barrelCorrectionX * rad);
    	double py1 = py * (1 - barrelCorrectionY * rad);

    	// then convert back
    	int pixi = (int) ((px1 + 1) * width / 2);
    	int pixj = (int) ((py1 + 1) * height / 2);
    	// System.out.println("New Pixel: (" + pixi + ", " + pixj + ")");
    	return new Point(pixi, pixj);
    }
    
    /**
     * Inverse correction for single points
     * 
     * @param p Point to "unfix"
     */
    public static Point invBarrelCorrect(Point p) {
    	// System.out.println("Pixel: (" + x + ", " + y + ")");
    	// first normalise pixel
    	double px = (2 * p.x - width) / (double) width;
    	double py = (2 * p.y - height) / (double) height;

    	// System.out.println("Norm Pixel: (" + px + ", " + py + ")");
    	// then compute the radius of the pixel you are working with
    	double rad = px * px + py * py;

    	// then compute new pixel
    	double px1 = px * (1 + barrelCorrectionX * rad);
    	double py1 = py * (1 + barrelCorrectionY * rad);

    	// then convert back
    	int pixi = (int) ((px1 + 1) * width / 2);
    	int pixj = (int) ((py1 + 1) * height / 2);
    	// System.out.println("New Pixel: (" + pixi + ", " + pixj + ")");
    	return new Point(pixi, pixj);
    }

	public void addReceiver(VideoReceiver receiver) {
		this.videoReceivers.add(receiver);
	}

	@Override
	public void sendFrame(BufferedImage frame, int frameRate, int frameCounter) {
		BufferedImage processedFrame;
		
		if (active) {
			int topBuffer = pitchConstants.getTopBuffer();
			int bottomBuffer = frame.getHeight() - pitchConstants.getBottomBuffer();
		  	int leftBuffer = pitchConstants.getLeftBuffer();
		  	int rightBuffer = frame.getWidth() - pitchConstants.getRightBuffer();
		  	
		  	processedFrame = removeBarrelDistortion(frame,
		  			leftBuffer, rightBuffer, topBuffer, bottomBuffer);
		}
		else
			processedFrame = frame;

		for (VideoReceiver receiver : videoReceivers)
			receiver.sendFrame(processedFrame, frameRate, frameCounter);
	}
}

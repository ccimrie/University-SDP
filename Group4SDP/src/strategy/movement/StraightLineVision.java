package strategy.movement;

import vision.PitchConstants;
import vision.ThresholdsState;
import vision.Vision;
import world.state.WorldState;
import au.edu.jcu.v4l4j.V4L4JConstants;

/**
 * @author Alex Adams
 */
public class StraightLineVision {
    private vision.VisionGUI thresholdsGUI;
    private Vision vision;
    private int startY;
    private int prevX, prevY;
    private WorldState worldState = new WorldState();
    
    private static final int CONST_A = 27;
    private static final int CONST_B = 14;
	
	public void initialize() {
	    ThresholdsState thresholdsState = new ThresholdsState();
	
	    // Default to main pitch.
	    PitchConstants pitchConstants = new PitchConstants(0);
	    
	    // Default values for the main vision window.
	    String videoDevice = "/dev/video0";
	    int width = 640;
	    int height = 480;
	    int channel = 0;
	    int videoStandard = V4L4JConstants.STANDARD_PAL;
	    int compressionQuality = 80;
	
	    try {
	        // Create a new Vision object to serve the main vision window.
	        vision = new Vision(videoDevice, width, height, channel, videoStandard,
	                compressionQuality, worldState, thresholdsState, pitchConstants);
	        
	        // Create the Control GUI for threshold setting/etc.
	        thresholdsGUI = new vision.VisionGUI(thresholdsState, worldState, pitchConstants);
	        thresholdsGUI.initGUI();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Sets the starting Y coordinate of the robot
	 * @param y
	 * 		The y coordinate of the robot
	 */
	public void setStart(int x, int y) {
		startY = y;
		prevX = x;
		prevY = y;
	}
	
	/**
	 * Calculates a correction for the robot if it's going off-center
	 * @return
	 * 		A 2-element int[] with the new settings for the main motors
	 */
	public int[] getCorrection() {
		worldState = vision.getWorldState();
		int[] correction = {0, 0};
		int targetY = startY, realY = worldState.getBlueY();
		int motionX, motionY;
		motionX = worldState.getBlueX() - prevX;
		motionY = worldState.getBlueY() - prevY;
		prevX = worldState.getBlueX();
		prevY = worldState.getBlueY();

		
		System.out.println("Motion: (" + motionX + ", " + motionY + ") ");
		System.out.println("Offset: " + (targetY - realY));
		// Apply appropriate correction, proportional to how far off
		// the line we are.

		correction[0] += (CONST_A * ((targetY - realY) / 2)) / CONST_B;
		correction[0] += -(int)(Math.toDegrees(Math.atan((double) motionY / (double) motionX)));
		correction[1] += -(CONST_A * ((targetY - realY) / 2)) / CONST_B;
		correction[1] += (int)(Math.toDegrees(Math.atan((double) motionY / (double) motionX)));
		
		/*if (targetY > realY) {
			correction[0] += (CONST_A * (Math.abs(targetY - realY) / 2)) / CONST_B;
			correction[0] += (int)(Math.toDegrees(Math.atan((double) motionY / (double) motionX)));
			correction[1] += -(CONST_A * (Math.abs(targetY - realY) / 2)) / CONST_B;
			correction[1] += -(int)(Math.toDegrees(Math.atan((double) motionY / (double) motionX)));
		}
		else if (targetY < realY) {
			correction[0] += -(CONST_A * (Math.abs(targetY - realY) / 2)) / CONST_B;
			correction[0] += -(int)(Math.toDegrees(Math.atan((double) motionY / (double) motionX)));
			correction[1] += (CONST_A * (Math.abs(targetY - realY) / 2)) / CONST_B;
			correction[1] += (int)(Math.toDegrees(Math.atan((double) motionY / (double) motionX)));
		}*/
		// else do nothing
		
		if (correction[0] > 127) correction[0] = 127;
		else if (correction[0] < -127) correction[0] = -127;
		if (correction[1] > 127) correction[1] = 127;
		else if (correction[1] < -127) correction[1] = -127;
		
		return correction;
	}
	
	public WorldState getWorldState() {
		return this.vision.getWorldState();
	}
}

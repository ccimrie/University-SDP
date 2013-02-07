package vision;
import vision.gui.VisionGUI;
import au.edu.jcu.v4l4j.V4L4JConstants;

/** 
 * The main class used to run the vision system. Creates the control
 * GUI, and initialises the image processing.
 * 
 * @author s0840449
 */
public class RunVision {
	private static final int SATURATION = 64;
	private static final int BRIGHTNESS = 128;
	private static final int CONTRAST = 64;
	private static final int HUE = 0;
	private static final int CHROMA_GAIN = 0;
	private static final int CHROMA_AGC = 1;
	
    /**
     * The main method for the class. Creates the control
     * GUI, and initialises the image processing.
     * 
     * @param args        Program arguments. Not used.
     */
    public static void main(String[] args) {
        WorldState worldState = new WorldState();

        // Default to main pitch
        PitchConstants pitchConstants = new PitchConstants(0);
        
        // Default values for the main vision window
        String videoDevice = "/dev/video0";
        int width = 640;
        int height = 480;
        int channel = 0;
        int videoStandard = V4L4JConstants.STANDARD_PAL;
        int compressionQuality = 80;

        try {
        	VideoStream vStream = new VideoStream(videoDevice, width, height,
        			channel, videoStandard, compressionQuality);
            
            vStream.setSaturation(SATURATION);
    		vStream.setBrightness(BRIGHTNESS);
    		vStream.setContrast(CONTRAST);
    		vStream.setHue(HUE);
    		vStream.setChromaGain(CHROMA_GAIN);
    		vStream.setChromaAGC(CHROMA_AGC);
    		vStream.updateVideoDeviceSettings();
    		
            // Create a new Vision object to serve the main vision window
            Vision vision = new Vision(worldState, pitchConstants);
    		
    		vStream.addReceiver(vision);
            
            // Create the Control GUI for threshold setting/etc
            VisionGUI gui = new VisionGUI(width, height, worldState, pitchConstants);
            
            vision.addVisionListener(gui);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

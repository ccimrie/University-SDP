package vision;
import au.edu.jcu.v4l4j.V4L4JConstants;

/** 
 * The main class used to run the vision system. Creates the control
 * GUI, and initialises the image processing.
 * 
 * @author s0840449
 */
public class RunVision {
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
            // Create a new Vision object to serve the main vision window
            new Vision(videoDevice, width, height, channel, videoStandard,
                    compressionQuality, worldState, pitchConstants);
            
            // Create the Control GUI for threshold setting/etc
            new VisionGUI(worldState, pitchConstants);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

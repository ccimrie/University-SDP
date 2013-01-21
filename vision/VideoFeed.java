package src.vision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.Control;
import au.edu.jcu.v4l4j.DeviceInfo;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.ImageFormat;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.ImageFormatException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

public class VideoFeed extends Frame implements Runnable {
	private static final int SATURATION = 50;
	private static final int BRIGHTNESS = 200;
	private static final int CONTRAST = 127;
	private static final int HUE = 0;
	private static final int CHROMA_GAIN = 0;
	private static final int CHROMA_AGC = 1;

    BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
	VideoDevice videoDev;
    FrameGrabber frameGrabber;
    
    Thread animation;
    int offset = 0;

    public VideoFeed() {
    	String videoDevice = "/dev/video0";
	    int width = 640;
	    int height = 480;
	    int channel = 0;
	    int videoStandard = V4L4JConstants.STANDARD_PAL;
	    
	    try {
			initFrameGrabber(videoDevice, width, height, channel, videoStandard);
		} catch (V4L4JException e) {
			e.printStackTrace();
		}
	    
        setTitle("Video Feed");
        setVisible(true);
        setSize(640, 480);

        animation = new Thread(this);
        animation.start();

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(
                    WindowEvent windowEvent) {
            	frameGrabber.stopCapture();
                System.exit(0);
            }
        });
    }
    
    private void initFrameGrabber(String videoDevice, int inWidth, int inHeight, int channel, int videoStandard) throws V4L4JException {
        videoDev = new VideoDevice(videoDevice);
    	try {
    		List<Control> controls = videoDev.getControlList().getList();
    		for(Control c: controls) {
    			System.out.println(c.getName());
    			if(c.getName().equals("Contrast"))
    				c.setValue(CONTRAST);
    			else if(c.getName().equals("Brightness"))
    				c.setValue(BRIGHTNESS);
    			else if(c.getName().equals("Hue"))
    				c.setValue(HUE);
    			else if(c.getName().equals("Saturation"))
    				c.setValue(SATURATION);
    			else if(c.getName().equals("Chroma Gain"))
    				c.setValue(CHROMA_GAIN);
    			else if(c.getName().equals("Chroma AGC"))
    				c.setValue(CHROMA_AGC);
    		}
    	}
    	catch(V4L4JException e3) {
    		System.out.println("Cannot set video device settings!");
    	}
    	videoDev.releaseControlList();

        DeviceInfo deviceInfo = videoDev.getDeviceInfo();

        if (deviceInfo.getFormatList().getNativeFormats().isEmpty()) {
        	throw new ImageFormatException("Unable to detect any native formats for the device!");
        }
        ImageFormat imageFormat = deviceInfo.getFormatList().getNativeFormats().get(0);

        frameGrabber = videoDev.getRGBFrameGrabber(inWidth, inHeight, channel, videoStandard, imageFormat);

        frameGrabber.setCaptureCallback(new CaptureCallback() {
            public void exceptionReceived(V4L4JException e) {
                System.err.println("Unable to capture frame:");
                e.printStackTrace();
            }

            public void nextFrame(VideoFrame frame) {
                image = frame.getBufferedImage();
                
                frame.recycle();
            }
        });

        frameGrabber.startCapture();
    }

    public static void main(String[] args) {
        new VideoFeed();
    }

    public void run() {
        while (true) {
        	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            repaint();
        }
    }

    @Override
    public void update(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
}
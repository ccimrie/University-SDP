package vision;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import vision.interfaces.VideoReceiver;

import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.Control;
import au.edu.jcu.v4l4j.DeviceInfo;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.ImageFormat;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.ImageFormatException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/**
 * Reads frames from a video device, giving options for camera controls
 * 
 * @author Alex Adams (s1046358)
 */
public class VideoStream {
	private String videoDevName;
	private int width;
	private int height;
	private int channel;
	private int videoStandard;
	private int compressionQuality;
	private ImageFormat imageFormat;

	private int saturation;
	private int brightness;
	private int contrast;
	private int hue;
	private int chroma_gain;
	private int chroma_agc;

	private VideoDevice videoDev;
	private FrameGrabber frameGrabber;

	private ArrayList<VideoReceiver> videoReceivers = new ArrayList<VideoReceiver>();
	// Used to calculate FPS
	private long lastFrame = System.currentTimeMillis();

	private final CaptureCallback frameGrabberCallback = new CaptureCallback() {
		public void exceptionReceived(V4L4JException e) {
			System.err.println("Unable to capture frame:");
			e.printStackTrace();
		}

		/**
		 * Called by V4L4J when a new frame is generated
		 * 
		 * @param frame
		 *            The frame that was generated
		 */
		public void nextFrame(VideoFrame frame) {
			// Calculate frame rate based on time between calls
			long thisFrame = System.currentTimeMillis();
			int frameRate = (int) (1000 / (thisFrame - lastFrame));
			lastFrame = thisFrame;

			// Wait for video device to initialise properly before reading
			// frames
			if (ready) {
				BufferedImage frameBuffer = frame.getBufferedImage();

				for (VideoReceiver receiver : videoReceivers)
					receiver.sendFrame(frameBuffer, frameRate, frameCounter);
			} else if (frameCounter > 3)
				ready = true;
			++frameCounter;
			frame.recycle();
		}
	};

	private int frameCounter = 0;
	private boolean ready = false;

	/**
	 * Constructs a VideoStream object connected to the specified video device
	 * 
	 * @param videoDevice
	 *            The name of the video device the stream is for
	 * @param width
	 *            The width in pixels of the stream source
	 * @param height
	 *            The height in pixels of the stream source
	 * @param channel
	 *            The video channel of the device
	 * @param videoStandard
	 *            The video standard of the device
	 * @param compressionQuality
	 *            The desired compression quality of the frames as a percentage
	 */
	public VideoStream(String videoDevice, int width, int height, int channel,
			int videoStandard, int compressionQuality) {
		this.videoDevName = videoDevice;
		this.channel = channel;
		this.videoStandard = videoStandard;
		this.compressionQuality = compressionQuality;

		try {
			videoDev = new VideoDevice(videoDevice);
			DeviceInfo deviceInfo = videoDev.getDeviceInfo();

			if (deviceInfo.getFormatList().getNativeFormats().isEmpty()) {
				throw new ImageFormatException(
						"Unable to detect any native formats for the device!");
			}
			imageFormat = deviceInfo.getFormatList().getYUVEncodableFormat(0);

			frameGrabber = videoDev.getJPEGFrameGrabber(width, height, channel,
					videoStandard, compressionQuality, imageFormat);
			frameGrabber.setCaptureCallback(frameGrabberCallback);
			frameGrabber.startCapture();

			this.width = frameGrabber.getWidth();
			this.height = frameGrabber.getHeight();
		} catch (V4L4JException e) {
			System.err.println("Couldn't initialise the frame grabber: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				frameGrabber.stopCapture();
			}
		});
	}

	/**
	 * Reinitialises the frame grabber for the video stream. This is called when
	 * either the video standard or compression quality is changed since these
	 * can't be updated otherwise
	 * 
	 * @throws V4L4JException
	 *             when the frame grabber fails to start capturing frames with
	 *             the new settings
	 */
	private void reinitialiseFrameGrabber() throws V4L4JException {
		frameGrabber.stopCapture();
		frameGrabber = videoDev.getJPEGFrameGrabber(width, height, channel,
				videoStandard, compressionQuality, imageFormat);
		frameGrabber.setCaptureCallback(frameGrabberCallback);
		frameGrabber.startCapture();
	}

	/**
	 * Gets the name of the video device the video stream is linked to
	 * 
	 * @return The name of the video device
	 */
	public String getVideoDeviceName() {
		return this.videoDevName;
	}

	/**
	 * Gets the width and height of the video stream as a Dimension object
	 * 
	 * @return The dimensions of the video stream in pixels
	 */
	public Dimension getDimensions() {
		return new Dimension(this.width, this.height);
	}

	/**
	 * Sets the video channel for the video stream
	 * 
	 * @param channel
	 *            The channel to set the video stream to
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}

	/**
	 * Gets the video channel used by the video stream
	 * 
	 * @return The channel used by the video stream
	 */
	public int getChannel() {
		return this.channel;
	}

	/**
	 * Sets a new value for the video standard of the video stream
	 * 
	 * @param videoStandard
	 */
	public void setVideoStandard(int videoStandard) {
		try {
			this.videoStandard = videoStandard;
			// Adjust the frame grabber to the new setting
			reinitialiseFrameGrabber();
		} catch (V4L4JException e) {
			System.err.println("Couldn't change the video standard: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Gets the video standard currently used by the video stream
	 * 
	 * @return The video standard used by the video stream
	 */
	public int getVideoStandard() {
		return this.videoStandard;
	}

	/**
	 * Sets a new value for the JPEG compression quality of the video stream
	 * 
	 * @param compressionQuality
	 */
	public void setCompressionQuality(int compressionQuality) {
		try {
			this.compressionQuality = compressionQuality;
			// Adjust the frame grabber to the new setting
			reinitialiseFrameGrabber();
		} catch (V4L4JException e) {
			System.err.println("Couldn't change the compressionQuality: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Gets the JPEG compression quality of the video stream
	 * 
	 * @return The JPEG compression quality the video stream is set to as a
	 *         percentage
	 */
	public int getCompressionQuality() {
		return this.compressionQuality;
	}

	/**
	 * Gets the saturation setting of the video device
	 * 
	 * @return The saturation setting for the video device
	 */
	public int getSaturation() {
		return saturation;
	}

	/**
	 * Sets the saturation setting of the video device
	 * 
	 * @param saturation
	 *            The new setting
	 */
	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}

	/**
	 * Gets the brightness setting of the video device
	 * 
	 * @return The brightness setting for the video device
	 */
	public int getBrightness() {
		return brightness;
	}

	/**
	 * Sets the brightness of the video device
	 * 
	 * @param brightness
	 */
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	/**
	 * Gets the contrast setting of the video device
	 * 
	 * @return The contrast setting for the video device
	 */
	public int getContrast() {
		return contrast;
	}

	/**
	 * Sets the contrast of the video device
	 * 
	 * @param contrast
	 */
	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	/**
	 * Gets the hue setting of the video device
	 * 
	 * @return The hue setting for the video device
	 */
	public int getHue() {
		return hue;
	}

	/**
	 * Sets the hue of the video device
	 * 
	 * @param hue
	 */
	public void setHue(int hue) {
		this.hue = hue;
	}

	/**
	 * Gets the Chroma Gain setting of the video device
	 * 
	 * @return The Chroma Gain setting for the video device
	 */
	public int getChromaGain() {
		return chroma_gain;
	}

	/**
	 * Sets the Chroma Gain setting of the video device
	 * 
	 * @param chromaGain
	 */
	public void setChromaGain(int chromaGain) {
		this.chroma_gain = chromaGain;
	}

	/**
	 * Gets the Chroma AGC setting of the video device
	 * 
	 * @return The Chroma AGC setting for the video device
	 */
	public boolean getChromaAGC() {
		return (chroma_agc == 1) ? true : false;
	}

	/**
	 * Sets the Chroma AGC setting of the video device
	 * 
	 * @param chromaAGC
	 */
	public void setChromaAGC(boolean chromaAGC) {
		this.chroma_agc = chromaAGC ? 1 : 0;
	}

	/**
	 * Updates the video device's controls with the settings of the video
	 * stream. This should be called after any call to setBrightness, etc if the
	 * settings are intended to affect the device output
	 */
	public void updateVideoDeviceSettings() {
		try {
			List<Control> controls = videoDev.getControlList().getList();
			for (Control c : controls) {
				if (c.getName().equals("Contrast"))
					c.setValue(contrast);
				else if (c.getName().equals("Brightness"))
					c.setValue(brightness);
				else if (c.getName().equals("Hue"))
					c.setValue(hue);
				else if (c.getName().equals("Saturation"))
					c.setValue(saturation);
				else if (c.getName().equals("Chroma Gain"))
					c.setValue(chroma_gain);
				else if (c.getName().equals("Chroma AGC"))
					c.setValue(chroma_agc);
			}
		} catch (V4L4JException e) {
			System.err.println("Cannot set video device settings: "
					+ e.getMessage());
			e.printStackTrace();
		}
		videoDev.releaseControlList();
	}

	/**
	 * Registers an object to receive frames from the video stream
	 * 
	 * @param receiver
	 *            The object being registered
	 */
	public void addReceiver(VideoReceiver receiver) {
		this.videoReceivers.add(receiver);
	}
}

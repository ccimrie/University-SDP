package JavaVision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

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
 * The main class for showing the video feed and processing the video data.
 * Identifies ball and robot locations, and robot orientations.
 * 
 * @author s0840449
 */
// TODO: separate the GUI - can combine with VisionGUI.java
public class Vision extends WindowAdapter implements VideoReceiver {
	// Video variables & constants
	private VideoStream vStream;
	private int width, height;
	
	boolean selectionActive = false;
	Point anchor;
	int a;
	int b;
	int c;
	int d;

	private static final int SATURATION = 64;
	private static final int BRIGHTNESS = 128;
	private static final int CONTRAST = 64;
	private static final int HUE = 0;
	private static final int CHROMA_GAIN = 0;
	private static final int CHROMA_AGC = 1;

	// GUI variables
	private JLabel label;
	private JFrame windowFrame;

	// Variables used in processing video
	private ThresholdsState thresholdsState;
	private PitchConstants pitchConstants;
	private static final double barrelCorrectionX = -0.016;
	private static final double barrelCorrectionY = -0.13;
	private WorldState worldState;

	/**
	 * Default constructor.
	 * 
	 * @param videoDevice
	 *            The video device file to capture from.
	 * @param width
	 *            The desired capture width.
	 * @param height
	 *            The desired capture height.
	 * @param videoStandard
	 *            The capture standard.
	 * @param channel
	 *            The capture channel.
	 * @param compressionQuality
	 *            The JPEG compression quality.
	 * @param worldState
	 * @param thresholdsState
	 * @param pitchConstants
	 * 
	 * @throws V4L4JException
	 *             If any parameter if invalid.
	 */
	public Vision(String videoDevice, int width, int height, int channel,
			int videoStandard, int compressionQuality, WorldState worldState,
			ThresholdsState thresholdsState, PitchConstants pitchConstants)
			throws V4L4JException {
		// Set the state fields.
		this.worldState = worldState;
		this.thresholdsState = thresholdsState;
		this.pitchConstants = pitchConstants;

		// Initialise the video stream
		vStream = new VideoStream(videoDevice, width, height, channel, videoStandard, compressionQuality);
		
		vStream.setSaturation(SATURATION);
		vStream.setBrightness(BRIGHTNESS);
		vStream.setContrast(CONTRAST);
		vStream.setHue(HUE);
		vStream.setChromaGain(CHROMA_GAIN);
		vStream.setChromaAGC(CHROMA_AGC);
		vStream.updateVideoDeviceSettings();
		
		vStream.registerReceiver(this);
		
		this.width = width;
		this.height = height;

		// Initialise the GUI that displays the video feed.
		initGUI();
	}
	

	/**
	 * @return The current world state
	 */
	public WorldState getWorldState() {
		return worldState;
	}

	/**
	 * Sends the vision system the next frame to process
	 */
	public void sendNextFrame(BufferedImage frame, int frameRate, int frameCounter) {
		processAndUpdateImage(frame, frameRate, frameCounter);
	}

	/**
	 * Creates the graphical interface components and initialises them
	 */
	private void initGUI() {
		windowFrame = new JFrame("Vision Window");
		label = new JLabel();
		windowFrame.getContentPane().add(label);
		windowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		windowFrame.addWindowListener(this);
		windowFrame.setVisible(true);
		windowFrame.setSize(width, height);

		MouseInputAdapter mouseSelector = new MouseInputAdapter() {
			Rectangle selection;

			public void mousePressed(MouseEvent e) {
				anchor = e.getPoint();
				System.out.println(anchor.x);
				System.out.println(anchor.y);
				selection = new Rectangle(anchor);
				selectionActive = true;
			}

			public void mouseDragged(MouseEvent e) {
				selection.setBounds((int) Math.min(anchor.x, e.getX()),
						(int) Math.min(anchor.y, e.getY()),
						(int) Math.abs(e.getX() - anchor.x),
						(int) Math.abs(e.getY() - anchor.y));

				a = (int) Math.min(anchor.x, e.getX());
				b = (int) Math.min(anchor.y, e.getY());
				c = (int) Math.abs(e.getX() - anchor.x);
				d = (int) Math.abs(e.getY() - anchor.y);
			}

			public void mouseReleased(MouseEvent e) {
				selectionActive = false;
				Object[] options = { "Main Pitch", "Side Pitch", "Cancel" };
				int pitchNum = JOptionPane.showOptionDialog(windowFrame.getComponent(0),
								"The parameters are to be set for this pitch",
								"A Silly Question",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);

				// If option wasn't Cancel and the dialog wasn't closed
				if (pitchNum != 2 && pitchNum != JOptionPane.CLOSED_OPTION) {
					System.out.println(pitchNum);
					try {
						int top = b;
						int bottom = height - d - b;
						int left = a;
						int right = width - c - a;
						if (top > 0 && bottom > 0 && left > 0 && right > 0) {
							FileWriter writer = new FileWriter(new File(
									"constants/pitch" + pitchNum + "Dimensions"));
	
							writer.write("" + top + "\n");
							writer.write("" + bottom + "\n");
							writer.write("" + left + "\n");
							writer.write("" + right + "\n");
	
							writer.flush();
							writer.close();
							System.out.println("Wrote pitch const");
						} else {
							System.out.println("Pitch selection NOT succesful");
						}
						System.out.print("Top: " + top + " Bottom " + bottom);
						System.out.println(" Right " + right + " Left " + left);
					} catch (IOException e1) {
						System.out.println("Error writing pitch dimensions");
						e1.printStackTrace();
					}
	
					System.out.println("A: " + a + " B: " + b + " C: " + c + " D:" + d);
				}
				windowFrame.repaint();
			}
		};

		label.addMouseListener(mouseSelector);
		label.addMouseMotionListener(mouseSelector);
	}

	/**
	 * Catches the window closing event, so that we can free up resources before
	 * exiting.
	 * 
	 * @param e
	 *            The window closing event.
	 */
	public void windowClosing(WindowEvent e) {
		windowFrame.dispose();

		System.exit(0);
	}

	/**
	 * Determines if a pixel is part of the blue T, based on input RGB colours
	 * and hsv values.
	 * 
	 * @param color
	 *            The RGB colours for the pixel.
	 * @param hsbvals
	 *            The HSV values for the pixel.
	 * 
	 * @return True if the RGB and HSV values are within the defined thresholds
	 *         (and thus the pixel is part of the blue T), false otherwise.
	 */
	private boolean isBlue(Color color, float[] hsbvals) {
		return hsbvals[0] <= thresholdsState.getBlue_h_high()
				&& hsbvals[0] >= thresholdsState.getBlue_h_low()
				&& hsbvals[1] <= thresholdsState.getBlue_s_high()
				&& hsbvals[1] >= thresholdsState.getBlue_s_low()
				&& hsbvals[2] <= thresholdsState.getBlue_v_high()
				&& hsbvals[2] >= thresholdsState.getBlue_v_low()
				&& color.getRed() <= thresholdsState.getBlue_r_high()
				&& color.getRed() >= thresholdsState.getBlue_r_low()
				&& color.getGreen() <= thresholdsState.getBlue_g_high()
				&& color.getGreen() >= thresholdsState.getBlue_g_low()
				&& color.getBlue() <= thresholdsState.getBlue_b_high()
				&& color.getBlue() >= thresholdsState.getBlue_b_low();
	}

	/**
	 * Determines if a pixel is part of the yellow T, based on input RGB colours
	 * and hsv values.
	 * 
	 * @param color
	 *            The RGB colours for the pixel.
	 * @param hsbvals
	 *            The HSV values for the pixel.
	 * 
	 * @return True if the RGB and HSV values are within the defined thresholds
	 *         (and thus the pixel is part of the yellow T), false otherwise.
	 */
	private boolean isYellow(Color colour, float[] hsbvals) {
		return hsbvals[0] <= thresholdsState.getYellow_h_high()
				&& hsbvals[0] >= thresholdsState.getYellow_h_low()
				&& hsbvals[1] <= thresholdsState.getYellow_s_high()
				&& hsbvals[1] >= thresholdsState.getYellow_s_low()
				&& hsbvals[2] <= thresholdsState.getYellow_v_high()
				&& hsbvals[2] >= thresholdsState.getYellow_v_low()
				&& colour.getRed() <= thresholdsState.getYellow_r_high()
				&& colour.getRed() >= thresholdsState.getYellow_r_low()
				&& colour.getGreen() <= thresholdsState.getYellow_g_high()
				&& colour.getGreen() >= thresholdsState.getYellow_g_low()
				&& colour.getBlue() <= thresholdsState.getYellow_b_high()
				&& colour.getBlue() >= thresholdsState.getYellow_b_low();
	}

	/**
	 * Determines if a pixel is part of the ball, based on input RGB colours and
	 * hsv values.
	 * 
	 * @param color
	 *            The RGB colours for the pixel.
	 * @param hsbvals
	 *            The HSV values for the pixel.
	 * 
	 * @return True if the RGB and HSV values are within the defined thresholds
	 *         (and thus the pixel is part of the ball), false otherwise.
	 */
	private boolean isBall(Color colour, float[] hsbvals) {
		return hsbvals[0] <= thresholdsState.getBall_h_high()
				&& hsbvals[0] >= thresholdsState.getBall_h_low()
				&& hsbvals[1] <= thresholdsState.getBall_s_high()
				&& hsbvals[1] >= thresholdsState.getBall_s_low()
				&& hsbvals[2] <= thresholdsState.getBall_v_high()
				&& hsbvals[2] >= thresholdsState.getBall_v_low()
				&& colour.getRed() <= thresholdsState.getBall_r_high()
				&& colour.getRed() >= thresholdsState.getBall_r_low()
				&& colour.getGreen() <= thresholdsState.getBall_g_high()
				&& colour.getGreen() >= thresholdsState.getBall_g_low()
				&& colour.getBlue() <= thresholdsState.getBall_b_high()
				&& colour.getBlue() >= thresholdsState.getBall_b_low();
	}

	/**
	 * Determines if a pixel is part of either grey circle, based on input RGB
	 * colours and hsv values.
	 * 
	 * @param color
	 *            The RGB colours for the pixel.
	 * @param hsbvals
	 *            The HSV values for the pixel.
	 * 
	 * @return True if the RGB and HSV values are within the defined thresholds
	 *         (and thus the pixel is part of a grey circle), false otherwise.
	 */
	private boolean isGrey(Color colour, float[] hsbvals) {
		return hsbvals[0] <= thresholdsState.getGrey_h_high()
				&& hsbvals[0] >= thresholdsState.getGrey_h_low()
				&& hsbvals[1] <= thresholdsState.getGrey_s_high()
				&& hsbvals[1] >= thresholdsState.getGrey_s_low()
				&& hsbvals[2] <= thresholdsState.getGrey_v_high()
				&& hsbvals[2] >= thresholdsState.getGrey_v_low()
				&& colour.getRed() <= thresholdsState.getGrey_r_high()
				&& colour.getRed() >= thresholdsState.getGrey_r_low()
				&& colour.getGreen() <= thresholdsState.getGrey_g_high()
				&& colour.getGreen() >= thresholdsState.getGrey_g_low()
				&& colour.getBlue() <= thresholdsState.getGrey_b_high()
				&& colour.getBlue() >= thresholdsState.getGrey_b_low();
	}

	/**
	 * Determines if a pixel is part of either green plate, based on input RGB
	 * colours and hsv values.
	 * 
	 * @param color
	 *            The RGB colours for the pixel.
	 * @param hsbvals
	 *            The HSV values for the pixel.
	 * 
	 * @return True if the RGB and HSV values are within the defined thresholds
	 *         (and thus the pixel is part of a green plate), false otherwise.
	 */
	private boolean isGreen(Color colour, float[] hsbvals) {
		return hsbvals[0] <= thresholdsState.getGreen_h_high()
				&& hsbvals[0] >= thresholdsState.getGreen_h_low()
				&& hsbvals[1] <= thresholdsState.getGreen_s_high()
				&& hsbvals[1] >= thresholdsState.getGreen_s_low()
				&& hsbvals[2] <= thresholdsState.getGreen_v_high()
				&& hsbvals[2] >= thresholdsState.getGreen_v_low()
				&& colour.getRed() <= thresholdsState.getGreen_r_high()
				&& colour.getRed() >= thresholdsState.getGreen_r_low()
				&& colour.getGreen() <= thresholdsState.getGreen_g_high()
				&& colour.getGreen() >= thresholdsState.getGreen_g_low()
				&& colour.getBlue() <= thresholdsState.getGreen_b_high()
				&& colour.getBlue() >= thresholdsState.getGreen_b_low();
	}

	/**
	 * Applies a correction to counteract the barrel or fish-eye effect caused
	 * by the camera lens
	 * 
	 * @param original
	 *            The point before correction
	 * @return The point after correction
	 */
	public Point convertToBarrelCorrected(Point original) {
		// first normalise pixel
		double px = (2 * original.getX() - width) / (double) width;
		double py = (2 * original.getY() - height * 1.005) / (double) height;

		// then compute the radius of the pixel you are working with
		double rad = px * px + py * py;

		// then compute new pixel
		double px1 = px * (1 - barrelCorrectionX * rad);
		double py1 = py * (1 - barrelCorrectionY * rad);

		// then convert back
		int pixi = (int) (((px1 + 1) * width) / 2);
		int pixj = (int) (((py1 + 1) * height) / 2);

		return new Point(pixi, pixj);
	}

	/**
	 * Processes an input image, extracting the ball and robot positions and
	 * robot orientations from it, and then displays the image (with some
	 * additional graphics layered on top for debugging) in the vision frame.
	 * 
	 * @param image
	 *            The image to process and then show.
	 * @param counter
	 */
	public void processAndUpdateImage(BufferedImage image, int frameRate, int counter) {
		int ballX = 0;
		int ballY = 0;
		int numBallPos = 0;

		int blueX = 0;
		int blueY = 0;
		int numBluePos = 0;

		int yellowX = 0;
		int yellowY = 0;
		int numYellowPos = 0;

		ArrayList<Integer> ballXPoints = new ArrayList<Integer>();
		ArrayList<Integer> ballYPoints = new ArrayList<Integer>();
		ArrayList<Integer> blueXPoints = new ArrayList<Integer>();
		ArrayList<Integer> blueYPoints = new ArrayList<Integer>();
		ArrayList<Integer> yellowXPoints = new ArrayList<Integer>();
		ArrayList<Integer> yellowYPoints = new ArrayList<Integer>();

		// For the black frame of the pitch
		//
		// ArrayList<Integer> blackXupperPoints = new ArrayList<Integer>();
		// ArrayList<Integer> blackXlowerPoints = new ArrayList<Integer>();

		int topBuffer = pitchConstants.topBuffer;
		int bottomBuffer = pitchConstants.bottomBuffer;
		int leftBuffer = pitchConstants.leftBuffer;
		int rightBuffer = pitchConstants.rightBuffer;

		// For every pixel within the pitch, test to see if it belongs to the
		// ball, the yellow T, the blue T, either green plate or a grey circle.
		for (int row = topBuffer; row < image.getHeight() - bottomBuffer; row++) {
			for (int column = leftBuffer; column < image.getWidth()
					- rightBuffer; column++) {
				// The RGB colours and hsv values for the current pixel.
				Color c = new Color(image.getRGB(column, row));
				float hsbvals[] = new float[3];
				Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);

				if (thresholdsState.isGrey_debug() && isGrey(c, hsbvals)) {
					image.setRGB(column, row, 0xFFFF0099);
				}
				if (thresholdsState.isGreen_debug() && isGreen(c, hsbvals)) {
					image.setRGB(column, row, 0xFFFF0099);
				}

				// Is this pixel part of the Blue T?
				if (isBlue(c, hsbvals)) {
					blueX += column;
					blueY += row;
					numBluePos++;

					blueXPoints.add(column);
					blueYPoints.add(row);

					// If we're in the "Blue" tab, we show what pixels we're
					// looking at, for debugging and to help with threshold
					// setting.
					if (thresholdsState.isBlue_debug()) {
						image.setRGB(column, row, 0xFFFF0099);
					}
				}

				// Is this pixel part of the Yellow T?
				if (isYellow(c, hsbvals)) {
					yellowX += column;
					yellowY += row;
					numYellowPos++;

					yellowXPoints.add(column);
					yellowYPoints.add(row);

					// If we're in the "Yellow" tab, we show what pixels we're
					// looking at, for debugging and to help with threshold
					// setting.
					if (thresholdsState.isYellow_debug()) {
						image.setRGB(column, row, 0xFFFF0099);
					}
				}

				// Is this pixel part of the Ball?
				if (isBall(c, hsbvals)) {
					ballX += column;
					ballY += row;
					numBallPos++;

					ballXPoints.add(column);
					ballYPoints.add(row);

					// If we're in the "Ball" tab, we show what pixels we're
					// looking at, for debugging and to help with threshold
					// setting.
					if (thresholdsState.isBall_debug()) {
						image.setRGB(column, row, 0xFF000000);
					}
				}
			}
		}

		// Position objects to hold the centre point of the ball and both
		// robots.
		Position ball;
		Position blue;
		Position yellow;

		// If we have only found a few 'Ball' pixels, chances are that the ball
		// has not actually been detected.
		if (numBallPos > 0) {
			ballX /= numBallPos;
			ballY /= numBallPos;

			ball = new Position(ballX, ballY);
			ball.fixValues(worldState.getBallX(), worldState.getBallY());
			ball.filterPoints(ballXPoints, ballYPoints);
		} else {
			ball = new Position(worldState.getBallX(), worldState.getBallY());
		}

		Point ballP = new Point(ballX, ballY);
		ArrayList<Point> goodPoints = Position.removeOutliers(ballXPoints,
				ballYPoints, ballP);

		ballXPoints = new ArrayList<Integer>();
		ballYPoints = new ArrayList<Integer>();
		for (int k = 0; k < goodPoints.size(); k++) {
			ballXPoints.add((int) goodPoints.get(k).getX());
			ballYPoints.add((int) goodPoints.get(k).getY());
		}

		// If we have only found a few 'Blue' pixels, chances are that the ball
		// has not actually been detected.
		if (numBluePos > 0) {
			blueX /= numBluePos;
			blueY /= numBluePos;

			blue = new Position(blueX, blueY);
			blue.fixValues(worldState.getBlueX(), worldState.getBlueY());
			blue.filterPoints(blueXPoints, blueYPoints);
		} else {
			blue = new Position(worldState.getBlueX(), worldState.getBlueY());
		}

		// If we have only found a few 'Yellow' pixels, chances are that the
		// yellow T has not actually been detected.
		if (numYellowPos > 0) {
			yellowX /= numYellowPos;
			yellowY /= numYellowPos;

			yellow = new Position(yellowX, yellowY);
			yellow.fixValues(worldState.getYellowX(), worldState.getYellowY());
			yellow.filterPoints(yellowXPoints, yellowYPoints);
		} else {
			yellow = new Position(worldState.getYellowX(), worldState.getYellowY());
		}

		Point yellowP = new Point(yellowX, yellowY);

		goodPoints = Position.removeOutliers(yellowXPoints, yellowYPoints, yellowP);

		yellowXPoints = new ArrayList<Integer>();
		yellowYPoints = new ArrayList<Integer>();
		for (int k = 0; k < goodPoints.size(); k++) {
			yellowXPoints.add((int) goodPoints.get(k).getX());
			yellowYPoints.add((int) goodPoints.get(k).getY());
		}

		// Attempt to find the blue robot's orientation.
		try {
			double blueOrientation = findOrient(image, blue, blueXPoints,
					blueYPoints, 120, 500);

			// If angle hasn't changed much, just use the old one
			double diff = Math.abs(blueOrientation - worldState.getBlueOrientation());
			if (blueOrientation != 0 && diff > 0.05) {
				// Clamp angle to 5 degree increments
				blueOrientation = Math.round(Math.toDegrees(blueOrientation) / 5) * 5;
				worldState.setBlueOrientation(Math.toRadians(blueOrientation));
			}
		} catch (NoAngleException e) {
			// TODO: fix the problem properly
//			System.out.println(e.getMessage());
//			e.printStackTrace();
		}

		// Attempt to find the yellow robot's orientation.
		try {
			double yellowOrientation = findOrient(image, yellow, yellowXPoints,yellowYPoints, 120, 500);

			// If angle hasn't changed much, just use the old one
			double diff = Math.abs(yellowOrientation - worldState.getYellowOrientation());
			if (yellowOrientation != 0 && diff > 0.05) {
				// Clamp angle to 5 degree increments
				yellowOrientation = Math.round(Math.toDegrees(yellowOrientation) / 5) * 5;
				worldState.setYellowOrientation(Math.toRadians(yellowOrientation));
			}
		} catch (NoAngleException e) {
			// TODO: fix the problem properly
//			System.out.println(e.getMessage());
//			e.printStackTrace();
		}

		// Apply Barrel correction (fixes fish-eye effect)
		// ball = convertToBarrelCorrected(ball);
		// blue = convertToBarrelCorrected(blue);
		// yellow = convertToBarrelCorrected(yellow);

		worldState.setBallX(ball.getX());
		worldState.setBallY(ball.getY());

		worldState.setBlueX(blue.getX());
		worldState.setBlueY(blue.getY());
		worldState.setYellowX(yellow.getX());
		worldState.setYellowY(yellow.getY());
		worldState.updateCounter();

		// Draw the image onto the vision frame.
		Graphics frameGraphics = label.getGraphics();
		Graphics imageGraphics = image.getGraphics();

		// Only display these markers in non-debug mode.
		if (!(thresholdsState.isBall_debug() || thresholdsState.isBlue_debug()
				|| thresholdsState.isYellow_debug() || thresholdsState.isGreen_debug()
				|| thresholdsState.isGrey_debug())) {
			imageGraphics.setColor(Color.red);
			imageGraphics.drawLine(0, ball.getY(), 640, ball.getY());
			imageGraphics.drawLine(ball.getX(), 0, ball.getX(), 480);
			imageGraphics.setColor(Color.blue);
			imageGraphics.drawOval(blue.getX() - 15, blue.getY() - 15, 30, 30);
			imageGraphics.setColor(Color.yellow);
			imageGraphics.drawOval(yellow.getX() - 15, yellow.getY() - 15, 30, 30);
			imageGraphics.setColor(Color.white);
		}

		// Display the FPS that the vision system is running at
		imageGraphics.setColor(Color.white);
		imageGraphics.drawString("FPS: " + frameRate, 15, 15);

		// Display Ball & Robot Positions
		imageGraphics.drawString("Ball: (" + worldState.getBallX() +
								 ", " + worldState.getBallY() + ")", 15, 30);
		imageGraphics.drawString("Blue: (" + worldState.getBlueX() +
								 ", " + worldState.getBlueY() + ") Orientation: " +
								 Math.toDegrees(worldState.getBlueOrientation()), 15, 45);
		imageGraphics.drawString("Yellow: (" + worldState.getYellowX() +
								 ", " + worldState.getYellowY() + ") Orientation: " +
								 Math.toDegrees(worldState.getYellowOrientation()), 15, 60);

		// Draw the line around the pitch dimensions
		if (selectionActive) {
			imageGraphics.setColor(Color.YELLOW);
			imageGraphics.drawRect(a, b, c, d);
		}
		frameGraphics.drawImage(image, 0, 0, width, height, null);
	}

	// TODO: find out what this is for and how it works
	public Position[] findFurthest(Position centroid,
			ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int distT,
			int distM) throws NoAngleException {
		if (xpoints.size() < 5) {
			throw new NoAngleException("List of points is too small to calculate angle");
		}

		Position[] points = new Position[4];
		for (int i = 0; i < points.length; i++) {
			points[i] = new Position(0, 0);
			points[i] = new Position(0, 0);
		}

		double dist = 0;
		int index = 0;

		for (int i = 0; i < xpoints.size(); i++) {
			double currentDist = Position.sqrdEuclidDist(
					centroid.getX(), centroid.getY(), xpoints.get(i), ypoints.get(i));

			if (currentDist > dist && currentDist < distM) {
				dist = currentDist;
				index = i;
			}
		}
		points[0].setX(xpoints.get(index));
		points[0].setY(ypoints.get(index));

		index = 0;
		dist = 0;

		for (int i = 0; i < xpoints.size(); i++) {
			double dc = Position.sqrdEuclidDist(
					centroid.getX(), centroid.getY(), xpoints.get(i), ypoints.get(i));
			double currentDist = Position.sqrdEuclidDist(
					points[0].getX(), points[0].getY(), xpoints.get(i), ypoints.get(i));
			if (currentDist > dist && dc < distM) {
				dist = currentDist;
				index = i;
			}
		}
		points[1].setX(xpoints.get(index));
		points[1].setY(ypoints.get(index));

		index = 0;
		dist = 0;

		if (points[0].getX() == points[1].getX()) {
			throw new NoAngleException("Points have same X-coordinate");
		}
		double m1 = (points[0].getY() - points[1].getY()) / (points[0].getX() - points[1].getX());
		double b1 = points[0].getY() - m1 * points[0].getX();

		for (int i = 0; i < xpoints.size(); i++) {
			double d = Math.abs(m1 * xpoints.get(i) - ypoints.get(i) + b1) / (Math.sqrt(m1 * m1 + 1));

			double dc = Position.sqrdEuclidDist(
					centroid.getX(), centroid.getY(), xpoints.get(i), ypoints.get(i));
			if (d > dist && dc < distM) {
				dist = d;
				index = i;
			}
		}

		points[2].setX(xpoints.get(index));
		points[2].setY(ypoints.get(index));

		index = 0;
		dist = 0;
		for (int i = 0; i < xpoints.size(); i++) {
			double dc = Position.sqrdEuclidDist(
					centroid.getX(), centroid.getY(), xpoints.get(i), ypoints.get(i));
			double d3 = Position.sqrdEuclidDist(
					points[2].getX(), points[2].getY(), xpoints.get(i), ypoints.get(i));
			if (d3 > dist && dc < distM) {
				dist = d3;
				index = i;
			}
		}
		points[3].setX(xpoints.get(index));
		points[3].setY(ypoints.get(index));

		Graphics frameGraphics = label.getGraphics();
		for (int i = 0; i < points.length; i++)
			frameGraphics.drawOval(points[i].getX(), points[i].getY(), 3, 3);

		return points;
	}

	// TODO: find out how this works
	public double findOrient(BufferedImage image, Position centroid,
			ArrayList<Integer> xPoints, ArrayList<Integer> yPoints, int distT,
			int distM) throws NoAngleException {
		Graphics imageGraphics = image.getGraphics();

		Position finalPoint = new Position(0, 0);
		if (xPoints.size() != yPoints.size()) {
			throw new NoAngleException("");	
		}

		Position[] furthest = findFurthest(centroid, xPoints, yPoints, distT, distM);

		int[][] distanceMatrix = new int[4][4];
		for (int i = 0; i < distanceMatrix.length; i++)
			for (int j = 0; j < distanceMatrix[0].length; j++) {
				distanceMatrix[i][j] = Position.sqrdEuclidDist(
						furthest[i].getX(), furthest[i].getY(),
						furthest[j].getX(), furthest[j].getY());
			}

		int distance = Integer.MAX_VALUE;
		int index1 = 0;
		int index2 = 0;
		int index3 = 0;
		int index4 = 0;

		for (int i = 0; i < distanceMatrix.length; i++)
			for (int j = 0; j < distanceMatrix[0].length; j++) {
				if (distanceMatrix[i][j] < distance && distanceMatrix[i][j] != 0) {
					distance = distanceMatrix[i][j];
					index1 = i;
					index2 = j;
				}
			}

		if (index1 + index2 != 3) {
			index3 = 3 - index1;
			index4 = 3 - index2;
		} else {
			if (index1 == 0 || index2 == 0) {
				index3 = 2;
				index4 = 1;
			} else if (index1 == 1 || index2 == 1) {
				index3 = 3;
				index4 = 0;
			}
		}

		Position p1 = furthest[index1];
		Position p2 = furthest[index3];
		Position p3 = furthest[index2];
		Position p4 = furthest[index4];

		if (furthest[index1].getY() < furthest[index2].getY()) {
			if (furthest[index3].getY() < furthest[index4].getY()) {
				p2 = furthest[index3];
				p4 = furthest[index4];
			} else {
				p2 = furthest[index4];
				p4 = furthest[index3];
			}
		} else if (furthest[index1].getY() > furthest[index2].getY()) {
			if (furthest[index3].getY() > furthest[index4].getY()) {
				p2 = furthest[index3];
				p4 = furthest[index4];
			} else {
				p2 = furthest[index4];
				p4 = furthest[index3];
			}
		} else { // the case when the Ys are equal
			if (furthest[index1].getX() < furthest[index2].getX()) {
				if (furthest[index3].getX() < furthest[index4].getX()) {
					p2 = furthest[index3];
					p4 = furthest[index4];
				} else {
					p2 = furthest[index4];
					p4 = furthest[index3];
				}
			} else if (furthest[index1].getX() > furthest[index2].getX()) {
				if (furthest[index3].getX() > furthest[index4].getX()) {
					p2 = furthest[index3];
					p4 = furthest[index4];
				} else {
					p2 = furthest[index4];
					p4 = furthest[index3];
				}
			}
		}

		if (p1.getX() == p2.getX() || p3.getX() == p4.getX()) {
			throw new NoAngleException("");
		}
		imageGraphics.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		imageGraphics.drawLine(p3.getX(), p3.getY(), p4.getX(), p4.getY());
		imageGraphics.drawOval(centroid.getX(), centroid.getY(), 3, 3);

		double m1 = (p1.getY() - p2.getY()) / (p1.getX() - p2.getX());
		double b1 = p1.getY() - m1 * p1.getX();

		double m2 = (p3.getY() - p4.getY()) / (p3.getX() - p4.getX());
		double b2 = p3.getY() - m2 * p3.getX();

		if (m1 == m2) {
			throw new NoAngleException("");
		}
		int interX = (int) ((b2 - b1) / (m1 - m2));
		int interY = (int) (m1 * interX + b1);

		finalPoint.setX(interX);
		finalPoint.setY(interY);

		imageGraphics.setColor(Color.RED);
		imageGraphics.drawOval(interX, interY, 3, 3);

		int xvector = interX - centroid.getX();
		int yvector = interY - centroid.getY();
		double angle = Math.atan2(xvector, yvector);

		angle = Math.PI - angle;

		return angle;
	}

	/**
	 * Finds the orientation of a robot, given a list of the points contained
	 * within it's T-shape (in terms of a list of x coordinates and y
	 * coordinates), the mean x and y coordinates, and the image from
	 * whdoubleich it was taken.
	 * 
	 * @param xpoints
	 *            The x-coordinates of the points contained within the T-shape.
	 * @param ypoints
	 *            The y-coordinates of the points contained within the T-shape.
	 * @param meanX
	 *            The mean x-point of the T.
	 * @param meanY
	 *            The mean y-point of the T.
	 * @param image
	 *            The image from which the points were taken.
	 * @param showImage
	 *            A boolean flag - if true a line will be drawn showing the
	 *            direction of orientation found.
	 * 
	 * @return An orientation from -Pi to Pi degrees.
	 * @throws NoAngleException
	 */
	public double findOrientation(ArrayList<Integer> xpoints,
			ArrayList<Integer> ypoints, int meanX, int meanY,
			BufferedImage image, boolean showImage) throws NoAngleException {
		assert (xpoints.size() == ypoints.size()) : "";

		if (xpoints.size() == 0) {
			throw new NoAngleException("");
		}

		int stdev = 0;
		// Standard deviation
		for (int i = 0; i < xpoints.size(); i++) {
			int x = xpoints.get(i);
			int y = ypoints.get(i);

			stdev += Position.sqrdEuclidDist(x, y, meanX, meanY);
		}
		stdev = (int) Math.sqrt(stdev / xpoints.size());

		// Find the position of the front of the T.
		int frontX = 0;
		int frontY = 0;
		int frontCount = 0;
		for (int i = 0; i < xpoints.size(); i++) {
			if (stdev > 15) {
				if (Math.abs(xpoints.get(i) - meanX) < stdev
					&& Math.abs(ypoints.get(i) - meanY) < stdev
					&& Position.sqrdEuclidDist(
							xpoints.get(i), ypoints.get(i), meanX, meanY) > 225) {
					frontCount++;
					frontX += xpoints.get(i);
					frontY += ypoints.get(i);
				}
			} else {
				if (Position.sqrdEuclidDist(xpoints.get(i), ypoints.get(i), meanX, meanY) > 225) {
					frontCount++;
					frontX += xpoints.get(i);
					frontY += ypoints.get(i);
				}
			}
		}

		// If no points were found, we'd better bail.
		if (frontCount == 0) {
			throw new NoAngleException("No points found");
		}

		// Otherwise, get the frontX and Y.
		frontX /= frontCount;
		frontY /= frontCount;

		// In here, calculate the vector between meanX/frontX and meanY/frontY,
		// and then get the angle of that vector.

		// Calculate the angle from center of the T to the front of the T
		int xDir = frontX - meanX, yDir = frontY - meanY;
		double length = Math.sqrt(xDir * xDir + yDir * yDir);
		double ax = (double) xDir / length;
		double ay = (double) yDir / length;
		double angle = Math.atan2(ay, ax);

		// Look in a cone in the opposite direction to try to find the grey
		// circle
		ArrayList<Integer> greyXPoints = new ArrayList<Integer>();
		ArrayList<Integer> greyYPoints = new ArrayList<Integer>();

		for (int a = -20; a < 21; a++) {
			ax = Math.cos(angle + Math.toRadians(a));
			ay = Math.sin(angle + Math.toRadians(a));
			for (int i = 15; i < 25; i++) {
				int greyX = meanX - (int) (ax * i);
				int greyY = meanY - (int) (ay * i);
				try {
					Color c = new Color(image.getRGB(greyX, greyY));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(),
							hsbvals);
					if (isGrey(c, hsbvals)) {
						greyXPoints.add(greyX);
						greyYPoints.add(greyY);
					}
				} catch (Exception e) {
					// This happens if part of the search area goes outside the
					// image
					// This is okay, just ignore and continue
				}
			}
		}

		// No grey circle found The angle found is probably wrong, skip this
		// value and return 0
		if (greyXPoints.size() < 30) {
			throw new NoAngleException("Couldn't find grey circle");
		}

		// Calculate center of grey circle points
		int totalX = 0;
		int totalY = 0;
		for (int i = 0; i < greyXPoints.size(); i++) {
			totalX += greyXPoints.get(i);
			totalY += greyYPoints.get(i);
		}

		// Center of grey circle
		double backX = totalX / greyXPoints.size();
		double backY = totalY / greyXPoints.size();

		// Check that the circle is surrounded by the green plate Currently
		// checks above and below the circle

		int foundGreen = 0;
		int greenSides = 0;
		// Check if green points are above the grey circle
		for (int x = (int) (backX - 2); x < (int) (backX + 3); x++) {
			for (int y = (int) (backY - 9); y < backY; y++) {
				try {
					Color c = new Color(image.getRGB(x, y));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(),
							hsbvals);
					if (isGreen(c, hsbvals)) {
						foundGreen++;
						break;
					}
				} catch (Exception e) {
					// Ignore.
				}
			}
		}

		if (foundGreen >= 3) {
			greenSides++;
		}

		// Check if green points are below the grey circle
		foundGreen = 0;
		for (int x = (int) (backX - 2); x < (int) (backX + 3); x++) {
			for (int y = (int) (backY); y < backY + 10; y++) {
				try {
					Color c = new Color(image.getRGB(x, y));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(),
							hsbvals);
					if (isGreen(c, hsbvals)) {
						foundGreen++;
						break;
					}
				} catch (Exception e) {
					// Ignore.
				}
			}
		}

		if (foundGreen >= 3) {
			greenSides++;
		}

		// Check if green points are left of the grey circle
		foundGreen = 0;
		for (int x = (int) (backX - 9); x < backX; x++) {
			for (int y = (int) (backY - 2); y < backY + 3; y++) {
				try {
					Color c = new Color(image.getRGB(x, y));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(),
							hsbvals);
					if (isGreen(c, hsbvals)) {
						foundGreen++;
						break;
					}
				} catch (Exception e) {
					// Ignore.
				}
			}
		}

		if (foundGreen >= 3) {
			greenSides++;
		}

		// Check if green points are right of the grey circle
		foundGreen = 0;
		for (int x = (int) (backX); x < (int) (backX + 10); x++) {
			for (int y = (int) (backY - 2); y < backY + 3; y++) {
				try {
					Color c = new Color(image.getRGB(x, y));
					float hsbvals[] = new float[3];
					Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(),
							hsbvals);
					if (isGreen(c, hsbvals)) {
						foundGreen++;
						break;
					}
				} catch (Exception e) {
					// Ignore.
				}
			}
		}

		if (foundGreen >= 3) {
			greenSides++;
		}

		if (greenSides < 3) {
			throw new NoAngleException("Not enough green areas around the grey circle");
		}

		// At this point, the following is true: Center of the T has been found
		// Front of the T has been found Grey circle has been found Grey circle
		// is surrounded by green plate pixels on at least 3 sides The grey
		// circle, center of the T and front of the T line up roughly with the
		// same angle

		// Calculate new angle using just the center of the T and the grey
		// circle
		length = Math.sqrt(Math.pow(meanX - backX, 2) + Math.pow(meanY - backY, 2));
		ax = (meanX - backX) / length;
		ay = (meanY - backY) / length;
		angle = Math.atan2(ay, ax);

		if (frontY < meanY) {
			angle = -angle;
		}
		if (angle == 0) {
			return 0.001;
		}

		return angle;
	}
}
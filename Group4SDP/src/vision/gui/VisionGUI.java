package vision.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import vision.DistortionFix;
import vision.PitchConstants;
import vision.VideoStream;
import vision.WorldState;
import vision.interfaces.VideoReceiver;
import vision.interfaces.VisionDebugReceiver;
import vision.interfaces.WorldStateReceiver;

@SuppressWarnings("serial")
public class VisionGUI extends JFrame implements VideoReceiver, VisionDebugReceiver,
	WorldStateReceiver {
	private final int videoWidth;
	private final int videoHeight;

	// Pitch dimension selector variables
	private boolean selectionActive = false;
	private Point anchor;
	private int a;
	private int b;
	private int c;
	private int d;
	
	// Stored to only have rendering happen in one place
	private BufferedImage frame;
	private int fps;
	private int frameCounter;
	private BufferedImage debugOverlay;

	// Mouse listener variable
	boolean letterAdjustment = false;
	int mouseX;
	int mouseY;
	String adjust = "";
	BufferedImage t = null;
	double locationX;
	double locationY;
	int rotation = 0;
	ArrayList<Integer> xList = new ArrayList<Integer>();
	ArrayList<Integer> yList = new ArrayList<Integer>();
	boolean theT = false;

	private final PitchConstants pitchConstants;
	private final VisionSettingsPanel settingsPanel;
	private final JPanel videoDisplay = new JPanel();
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			dispose();

			System.exit(0);
		}
	};

	public VisionGUI(final int videoWidth, final int videoHeight,
			WorldState worldState, final PitchConstants pitchConsts,
			final VideoStream vStream, final DistortionFix distortionFix) {

		super("Vision");
		this.videoWidth = videoWidth;
		this.videoHeight = videoHeight;

		// Set pitch constraints
		this.pitchConstants = pitchConsts;
		this.a = pitchConstants.getLeftBuffer();
		this.b = pitchConstants.getTopBuffer();
		this.c = this.videoWidth - pitchConstants.getRightBuffer() - a;
		this.d = this.videoHeight - pitchConstants.getBottomBuffer() - b;

		// Image T
		File img = new File("icons/Tletter2.png");
		try {
			this.t = ImageIO.read(img);
			locationX = this.t.getWidth(null) / 2;
			locationY = this.t.getHeight(null) / 2;
		} catch (IOException e) {
			System.out.println("No T image found");
		}

		Container contentPane = this.getContentPane();

		Dimension videoSize = new Dimension(videoWidth, videoHeight);
		BufferedImage blankInitialiser = new BufferedImage(videoWidth,
				videoHeight, BufferedImage.TYPE_INT_RGB);
		getContentPane().setLayout(null);
		videoDisplay.setLocation(0, 0);
		this.videoDisplay.setMinimumSize(videoSize);
		this.videoDisplay.setSize(videoSize);
		contentPane.add(videoDisplay);

		this.settingsPanel = new VisionSettingsPanel(worldState,
				pitchConstants, vStream, distortionFix);

		settingsPanel.setLocation(videoSize.width, 0);
		contentPane.add(settingsPanel);

		this.setVisible(true);
		this.getGraphics().drawImage(blankInitialiser, 0, 0, null);

		settingsPanel.setSize(settingsPanel.getPreferredSize());
		Dimension frameSize = new Dimension(videoWidth
				+ settingsPanel.getPreferredSize().width, Math.max(videoHeight,
				settingsPanel.getPreferredSize().height));
		contentPane.setSize(frameSize);
		this.setSize(frameSize.width + 8, frameSize.height + 30);
		// Wait for size to actually be set before setting resizable to false.
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.setResizable(false);
		videoDisplay.setFocusable(true);
		videoDisplay.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent ke) {
			}

			public void keyReleased(KeyEvent ke) {
				adjust = KeyEvent.getKeyText(ke.getKeyCode());
			}

			public void keyTyped(KeyEvent e) {
			}
		});

		MouseInputAdapter mouseSelector = new MouseInputAdapter() {
			Rectangle selection;

			public void mousePressed(MouseEvent e) {
				// Mouse clicked
				selectionActive = true;
				switch (settingsPanel.getMouseMode()) {
				case VisionSettingsPanel.MOUSE_MODE_OFF:
					break;
				case VisionSettingsPanel.MOUSE_MODE_PITCH_BOUNDARY:
					System.out.println("Initialised anchor");
					// Pitch dimension selector
					anchor = e.getPoint();
					System.out.println(anchor.x);
					System.out.println(anchor.y);
					selection = new Rectangle(anchor);
					break;
				case VisionSettingsPanel.MOUSE_MODE_BLUE_T:
					videoDisplay.grabFocus();
					mouseX = e.getX();
					mouseY = e.getY();
					break;
				case VisionSettingsPanel.MOUSE_MODE_YELLOW_T:
					videoDisplay.grabFocus();
					mouseX = e.getX();
					mouseY = e.getY();
					break;
				}

			}

			public void mouseDragged(MouseEvent e) {
				switch (settingsPanel.getMouseMode()) {
				case VisionSettingsPanel.MOUSE_MODE_OFF:
					break;
				case VisionSettingsPanel.MOUSE_MODE_PITCH_BOUNDARY:
					selection.setBounds((int) Math.min(anchor.x, e.getX()),
							(int) Math.min(anchor.y, e.getY()),
							(int) Math.abs(e.getX() - anchor.x),
							(int) Math.abs(e.getY() - anchor.y));
					a = (int) Math.min(anchor.x, e.getX());
					b = (int) Math.min(anchor.y, e.getY());
					c = (int) Math.abs(e.getX() - anchor.x);
					d = (int) Math.abs(e.getY() - anchor.y);
					break;
				case VisionSettingsPanel.MOUSE_MODE_BLUE_T:
					mouseX = e.getX();
					mouseY = e.getY();
					break;
				case VisionSettingsPanel.MOUSE_MODE_YELLOW_T:
					mouseX = e.getX();
					mouseY = e.getY();
					break;
				}
			}

			public void mouseReleased(MouseEvent e) {
				selectionActive = false;

				switch (settingsPanel.getMouseMode()) {
				case VisionSettingsPanel.MOUSE_MODE_OFF:
					break;
				case VisionSettingsPanel.MOUSE_MODE_PITCH_BOUNDARY:
					if (e.getPoint().distance(anchor) > 5) {
						Object[] options = { "Main Pitch", "Side Pitch",
								"Cancel" };
						int pitchNum = JOptionPane.showOptionDialog(
								getComponent(0),
								"The parameters are to be set for this pitch",
								"Picking a pitch",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);

						// If option wasn't Cancel and the dialog wasn't closed
						if (pitchNum != 2
								&& pitchNum != JOptionPane.CLOSED_OPTION) {
							System.out.println(pitchNum);
							try {
								int top = b;
								int bottom = videoHeight - d - b;
								int left = a;
								int right = videoWidth - c - a;

								if (top > 0 && bottom > 0 && left > 0
										&& right > 0) {
									// Update pitch constants
									pitchConstants.setTopBuffer(top);
									pitchConstants.setBottomBuffer(bottom);
									pitchConstants.setLeftBuffer(left);
									pitchConstants.setRightBuffer(right);

									// Writing the new dimensions to file
									FileWriter writer = new FileWriter(
											new File("constants/pitch"
													+ pitchNum + "Dimensions"));

									writer.write("" + top + "\n");
									writer.write("" + bottom + "\n");
									writer.write("" + left + "\n");
									writer.write("" + right + "\n");

									writer.close();
									System.out.println("Wrote pitch const");
								} else {
									System.out
											.println("Pitch selection NOT succesful");
								}
								System.out.print("Top: " + top + " Bottom "
										+ bottom);
								System.out.println(" Right " + right + " Left "
										+ left);
							} catch (IOException e1) {
								System.out
										.println("Error writing pitch dimensions to file");
								e1.printStackTrace();
							}

							System.out.println("A: " + a + " B: " + b + " C: "
									+ c + " D:" + d);
						}
						repaint();
					}
					break;
				case VisionSettingsPanel.MOUSE_MODE_BLUE_T:
					letterAdjustment = true;
					break;
				case VisionSettingsPanel.MOUSE_MODE_YELLOW_T:
					letterAdjustment = true;
					break;
				}
			}
		};

		this.videoDisplay.addMouseListener(mouseSelector);
		this.videoDisplay.addMouseMotionListener(mouseSelector);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(windowAdapter);
	}

	@Override
	public void sendFrame(BufferedImage frame, int fps, int frameCounter) {
		this.frame = frame;
		this.fps = fps;
		this.frameCounter = frameCounter;
	}
	
	@Override
	public void sendDebugOverlay(BufferedImage debug) {
		this.debugOverlay = debug;
		Graphics debugGraphics = debugOverlay.getGraphics();

		// Draw the line around the pitch dimensions
		if (selectionActive) {
			debugGraphics.setColor(Color.YELLOW);
			debugGraphics.drawRect(a, b, c, d);
		}

		Graphics2D g2d = (Graphics2D) debugGraphics;

		boolean mouseModeBlueT =
				settingsPanel.getMouseMode() == VisionSettingsPanel.MOUSE_MODE_BLUE_T;
		boolean mouseModeYellowT = 
				settingsPanel.getMouseMode() == VisionSettingsPanel.MOUSE_MODE_YELLOW_T;
		if (mouseModeBlueT || mouseModeYellowT) {
			if (letterAdjustment) {
				if (adjust.equals("Up")) {
					mouseY--;
				} else if (adjust.equals("Down")) {
					mouseY++;
				} else if (adjust.equals("Left")) {
					mouseX--;
				} else if (adjust.equals("Right")) {
					mouseX++;
				} else if (adjust.equals("Enter")) {
					letterAdjustment = false;
					if (mouseModeBlueT)
						getColourRange(frame, PitchConstants.BLUE );
					else
						getColourRange(frame, PitchConstants.YELLOW );
					
					
					//Reset the T image to initial
					File img = new File("icons/Tletter2.png");
					
					try {
						t = ImageIO.read(img);
						locationX = this.t.getWidth(null) / 2;
						locationY = this.t.getHeight(null) / 2;
					} catch (IOException e) {
	
					}
				} else if (adjust.equals("Z")) {
					double rotationRequired = Math.toRadians((double) rotation--);
	
					AffineTransform tx = AffineTransform.getRotateInstance(
							rotationRequired, locationX, locationY);
					AffineTransformOp op = new AffineTransformOp(tx,
							AffineTransformOp.TYPE_BILINEAR);
					File img = new File("icons/Tletter2.png");
	
					try {
						t = ImageIO.read(img);
						locationX = this.t.getWidth(null) / 2;
						locationY = this.t.getHeight(null) / 2;
					} catch (IOException e) {
	
					}
	
					t = op.filter(t, null);
				} else if (adjust.equals("X")) {
					double rotationRequired = Math.toRadians((double) rotation++);
	
					AffineTransform tx = AffineTransform.getRotateInstance(
							rotationRequired, locationX, locationY);
					AffineTransformOp op = new AffineTransformOp(tx,
							AffineTransformOp.TYPE_BILINEAR);
					File img = new File("icons/Tletter2.png");
	
					try {
						t = ImageIO.read(img);
						locationX = this.t.getWidth(null) / 2;
						locationY = this.t.getHeight(null) / 2;
					} catch (IOException e) {
	
					}
					t = op.filter(t, null);
	
				}
				adjust = "";
			}
	
			if (selectionActive || letterAdjustment) {
				g2d.drawImage(t, mouseX, mouseY, null);
			}
		}

		if (!selectionActive) {
			// Making the pitch surroundings transparent
			Composite originalComposite = g2d.getComposite();
			int type = AlphaComposite.SRC_OVER;
			AlphaComposite alphaComp = (AlphaComposite.getInstance(type, 0.6f));
			g2d.setComposite(alphaComp);
			debugGraphics.setColor(Color.BLACK);
			// Rectangle covering the BOTTOM
			debugGraphics.fillRect(0, 0, videoWidth, b);
			// Rectangle covering the LEFT
			debugGraphics.fillRect(0, b, a, videoHeight);
			// Rectangle covering the BOTTOM
			debugGraphics.fillRect(a + c, b, videoWidth - a, videoHeight - b);
			// Rectangle covering the RIGHT
			debugGraphics.fillRect(a, b + d, c, videoHeight - d);
			// Setting back normal settings
			g2d.setComposite(originalComposite);
		}
		
		// Eliminating area around the pitch dimensions
		if (settingsPanel.getMouseMode() == VisionSettingsPanel.MOUSE_MODE_PITCH_BOUNDARY) {

			// Draw the line around the pitch dimensions
			if (selectionActive) {
				debugGraphics.setColor(Color.YELLOW);
				debugGraphics.drawRect(a, b, c, d);
			}
		}
	}
	
	@Override
	public void sendWorldState(WorldState worldState) {
		Graphics frameGraphics = frame.getGraphics();
		
		// Draw overlay on top of raw frame
		frameGraphics.drawImage(debugOverlay, 0, 0, null);
		
		// Draw frame info and worldstate on top of the result
		// Display the FPS that the vision system is running at
		frameGraphics.setColor(Color.white);
		frameGraphics.drawString("Frame: " + frameCounter, 15, 15);
		frameGraphics.drawString("FPS: " + fps, 15, 30);

		// Display Ball & Robot Positions
		int ballX = worldState.getBallX();
		int ballY = worldState.getBallY();
		frameGraphics.drawString("Ball: (" + ballX + ", " + ballY + ")", 15, 45);
		int blueX = worldState.getBlueX();
		int blueY = worldState.getBlueY();
		double blueOrient = Math.toDegrees(worldState.getBlueOrientation());
		frameGraphics.drawString("Blue: (" + blueX + ", " + blueY + ") Orientation: "
				+ blueOrient, 15, 60);
		int yellowX = worldState.getYellowX();
		int yellowY = worldState.getYellowY();
		double yellowOrient = Math.toDegrees(worldState.getYellowOrientation());
		frameGraphics.drawString("Yellow: (" + yellowX + ", " + yellowY + ") Orientation: "
				+ yellowOrient, 15, 75);
		
		// Draw overall composite to screen
		Graphics videoGraphics = videoDisplay.getGraphics();
		videoGraphics.drawImage(frame, 0, 0, null);
	}

	public void getColourRange(BufferedImage frame, int object) {
		
		ArrayList<Integer> redList = new ArrayList<Integer>();
		ArrayList<Integer> greenList = new ArrayList<Integer>();
		ArrayList<Integer> blueList = new ArrayList<Integer>();
		ArrayList<Float> hueList = new ArrayList<Float>();
		ArrayList<Float> satList = new ArrayList<Float>();
		ArrayList<Float> valList = new ArrayList<Float>();

		int lX = (int)locationX;
		int lY = (int)locationY;

		//Top part of the letter T 
		for (int x = 0 - lX + 12; x < 23 + 12 - lX; x++)
			for (int y = 0 + 15 - lY; y < 9 + 15 - lY; y++) {
				double xR = x * Math.cos(Math.toRadians((double) rotation)) - y
						* Math.sin(Math.toRadians((double) rotation));
				double yR = x * Math.sin(Math.toRadians((double) rotation)) + y
						* Math.cos(Math.toRadians((double) rotation));

				xList.add(mouseX + lX + (int) xR);
				yList.add(mouseY + lY + (int) yR);

				Color c = new Color(frame.getRGB(mouseX + lX + (int) xR, mouseY
						+ lY + (int) yR));

				float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(),
						c.getBlue(), null);
				
				hueList.add(hsbvals[0]);
				satList.add(hsbvals[1]);
				valList.add(hsbvals[2]);
				redList.add(c.getRed());
				greenList.add(c.getGreen());
				blueList.add(c.getBlue());
				// frame.setRGB(mouseX + lX + (int) xR, mouseY + lY + (int) yR,
				// 65535);

			}

		//Bottom part of the letter T 
		for (int x = 9 +12 - lX; x < 18 + 12- lX; x++)
			for (int y = 9 + 15 - lY; y < 29 +15 - lY; y++) {
				double xR = x * Math.cos(Math.toRadians((double) rotation)) - y
						* Math.sin(Math.toRadians((double) rotation));
				double yR = x * Math.sin(Math.toRadians((double) rotation)) + y
						* Math.cos(Math.toRadians((double) rotation));

				xList.add(mouseX + lX + (int) xR);
				yList.add(mouseY + lY + (int) yR);

				Color c = new Color(frame.getRGB(mouseX + lX + (int) xR, mouseY
						+ lY + (int) yR));

				float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(),
						c.getBlue(), null);			
				redList.add(c.getRed());
				greenList.add(c.getGreen());
				blueList.add(c.getBlue());
				hueList.add(hsbvals[0]);
				satList.add(hsbvals[1]);
				valList.add(hsbvals[2]);
			
				// frame.setRGB(mouseX + lX + (int) xR, mouseY + lY + (int) yR,
				// 65535);
			}

		//Mean and Standard deviation calculations for the RGB and HSB values
		double meanR = calcMean(redList);
		double stdevR = calcStandardDeviation(redList);
		double meanG = calcMean(greenList);
		double stdevG = calcStandardDeviation(greenList);
		double meanB = calcMean(blueList);
		double stdevB = calcStandardDeviation(blueList);
		double meanH = calcMeanFloat(hueList);
		double stdevH = calcStandardDeviationFloat(hueList);
		double meanS = calcMeanFloat(satList);
		double stdevS = calcStandardDeviationFloat(satList);
		double meanV = calcMeanFloat(valList);
		double stdevV = calcStandardDeviationFloat(valList);

		System.out.println("Red mean " + meanR);
		System.out.println("Green mean " + meanG);
		System.out.println("Blue mean " + meanB);
		System.out.println("Red std " + stdevR);
		System.out.println("Green std " + stdevG);
		System.out.println("Blue std " + stdevB);
		System.out.println("H mean " + meanH);
		System.out.println("S mean " + meanS);
		System.out.println("V mean " + meanV);
		System.out.println("H std " + stdevH);
		System.out.println("S std " + stdevS);
		System.out.println("V std " + stdevV);

		//Setting the sliders
		pitchConstants.setRedLower(object, Math.max(PitchConstants.RGBMIN,(int) (meanR - 1.5*stdevR)));
		pitchConstants.setRedUpper(object, Math.min(PitchConstants.RGBMAX, (int)(meanR + 1.5*stdevR)));
		
		pitchConstants.setGreenLower(object, Math.max(PitchConstants.RGBMIN,(int) (meanG - 1.5*stdevG)));
		pitchConstants.setGreenUpper(object, Math.min(PitchConstants.RGBMAX, (int)(meanG + 1.5*stdevG)));
		
		pitchConstants.setBlueLower(object, Math.max(PitchConstants.RGBMIN,(int) (meanB - 1.5*stdevB)));
		pitchConstants.setBlueUpper(object, Math.min(PitchConstants.RGBMAX, (int)(meanB + 1.5*stdevB)));
		
		//DO NOT DELETE THESE might be useful in the future
//		pitchConstants.setHueLower(object, Math.max(PitchConstants.HSVMIN,(float)(meanH - 1.5*stdevH)));
//		pitchConstants.setHueUpper(object, Math.min(PitchConstants.HSVMAX, (float)(meanH + 1.5*stdevH)));
		
		//Works best with the Hue range 0-1 for the blue and yellow Ts
		pitchConstants.setHueLower(object, Math.max(PitchConstants.HSVMIN,(float)(0)));
		pitchConstants.setHueUpper(object, Math.min(PitchConstants.HSVMAX, (float)(1)));

		pitchConstants.setSaturationLower(object, Math.max(PitchConstants.HSVMIN,(float) (meanS - 1.5*stdevS)));
		pitchConstants.setSaturationUpper(object, Math.min(PitchConstants.HSVMAX, (float)(meanS + 1.5*stdevS)));
		
		pitchConstants.setValueLower(object, Math.max(PitchConstants.HSVMIN,(float) (meanV - 1.5*stdevV)));
		pitchConstants.setValueUpper(object, Math.min(PitchConstants.HSVMAX, (float)(meanV + 1.5*stdevV)));
		
		settingsPanel.reloadSliderDefaults();
		
		theT = true;
		rotation = 0;
	}

	public double calcStandardDeviationFloat(ArrayList<Float> points) {

		double mean = calcMeanFloat(points);
		double sum = 0;
		for (int i = 0; i < points.size(); i++) {
			float p = points.get(i);
			double diff = p - mean;
			sum += diff * diff;
		}

		return Math.sqrt(sum / points.size());
	}

	public double calcMeanFloat(ArrayList<Float> points) {
		float sum = 0;
		for (int i = 0; i < points.size(); i++) {
			sum += points.get(i);
		}
		return (double) (sum) / points.size();
	}
	public double calcStandardDeviation(ArrayList<Integer> points) {

		double mean = calcMean(points);
		double sum = 0;
		for (int i = 0; i < points.size(); i++) {
			int p = points.get(i);
			double diff = p - mean;
			sum += diff * diff;
		}

		return Math.sqrt(sum / points.size());
	}

	public double calcMean(ArrayList<Integer> points) {
		int sum = 0;
		for (int i = 0; i < points.size(); i++) {
			sum += points.get(i);
		}
		return (double) (sum) / points.size();
	}

}

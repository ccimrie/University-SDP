package vision.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import vision.PitchConstants;
import vision.VideoStream;
import vision.VisionInterface;
import vision.WorldState;

@SuppressWarnings("serial")
public class VisionGUI extends JFrame implements VisionInterface {
	private final int videoWidth;
	private final int videoHeight;
	
	// Pitch dimension selector variables
	private boolean selectionActive = false;
	private Point anchor;
	private int a;
	private int b;
	private int c;
	private int d;

	// Mouse listener variable
	private int mouse_event = 0;
	private boolean letterAdjustment = false;
	private int mouseX;
	private int mouseY;
	private String adjust = "";
	
	private final VisionSettingsPanel settingsPanel;
	private final JPanel videoDisplay = new JPanel();
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			dispose();

			System.exit(0);
		}
	};
	
	public VisionGUI(final int videoWidth, final int videoHeight, WorldState worldState,
			final PitchConstants pitchConstants, final VideoStream vStream) {
		super("Vision");
		this.videoWidth = videoWidth;
		this.videoHeight = videoHeight;

		// Set pitch constraints
		this.a = pitchConstants.getLeftBuffer();
		this.b = pitchConstants.getTopBuffer();
		this.c = this.videoWidth - pitchConstants.getRightBuffer() - a;
		this.d = this.videoHeight - pitchConstants.getBottomBuffer() - b;
		
		Container contentPane = this.getContentPane();
		
		Dimension videoSize = new Dimension(videoWidth, videoHeight);
		BufferedImage blankInitialiser = new BufferedImage(
				videoWidth, videoHeight, BufferedImage.TYPE_INT_RGB);		
		getContentPane().setLayout(null);
		videoDisplay.setLocation(0, 0);
		this.videoDisplay.setMinimumSize(videoSize);
		this.videoDisplay.setSize(videoSize);
		contentPane.add(videoDisplay);
		
		this.settingsPanel = new VisionSettingsPanel(worldState, pitchConstants, vStream);
		settingsPanel.setLocation(videoSize.width, 0);
		contentPane.add(settingsPanel);

		this.setVisible(true);
		this.videoDisplay.getGraphics().drawImage(blankInitialiser, 0, 0, null);
		
		settingsPanel.setSize(settingsPanel.getPreferredSize());
		Dimension frameSize = new Dimension(
					videoWidth + settingsPanel.getPreferredSize().width,
					Math.max(videoHeight, settingsPanel.getPreferredSize().height)
				);
		contentPane.setSize(frameSize);
		this.setSize(frameSize.width + 8, frameSize.height + 30);
		// Wait for size to actually be set before setting resizable to false.
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.setResizable(false);
		
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent ke) { }

			public void keyReleased(KeyEvent ke) {
				adjust = KeyEvent.getKeyText(ke.getKeyCode());
			}

			// TODO: remove commented code if it's not going to be used
			public void keyTyped(KeyEvent e) {
				// System.out.println("keyPressed "+e.getKeyChar());
			}
		});
		
		MouseInputAdapter mouseSelector = new MouseInputAdapter() {
			Rectangle selection;

			public void mousePressed(MouseEvent e) {
				// Mouse clicked
				mouse_event = 1;
				switch (mouse_event) {
				case 0:
					break;
				case 1:
					selectionActive = true;
					// Pitch dimension selector
					anchor = e.getPoint();
					System.out.println(anchor.x);
					System.out.println(anchor.y);
					selection = new Rectangle(anchor);
					break;
				case 2:
					mouseX = e.getX();
					mouseY = e.getY();
					break;
				}
			}

			public void mouseEntered(MouseEvent e) {}

			public void mouseDragged(MouseEvent e) {
				switch (mouse_event) {
				case 0:
					break;
				case 1:
					selection.setBounds((int) Math.min(anchor.x, e.getX()),
							(int) Math.min(anchor.y, e.getY()),
							(int) Math.abs(e.getX() - anchor.x),
							(int) Math.abs(e.getY() - anchor.y));
					a = (int) Math.min(anchor.x, e.getX());
					b = (int) Math.min(anchor.y, e.getY());
					c = (int) Math.abs(e.getX() - anchor.x);
					d = (int) Math.abs(e.getY() - anchor.y);
					break;
				case 2:
					mouseX = e.getX();
					mouseY = e.getY();
					break;
				}
			}

			public void mouseReleased(MouseEvent e) {
				selectionActive = false;

				switch (mouse_event) {
				case 0:
					break;
				case 1:
					if (e.getPoint().distance(anchor) > 5) {
						Object[] options = { "Main Pitch", "Side Pitch", "Cancel" };
						int pitchNum = JOptionPane.showOptionDialog(
								getComponent(0),
								"The parameters are to be set for this pitch",
								"Picking a pitch",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
	
						// If option wasn't Cancel and the dialog wasn't closed
						if (pitchNum != 2 && pitchNum != JOptionPane.CLOSED_OPTION) {
							int top = b;
							int bottom = videoHeight - d - b;
							int left = a;
							int right = videoWidth - c - a;
	
							if (top > 0 && bottom > 0 && left > 0 && right > 0) {
								// Update pitch constants
								pitchConstants.setTopBuffer(top);
								pitchConstants.setBottomBuffer(bottom);
								pitchConstants.setLeftBuffer(left);
								pitchConstants.setRightBuffer(right);
							} else {
								System.out.println("Pitch boundary selection failed");
							}
						}
					}
					repaint();
					break;
				case 2:
					letterAdjustment = true;
					break;
				}

				mouse_event = 0;
			}
		};
		
		this.videoDisplay.addMouseListener(mouseSelector);
		this.videoDisplay.addMouseMotionListener(mouseSelector);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(windowAdapter);
	}

	@Override
	public void sendFrame(BufferedImage frame, int fps, Point ball,
			Point blueRobot, double blueOrientation,
			Point yellowRobot, double yellowOrientation) {
		Graphics videoGraphics = videoDisplay.getGraphics();
		Graphics imageGraphics = frame.getGraphics();
		// Draw the line around the pitch dimensions
		if (selectionActive) {
			imageGraphics.setColor(Color.YELLOW);
			imageGraphics.drawRect(a, b, c, d);
		}

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image letterT = toolkit.getImage("icons/T.gif");
		Graphics2D g2d = (Graphics2D) imageGraphics;

		// TODO: Show T colour selector
		if (selectionActive && mouse_event == 2 || letterAdjustment) {
			g2d.drawImage(letterT, mouseX, mouseY, null);
		}

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
			}
			adjust = "";
		}

		// Eliminating area around the pitch dimensions
		if (!selectionActive) {
			// Making the pitch surroundings transparent
			Composite originalComposite = g2d.getComposite();
			int type = AlphaComposite.SRC_OVER;
			AlphaComposite alphaComp = (AlphaComposite.getInstance(type, 0.6f));
			g2d.setComposite(alphaComp);
			imageGraphics.setColor(Color.BLACK);
			// Rectangle covering the BOTTOM
			imageGraphics.fillRect(0, 0, videoWidth, b);
			// Rectangle covering the LEFT
			imageGraphics.fillRect(0, b, a, videoHeight);
			// Rectangle covering the BOTTOM
			imageGraphics.fillRect(a + c, b, videoWidth - a, videoHeight - b);
			// Rectangle covering the RIGHT
			imageGraphics.fillRect(a, b + d, c, videoHeight - d);
			// Setting back normal settings
			g2d.setComposite(originalComposite);
		}

		// Draw the line around the pitch dimensions
		if (selectionActive) {
			imageGraphics.setColor(Color.YELLOW);
			imageGraphics.drawRect(a, b, c, d);
		}

		// Display the FPS that the vision system is running at
		imageGraphics.setColor(Color.white);
		imageGraphics.drawString("FPS: " + fps, 15, 15);

		// Display Ball & Robot Positions
		imageGraphics.drawString("Ball: (" + ball.x + ", " + ball.y + ")", 15, 30);
		imageGraphics.drawString("Blue: (" + blueRobot.x + ", " + blueRobot.y
				+ ") Orientation: " + Math.toDegrees(blueOrientation), 15, 45);
		imageGraphics.drawString("Yellow: (" + yellowRobot.x + ", " + yellowRobot.y
				+ ") Orientation: " + Math.toDegrees(yellowOrientation), 15, 60);
		
		videoGraphics.drawImage(frame, 0, 0, null);
	}
}

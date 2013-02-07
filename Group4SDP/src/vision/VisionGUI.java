package vision;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Creates and maintains the swing-based Control GUI, which 
 * provides both control manipulation (pitch choice, direction,
 * etc) and threshold setting. Also allows the saving/loading of
 * threshold values to a file.
 * 
 * @author s0840449 (original)
 * @author Alex Adams (heavy refactoring)
 */
@SuppressWarnings("serial")
public class VisionGUI extends JFrame {
	// A PitchConstants class used to load/save constants for the pitch
	private PitchConstants pitchConstants;
	
	// Stores information about the current world state, such as shooting
	// direction, ball location, etc
	private WorldState worldState;
	
	// Load/Save buttons
	private JButton saveButton;
	private JButton loadButton;
	
	// Tabs
	private final JTabbedPane tabPane = new JTabbedPane();
	private final JPanel mainPanel = new JPanel();
	private final ThresholdsPanel ballPanel = new ThresholdsPanel();
	private final ThresholdsPanel bluePanel = new ThresholdsPanel();
	private final ThresholdsPanel yellowPanel = new ThresholdsPanel();
	private final ThresholdsPanel greyPanel = new ThresholdsPanel();
	private final ThresholdsPanel greenPanel = new ThresholdsPanel();
	private final ChangeListener tabChangeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			// Update the debug overlay settings
			int index = tabPane.getSelectedIndex();
			
			switch(index) {
			case(0):
				// Disable all debug modes
				pitchConstants.setDebugMode(PitchConstants.BALL, false);
				pitchConstants.setDebugMode(PitchConstants.BLUE, false);
				pitchConstants.setDebugMode(PitchConstants.YELLOW, false);
				pitchConstants.setDebugMode(PitchConstants.GREY, false);
				pitchConstants.setDebugMode(PitchConstants.GREEN, false);
				break;
			case(1):
				// Enable only Ball debug mode
				pitchConstants.setDebugMode(PitchConstants.BALL, true, false);
				break;
			case(2):
				// Enable only Blue Robot debug mode
				pitchConstants.setDebugMode(PitchConstants.BLUE, true, false);
				break;
			case(3):
				// Enable only Yellow Robot debug mode
				pitchConstants.setDebugMode(PitchConstants.YELLOW, true, false);
				break;
			case(4):
				// Enable only Grey Circle debug mode
				pitchConstants.setDebugMode(PitchConstants.GREY, true, false);
				break;
			case(5):
				// Enable only Green Plate debug mode
				pitchConstants.setDebugMode(PitchConstants.GREEN, true, false);
				break;
			default:
				System.out.println("VisionGUI: Invalid tab index");
				System.exit(1);
			}
		}
	};

	// Radio buttons and their change listeners
	private final JRadioButton rdbtnPitch0 = new JRadioButton("Main");
	private final JRadioButton rdbtnPitch1 = new JRadioButton("Side Room");
	private final MouseAdapter pitchMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			// Update the world state and pitch constants
			int pitchNum = rdbtnPitch0.isSelected() ? 0 : 1;
			worldState.setPitch(pitchNum);
			pitchConstants.setPitchNum(pitchNum);
			reloadSliderDefaults();
		}
	};

	private final JRadioButton rdbtnYellow = new JRadioButton("Yellow");
	private final JRadioButton rdbtnBlue = new JRadioButton("Blue");
	private final MouseAdapter colourMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			// Update which colour is ours
			worldState.setColour(rdbtnBlue.isSelected() ? 1 : 0);
		}
	};

	private final JRadioButton rdbtnRight = new JRadioButton("Right");
	private final JRadioButton rdbtnLeft = new JRadioButton("Left");
	private final MouseAdapter directionMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			// Update which direction the other team's goal is in
			int isLeft = rdbtnLeft.isSelected() ? 1 : 0;
			worldState.setDirection(isLeft);
			System.out.println("Changed Direction to " + isLeft);
		}
	};
	
	private abstract class BaseSliderChangeListener implements ChangeListener {
		protected ThresholdsPanel sliderPanel;
		protected int index;
		public BaseSliderChangeListener(ThresholdsPanel sliderPanel, int index) {
			assert(sliderPanel != null) : "ThresholdChangeListener's parent is null";
			this.sliderPanel = sliderPanel;
			this.index = index;
		}
		
		/*@Override
		public void stateChanged(ChangeEvent e) {
			

			lowerUpper = sliderPanel.getSaturationSliderValues();
			pitchConstants.setValueLower(index, (double) lowerUpper[0] / 255.0);
			pitchConstants.setValueUpper(index, (double) lowerUpper[1] / 255.0);
		}*/
	}

	private class RedSliderChangeListener extends BaseSliderChangeListener {
		public RedSliderChangeListener(ThresholdsPanel sliderPanel, int index) {
			super(sliderPanel, index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = super.sliderPanel.getRedSliderValues();
			pitchConstants.setRedLower(super.index, lowerUpper[0]);
			pitchConstants.setRedUpper(super.index, lowerUpper[1]);
		}
	}
	
	private class GreenSliderChangeListener extends BaseSliderChangeListener {
		public GreenSliderChangeListener(ThresholdsPanel sliderPanel, int index) {
			super(sliderPanel, index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = super.sliderPanel.getGreenSliderValues();
			pitchConstants.setGreenLower(super.index, lowerUpper[0]);
			pitchConstants.setGreenUpper(super.index, lowerUpper[1]);
		}
	}
	
	private class BlueSliderChangeListener extends BaseSliderChangeListener {
		public BlueSliderChangeListener(ThresholdsPanel sliderPanel, int index) {
			super(sliderPanel, index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = super.sliderPanel.getBlueSliderValues();
			pitchConstants.setBlueLower(super.index, lowerUpper[0]);
			pitchConstants.setBlueUpper(super.index, lowerUpper[1]);
		}
	}
	
	private class HueSliderChangeListener extends BaseSliderChangeListener {
		public HueSliderChangeListener(ThresholdsPanel sliderPanel, int index) {
			super(sliderPanel, index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = super.sliderPanel.getHueSliderValues();
			pitchConstants.setHueLower(super.index, (double) lowerUpper[0] / 255.0);
			pitchConstants.setHueUpper(super.index, (double) lowerUpper[1] / 255.0);
		}
	}
	
	private class SaturationSliderChangeListener extends BaseSliderChangeListener {
		public SaturationSliderChangeListener(ThresholdsPanel sliderPanel, int index) {
			super(sliderPanel, index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = super.sliderPanel.getSaturationSliderValues();
			pitchConstants.setSaturationLower(super.index, (double) lowerUpper[0] / 255.0);
			pitchConstants.setSaturationUpper(super.index, (double) lowerUpper[1] / 255.0);
		}
	}

	private class ValueSliderChangeListener extends BaseSliderChangeListener {
		public ValueSliderChangeListener(ThresholdsPanel sliderPanel, int index) {
			super(sliderPanel, index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = super.sliderPanel.getValueSliderValues();
			pitchConstants.setValueLower(super.index, (double) lowerUpper[0] / 255.0);
			pitchConstants.setValueUpper(super.index, (double) lowerUpper[1] / 255.0);
		}
	}
	
	/**
	 * Default constructor. 
	 * 
	 * @param pitchConstants	A PitchConstants object to update the threshold slider
	 * 							values.			
	 * @param worldState		A WorldState object to update the pitch choice, shooting
	 * 							direction, etc.
	 * @param pitchConstants	A PitchConstants object to allow saving/loading of data.
	 */
	public VisionGUI(WorldState worldState, PitchConstants pitchConstants) {
		super("Vision Control");
		setResizable(false);
		// Both state objects must not be null.
		assert (worldState != null) : "worldState is null";
		assert (pitchConstants != null) : "pitchConstants is null";
		
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());

        // The main (default) tab
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setUpMainPanel();
        
        // The five threshold tabs.
        ballPanel.setRedSliderChangeListener(
        		new RedSliderChangeListener(ballPanel, PitchConstants.BALL));
        ballPanel.setGreenSliderChangeListener(
        		new GreenSliderChangeListener(ballPanel, PitchConstants.BALL));
        ballPanel.setBlueSliderChangeListener(
        		new BlueSliderChangeListener(ballPanel, PitchConstants.BALL));
        ballPanel.setHueSliderChangeListener(
        		new HueSliderChangeListener(ballPanel, PitchConstants.BALL));
        ballPanel.setSaturationSliderChangeListener(
        		new SaturationSliderChangeListener(ballPanel, PitchConstants.BALL));
        ballPanel.setValueSliderChangeListener(
        		new ValueSliderChangeListener(ballPanel, PitchConstants.BALL));

        bluePanel.setRedSliderChangeListener(
        		new RedSliderChangeListener(bluePanel, PitchConstants.BLUE));
        bluePanel.setGreenSliderChangeListener(
        		new GreenSliderChangeListener(bluePanel, PitchConstants.BLUE));
        bluePanel.setBlueSliderChangeListener(
        		new BlueSliderChangeListener(bluePanel, PitchConstants.BLUE));
        bluePanel.setHueSliderChangeListener(
        		new HueSliderChangeListener(bluePanel, PitchConstants.BLUE));
        bluePanel.setSaturationSliderChangeListener(
        		new SaturationSliderChangeListener(bluePanel, PitchConstants.BLUE));
        bluePanel.setValueSliderChangeListener(
        		new ValueSliderChangeListener(bluePanel, PitchConstants.BLUE));
        
        yellowPanel.setRedSliderChangeListener(
        		new RedSliderChangeListener(yellowPanel, PitchConstants.YELLOW));
        yellowPanel.setGreenSliderChangeListener(
        		new GreenSliderChangeListener(yellowPanel, PitchConstants.YELLOW));
        yellowPanel.setBlueSliderChangeListener(
        		new BlueSliderChangeListener(yellowPanel, PitchConstants.YELLOW));
        yellowPanel.setHueSliderChangeListener(
        		new HueSliderChangeListener(yellowPanel, PitchConstants.YELLOW));
        yellowPanel.setSaturationSliderChangeListener(
        		new SaturationSliderChangeListener(yellowPanel, PitchConstants.YELLOW));
        yellowPanel.setValueSliderChangeListener(
        		new ValueSliderChangeListener(yellowPanel, PitchConstants.YELLOW));
        
        greyPanel.setRedSliderChangeListener(
        		new RedSliderChangeListener(greyPanel, PitchConstants.GREY));
        greyPanel.setGreenSliderChangeListener(
        		new GreenSliderChangeListener(greyPanel, PitchConstants.GREY));
        greyPanel.setBlueSliderChangeListener(
        		new BlueSliderChangeListener(greyPanel, PitchConstants.GREY));
        greyPanel.setHueSliderChangeListener(
        		new HueSliderChangeListener(greyPanel, PitchConstants.GREY));
        greyPanel.setSaturationSliderChangeListener(
        		new SaturationSliderChangeListener(greyPanel, PitchConstants.GREY));
        greyPanel.setValueSliderChangeListener(
        		new ValueSliderChangeListener(greyPanel, PitchConstants.GREY));
        
        greenPanel.setRedSliderChangeListener(
        		new RedSliderChangeListener(greenPanel, PitchConstants.GREEN));
        greenPanel.setGreenSliderChangeListener(
        		new GreenSliderChangeListener(greenPanel, PitchConstants.GREEN));
        greenPanel.setBlueSliderChangeListener(
        		new BlueSliderChangeListener(greenPanel, PitchConstants.GREEN));
        greenPanel.setHueSliderChangeListener(
        		new HueSliderChangeListener(greenPanel, PitchConstants.GREEN));
        greenPanel.setSaturationSliderChangeListener(
        		new SaturationSliderChangeListener(greenPanel, PitchConstants.GREEN));
        greenPanel.setValueSliderChangeListener(
        		new ValueSliderChangeListener(greenPanel, PitchConstants.GREEN));
        
        tabPane.addTab("Main", mainPanel);
        tabPane.addTab("Ball", ballPanel);
        tabPane.addTab("Blue Robot", bluePanel);
        tabPane.addTab("Yellow Robot", yellowPanel);
        tabPane.addTab("Grey Circles", greyPanel);
        tabPane.addTab("Green Plates", greenPanel);
        
        tabPane.addChangeListener(tabChangeListener);
        
        getContentPane().add(tabPane);
        this.pack();
        this.setVisible(true);
        
		reloadSliderDefaults();
	}
	
	/**
	 * Sets up the main tab, adding in the pitch choice, the direction
	 * choice, the robot-colour choice and save/load buttons.
	 */
	private void setUpMainPanel() {
		// Pitch choice
		JPanel pitchPanel = new JPanel();
		JLabel pitchLabel = new JLabel("Pitch:");
		pitchPanel.add(pitchLabel);
		
		ButtonGroup pitchChoice = new ButtonGroup();
		pitchChoice.add(rdbtnPitch0);
		pitchChoice.add(rdbtnPitch1);
		pitchPanel.add(rdbtnPitch0);
		pitchPanel.add(rdbtnPitch1);
		
		rdbtnPitch0.setSelected(true);
		
		rdbtnPitch0.addMouseListener(pitchMouseListener);
		rdbtnPitch1.addMouseListener(pitchMouseListener);
		
		mainPanel.add(pitchPanel);
		
		// Colour choice
		JPanel colourPanel = new JPanel();
		JLabel colourLabel = new JLabel("Our colour:");
		colourPanel.add(colourLabel);
		
		ButtonGroup colourChoice = new ButtonGroup();
		colourChoice.add(rdbtnYellow);
		colourPanel.add(rdbtnYellow);
		colourChoice.add(rdbtnBlue);
		colourPanel.add(rdbtnBlue);
		
		rdbtnYellow.setSelected(true);
		
		rdbtnYellow.addMouseListener(colourMouseListener);
		rdbtnBlue.addMouseListener(colourMouseListener);
		
		mainPanel.add(colourPanel);
		
		// Direction choice
		JPanel directionPanel = new JPanel();
		JLabel directionLabel = new JLabel("Our shooting direction:");
		directionPanel.add(directionLabel);
		
		ButtonGroup directionChoice = new ButtonGroup();
		directionChoice.add(rdbtnRight);
		directionPanel.add(rdbtnRight);
		directionChoice.add(rdbtnLeft);
		directionPanel.add(rdbtnLeft);
		
		rdbtnRight.setSelected(true);
		
		rdbtnRight.addMouseListener(directionMouseListener);
		rdbtnLeft.addMouseListener(directionMouseListener);
		
		mainPanel.add(directionPanel);
		
		// Save/load buttons
		JPanel saveLoadPanel = new JPanel();
		
		saveButton = new JButton("Save Thresholds");
		saveButton.addMouseListener(new MouseAdapter() {
			// Attempt to write all of the current thresholds to a file with a name 
			// based on the currently selected pitch.
			@Override
			public void mouseClicked(MouseEvent e) {
				int pitchNum = (rdbtnPitch0.isSelected()) ? 0 : 1;
				
				int result = JOptionPane.showConfirmDialog(saveButton,
						"Are you sure you want to save current constants for pitch " + pitchNum + "?");
				
				if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) return;
				
				pitchConstants.saveConstants(System.getProperty("user.dir") + "/constants/pitch" + pitchNum);
			}
		});
		
		saveLoadPanel.add(saveButton);
		
		loadButton = new JButton("Load Thresholds");
		loadButton.addMouseListener(new MouseAdapter() {
			// Override the current threshold settings from those set in
			// the correct constants file for the current pitch.
			@Override
			public void mouseClicked(MouseEvent e) {
				int pitchNum = rdbtnPitch0.isSelected() ? 0 : 1;
				
				int result = JOptionPane.showConfirmDialog(loadButton, "Are you sure you want to load " +
						"pre-saved constants for pitch " + pitchNum + "?");
				
				if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) return;
				
				pitchConstants.setPitchNum(pitchNum);
				reloadSliderDefaults();
			}
		});
		
		saveLoadPanel.add(loadButton);
		
		mainPanel.add(saveLoadPanel);
	}
	
	/**
	 * Reloads the default values for the sliders from the PitchConstants file.
	 */
	public void reloadSliderDefaults() {
		ballPanel.setSliderValues(PitchConstants.BALL, pitchConstants);
		bluePanel.setSliderValues(PitchConstants.BLUE, pitchConstants);
		yellowPanel.setSliderValues(PitchConstants.YELLOW, pitchConstants);
		greyPanel.setSliderValues(PitchConstants.GREY, pitchConstants);
		greenPanel.setSliderValues(PitchConstants.GREEN, pitchConstants);
	}
}

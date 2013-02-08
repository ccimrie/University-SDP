package vision.gui;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vision.PitchConstants;
import vision.WorldState;

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
public class VisionSettingsPanel extends JPanel {
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
	private final JPanel mainTabPanel = new JPanel();
	private final JPanel DistTabPanel = new JPanel();
	private final ThresholdsPanel[] tabPanels = new ThresholdsPanel[] {
		new ThresholdsPanel(),
		new ThresholdsPanel(),
		new ThresholdsPanel(),
		new ThresholdsPanel(),
		new ThresholdsPanel()
	};
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
				pitchConstants.setDebugMode(PitchConstants.DIST, false);
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
			case(6):
				pitchConstants.setDebugMode(PitchConstants.DIST, true, false);
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
	private final JRadioButton rdbtnDist0 = new JRadioButton("On");
	private final JRadioButton rdbtnDist1 = new JRadioButton("Off");
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
	private final MouseAdapter distMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			// Update the world state and pitch constants
			int distnum = rdbtnDist0.isSelected() ? 0 : 1;
			boolean active_or_not;
			
			if (distnum == 1 ) {active_or_not = false;}
			else {active_or_not = true; }
			pitchConstants.setDistortBool(active_or_not);
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
		protected int index;
		public BaseSliderChangeListener(int index) {
			this.index = index;
		}
	}

	private class RedSliderChangeListener extends BaseSliderChangeListener {
		public RedSliderChangeListener(int index) {
			super(index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = tabPanels[super.index].getRedSliderValues();
			pitchConstants.setRedLower(super.index, lowerUpper[0]);
			pitchConstants.setRedUpper(super.index, lowerUpper[1]);
		}
	}
	
	private class GreenSliderChangeListener extends BaseSliderChangeListener {
		public GreenSliderChangeListener(int index) {
			super(index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = tabPanels[super.index].getGreenSliderValues();
			pitchConstants.setGreenLower(super.index, lowerUpper[0]);
			pitchConstants.setGreenUpper(super.index, lowerUpper[1]);
		}
	}
	
	private class BlueSliderChangeListener extends BaseSliderChangeListener {
		public BlueSliderChangeListener(int index) {
			super(index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = tabPanels[super.index].getBlueSliderValues();
			pitchConstants.setBlueLower(super.index, lowerUpper[0]);
			pitchConstants.setBlueUpper(super.index, lowerUpper[1]);
		}
	}
	
	private class HueSliderChangeListener extends BaseSliderChangeListener {
		public HueSliderChangeListener(int index) {
			super(index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = tabPanels[super.index].getHueSliderValues();
			pitchConstants.setHueLower(super.index, (double) lowerUpper[0] / 255.0);
			pitchConstants.setHueUpper(super.index, (double) lowerUpper[1] / 255.0);
		}
	}
	
	private class SaturationSliderChangeListener extends BaseSliderChangeListener {
		public SaturationSliderChangeListener(int index) {
			super(index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = tabPanels[super.index].getSaturationSliderValues();
			pitchConstants.setSaturationLower(super.index, (double) lowerUpper[0] / 255.0);
			pitchConstants.setSaturationUpper(super.index, (double) lowerUpper[1] / 255.0);
		}
	}

	private class ValueSliderChangeListener extends BaseSliderChangeListener {
		public ValueSliderChangeListener(int index) {
			super(index);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			int[] lowerUpper = tabPanels[super.index].getValueSliderValues();
			pitchConstants.setValueLower(super.index, (double) lowerUpper[0] / 255.0);
			pitchConstants.setValueUpper(super.index, (double) lowerUpper[1] / 255.0);
		}
	}
	
	/**
	 * Default constructor. 
	 * 
	 * @param worldState		A WorldState object to update the pitch choice, shooting
	 * 							direction, etc.
	 * @param pitchConstants	A PitchConstants object to allow saving/loading of data.
	 */
	public VisionSettingsPanel(WorldState worldState, PitchConstants pitchConstants) {
		// Both state objects must not be null.
		assert (worldState != null) : "worldState is null";
		assert (pitchConstants != null) : "pitchConstants is null";
		
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;
		
		this.setLayout(new FlowLayout());

        // The main (default) tab
        mainTabPanel.setLayout(new BoxLayout(mainTabPanel, BoxLayout.Y_AXIS));
        setUpMainPanel();
        setUpDist();
        
        // The five threshold tabs
        for (int i = 0; i < PitchConstants.NUM_THRESHOLDS; ++i) {
            tabPanels[i].setRedSliderChangeListener(new RedSliderChangeListener(i));
            tabPanels[i].setGreenSliderChangeListener(new GreenSliderChangeListener(i));
            tabPanels[i].setBlueSliderChangeListener(new BlueSliderChangeListener(i));
            tabPanels[i].setHueSliderChangeListener(new HueSliderChangeListener(i));
            tabPanels[i].setSaturationSliderChangeListener(new SaturationSliderChangeListener(i));
            tabPanels[i].setValueSliderChangeListener(new ValueSliderChangeListener(i));
        }
        DistTabPanel.setLayout(new BoxLayout(DistTabPanel, BoxLayout.Y_AXIS));
        
        tabPane.addTab("Main", mainTabPanel);
        tabPane.addTab("Ball", tabPanels[PitchConstants.BALL]);
        tabPane.addTab("Blue Robot", tabPanels[PitchConstants.BLUE]);
        tabPane.addTab("Yellow Robot", tabPanels[PitchConstants.YELLOW]);
        tabPane.addTab("Grey Circles", tabPanels[PitchConstants.GREY]);
        tabPane.addTab("Green Plates", tabPanels[PitchConstants.GREEN]);
        tabPane.addTab("Distortion", DistTabPanel);
        
        tabPane.addChangeListener(tabChangeListener);
        this.add(tabPane);
        
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
		
		mainTabPanel.add(pitchPanel);
		
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
		
		mainTabPanel.add(colourPanel);
		
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
		rdbtnDist0.addMouseListener(distMouseListener);
		rdbtnDist1.addMouseListener(distMouseListener);
		rdbtnRight.addMouseListener(directionMouseListener);
		rdbtnLeft.addMouseListener(directionMouseListener);
		
		mainTabPanel.add(directionPanel);
		
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
		
		mainTabPanel.add(saveLoadPanel);
	}
	
	/**
	 * Reloads the default values for the sliders from the PitchConstants file.
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	
	
	private void setUpDist() {
		// Pitch choice
		JPanel distPanel = new JPanel();
		JLabel distLabel = new JLabel("Activate Distortion:");
		distPanel.add(distLabel);
		
		ButtonGroup DisthChoice = new ButtonGroup();
		DisthChoice.add(rdbtnDist0);
		DisthChoice.add(rdbtnDist1);
		distPanel.add(rdbtnDist0);
		distPanel.add(rdbtnDist1);
		
		rdbtnDist0.setSelected(true);
		
		rdbtnDist0.addMouseListener(pitchMouseListener);
		rdbtnDist1.addMouseListener(pitchMouseListener);
		
		DistTabPanel.add(distPanel);
		


	}
	public void reloadSliderDefaults() {
		for (int i = 0; i < PitchConstants.NUM_THRESHOLDS; ++i)
			tabPanels[i].setSliderValues(i, pitchConstants);
	}
}

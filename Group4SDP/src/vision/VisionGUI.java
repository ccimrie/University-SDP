package vision;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

/**
 * Creates and maintains the swing-based Control GUI, which 
 * provides both control manipulation (pitch choice, direction,
 * etc) and threshold setting. Also allows the saving/loading of
 * threshold values to a file.
 * 
 * @author s0840449
 */
@SuppressWarnings("serial")
// TODO: link new ThresholdsPanel objects to PitchConstants
public class VisionGUI extends JFrame implements ChangeListener {
	// A PitchConstants class used to load/save constants for the pitch.
	private PitchConstants pitchConstants;
	
	// Stores information about the current world state, such as shooting
	// direction, ball location, etc.
	private WorldState worldState;
	
	// Load/Save buttons.
	private JButton saveButton;
	private JButton loadButton;
	
	// Tabs.
	private JTabbedPane tabPane;
	private JPanel defaultPanel;
	private ThresholdsPanel ballPanel;
	private ThresholdsPanel bluePanel;
	private ThresholdsPanel yellowPanel;
	private ThresholdsPanel greyPanel;
	private ThresholdsPanel greenPanel;

	// Radio buttons
	JRadioButton pitch_0;
	JRadioButton pitch_1;
	JRadioButton colour_yellow;
	JRadioButton colour_blue;
	JRadioButton direction_right;
	JRadioButton direction_left;
	
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
		// Both state objects must not be null.
		assert (worldState != null) : "worldState is null";
		assert (pitchConstants != null) : "pitchConstants is null";
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        
        // Create panels for each of the tabs
        tabPane = new JTabbedPane();
        
        defaultPanel = new JPanel();
        defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.Y_AXIS));
        // The main (default) tab
        setUpMainPanel();
        
        // The five threshold tabs.
        ballPanel = new ThresholdsPanel(0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255);
        ballPanel.setSlidersChangeListener(this);
        
        bluePanel = new ThresholdsPanel(0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255);
        bluePanel.setSlidersChangeListener(this);
        
        yellowPanel = new ThresholdsPanel(0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255);
        yellowPanel.setSlidersChangeListener(this);
        
        greyPanel = new ThresholdsPanel(0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255);
        greyPanel.setSlidersChangeListener(this);
        
        greenPanel = new ThresholdsPanel(0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255);
        greenPanel.setSlidersChangeListener(this);
        
        tabPane.addTab("default", defaultPanel);
        tabPane.addTab("Ball", ballPanel);
        tabPane.addTab("Blue Robot", bluePanel);
        tabPane.addTab("Yellow Robot", yellowPanel);
        tabPane.addTab("Grey Circles", greyPanel);
        tabPane.addTab("Green Plates", greenPanel);
        
        //tabPane.addChangeListener(this);
        
        getContentPane().add(tabPane);
        this.pack();
        this.setVisible(true);
		
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;

        // Fires off an initial pass through the ChangeListener method,
        // to initialise all of the default values.
		this.stateChanged(null);
	}
	
	/**
	 * Sets up the main tab, adding in the pitch choice, the direction
	 * choice, the robot-colour choice and save/load buttons.
	 */
	private void setUpMainPanel() {
		// Pitch choice
		JPanel pitch_panel = new JPanel();
		JLabel pitch_label = new JLabel("Pitch:");
		pitch_panel.add(pitch_label);
		
		ButtonGroup pitch_choice = new ButtonGroup();
		pitch_0 = new JRadioButton("Main");
		pitch_1 = new JRadioButton("Side Room");
		pitch_choice.add(pitch_0);
		pitch_panel.add(pitch_0);
		pitch_choice.add(pitch_1);
		pitch_panel.add(pitch_1);
		
		pitch_0.setSelected(true);
		
		//pitch_0.addChangeListener(this);
		//pitch_1.addChangeListener(this);
		
		defaultPanel.add(pitch_panel);
		
		// Colour choice
		JPanel colour_panel = new JPanel();
		JLabel colour_label = new JLabel("Our colour:");
		colour_panel.add(colour_label);
		
		ButtonGroup colour_choice = new ButtonGroup();
		colour_yellow = new JRadioButton("Yellow");
		colour_blue = new JRadioButton("Blue");
		colour_choice.add(colour_yellow);
		colour_panel.add(colour_yellow);
		colour_choice.add(colour_blue);
		colour_panel.add(colour_blue);
		
		colour_yellow.setSelected(true);
		
		//colour_yellow.addChangeListener(this);
		//colour_blue.addChangeListener(this);
		
		defaultPanel.add(colour_panel);
		
		// Direction choice
		JPanel direction_panel = new JPanel();
		JLabel direction_label = new JLabel("Our shooting direction:");
		direction_panel.add(direction_label);
		
		ButtonGroup direction_choice = new ButtonGroup();
		direction_right = new JRadioButton("Right");
		direction_left = new JRadioButton("Left");
		direction_choice.add(direction_right);
		direction_panel.add(direction_right);
		direction_choice.add(direction_left);
		direction_panel.add(direction_left);
		
		direction_right.setSelected(true);
		
		//direction_right.addChangeListener(this);
		//direction_left.addChangeListener(this);
		
		defaultPanel.add(direction_panel);
		
		// Save/load buttons
		JPanel saveLoadPanel = new JPanel();
		
		saveButton = new JButton("Save Thresholds");
/*		saveButton.addActionListener(new ActionListener() {
			
			// Attempt to write all of the current thresholds to a file with a name 
			// based on the currently selected pitch.
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int pitchNum = (pitch_0.isSelected()) ? 0 : 1;
				
				int result = JOptionPane.showConfirmDialog(saveButton,
						"Are you sure you want to save current constants for pitch " + pitchNum + "?");
				
				if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) return;
				
				try {
					
					FileWriter writerDim = new FileWriter(new File("constants/pitch" + pitchNum + "Dimensions"));
					// We need to re-write the pitch dimensions. 
					// TODO: This currently means that cross-saving values
					// is basically unsupported as they will overwrite the
					// pitch dimensions incorrectly.
					writerDim.write(String.valueOf(pitchConstants.topBuffer) + "\n");
					writerDim.write(String.valueOf(pitchConstants.bottomBuffer) + "\n");
					writerDim.write(String.valueOf(pitchConstants.leftBuffer) + "\n");
					writerDim.write(String.valueOf(pitchConstants.rightBuffer) + "\n");
					writerDim.close();
					
					FileWriter writer = new FileWriter(new File("constants/pitch" + pitchNum));
					
					// Ball
					writer.write(String.valueOf(ball_r.getValue()) + "\n");
					writer.write(String.valueOf(ball_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(ball_g.getValue()) + "\n");
					writer.write(String.valueOf(ball_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(ball_b.getValue()) + "\n");
					writer.write(String.valueOf(ball_b.getUpperValue()) + "\n");
					writer.write(String.valueOf(ball_h.getValue()) + "\n");
					writer.write(String.valueOf(ball_h.getUpperValue()) + "\n");
					writer.write(String.valueOf(ball_s.getValue()) + "\n");
					writer.write(String.valueOf(ball_s.getUpperValue()) + "\n");
					writer.write(String.valueOf(ball_v.getValue()) + "\n");
					writer.write(String.valueOf(ball_v.getUpperValue()) + "\n");
					
					// Blue
					writer.write(String.valueOf(blue_r.getValue()) + "\n");
					writer.write(String.valueOf(blue_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(blue_g.getValue()) + "\n");
					writer.write(String.valueOf(blue_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(blue_b.getValue()) + "\n");
					writer.write(String.valueOf(blue_b.getUpperValue()) + "\n");
					writer.write(String.valueOf(blue_h.getValue()) + "\n");
					writer.write(String.valueOf(blue_h.getUpperValue()) + "\n");
					writer.write(String.valueOf(blue_s.getValue()) + "\n");
					writer.write(String.valueOf(blue_s.getUpperValue()) + "\n");
					writer.write(String.valueOf(blue_v.getValue()) + "\n");
					writer.write(String.valueOf(blue_v.getUpperValue()) + "\n");
					
					// Yellow
					writer.write(String.valueOf(yellow_r.getValue()) + "\n");
					writer.write(String.valueOf(yellow_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(yellow_g.getValue()) + "\n");
					writer.write(String.valueOf(yellow_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(yellow_b.getValue()) + "\n");
					writer.write(String.valueOf(yellow_b.getUpperValue()) + "\n");
					writer.write(String.valueOf(yellow_h.getValue()) + "\n");
					writer.write(String.valueOf(yellow_h.getUpperValue()) + "\n");
					writer.write(String.valueOf(yellow_s.getValue()) + "\n");
					writer.write(String.valueOf(yellow_s.getUpperValue()) + "\n");
					writer.write(String.valueOf(yellow_v.getValue()) + "\n");
					writer.write(String.valueOf(yellow_v.getUpperValue()) + "\n");
					
					// Grey
					writer.write(String.valueOf(grey_r.getValue()) + "\n");
					writer.write(String.valueOf(grey_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(grey_g.getValue()) + "\n");
					writer.write(String.valueOf(grey_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(grey_b.getValue()) + "\n");
					writer.write(String.valueOf(grey_b.getUpperValue()) + "\n");
					writer.write(String.valueOf(grey_h.getValue()) + "\n");
					writer.write(String.valueOf(grey_h.getUpperValue()) + "\n");
					writer.write(String.valueOf(grey_s.getValue()) + "\n");
					writer.write(String.valueOf(grey_s.getUpperValue()) + "\n");
					writer.write(String.valueOf(grey_v.getValue()) + "\n");
					writer.write(String.valueOf(grey_v.getUpperValue()) + "\n");
					
					// Green
					writer.write(String.valueOf(green_r.getValue()) + "\n");
					writer.write(String.valueOf(green_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(green_g.getValue()) + "\n");
					writer.write(String.valueOf(green_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(green_b.getValue()) + "\n");
					writer.write(String.valueOf(green_b.getUpperValue()) + "\n");
					writer.write(String.valueOf(green_h.getValue()) + "\n");
					writer.write(String.valueOf(green_h.getUpperValue()) + "\n");
					writer.write(String.valueOf(green_s.getValue()) + "\n");
					writer.write(String.valueOf(green_s.getUpperValue()) + "\n");
					writer.write(String.valueOf(green_v.getValue()) + "\n");
					writer.write(String.valueOf(green_v.getUpperValue()) + "\n");
					
					writer.flush();
					writer.close();
					
					System.out.println("Wrote successfully!");
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});*/
		
		saveLoadPanel.add(saveButton);
		
		loadButton = new JButton("Load Thresholds");
		loadButton.addActionListener(new ActionListener() {
			
			// Override the current threshold settings from those set in
			// the correct constants file for the current pitch.
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int pitchNum = (pitch_0.isSelected()) ? 0 : 1;
				
				int result = JOptionPane.showConfirmDialog(loadButton, "Are you sure you want to load " +
						"pre-saved constants for pitch " + pitchNum + "?");
				
				if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) return;
				
				pitchConstants.setPitchNum(pitchNum);
				reloadSliderDefaults();
			}
		});
		
		saveLoadPanel.add(loadButton);
		
		defaultPanel.add(saveLoadPanel);
	}

	/**
	 * A Change listener for various components on the GUI. When a component is
	 * changed all information is updated.
	 * 
	 *  @param e		The event that was created for the change.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		// Update the world state
		if (pitch_0.isSelected()) {
			worldState.setPitch(0);
		} else {
			worldState.setPitch(1);
		}
		if(colour_yellow.isSelected()) {
			worldState.setColour(0);
		} else {
			worldState.setColour(1);
		}
		if(direction_right.isSelected()) {
			worldState.setDirection(0);
		} else {
			worldState.setDirection(1);
		}
		
		// Update the PitchConstants object
		int index = tabPane.getSelectedIndex();
		
		switch(index) {
		case(0):
			pitchConstants.setDebugMode(PitchConstants.BALL, false);
			pitchConstants.setDebugMode(PitchConstants.BLUE, false);
			pitchConstants.setDebugMode(PitchConstants.YELLOW, false);
			pitchConstants.setDebugMode(PitchConstants.GREY, false);
			pitchConstants.setDebugMode(PitchConstants.GREEN, false);
			break;
		case(1):
			pitchConstants.setDebugMode(PitchConstants.BALL, true);
			pitchConstants.setDebugMode(PitchConstants.BLUE, false);
			pitchConstants.setDebugMode(PitchConstants.YELLOW, false);
			pitchConstants.setDebugMode(PitchConstants.GREY, false);
			pitchConstants.setDebugMode(PitchConstants.GREEN, false);
			break;
		case(2):
			pitchConstants.setDebugMode(PitchConstants.BALL, false);
			pitchConstants.setDebugMode(PitchConstants.BLUE, true);
			pitchConstants.setDebugMode(PitchConstants.YELLOW, false);
			pitchConstants.setDebugMode(PitchConstants.GREY, false);
			pitchConstants.setDebugMode(PitchConstants.GREEN, false);
			break;
		case(3):
			pitchConstants.setDebugMode(PitchConstants.BALL, false);
			pitchConstants.setDebugMode(PitchConstants.BLUE, false);
			pitchConstants.setDebugMode(PitchConstants.YELLOW, true);
			pitchConstants.setDebugMode(PitchConstants.GREY, false);
			pitchConstants.setDebugMode(PitchConstants.GREEN, false);
			break;
		case(4):
			pitchConstants.setDebugMode(PitchConstants.BALL, false);
			pitchConstants.setDebugMode(PitchConstants.BLUE, false);
			pitchConstants.setDebugMode(PitchConstants.YELLOW, false);
			pitchConstants.setDebugMode(PitchConstants.GREY, true);
			pitchConstants.setDebugMode(PitchConstants.GREEN, false);
			break;
		case(5):
			pitchConstants.setDebugMode(PitchConstants.BALL, false);
			pitchConstants.setDebugMode(PitchConstants.BLUE, false);
			pitchConstants.setDebugMode(PitchConstants.YELLOW, false);
			pitchConstants.setDebugMode(PitchConstants.GREY, false);
			pitchConstants.setDebugMode(PitchConstants.GREEN, true);
			break;
		default:
			pitchConstants.setDebugMode(PitchConstants.BALL, false);
			pitchConstants.setDebugMode(PitchConstants.BLUE, false);
			pitchConstants.setDebugMode(PitchConstants.YELLOW, false);
			pitchConstants.setDebugMode(PitchConstants.GREY, false);
			pitchConstants.setDebugMode(PitchConstants.GREEN, false);
			break;
		}
		
		// Ball
/*		pitchConstants.setRedMin(PitchConstants.BALL, ball_r.getValue());
		pitchConstants.setRedMax(PitchConstants.BALL, ball_r.getUpperValue());

		pitchConstants.setGreenMin(PitchConstants.BALL, ball_g.getValue());
		pitchConstants.setGreenMax(PitchConstants.BALL, ball_g.getUpperValue());
		
		pitchConstants.setBlueMin(PitchConstants.BALL, ball_b.getValue());
		pitchConstants.setBlueMax(PitchConstants.BALL, ball_b.getUpperValue());
		
		pitchConstants.setHueMin(PitchConstants.BALL, ball_h.getValue() / 255.0);
		pitchConstants.setHueMax(PitchConstants.BALL, ball_h.getUpperValue() / 255.0);

		pitchConstants.setSaturationMin(PitchConstants.BALL, ball_s.getValue() / 255.0);
		pitchConstants.setSaturationMax(PitchConstants.BALL, ball_s.getUpperValue() / 255.0);
		
		pitchConstants.setValueMin(PitchConstants.BALL, ball_v.getValue() / 255.0);
		pitchConstants.setValueMax(PitchConstants.BALL, ball_v.getUpperValue() / 255.0);*/
		
		// Blue Robot
		/*pitchConstants.setBlue_r_low(blue_r.getValue());
		pitchConstants.setBlue_r_high(blue_r.getUpperValue());

		pitchConstants.setBlue_g_low(blue_g.getValue());
		pitchConstants.setBlue_g_high(blue_g.getUpperValue());
		
		pitchConstants.setBlue_b_low(blue_b.getValue());
		pitchConstants.setBlue_b_high(blue_b.getUpperValue());
		
		pitchConstants.setBlue_h_low(blue_h.getValue() / 255.0);
		pitchConstants.setBlue_h_high(blue_h.getUpperValue() / 255.0);

		pitchConstants.setBlue_s_low(blue_s.getValue() / 255.0);
		pitchConstants.setBlue_s_high(blue_s.getUpperValue() / 255.0);
		
		pitchConstants.setBlue_v_low(blue_v.getValue() / 255.0);
		pitchConstants.setBlue_v_high(blue_v.getUpperValue() / 255.0);
		
		// Yellow Robot
		pitchConstants.setYellow_r_low(yellow_r.getValue());
		pitchConstants.setYellow_r_high(yellow_r.getUpperValue());

		pitchConstants.setYellow_g_low(yellow_g.getValue());
		pitchConstants.setYellow_g_high(yellow_g.getUpperValue());
		
		pitchConstants.setYellow_b_low(yellow_b.getValue());
		pitchConstants.setYellow_b_high(yellow_b.getUpperValue());
		
		pitchConstants.setYellow_h_low(yellow_h.getValue() / 255.0);
		pitchConstants.setYellow_h_high(yellow_h.getUpperValue() / 255.0);

		pitchConstants.setYellow_s_low(yellow_s.getValue() / 255.0);
		pitchConstants.setYellow_s_high(yellow_s.getUpperValue() / 255.0);
		
		pitchConstants.setYellow_v_low(yellow_v.getValue() / 255.0);
		pitchConstants.setYellow_v_high(yellow_v.getUpperValue() / 255.0);
		
		// Grey Circles
		pitchConstants.setGrey_r_low(grey_r.getValue());
		pitchConstants.setGrey_r_high(grey_r.getUpperValue());

		pitchConstants.setGrey_g_low(grey_g.getValue());
		pitchConstants.setGrey_g_high(grey_g.getUpperValue());
		
		pitchConstants.setGrey_b_low(grey_b.getValue());
		pitchConstants.setGrey_b_high(grey_b.getUpperValue());
		
		pitchConstants.setGrey_h_low(grey_h.getValue() / 255.0);
		pitchConstants.setGrey_h_high(grey_h.getUpperValue() / 255.0);

		pitchConstants.setGrey_s_low(grey_s.getValue() / 255.0);
		pitchConstants.setGrey_s_high(grey_s.getUpperValue() / 255.0);
		
		pitchConstants.setGrey_v_low(grey_v.getValue() / 255.0);
		pitchConstants.setGrey_v_high(grey_v.getUpperValue() / 255.0);
		
		// Green Circles
		pitchConstants.setGreen_r_low(green_r.getValue());
		pitchConstants.setGreen_r_high(green_r.getUpperValue());

		pitchConstants.setGreen_g_low(green_g.getValue());
		pitchConstants.setGreen_g_high(green_g.getUpperValue());
		
		pitchConstants.setGreen_b_low(green_b.getValue());
		pitchConstants.setGreen_b_high(green_b.getUpperValue());
		
		pitchConstants.setGreen_h_low(green_h.getValue() / 255.0);
		pitchConstants.setGreen_h_high(green_h.getUpperValue() / 255.0);

		pitchConstants.setGreen_s_low(green_s.getValue() / 255.0);
		pitchConstants.setGreen_s_high(green_s.getUpperValue() / 255.0);
		
		pitchConstants.setGreen_v_low(green_v.getValue() / 255.0);
		pitchConstants.setGreen_v_high(green_v.getUpperValue() / 255.0);*/
	}
	
	/**
	 * Reloads the default values for the sliders from the PitchConstants file.
	 */
	public void reloadSliderDefaults() {
		// Ball slider
		/*setSliderVals(ball_r, pitchConstants.getRedMin(PitchConstants.BALL), pitchConstants.getRedMax(PitchConstants.BALL));
		setSliderVals(ball_g, pitchConstants.getGreenMin(PitchConstants.BALL), pitchConstants.getGreenMax(PitchConstants.BALL));
		setSliderVals(ball_b, pitchConstants.getBlueMin(PitchConstants.BALL), pitchConstants.getBlueMax(PitchConstants.BALL));
		setSliderVals(ball_h, pitchConstants.getHueMin(PitchConstants.BALL), pitchConstants.getHueMax(PitchConstants.BALL));
		setSliderVals(ball_s, pitchConstants.getSaturationMin(PitchConstants.BALL), pitchConstants.getSaturationMax(PitchConstants.BALL));
		setSliderVals(ball_v, pitchConstants.getValueMin(PitchConstants.BALL), pitchConstants.getValueMax(PitchConstants.BALL));
		
		// Blue slider
		setSliderVals(blue_r, pitchConstants.blue_r_low, pitchConstants.blue_r_high);
		setSliderVals(blue_g, pitchConstants.blue_g_low, pitchConstants.blue_g_high);
		setSliderVals(blue_b, pitchConstants.blue_b_low, pitchConstants.blue_b_high);
		setSliderVals(blue_h, pitchConstants.blue_h_low, pitchConstants.blue_h_high);
		setSliderVals(blue_s, pitchConstants.blue_s_low, pitchConstants.blue_s_high);
		setSliderVals(blue_v, pitchConstants.blue_v_low, pitchConstants.blue_v_high);
		
		// Yellow slider
		setSliderVals(yellow_r, pitchConstants.yellow_r_low, pitchConstants.yellow_r_high);
		setSliderVals(yellow_g, pitchConstants.yellow_g_low, pitchConstants.yellow_g_high);
		setSliderVals(yellow_b, pitchConstants.yellow_b_low, pitchConstants.yellow_b_high);
		setSliderVals(yellow_h, pitchConstants.yellow_h_low, pitchConstants.yellow_h_high);
		setSliderVals(yellow_s, pitchConstants.yellow_s_low, pitchConstants.yellow_s_high);
		setSliderVals(yellow_v, pitchConstants.yellow_v_low, pitchConstants.yellow_v_high);
		
		// Grey slider
		setSliderVals(grey_r, pitchConstants.grey_r_low, pitchConstants.grey_r_high);
		setSliderVals(grey_g, pitchConstants.grey_g_low, pitchConstants.grey_g_high);
		setSliderVals(grey_b, pitchConstants.grey_b_low, pitchConstants.grey_b_high);
		setSliderVals(grey_h, pitchConstants.grey_h_low, pitchConstants.grey_h_high);
		setSliderVals(grey_s, pitchConstants.grey_s_low, pitchConstants.grey_s_high);
		setSliderVals(grey_v, pitchConstants.grey_v_low, pitchConstants.grey_v_high);
		
		// Green slider
		setSliderVals(green_r, pitchConstants.green_r_low, pitchConstants.green_r_high);
		setSliderVals(green_g, pitchConstants.green_g_low, pitchConstants.green_g_high);
		setSliderVals(green_b, pitchConstants.green_b_low, pitchConstants.green_b_high);
		setSliderVals(green_h, pitchConstants.green_h_low, pitchConstants.green_h_high);
		setSliderVals(green_s, pitchConstants.green_s_low, pitchConstants.green_s_high);
		setSliderVals(green_v, pitchConstants.green_v_low, pitchConstants.green_v_high);*/
	}

	/**
	 * Set the the values of a range slider.
	 * 
	 * @param rangeSlider		The range slider to set the values for.
	 * @param low				The lower end of the range.
	 * @param high				The higher end of the range.
	 */
	private void setSliderVals(RangeSlider rangeSlider, int low, int high) {
		rangeSlider.setUpperValue(high);
		rangeSlider.setValue(low);
	}
}

package vision;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

/**
 * A UI container for holding the contents of each of the threshold tabs
 * 
 * @author Alex Adams
 */
@SuppressWarnings("serial")
public class ThresholdsPanel extends JPanel {
	private static final int MAJOR_TICK = 50;
	private static final int MINOR_TICK = 10;
	
	private int redMin;
	private int redMax;
	private final JPanel redPanel = new JPanel();
	private final JLabel redLabel = new JLabel("Red:");
	private RangeSlider redSlider;

	private int greenMin;
	private int greenMax;
	private final JPanel greenPanel = new JPanel();
	private final JLabel greenLabel = new JLabel("Green:");
	private RangeSlider greenSlider;

	private int blueMin;
	private int blueMax;
	private final JPanel bluePanel = new JPanel();
	private final JLabel blueLabel = new JLabel("Blue:");
	private RangeSlider blueSlider;

	private int hueMin;
	private int hueMax;
	private final JPanel huePanel = new JPanel();
	private final JLabel hueLabel = new JLabel("Hue:");
	private RangeSlider hueSlider;
	
	private int saturationMin;
	private int saturationMax;
	private final JPanel saturationPanel = new JPanel();
	private final JLabel saturationLabel = new JLabel("Sat:");
	private RangeSlider saturationSlider;

	private int valueMin;
	private int valueMax;
	private final JPanel valuePanel = new JPanel();
	private final JLabel valueLabel = new JLabel("Value:");
	private RangeSlider valueSlider;
	
	/**
	 * Constructs a ThresholdsPanel with the default setting of all minimums to 0,
	 * all maximums to 255.
	 * 
	 * TODO: Possibly add functionality for different settings. (Currently not needed)
	 */
	public ThresholdsPanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// Set default values
		redMin = 0;			redMax = 255;
		greenMin = 0;		greenMax = 255;
		blueMin = 0;		blueMax = 255;
		hueMin = 0;			hueMax = 255;
		saturationMin = 0;	saturationMax = 255;
		valueMin = 0;		valueMax = 255;
		
		redSlider = new RangeSlider(redMin, redMax);
		initialiseSlider(redSlider);
		redPanel.add(redLabel);
		redPanel.add(redSlider);
		this.add(redPanel);
		
		greenSlider = new RangeSlider(greenMin, greenMax);
		initialiseSlider(greenSlider);
		greenPanel.add(greenLabel);
		greenPanel.add(greenSlider);
		this.add(greenPanel);
		
		blueSlider = new RangeSlider(blueMin, blueMax);
		initialiseSlider(blueSlider);
		bluePanel.add(blueLabel);
		bluePanel.add(blueSlider);
		this.add(bluePanel);
		
		hueSlider = new RangeSlider(hueMin, hueMax);
		initialiseSlider(hueSlider);
		huePanel.add(hueLabel);
		huePanel.add(hueSlider);
		this.add(huePanel);
		
		saturationSlider = new RangeSlider(saturationMin, saturationMax);
		initialiseSlider(saturationSlider);
		saturationPanel.add(saturationLabel);
		saturationPanel.add(saturationSlider);
		this.add(saturationPanel);
		
		valueSlider = new RangeSlider(valueMin, valueMax);
		initialiseSlider(valueSlider);
		valuePanel.add(valueLabel);
		valuePanel.add(valueSlider);
		this.add(valuePanel);
	}
	
	private void initialiseSlider(RangeSlider slider) {
		slider.setMinorTickSpacing(MINOR_TICK);
		slider.setMajorTickSpacing(MAJOR_TICK);
		
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
	}
	
	public int[] getRedSliderValues() {
		int[] lowerUpper = new int[] {redSlider.getLowerValue(), redSlider.getUpperValue()};
		return lowerUpper;
	}
	public void setRedSliderValues(int lower, int upper) {
		redSlider.setLowerValue(lower);
		redSlider.setUpperValue(upper);
	}

	public int[] getGreenSliderValues() {
		int[] lowerUpper = new int[] {greenSlider.getLowerValue(), greenSlider.getUpperValue()};
		return lowerUpper;
	}
	public void setGreenSliderValues(int lower, int upper) {
		greenSlider.setLowerValue(lower);
		greenSlider.setUpperValue(upper);
	}

	public int[] getBlueSliderValues() {
		int[] lowerUpper = new int[] {blueSlider.getLowerValue(), blueSlider.getUpperValue()};
		return lowerUpper;
	}
	public void setBlueSliderValues(int lower, int upper) {
		blueSlider.setLowerValue(lower);
		blueSlider.setUpperValue(upper);
	}

	public int[] getHueSliderValues() {
		int[] lowerUpper = new int[] {hueSlider.getLowerValue(), hueSlider.getUpperValue()};
		return lowerUpper;
	}
	public void setHueSliderValues(int lower, int upper) {
		hueSlider.setLowerValue(lower);
		hueSlider.setUpperValue(upper);
	}

	public int[] getSaturationSliderValues() {
		int[] lowerUpper = new int[] {saturationSlider.getLowerValue(), saturationSlider.getUpperValue()};
		return lowerUpper;
	}
	public void setSaturationSliderValues(int lower, int upper) {
		saturationSlider.setLowerValue(lower);
		saturationSlider.setUpperValue(upper);
	}

	public int[] getValueSliderValues() {
		int[] lowerUpper = new int[] {valueSlider.getLowerValue(), valueSlider.getUpperValue()};
		return lowerUpper;
	}
	public void setValueSliderValues(int lower, int upper) {
		valueSlider.setLowerValue(lower);
		valueSlider.setUpperValue(upper);
	}
	
	public void setSliderValues(int index, PitchConstants pitchConstants) {
		setRedSliderValues(pitchConstants.getRedLower(index),
						   pitchConstants.getRedUpper(index));
		setGreenSliderValues(pitchConstants.getGreenLower(index),
							 pitchConstants.getGreenUpper(index));
		setBlueSliderValues(pitchConstants.getBlueLower(index),
							pitchConstants.getBlueUpper(index));
		
		setHueSliderValues((int)(255.0 * pitchConstants.getHueLower(index)),
						   (int)(255.0 * pitchConstants.getHueUpper(index)));
		setSaturationSliderValues((int)(255.0 * pitchConstants.getSaturationLower(index)),
								  (int)(255.0 * pitchConstants.getSaturationUpper(index)));
		setValueSliderValues((int)(255.0 * pitchConstants.getValueLower(index)),
							 (int)(255.0 * pitchConstants.getValueUpper(index)));
	}
	
	public void setRedSliderChangeListener(ChangeListener listener) {
		redSlider.addChangeListener(listener);
	}
	public void setGreenSliderChangeListener(ChangeListener listener) {
		greenSlider.addChangeListener(listener);
	}
	public void setBlueSliderChangeListener(ChangeListener listener) {
		blueSlider.addChangeListener(listener);
	}
	public void setHueSliderChangeListener(ChangeListener listener) {
		hueSlider.addChangeListener(listener);
	}
	public void setSaturationSliderChangeListener(ChangeListener listener) {
		saturationSlider.addChangeListener(listener);
	}
	public void setValueSliderChangeListener(ChangeListener listener) {
		valueSlider.addChangeListener(listener);
	}
}

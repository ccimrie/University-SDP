package vision.gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import vision.PitchConstants;

/**
 * A UI container for holding the contents of each of the threshold tabs
 * 
 * @author Alex Adams
 */
@SuppressWarnings("serial")
public class ThresholdsPanel extends JPanel {
	private static final int SLIDER_MIN = 0;
	private static final int SLIDER_MAX = 256;
	
	private final int redMin = SLIDER_MIN;
	private final int redMax = SLIDER_MAX;
	private final JPanel redPanel = new JPanel();
	private final JLabel redLabel = new JLabel("Red:");
	private RangeSlider redSlider;

	private final int greenMin = SLIDER_MIN;
	private final int greenMax = SLIDER_MAX;
	private final JPanel greenPanel = new JPanel();
	private final JLabel greenLabel = new JLabel("Green:");
	private RangeSlider greenSlider;

	private final int blueMin = SLIDER_MIN;
	private final int blueMax = SLIDER_MAX;
	private final JPanel bluePanel = new JPanel();
	private final JLabel blueLabel = new JLabel("Blue:");
	private RangeSlider blueSlider;

	private final int hueMin = SLIDER_MIN;
	private final int hueMax = SLIDER_MAX;
	private final JPanel huePanel = new JPanel();
	private final JLabel hueLabel = new JLabel("Hue:");
	private RangeSlider hueSlider;
	
	private final int saturationMin = SLIDER_MIN;
	private final int saturationMax = SLIDER_MAX;
	private final JPanel saturationPanel = new JPanel();
	private final JLabel saturationLabel = new JLabel("Sat:");
	private RangeSlider saturationSlider;

	private final int valueMin = SLIDER_MIN;
	private final int valueMax = SLIDER_MAX;
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
		
		redSlider = new RangeSlider(redMin, redMax);
		redPanel.add(redLabel);
		redPanel.add(redSlider);
		this.add(redPanel);
		
		greenSlider = new RangeSlider(greenMin, greenMax);
		greenPanel.add(greenLabel);
		greenPanel.add(greenSlider);
		this.add(greenPanel);
		
		blueSlider = new RangeSlider(blueMin, blueMax);
		bluePanel.add(blueLabel);
		bluePanel.add(blueSlider);
		this.add(bluePanel);
		
		hueSlider = new RangeSlider(hueMin, hueMax);
		huePanel.add(hueLabel);
		huePanel.add(hueSlider);
		this.add(huePanel);
		
		saturationSlider = new RangeSlider(saturationMin, saturationMax);
		saturationPanel.add(saturationLabel);
		saturationPanel.add(saturationSlider);
		this.add(saturationPanel);
		
		valueSlider = new RangeSlider(valueMin, valueMax);
		valuePanel.add(valueLabel);
		valuePanel.add(valueSlider);
		this.add(valuePanel);
	}
	
	public int[] getRedSliderValues() {
		int[] lowerUpper = new int[] {redSlider.getLowerValue() - 1,
				redSlider.getUpperValue() - 1};
		return lowerUpper;
	}
	public void setRedSliderValues(int lower, int upper) {
		redSlider.setLowerValue(1 + lower);
		redSlider.setUpperValue(1 + upper);
	}

	public int[] getGreenSliderValues() {
		int[] lowerUpper = new int[] {greenSlider.getLowerValue() - 1,
				greenSlider.getUpperValue() - 1};
		return lowerUpper;
	}
	public void setGreenSliderValues(int lower, int upper) {
		greenSlider.setLowerValue(1 + lower);
		greenSlider.setUpperValue(1 + upper);
	}

	public int[] getBlueSliderValues() {
		int[] lowerUpper = new int[] {blueSlider.getLowerValue() - 1,
				blueSlider.getUpperValue() - 1};
		return lowerUpper;
	}
	public void setBlueSliderValues(int lower, int upper) {
		blueSlider.setLowerValue(1 + lower);
		blueSlider.setUpperValue(1 + upper);
	}

	public int[] getHueSliderValues() {
		int[] lowerUpper = new int[] {hueSlider.getLowerValue() - 1,
				hueSlider.getUpperValue() - 1};
		return lowerUpper;
	}
	public void setHueSliderValues(int lower, int upper) {
		hueSlider.setLowerValue(1 + lower);
		hueSlider.setUpperValue(1 + upper);
	}

	public int[] getSaturationSliderValues() {
		int[] lowerUpper = new int[] {saturationSlider.getLowerValue() - 1,
				saturationSlider.getUpperValue() - 1};
		return lowerUpper;
	}
	public void setSaturationSliderValues(int lower, int upper) {
		saturationSlider.setLowerValue(1 + lower);
		saturationSlider.setUpperValue(1 + upper);
	}

	public int[] getValueSliderValues() {
		int[] lowerUpper = new int[] {valueSlider.getLowerValue() - 1,
				valueSlider.getUpperValue() - 1};
		return lowerUpper;
	}
	public void setValueSliderValues(int lower, int upper) {
		valueSlider.setLowerValue(1 + lower);
		valueSlider.setUpperValue(1 + upper);
	}
	
	public void setSliderValues(int index, PitchConstants pitchConstants) {
		setRedSliderValues(1 + pitchConstants.getRedLower(index),
						   1 + pitchConstants.getRedUpper(index));
		setGreenSliderValues(1 + pitchConstants.getGreenLower(index),
							 1 + pitchConstants.getGreenUpper(index));
		setBlueSliderValues(1 + pitchConstants.getBlueLower(index),
							1 + pitchConstants.getBlueUpper(index));
		
		setHueSliderValues(1 + (int)(255.0 * pitchConstants.getHueLower(index)),
						   1 + (int)(255.0 * pitchConstants.getHueUpper(index)));
		setSaturationSliderValues(1 + (int)(255.0 * pitchConstants.getSaturationLower(index)),
								  1 + (int)(255.0 * pitchConstants.getSaturationUpper(index)));
		setValueSliderValues(1 + (int)(255.0 * pitchConstants.getValueLower(index)),
							 1 + (int)(255.0 * pitchConstants.getValueUpper(index)));
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

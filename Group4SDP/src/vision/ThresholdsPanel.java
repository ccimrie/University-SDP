package vision;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

/**
 * A UI container for holding the contents of each of the threshold tabs
 * @author Alex Adams
 */
@SuppressWarnings("serial")
public class ThresholdsPanel extends JPanel {
	private static final int MAJOR_TICK = 50;
	private static final int MINOR_TICK = 10;
	
	private final JPanel redPanel = new JPanel();
	private final JLabel redLabel = new JLabel("Red:");
	private RangeSlider redSlider;
	
	private final JPanel greenPanel = new JPanel();
	private final JLabel greenLabel = new JLabel("Green:");
	private RangeSlider greenSlider;
	
	private final JPanel bluePanel = new JPanel();
	private final JLabel blueLabel = new JLabel("Blue:");
	private RangeSlider blueSlider;
	
	private final JPanel huePanel = new JPanel();
	private final JLabel hueLabel = new JLabel("Hue:");
	private RangeSlider hueSlider;
	
	private final JPanel saturationPanel = new JPanel();
	private final JLabel saturationLabel = new JLabel("Sat:");
	private RangeSlider saturationSlider;

	private final JPanel valuePanel = new JPanel();
	private final JLabel valueLabel = new JLabel("Value:");
	private RangeSlider valueSlider;
	
	public ThresholdsPanel(int redMin, int redMax, int greenMin, int greenMax,
						   int blueMin, int blueMax, int hueMin, int hueMax,
						   int saturationMin, int saturationMax, int valueMin, int valueMax) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		redSlider = new RangeSlider(redMin, redMax);
		initialiseSlider(redSlider);
		redPanel.add(redLabel);
		redPanel.add(redSlider);
		this.add(redPanel);
		
		greenSlider = new RangeSlider(redMin, redMax);
		initialiseSlider(greenSlider);
		greenPanel.add(greenLabel);
		greenPanel.add(greenSlider);
		this.add(greenPanel);
		
		blueSlider = new RangeSlider(redMin, redMax);
		initialiseSlider(blueSlider);
		bluePanel.add(blueLabel);
		bluePanel.add(blueSlider);
		this.add(bluePanel);
		
		hueSlider = new RangeSlider(redMin, redMax);
		initialiseSlider(hueSlider);
		huePanel.add(hueLabel);
		huePanel.add(hueSlider);
		this.add(huePanel);
		
		saturationSlider = new RangeSlider(redMin, redMax);
		initialiseSlider(saturationSlider);
		saturationPanel.add(saturationLabel);
		saturationPanel.add(saturationSlider);
		this.add(saturationPanel);
		
		valueSlider = new RangeSlider(redMin, redMax);
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
	
	public void setSliderValues(int index, PitchConstants pitchConstants) {
		
	}
	
	public void setSlidersChangeListener(ChangeListener listener) {
		redSlider.addChangeListener(listener);
		greenSlider.addChangeListener(listener);
		blueSlider.addChangeListener(listener);
		hueSlider.addChangeListener(listener);
		saturationSlider.addChangeListener(listener);
		valueSlider.addChangeListener(listener);
	}
}

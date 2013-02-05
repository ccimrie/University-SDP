package JavaVision;

/**
 * Stores the states of the various thresholds.
 * 
 * @author s0840449
 */
public class ThresholdsState {
	public static final int BALL = 0;
	public static final int BLUE = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	public static final int GREY = 4;
	
	public static final int RGBMIN = 0;
	public static final int RGBMAX = 255;
	public static final double HSVMIN = 0.0;
	public static final double HSVMAX = 1.0;
	
	private int[] redMin = new int[5];
	private int[] redMax = new int[5];
	private int[] greenMin = new int[5];
	private int[] greenMax = new int[5];
	private int[] blueMin = new int[5];
	private int[] blueMax = new int[5];
	private double[] hueMin = new double[5];
	private double[] hueMax = new double[5];
	private double[] saturationMin = new double[5];
	private double[] saturationMax = new double[5];
	private double[] valueMin = new double[5];
	private double[] valueMax = new double[5];
	private boolean[] debug = new boolean[] {false, false, false, false, false};
	
	public int getRedMin(int i) {
		return redMin[i];
	}
	public void setRedMin(int i, int redMin) {
		this.redMin[i] = redMin;
	}
	public int getRedMax(int i) {
		return redMax[i];
	}
	public void setRedMax(int i, int redMax) {
		this.redMax[i] = redMax;
	}
	
	public int getGreenMin(int i) {
		return greenMin[i];
	}
	public void setGreenMin(int i, int greenMin) {
		this.greenMin[i] = greenMin;
	}
	public int getGreenMax(int i) {
		return greenMax[i];
	}
	public void setGreenMax(int i, int greenMax) {
		this.greenMax[i] = greenMax;
	}
	
	public int getBlueMin(int i) {
		return blueMin[i];
	}
	public void setBlueMin(int i, int blueMin) {
		this.blueMin[i] = blueMin;
	}
	public int getBlueMax(int i) {
		return blueMax[i];
	}
	public void setBlueMax(int i, int blueMax) {
		this.blueMax[i] = blueMax;
	}
	
	public double getHueMin(int i) {
		return hueMin[i];
	}
	public void setHueMin(int i, double hueMin) {
		this.hueMin[i] = hueMin;
	}
	public double getHueMax(int i) {
		return hueMax[i];
	}
	public void setHueMax(int i, double hueMax) {
		this.hueMax[i] = hueMax;
	}
	
	public double getSaturationMin(int i) {
		return saturationMin[i];
	}
	public void setSaturationMin(int i, double saturationMin) {
		this.saturationMin[i] = saturationMin;
	}
	public double getSaturationMax(int i) {
		return saturationMax[i];
	}
	public void setSaturationMax(int i, double saturationMax) {
		this.saturationMax[i] = saturationMax;
	}
	
	public double getValueMin(int i) {
		return valueMin[i];
	}
	public void setValueMin(int i, double valueMin) {
		this.valueMin[i] = valueMin;
	}
	public double getValueMax(int i) {
		return valueMax[i];
	}
	public void setValueMax(int i, double valueMax) {
		this.valueMax[i] = valueMax;
	}
	
	public boolean debugMode(int i) {
		return this.debug[i];
	}
	public void setDebugMode(int i, boolean debug) {
		this.debug[i] = debug;
	}
}

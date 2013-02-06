package vision;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A state object that holds the constants for various values about the pitch,
 * such as thresholding values and dimension variables.
 * 
 * @author s0840449
 */
public class PitchConstants {
	// The pitch number. 0 is the main pitch, 1 is the side pitch
	private int pitchNum;

	public static final int BALL = 0;
	public static final int BLUE = 1;
	public static final int YELLOW = 2;
	public static final int GREY = 3;
	public static final int GREEN = 4;
	
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
	
	// Pitch dimensions:
	// When scanning the pitch we look at pixels starting from 0 + topBuffer and 
	// 0 + leftBuffer, and then scan to pixels at 480 - bottomBuffer and 
	// 640 - rightBuffer.

	public int topBuffer;
	public int bottomBuffer;
	public int leftBuffer;
	public int rightBuffer;

	/**
	 * Default constructor.
	 * 
	 * @param pitchNum
	 *            The pitch that we are on.
	 */
	public PitchConstants(int pitchNum) {
		// Just call the setPitchNum method to load in the constants
		setPitchNum(pitchNum);
	}

	/**
	 * Sets a new pitch number, loading in constants from the corresponding
	 * file.
	 * 
	 * @param newPitchNum
	 *            The pitch number to use.
	 */
	public void setPitchNum(int newPitchNum) {
		assert (newPitchNum >= 0 && newPitchNum <= 1) : "Invalid pitch number";
		this.pitchNum = newPitchNum;
		
		loadConstants(System.getProperty("user.dir") + "/constants/pitch" + pitchNum);
	}

	/**
	 * Load in the constants from a file. Note that this assumes that the
	 * constants file is well formed.
	 * 
	 * @param fileName
	 *            The file name to load constants from.
	 */
	public void loadConstants(String fileName) {
		Scanner scannerDim;

		try {
			scannerDim = new Scanner(new File(fileName + "Dimensions"));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot load constants file " + fileName + "Dimensions" + ":");
			System.err.println(e.getMessage());
			loadDefaultConstants();
			
			return;
		}

		assert (scannerDim != null);

		// Pitch Dimensions
		this.topBuffer = scannerDim.nextInt();
		this.bottomBuffer = scannerDim.nextInt();
		this.leftBuffer = scannerDim.nextInt();
		this.rightBuffer = scannerDim.nextInt();

		Scanner scanner;

		try {
			scanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot load constants file " + fileName + ":");
			System.err.println(e.getMessage());
			loadDefaultConstants();
			return;
		}
		
		assert(scanner != null);
		
		// We assume that the file is well formed
		
		// Iterate over ball, blue robot, yellow robot, grey circles, and then green plates
		// in the order they're defined above.
		for (int i = 0; i < 5; ++i) {
			this.redMin[i] = scanner.nextInt();
			this.redMax[i] = scanner.nextInt();
			this.greenMin[i] = scanner.nextInt();
			this.greenMax[i] = scanner.nextInt();
			this.blueMin[i] = scanner.nextInt();
			this.blueMax[i] = scanner.nextInt();
			this.hueMin[i] = scanner.nextInt();
			this.hueMax[i] = scanner.nextInt();
			this.saturationMin[i] = scanner.nextInt();
			this.saturationMax[i] = scanner.nextInt();
			this.valueMin[i] = scanner.nextInt();
			this.valueMax[i] = scanner.nextInt();
		}
	}

	/**
	 * Loads default values for the constants, used when loading from a file
	 * fails.
	 */
	public void loadDefaultConstants() {
		// Iterate over ball, blue robot, yellow robot, grey circles, and then green plates
		// in the order they're defined above.
		for (int i = 0; i < 5; ++i) {
			this.redMin[i] = 0;
			this.redMax[i] = 255;
			this.greenMin[i] = 0;
			this.greenMax[i] = 255;
			this.blueMin[i] = 0;
			this.blueMax[i] = 255;
			this.hueMin[i] = 0;
			this.hueMax[i] = 10;
			this.saturationMin[i] = 0;
			this.saturationMax[i] = 10;
			this.valueMin[i] = 0;
			this.valueMax[i] = 10;
		}

		// Pitch Dimensions
		this.topBuffer = 0;
		this.bottomBuffer = 0;
		this.leftBuffer = 0;
		this.rightBuffer = 0;
	}
}

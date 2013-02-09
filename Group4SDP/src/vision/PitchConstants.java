package vision;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * A state object that holds the constants for various values about the pitch,
 * such as thresholding values and dimension variables.
 * 
 * @author s0840449
 */
public class PitchConstants {
	public static final int NUM_THRESHOLDS = 5;

	public static final int BALL = 0;
	public static final int BLUE = 1;
	public static final int YELLOW = 2;
	public static final int GREY = 3;
	public static final int GREEN = 4;
	
	public static final int RGBMIN = 0;
	public static final int RGBMAX = 255;
	public static final double HSVMIN = 0.0;
	public static final double HSVMAX = 1.0;

	// The pitch number. 0 is the main pitch, 1 is the side pitch
	private int pitchNum;
	
	// Threshold upper and lower values
	private int[] redLower = new int[NUM_THRESHOLDS];
	private int[] redUpper = new int[NUM_THRESHOLDS];
	private int[] greenLower = new int[NUM_THRESHOLDS];
	private int[] greenUpper = new int[NUM_THRESHOLDS];
	private int[] blueLower = new int[NUM_THRESHOLDS];
	private int[] blueUpper = new int[NUM_THRESHOLDS];
	private double[] hueLower = new double[NUM_THRESHOLDS];
	private double[] hueUpper = new double[NUM_THRESHOLDS];
	private double[] saturationLower = new double[NUM_THRESHOLDS];
	private double[] saturationUpper = new double[NUM_THRESHOLDS];
	private double[] valueLower = new double[NUM_THRESHOLDS];
	private double[] valueUpper = new double[NUM_THRESHOLDS];
	// Debug 
	private boolean[] debug = new boolean[NUM_THRESHOLDS];
	
	// Pitch dimensions:
	// When scanning the pitch we look at pixels starting from 0 + topBuffer and 
	// 0 + leftBuffer, and then scan to pixels at 480 - bottomBuffer and 
	// 640 - rightBuffer.
	private int topBuffer;
	private int bottomBuffer;
	private int leftBuffer;
	private int rightBuffer;
	
	/**
	 * Default constructor.
	 * 
	 * @param pitchNum
	 *            The pitch that we are on.
	 */
	public PitchConstants(int pitchNum) {
		for (int i = 0; i < NUM_THRESHOLDS; ++i)
			debug[i] = false;
		// Just call the setPitchNum method to load in the constants
		setPitchNum(pitchNum);
	}
	
	public int getRedLower(int i) {
		return redLower[i];
	}
	public void setRedLower(int i, int lower) {
		this.redLower[i] = lower;
	}
	public int getRedUpper(int i) {
		return redUpper[i];
	}
	public void setRedUpper(int i, int upper) {
		this.redUpper[i] = upper;
	}
	
	public int getGreenLower(int i) {
		return greenLower[i];
	}
	public void setGreenLower(int i, int lower) {
		this.greenLower[i] = lower;
	}
	public int getGreenUpper(int i) {
		return greenUpper[i];
	}
	public void setGreenUpper(int i, int upper) {
		this.greenUpper[i] = upper;
	}
	
	public int getBlueLower(int i) {
		return blueLower[i];
	}
	public void setBlueLower(int i, int lower) {
		this.blueLower[i] = lower;
	}
	public int getBlueUpper(int i) {
		return blueUpper[i];
	}
	public void setBlueUpper(int i, int upper) {
		this.blueUpper[i] = upper;
	}
	
	public double getHueLower(int i) {
		return hueLower[i];
	}
	public void setHueLower(int i, double lower) {
		this.hueLower[i] = lower;
	}
	public double getHueUpper(int i) {
		return hueUpper[i];
	}
	public void setHueUpper(int i, double upper) {
		this.hueUpper[i] = upper;
	}
	
	public double getSaturationLower(int i) {
		return saturationLower[i];
	}
	public void setSaturationLower(int i, double lower) {
		this.saturationLower[i] = lower;
	}
	public double getSaturationUpper(int i) {
		return saturationUpper[i];
	}
	public void setSaturationUpper(int i, double upper) {
		this.saturationUpper[i] = upper;
	}
	
	public double getValueLower(int i) {
		return valueLower[i];
	}
	public void setValueLower(int i, double lower) {
		this.valueLower[i] = lower;
	}
	public double getValueUpper(int i) {
		return valueUpper[i];
	}
	public void setValueUpper(int i, double upper) {
		this.valueUpper[i] = upper;
	}
	

	public int getTopBuffer() {
		return topBuffer;
	}
	public void setTopBuffer(int topBuffer) {
		this.topBuffer = topBuffer;
	}

	public int getBottomBuffer() {
		return bottomBuffer;
	}
	public void setBottomBuffer(int bottomBuffer) {
		this.bottomBuffer = bottomBuffer;
	}

	public int getLeftBuffer() {
		return leftBuffer;
	}
	public void setLeftBuffer(int leftBuffer) {
		this.leftBuffer = leftBuffer;
	}

	public int getRightBuffer() {
		return rightBuffer;
	}
	public void setRightBuffer(int rightBuffer) {
		this.rightBuffer = rightBuffer;
	}
	
	/**
	 * Tests whether debug mode is enabled for the threshold set i refers to
	 * @param i
	 * 		One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will cause
	 * 		an ArrayIndexOutOfBoundsException
	 * @return true if debug mode is enabled, false otherwise
	 */
	public boolean debugMode(int i) {
		return this.debug[i];
	}
	/**
	 * Enables or disables debug mode for the threshold set i refers to.
	 * This method permits multiple debug modes to be enabled
	 * @param i
	 * 		One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will cause
	 * 		an ArrayIndexOutOfBoundsException
	 * @param debug
	 * 		A boolean value to enable debug mode if true, and disable otherwise
	 */
	public void setDebugMode(int i, boolean debug) {
		this.debug[i] = debug;
	}
	/**
	 * Enables or disables debug mode for the threshold set i refers to.
	 * This method permits multiple debug modes to be enabled only if
	 * allowMultiple is set to true.
	 * @param i
	 * 		One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will cause
	 * 		an ArrayIndexOutOfBoundsException
	 * @param debug
	 * 		A boolean value to enable debug mode if true, and disable otherwise
	 * @param allowMultiple
	 * 		A boolean value specifying whether to allow multiple debug modes
	 * 		to be set
	 */
	public void setDebugMode(int i, boolean debug, boolean allowMultiple) {
		if (allowMultiple)
			setDebugMode(i, debug);
		else {
			for (int j = 0; j < 5; ++j)
				setDebugMode(j, (i == j) && debug);
		}
	}
	
	public int getPitchNum() {
		return this.pitchNum;
	}

	/**
	 * Sets a new pitch number, loading in constants from the corresponding
	 * file.
	 * 
	 * @param newPitchNum
	 *            The pitch number to use.
	 */
	public void setPitchNum(int newPitchNum) {
		assert (newPitchNum == 0 || newPitchNum == 1) : "Invalid pitch number";
		this.pitchNum = newPitchNum;
		
		loadConstants(System.getProperty("user.dir") + "/constants/pitch" + pitchNum);
	}

	public void saveConstants(String fileName) {
		try {
			// Update the pitch dimensions file 
			FileWriter pitchDimFile = new FileWriter(new File("constants/pitch" + pitchNum + "Dimensions"));
			pitchDimFile.write(String.valueOf(getTopBuffer()) + "\n");
			pitchDimFile.write(String.valueOf(getBottomBuffer()) + "\n");
			pitchDimFile.write(String.valueOf(getLeftBuffer()) + "\n");
			pitchDimFile.write(String.valueOf(getRightBuffer()) + "\n");
			pitchDimFile.close();
			
			FileWriter pitchFile = new FileWriter(new File("constants/pitch" + pitchNum));
			// Iterate over ball, blue robot, yellow robot, grey circles, and green plates
			// in the order they're defined above
			for (int i = 0; i < NUM_THRESHOLDS; ++i) {
				pitchFile.write(String.valueOf(getRedLower(i)) + "\n");
				pitchFile.write(String.valueOf(getRedUpper(i)) + "\n");
				
				pitchFile.write(String.valueOf(getGreenLower(i)) + "\n");
				pitchFile.write(String.valueOf(getGreenUpper(i)) + "\n");

				pitchFile.write(String.valueOf(getBlueLower(i)) + "\n");
				pitchFile.write(String.valueOf(getBlueUpper(i)) + "\n");
				
				pitchFile.write(String.valueOf(getHueLower(i)) + "\n");
				pitchFile.write(String.valueOf(getHueUpper(i)) + "\n");

				pitchFile.write(String.valueOf(getSaturationLower(i)) + "\n");
				pitchFile.write(String.valueOf(getSaturationUpper(i)) + "\n");

				pitchFile.write(String.valueOf(getValueLower(i)) + "\n");
				pitchFile.write(String.valueOf(getValueUpper(i)) + "\n");
			}
			pitchFile.close();
			
			System.out.println("Wrote successfully!");
		} catch (IOException e) {
			System.out.println("Cannot save constants file " + fileName + ":");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
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
			System.err.println("Cannot load constants file " + fileName + "Dimensions:");
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
			e.printStackTrace();
			loadDefaultConstants();
			return;
		}
		
		assert(scanner != null);
		
		// Iterate over ball, blue robot, yellow robot, grey circles, and then green plates
		// in the order they're defined above.
		for (int i = 0; i < NUM_THRESHOLDS; ++i) {
			this.redLower[i] = scanner.nextInt();
			System.out.println("Threshold " + i + " redLower: " + this.redLower[i]);
			this.redUpper[i] = scanner.nextInt();
			System.out.println("Threshold " + i + " redUpper: " + this.redUpper[i]);
			this.greenLower[i] = scanner.nextInt();
			System.out.println("Threshold " + i + " greenLower: " + this.greenLower[i]);
			this.greenUpper[i] = scanner.nextInt();
			System.out.println("Threshold " + i + " greenUpper: " + this.greenUpper[i]);
			this.blueLower[i] = scanner.nextInt();
			this.blueUpper[i] = scanner.nextInt();
			this.hueLower[i] = scanner.nextDouble();
			this.hueUpper[i] = scanner.nextDouble();
			this.saturationLower[i] = scanner.nextDouble();
			this.saturationUpper[i] = scanner.nextDouble();
			this.valueLower[i] = scanner.nextDouble();
			this.valueUpper[i] = scanner.nextDouble();
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
			this.redLower[i] = 0;
			this.redUpper[i] = 255;
			this.greenLower[i] = 0;
			this.greenUpper[i] = 255;
			this.blueLower[i] = 0;
			this.blueUpper[i] = 255;
			this.hueLower[i] = 0.0;
			this.hueUpper[i] = 1.0;
			this.saturationLower[i] = 0.0;
			this.saturationUpper[i] = 1.0;
			this.valueLower[i] = 0.0;
			this.valueUpper[i] = 1.0;
		}

		// Pitch Dimensions
		this.topBuffer = 40;
		this.bottomBuffer = 40;
		this.leftBuffer = 20;
		this.rightBuffer = 20;
	}
}

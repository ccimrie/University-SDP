package balle.main;

import java.util.ArrayList;



public class Config  {
	public static final int NAME = 0;
	public static final int GREEN_STRATEGY = 1;
	public static final int RED_STRATEGY = 2;
	public static final int POWER_VELO_DATA = 3;

	// Instance

	protected String[] args;

	public Config(String[] args) {
		this.args = args;
	}

	public Config() {
		this.args = defaults();
	}

	protected static String[] defaults() {
		String[] args = new String[4];
		args[0] = "Default";
		args[1] = "NullStrategy";
		args[2] = "NullStrategy";
		args[3] = "[DEFAULT]";
		return args;
	}
	

	


	

	// Interface
	
	public String get(int index) {
		return args[index];
	}

	public void set(int index, String str) {
		args[index] = str;
	}
}

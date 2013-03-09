package strategy.planning;


public class Strategy {
	public static boolean alldie = false;

	public static void stop() {
		alldie = true;
	}

	public static void reset() {
		alldie = false;
	}
}
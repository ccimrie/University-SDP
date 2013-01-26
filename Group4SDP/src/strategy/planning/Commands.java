package strategy.planning;

/**
 * Opcodes for commands.
 * Each command should be an int[] made up of 4 bytes.
 * First byte - opcode
 * Remaining three bytes will be options for the commands E.g. 
 * @author Maithu Venkatesh
 *
 */
public class Commands {
	public final static int DO_NOTHING = 0;
	public final static int FORWARDS = 1;
	public final static int BACKWARDS = 2;
	public final static int STOP = 3;
	public final static int KICK = 4;
	public final static int QUIT = 5;
	public final static int FORWARDS_TRAVEL = 6;
	public final static int TRAVEL_BACKWARDS_SLIGHTLY = 7;
	public final static int TRAVEL_ARC = 8;
	public final static int ACCELERATE = 9;
	public final static int LEFT = 10;
	public final static int RIGHT= 11;
	public final static int ROTATE = 12;
	public final static int TEST = 66;
}
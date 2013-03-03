package communication;

/**
 * An interface to provide a common basis for both the real robot, and the
 * simulator's version of the robot
 * 
 * @author Alex Adams (s1046358)
 */
public interface RobotController {

	/**
	 * Tells the robot to shut down
	 */
	public void quit();

	/**
	 * Tells the robot to stop
	 * 
	 * @return A confirmation code for the command
	 */
	public int stop();

	/**
	 * Tells the robot to kick
	 * 
	 * @return A confirmation code for the command
	 */
	public int kick();

	/**
	 * Tells the robot to move along a vector, relative to the robot
	 * 
	 * @param speedX
	 *            A speed setting for the left/right wheels, from -100 to 100
	 * @param speedY
	 *            A speed setting for the forward/backward wheels, from -100 to
	 *            100
	 * @return A confirmation code for the command
	 */
	public int move(int speedX, int speedY);

	/**
	 * Tells the robot to rotate by an angle
	 * 
	 * @param angleDeg
	 *            The angle to rotate by, in degrees
	 * @return A confirmation code for the command
	 */
	public int rotate(int angleDeg);

	/**
	 * Tells the robot to rotate while moving along a vector, relative to the
	 * robot<br/>
	 * NOTE: this vector will change as the robot rotates!
	 * 
	 * @param speedX
	 *            A speed setting for the left/right wheels, from -100 to 100
	 * @param speedY
	 *            A speed setting for the forward/backward wheels, from -100 to
	 *            100
	 * @param rotSpeed
	 *            The speed to rotate at, in degrees per second
	 * @return A confirmation code for the command
	 */
	public int rotateMove(int speedX, int speedY, int rotSpeed);

	/**
	 * Clears the command / confirmation buffers for the robot
	 */
	public void clearBuff();
}

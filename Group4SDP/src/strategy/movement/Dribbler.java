package strategy.movement;
import movement.RobotMover;

/* In the future we should extend this with more dribbling techniques
 * 
 */

public class Dribbler {
	/**
	 * Generic dribbler. It's purpose is to dribble the ball left/right
	 * (assuming it already has the ball). We can use that to sidestep
	 * an enemy robot on our way to the goal. This should only be called
	 * if the robot is currently moving. it shouldn't stop before the command
	 * otherwise the ball would move slightly in front of us and we'd lose it.
	 * In order to straighten the movement direction again. call the command
	 * with the reverse direction.
	 * 
	 * @param mover
	 * 			  The mover object
	 * @param direction
	 *            1 for right
	 *            2 for left
	 */
	public static void dribble(RobotMover mover, int direction){
		switch (direction){
		case 1:	mover.move(45);
				break;
		case 2: mover.move(-45);
				break;
		}
	}
}

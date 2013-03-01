package strategy.calculations;

import vision.PitchConstants;
import vision.Position;

public class GoalInfo {
	public final int width = 640;
	public final int height = 480;
	public final int goalWidth = 15;
	public final int goalRadius = 60;
	public final PitchConstants pitchConst;
	
	public GoalInfo(final PitchConstants pitchConstants) {
		this.pitchConst = pitchConstants;
	}

	/**
	 * Gets the position of the left goal
	 * 
	 * @return a Position object for the left goal
	 */
	public Position getLeftGoalCenter() {
		int middleY = (pitchConst.getTopBuffer() + (height - pitchConst.getBottomBuffer())) / 2;
		Position result =  new Position(pitchConst.getLeftBuffer() + goalWidth, middleY);
		return result;
	}
	
	/**
	 * Gets the position of the top of the left goal
	 * 
	 * @return a Position object for the top of the left goal
	 */
	public Position getLeftGoalTop() {
		Position result = getLeftGoalCenter();
		result.setY(result.getY() - goalRadius);
		return result;
	}
	
	/**
	 * Gets the position of the top of the left goal
	 * 
	 * @return a Position object for the top of the left goal
	 */
	public Position getLeftGoalBottom() {
		Position result = getLeftGoalCenter();
		result.setY(result.getY() + goalRadius);
		return result;
	}
	
	/**
	 * Gets the position of the left goal
	 * 
	 * @return a Position object for the right goal
	 */
	public Position getRightGoalCenter() {
		int middleY = (pitchConst.getTopBuffer() + (height - pitchConst.getBottomBuffer())) / 2;
		Position result = new Position(width - pitchConst.getRightBuffer() - goalWidth, middleY);
		return result;
	}
	
	/**
	 * Gets the position of the top of the left goal
	 * 
	 * @return a Position object for the top of the left goal
	 */
	public Position getRightGoalTop() {
		Position result = getRightGoalCenter();
		result.setY(result.getY() - goalRadius);
		return result;
	}
	
	/**
	 * Gets the position of the top of the left goal
	 * 
	 * @return a Position object for the top of the left goal
	 */
	public Position getRightGoalBottom() {
		Position result = getRightGoalCenter();
		result.setY(result.getY() + goalRadius);
		return result;
	}
}

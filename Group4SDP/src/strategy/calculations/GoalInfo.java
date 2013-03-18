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
	
	/**Hardcoded Goal Constants.
	 * @param int pitch, Main Pitch 0; Side Pitch 1, for now returns only one value which is the one of the Main pitch
	 * @author Simona Petrova & Marija Pinkute. 2013.
	 */
		
	public Position getRightGoalTopCorner(int pitch){
		return new Position(604, 158);
	}
	
	public Position getRightGoalCenter(int pitch){
		return new Position(604, 240);
	}
	
	public Position getRightGoalBottom(int pitch){
		return new Position(604, 316);
	}
	
	public Position getLeftGoalTop(int pitch){
		return new Position(32,170);
	}
	
	public Position getLeftGoalCenter(int pitch){
		return new Position(32,249);
	}
	
	public Position getLeftGoalBottom(int pitch){
		return new Position(32,328);
	}
	
	/**Calculating the goal positions according to the selected pitch area in vision*/
	
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
	
	public Position getBotRightCorner() {
		Position result = new Position(604, 400);
		return result; 
	}

	public Position getTopLeftCorner() {
		Position result = new Position(35, 92);
		
		return result;
	}
	public Position getTopRightCorner() {
		Position result = new Position(601, 84);
		
		return result;
	}
	public Position getBotLeftCorner() {
		Position result = new Position(35, 392);
		
		return result;
	}
	public Position getLeftPenalty() {
		Position result = new Position(116, 238);
		
		return result;
	}
	public Position getRightPenalty() {
		Position result = new Position(345, 238);
		
		return result;
	}
	public Position getMid() {
		Position result = new Position(324, 238);
		
		return result;
	}
	

}

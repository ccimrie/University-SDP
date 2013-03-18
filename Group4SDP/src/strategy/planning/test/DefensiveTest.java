package strategy.planning.test;

import junit.framework.TestCase;
import movement.RobotMover;

import org.junit.Test;

import strategy.calculations.GoalInfo;
import strategy.planning.Defensive;
import vision.PitchConstants;
import world.state.WorldState;

/**
 * This class tests the calculations made in the defence. It is done by crating a bogus World State.
 * 
 * @author Marija Pinkute. 2013.
 */
public class DefensiveTest extends TestCase {

	private final int threshold = 30;
	PitchConstants pitchConstants = new PitchConstants(0);
	GoalInfo goalInfo = new GoalInfo(pitchConstants);
	WorldState worldState = new WorldState(goalInfo);
	RobotMover mover = new RobotMover(worldState, null);
	Defensive strategy;

	/** Tests whether the right goal defend positions for the goal are set. */
	@Test
	public void testSetGoalVariables() {
		strategy = new Defensive(worldState, mover);
		/** We are on the left test case */
		worldState.setWeAreOnLeft(true);
		Defensive strategy = new Defensive(worldState, mover);
		assertEquals(strategy.setGoalVariables().getX(), worldState.goalInfo
				.getLeftGoalCenter(0).getX() + threshold);
		assertEquals(strategy.setGoalVariables().getY(), worldState.goalInfo
				.getLeftGoalCenter(0).getY());

		/** We are on the right test case */
		worldState.setWeAreOnLeft(false);
		assertEquals(strategy.setGoalVariables().getX(), worldState.goalInfo
				.getRightGoalCenter(0).getX() - threshold);
		assertEquals(strategy.setGoalVariables().getY(), worldState.goalInfo
				.getRightGoalCenter(0).getY());
	}

	/** Testing the angle by which our robot should turn to face the other robot */
	@Test
	public void testCalcAngleToTrunToFaceOtherRobot() {

		// TEST CASE 1: Our orient = 50; Their orient = 250; Angle to turn = 20
		WorldState world = new WorldState(goalInfo, Math.toRadians(50),
				Math.toRadians(250), true, true, 0, 0, 0, 0);
		
		strategy = new Defensive(world, mover);
		assertEquals(20, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 2: Our orient = 250; Their orient = 50; Angle to turn = -20
		world = new WorldState(goalInfo, Math.toRadians(250),
				Math.toRadians(50), true, true, 0, 0, 0, 0);
		strategy = new Defensive(world, mover);
		assertEquals(-20, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 3: Our orient = 250; Their orient = 290; Angle to turn =
		// -140
		world = new WorldState(goalInfo, Math.toRadians(250),
				Math.toRadians(290), true, true, 0, 0, 0, 0);
		strategy = new Defensive(world, mover);
		assertEquals(-140, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 4: Our orient = 50; Their orient = 75; Angle to turn = -155
		world = new WorldState(goalInfo, Math.toRadians(50),
				Math.toRadians(75), true, true, 0, 0, 0, 0);
		strategy = new Defensive(world, mover);
		assertEquals(-155, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 5: Our orient = 0; Their orient = 180; Angle to turn = 0
		world = new WorldState(goalInfo, Math.toRadians(0),
				Math.toRadians(180), true, true, 0, 0, 0, 0);
		strategy = new Defensive(world, mover);
		assertEquals(0, strategy.calcAngleToTrunToFaceOtherRobot(), 2);
		
		// TEST CASE 6: Our orient = 270; Their orient = 90; Angle to turn = 0
		world = new WorldState(goalInfo, Math.toRadians(270),
				Math.toRadians(90), true, true, 0, 0, 0, 0);
		strategy = new Defensive(world, mover);
		assertEquals(0, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

	}
	
	@Test
	public void testCalcDistYAxisAcordingToEnemy(){
		//~~~~~OUR GOAL IS ON THE LEFT~~~~
		//They are not facing us. Their orient = 15.
		WorldState world = new WorldState(goalInfo, Math.toRadians(90),
				Math.toRadians(15), true, true, 0, 0, 0, 0);
		strategy = new Defensive(world, mover);
		strategy.setGoalVariables();
		assertEquals(worldState.goalInfo
				.getLeftGoalCenter(0).getY(), strategy.calcDistYAxisAcordingToEnemy(), 2);
		
		//They are facing us.
		world = new WorldState(goalInfo, Math.toRadians(90),
				Math.toRadians(225), true, true, 62, 243, 150, 150);;
		strategy = new Defensive(world, mover);
		strategy.setGoalVariables();
		
		assertEquals(268, strategy.calcDistYAxisAcordingToEnemy(), 2);
			
		//~~~~~~~OUR GOAL IS ON THE RIGHT~~~~~ 
		world = new WorldState(goalInfo, Math.toRadians(270),
				Math.toRadians(195), true, false, 0, 0, 0, 0);
		strategy = new Defensive(world, mover);
		strategy.setGoalVariables();		
		assertEquals(worldState.goalInfo
				.getRightGoalCenter(0).getY(), strategy.calcDistYAxisAcordingToEnemy(), 2);
		
		
		
	}
}

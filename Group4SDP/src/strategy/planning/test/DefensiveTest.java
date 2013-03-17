package strategy.planning.test;

import junit.framework.TestCase;
import movement.RobotMover;

import org.junit.Test;

import strategy.calculations.GoalInfo;
import strategy.planning.Defensive;
import vision.PitchConstants;
import world.state.WorldState;

public class DefensiveTest extends TestCase {

	private final int threshold = 30;
	PitchConstants pitchConstants = new PitchConstants(0);
	GoalInfo goalInfo = new GoalInfo(pitchConstants);
	WorldState worldState = new WorldState(goalInfo);
	RobotMover mover = new RobotMover(worldState, null);

	Defensive strategy = new Defensive(worldState, mover);

	/** Tests whether the right goal defend positions for the goal are set. */
	@Test
	public void testSetGoalVariables() {

		/** We are on the left test case */
		worldState.setWeAreOnLeft(true);
		Defensive strategy = new Defensive(worldState, mover);
		assertEquals(strategy.setGoalVariables().getX(), worldState.goalInfo
				.getLeftGoalCenter().getX() + threshold);
		assertEquals(strategy.setGoalVariables().getY(), worldState.goalInfo
				.getLeftGoalCenter().getY());

		/** We are on the right test case */
		worldState.setWeAreOnLeft(false);
		assertEquals(strategy.setGoalVariables().getX(), worldState.goalInfo
				.getRightGoalCenter().getX() - threshold);
		assertEquals(strategy.setGoalVariables().getY(), worldState.goalInfo
				.getRightGoalCenter().getY());
	}

	/** Testing the angle by which our robot should turn to face the other robot */
	@Test
	public void testCalcAngleToTrunToFaceOtherRobot() {

		// TEST CASE 1: Our orient = 50; Their orient = 250; Angle to turn = 20
		WorldState world = new WorldState(goalInfo, Math.toRadians(50),
				Math.toRadians(250), true);
		strategy = new Defensive(world, mover);
		assertEquals(20, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 2: Our orient = 250; Their orient = 50; Angle to turn = -20
		world = new WorldState(goalInfo, Math.toRadians(250),
				Math.toRadians(50), true);
		strategy = new Defensive(world, mover);
		assertEquals(-20, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 3: Our orient = 250; Their orient = 290; Angle to turn =
		// -140
		world = new WorldState(goalInfo, Math.toRadians(250),
				Math.toRadians(290), true);
		strategy = new Defensive(world, mover);
		assertEquals(-140, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 4: Our orient = 50; Their orient = 75; Angle to turn = -155
		world = new WorldState(goalInfo, Math.toRadians(50),
				Math.toRadians(75), true);
		strategy = new Defensive(world, mover);
		assertEquals(-155, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

		// TEST CASE 5: Our orient = 0; Their orient = 180; Angle to turn = 0
		world = new WorldState(goalInfo, Math.toRadians(0),
				Math.toRadians(180), true);
		strategy = new Defensive(world, mover);
		assertEquals(0, strategy.calcAngleToTrunToFaceOtherRobot(), 2);
		
		// TEST CASE 6: Our orient = 270; Their orient = 90; Angle to turn = 0
		world = new WorldState(goalInfo, Math.toRadians(270),
				Math.toRadians(90), true);
		strategy = new Defensive(world, mover);
		assertEquals(0, strategy.calcAngleToTrunToFaceOtherRobot(), 2);

	}
	
	@Test
	public void testCalcDistYAxisAcordingToEnemy(){
		
	}
}

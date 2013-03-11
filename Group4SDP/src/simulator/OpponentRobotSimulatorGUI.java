package simulator;

// UI imports
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import movement.RobotMover;
import strategy.calculations.GoalInfo;
import strategy.movement.TurnToBall;
import strategy.planning.DribbleBall5;
import strategy.planning.MainPlanner;
import strategy.planning.PenaltyAttack;
import strategy.planning.PenaltyDefense;
import strategy.planning.Strategy;
import strategy.planning.StrategyInterface;
import vision.PitchConstants;
import world.state.RobotType;
import world.state.WorldState;



import communication.RobotController;

/**
 * Simulator GUI class based on ControlGUI2 but adapted to use two panels - 
 * one for our robot and one for opponent
 * @author Maithu
 *
 */
@SuppressWarnings("serial")
public class OpponentRobotSimulatorGUI extends JFrame {
	// GUI elements

	private final JPanel robotSelectionPanel = new JPanel();
	private final JPanel startStopQuitPanel = new JPanel();
	private final JPanel optionsPanel = new JPanel();
	private final JPanel simpleMovePanel = new JPanel();
	private final JPanel complexMovePanel = new JPanel();
	private final JPanel moveTargetPanel = new JPanel();
	private final JPanel moveTargetOptionsPanel = new JPanel();
	
	//Simulator specific object
	private final JLabel robotSelector = new JLabel("Please select a colour: ");
	private final JRadioButton radioButton1 = new JRadioButton("Blue");
	private final JRadioButton radioButton2 = new JRadioButton("Yellow");
	
	
	// General control buttons
	private final JButton startButton = new JButton("Start");
	private final JButton quitButton = new JButton("Quit");
	private final JButton stopButton = new JButton("Stop");
	private final JButton stratStartButton = new JButton("Strat Start");
	private final JButton penaltyAtkButton = new JButton("Penalty Attack");
	private final JButton penaltyDefButton = new JButton("Penalty Defend");
	private final JButton moveNoCollTarget = new JButton(
			"Move while avoiding just opponent");
	private final JButton moveNoCollOppTarget = new JButton(
			"Move while avoiding all obstacles");
	// Basic movement
	private final JButton forwardButton = new JButton("Forward");
	private final JButton backwardButton = new JButton("Backward");
	private final JButton leftButton = new JButton("Left");
	private final JButton rightButton = new JButton("Right");
	// Kick
	private final JButton kickButton = new JButton("Kick");
	// Other movement
	private final JButton rotateButton = new JButton("Rotate");
	private final JButton moveButton = new JButton("Move");
	private final JButton moveToButton = new JButton("Move To");
	private final JButton rotateAndMoveButton = new JButton("Rotate & Move");
	// TODO: remove
	// private final JButton moveToBallButton = new JButton("MoveToBall");
	private final JButton dribbleButton = new JButton("Dribble");

	// OPcode fields
	private final JLabel op1label = new JLabel("Option 1: ");
	private final JLabel op2label = new JLabel("Option 2: ");
	private final JLabel op3label = new JLabel("Option 3: ");
	private final static JLabel op4label = new JLabel("Move to (x label): ");
	private final static JLabel op5label = new JLabel("Move to (y label): ");
	private final JTextField op1field = new JTextField();
	private final JTextField op2field = new JTextField();
	private final JTextField op3field = new JTextField();
	public static final JTextField op4field = new JTextField();
	public static final JTextField op5field = new JTextField();

	private WorldState worldState;
	private final RobotController robot;

	public OpponentRobotSimulatorGUI(final WorldState worldState, final RobotController robot) {
		this.worldState = worldState;
		this.robot = robot;

		op1field.setColumns(6);
		op2field.setColumns(6);
		op3field.setColumns(6);
		op1field.setText("0");
		op2field.setText("0");
		op3field.setText("0");
		// Auto-generated GUI code (made more readable)
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.getContentPane().setLayout(gridBagLayout);

		GridBagConstraints gbc_startStopQuitPanel = new GridBagConstraints();
		gbc_startStopQuitPanel.anchor = GridBagConstraints.NORTH;
		gbc_startStopQuitPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_startStopQuitPanel.insets = new Insets(0, 0, 5, 0);
		gbc_startStopQuitPanel.gridx = 0;
		gbc_startStopQuitPanel.gridy = 1;
		this.getContentPane().add(startStopQuitPanel, gbc_startStopQuitPanel);
		startStopQuitPanel.add(startButton);
		startStopQuitPanel.add(stopButton);
		startStopQuitPanel.add(quitButton);
		startStopQuitPanel.add(stratStartButton);
		startStopQuitPanel.add(penaltyAtkButton);
		startStopQuitPanel.add(penaltyDefButton);
	}
}

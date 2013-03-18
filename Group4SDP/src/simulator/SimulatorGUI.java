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
public class SimulatorGUI extends JFrame {
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

	// Strategy used for driving part of milestone 2
     
	private DribbleBall5 dribbleBall = new DribbleBall5();
	private DribbleBallThread dribbleThread;

	private WorldState worldState;

	private Thread strategyThread;
	private StrategyInterface strategy;

	private final RobotController robot;
	private final RobotMover mover;

	public static void main(String[] args) throws IOException {
		// Make the GUI pretty
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// Default to main pitch
			PitchConstants pitchConstants = new PitchConstants(0);
			GoalInfo goalInfo = new GoalInfo(pitchConstants);
			WorldState worldState = new WorldState(goalInfo);

			SimulatorTestbed simTest = new SimulatorTestbed(worldState);
			Simulator simulator = new Simulator(simTest);

			// Sets up both robots
			SimulatorRobot ourRobot = new SimulatorRobot(RobotType.Us, simTest.simOurRobot);
			SimulatorRobot theirRobot = new SimulatorRobot(RobotType.Them, simTest.simTheirRobot);
			theirRobot.setPower(1.5f);
			// Sets up the GUI
			SimulatorGUI ourRobotGUI = new SimulatorGUI(worldState, ourRobot);
			ourRobotGUI.setTitle("Our Robot Control GUI");
			OpponentRobotSimulatorGUI theirRobotGUI = new OpponentRobotSimulatorGUI(worldState, theirRobot);
			theirRobotGUI.setTitle("Opponent Robot Control GUI");
			
			ourRobotGUI.setVisible(true);
			theirRobotGUI.setVisible(true);

			ourRobot.connect();
			theirRobot.connect();
			
			while (!ourRobot.isConnected() && theirRobot.isConnected()) {
				// Reduce CPU cost
					Thread.sleep(10);
			}
			System.out.println("Robot ready!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SimulatorGUI(final WorldState worldState, final RobotController robot) {
		this.worldState = worldState;
		this.robot = robot;
		this.mover = new RobotMover(worldState, robot);
		this.mover.start();

		op1field.setColumns(6);
		op2field.setColumns(6);
		op3field.setColumns(6);
		op1field.setText("0");
		op2field.setText("0");
		op3field.setText("0");
		// Auto-generated GUI code (made more readable)
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.getContentPane().setLayout(gridBagLayout);
		
		GridBagConstraints gbc_robotSelectionPanel = new GridBagConstraints();
		gbc_robotSelectionPanel.anchor = GridBagConstraints.NORTH;
		gbc_robotSelectionPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_robotSelectionPanel.insets = new Insets(0, 0, 5, 0);
		gbc_robotSelectionPanel.gridx = 0;
		gbc_robotSelectionPanel.gridy = 0;
		this.getContentPane().add(robotSelectionPanel, gbc_robotSelectionPanel);
		robotSelectionPanel.add(robotSelector);
		robotSelectionPanel.add(radioButton1);
		robotSelectionPanel.add(radioButton2);


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

		GridBagConstraints gbc_simpleMoveTestPanel = new GridBagConstraints();
		gbc_simpleMoveTestPanel.anchor = GridBagConstraints.NORTH;
		gbc_simpleMoveTestPanel.fill = GridBagConstraints.VERTICAL;
		gbc_simpleMoveTestPanel.insets = new Insets(0, 0, 5, 0);
		gbc_simpleMoveTestPanel.gridx = 0;
		gbc_simpleMoveTestPanel.gridy = 2;
		// gbc_simpleMoveTestPanel.gridwidth = 2;
		this.getContentPane().add(optionsPanel, gbc_simpleMoveTestPanel);
		optionsPanel.add(op1label);
		optionsPanel.add(op1field);
		optionsPanel.add(op2label);
		optionsPanel.add(op2field);
		optionsPanel.add(op3label);
		optionsPanel.add(op3field);

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		this.getContentPane().add(simpleMovePanel, gbc_panel);
		simpleMovePanel.add(forwardButton);
		simpleMovePanel.add(backwardButton);
		simpleMovePanel.add(leftButton);
		simpleMovePanel.add(rightButton);
		simpleMovePanel.add(kickButton);

		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 4;
		this.getContentPane().add(complexMovePanel, gbc_panel_1);
		complexMovePanel.add(rotateButton);
		complexMovePanel.add(moveButton);
		complexMovePanel.add(moveToButton);
		complexMovePanel.add(rotateAndMoveButton);

		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 5;
		this.getContentPane().add(moveTargetPanel, gbc_panel_2);
		moveTargetPanel.add(moveNoCollTarget);
		moveTargetPanel.add(moveNoCollOppTarget);

		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 6;
		this.getContentPane().add(moveTargetOptionsPanel, gbc_panel_3);
		op4field.setColumns(6);
		op5field.setColumns(6);
		op4field.setText("" + 100);
		op5field.setText("" + 100);
		moveTargetOptionsPanel.add(op4label);
		moveTargetOptionsPanel.add(op4field);
		moveTargetOptionsPanel.add(op5label);
		moveTargetOptionsPanel.add(op5field);

		// TODO: remove
		complexMovePanel.add(dribbleButton);

		this.addWindowListener(new ListenCloseWdw());

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// mover.move(100, 100);
				// mover.moveToAStar(worldState.ball.x, worldState.ball.y,
				// false);

				double angle = TurnToBall.AngleTurner(worldState.ourRobot,
						worldState.ball.x, worldState.ball.y);
				System.out.println("Angle is " + (int) angle);
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop the dribble thread if it's running
				if (dribbleThread != null && dribbleThread.isAlive()) {
					DribbleBall5.die = true;
					try {
						mover.resetQueue();
						dribbleThread.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				// Stop strategy if it's running
				if (strategyThread != null && strategyThread.isAlive()) {
					Strategy.stop();
					strategy.kill();
				}
				// Stop the robot.
				mover.stopRobot();
			}
			// Run the strategy from here.
		});

		stratStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Allow restart of strategies after previously killing all
				// strategies
				Strategy.reset();

				// Run in a new thread to free up UI while running
				strategy = new MainPlanner(worldState, mover);
				strategyThread = new Thread(strategy);
				strategyThread.start();
			}
		});

		penaltyAtkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PenaltyAttack penaltyAtk = new PenaltyAttack(worldState, mover);
				penaltyAtk.run();
			}
		});

		penaltyDefButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PenaltyDefense penaltyDef = new PenaltyDefense(worldState,
						mover);
				penaltyDef.run();
			}
		});

		kickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mover.kick();
			}
		});

		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());

				mover.move(0, op1);
			}
		});

		backwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());

				mover.move(0, -op1);
			}
		});

		leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());

				mover.move(-op1, 0);
			}
		});

		rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());

				mover.move(op1, 0);
			}
		});

		dribbleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dribbleThread == null || !dribbleThread.isAlive()) {
					dribbleThread = new DribbleBallThread();
					dribbleThread.start();
				} else {
					System.out.println("Dribble is already active!");
				}
			}
		});

		rotateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int angle = Integer.parseInt(op1field.getText());

				mover.rotate(Math.toRadians(angle));
			}
		});

		moveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());

				mover.move(op1, op2);
			}
		});

		moveToButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());

				mover.moveToAndStop(op1, op2);
			}
		});

		rotateAndMoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());
				int op3 = Integer.parseInt(op3field.getText());

				robot.rotateMove(op1, op2, op3);
			}
		});

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Kill the mover and wait for it to stop completely
				// try {
				// mover.kill();
				// } catch (InterruptedException e1) {
				// e1.printStackTrace();
				// }
				robot.disconnect();

				System.out.println("Quitting the GUI");
				System.exit(0);
			}
		});

		moveNoCollTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mover.moveToAStar(Integer.parseInt(op4field.getText()),
						Integer.parseInt(op5field.getText()), false, true);
			}
		});

		moveNoCollOppTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mover.moveToAStar(Integer.parseInt(op4field.getText()),
						Integer.parseInt(op5field.getText()), true, true);
			}
		});

		// Center the window on startup
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getPreferredSize();
		this.setLocation((dim.width - frameSize.width) / 2,
				(dim.height - frameSize.height) / 2);
		this.setResizable(false);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
	}

	public class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try {
				mover.kill();
				mover.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			robot.disconnect();
			System.out.println("Quit...");
		}
	}

	class DribbleBallThread extends Thread {
		public void run() {
			try {
				DribbleBall5.die = false;
				dribbleBall.dribbleBall(worldState, mover);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

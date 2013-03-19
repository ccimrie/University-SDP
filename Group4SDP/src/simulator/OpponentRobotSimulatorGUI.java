package simulator;

// UI imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import movement.RobotMover;

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

	private final JPanel mainPanel = new JPanel();
	
	// General control buttons
	private final JButton startButton = new JButton("Start");
	private final JButton quitButton = new JButton("Quit");
	private final JButton stopButton = new JButton("Stop");
	private final JButton stratStartButton = new JButton("Strat Start");
	private final JButton penaltyAtkButton = new JButton("Penalty Attack");
	private final JButton penaltyDefButton = new JButton("Penalty Defend");
	private final JButton setEnnemySpeed = new JButton("Set Speed");

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
	private final JLabel op1label = new JLabel("Speed of the opponent: ");
	private final JLabel op2label = new JLabel("Option 2: ");
	private final JLabel op3label = new JLabel("Option 3: ");
	private final static JLabel op4label = new JLabel("Move to (x label): ");
	private final static JLabel op5label = new JLabel("Move to (y label): ");
	private final JTextField op1field = new JTextField();
	private final JTextField op2field = new JTextField();
	private final JTextField op3field = new JTextField();
	public static final JTextField op4field = new JTextField();
	public static final JTextField op5field = new JTextField();

	private final RobotMover mover;
	private WorldState worldState;
	private final RobotController robot;
	public OpponentRobotSimulatorGUI(final WorldState worldState, final RobotController robot, final SimulatorRobot zeEnnemy) {

		
		mover = new RobotMover(worldState, robot);
		
		mover.start();
		
		this.worldState = worldState;
		this.robot = robot;
		

		op1field.setColumns(6);
		op2field.setColumns(6);
		op3field.setColumns(6);
		op1field.setText("1");
		op2field.setText("0");
		op3field.setText("0");
		// Auto-generated GUI code (made more readable)
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.getContentPane().setLayout(gridBagLayout);

		GridBagConstraints gbc_mainPanel = new GridBagConstraints();
		gbc_mainPanel.anchor = GridBagConstraints.NORTH;
		gbc_mainPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_mainPanel.insets = new Insets(0, 0, 5, 0);
		gbc_mainPanel.gridx = 0;
		gbc_mainPanel.gridy = 1;
		this.getContentPane().add(mainPanel, gbc_mainPanel);
		mainPanel.add(startButton);
		mainPanel.add(stopButton);
		mainPanel.add(quitButton);
		mainPanel.add(stratStartButton);
		mainPanel.add(penaltyAtkButton);
		mainPanel.add(penaltyDefButton);
		mainPanel.add(op1label);
		mainPanel.add(op1field);
		mainPanel.add(setEnnemySpeed);
		
		
		
		
		
		setEnnemySpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());

				zeEnnemy.setPower(op1);
			}
		});
		
		
		
		
		
		
		
		mainPanel.setFocusable(true);
		mainPanel.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar()  == 's') {
					mover.move(0, -100);
				}
				if (e.getKeyChar() == 'w') {
					mover.move(0, 100);
				}
				if (e.getKeyChar() == 'a') {
					mover.rotate(-1* Math.toRadians(8));

				}
				if (e.getKeyChar() == 'd') {
					mover.rotate(Math.toRadians(8));
				}
				if (e.getKeyChar() == 'k') {
					mover.kick();
				}
				
			}

			public void keyReleased(KeyEvent e) {
				mover.stopRobot();
			}

			public void keyTyped(KeyEvent e) {
			}
		});
		pack();
	}

}

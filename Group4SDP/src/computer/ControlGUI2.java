package computer;

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
import javax.swing.JTextField;
import javax.swing.UIManager;

import movement.RobotMover;

import strategy.calculations.GoalInfo;
import strategy.planning.Commands;
import strategy.planning.PenaltyAttack;
import strategy.planning.PenaltyDefense;
import strategy.planning.Strategy;
import vision.DistortionFix;
import vision.PitchConstants;
import vision.VideoStream;
import vision.Vision;
import vision.gui.VisionGUI;
import world.state.RobotType;
import world.state.WorldState;
import au.edu.jcu.v4l4j.V4L4JConstants;

import communication.BluetoothCommunication;
import communication.BluetoothRobot;
import communication.DeviceInfo;

// TODO: clean up unused stuff
@SuppressWarnings("serial")
public class ControlGUI2 extends JFrame {
	// GUI elements

	private final JPanel startStopQuitPanel = new JPanel();
	private final JPanel optionsPanel = new JPanel();
	private final JPanel simpleMovePanel = new JPanel();
	private final JPanel complexMovePanel = new JPanel();
	// General control buttons
	private final JButton startButton = new JButton("Start");
	private final JButton quitButton = new JButton("Quit");
	private final JButton stopButton = new JButton("Stop");
	private final JButton stratStartButton = new JButton("Strat Start");
	private final JButton penaltyAtkButton = new JButton("Penalty Attack");
	private final JButton penaltyDefButton = new JButton("Penalty Defend");
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
	// private final JButton dribbleButton = new JButton("Dribble");

	// OPcode fields
	private final JLabel op1label = new JLabel("Option 1: ");
	private final JLabel op2label = new JLabel("Option 2: ");
	private final JLabel op3label = new JLabel("Option 3: ");
	private final JTextField op1field = new JTextField();
	private final JTextField op2field = new JTextField();
	private final JTextField op3field = new JTextField();

	// Communication variables
	public static BluetoothCommunication comms;
	private static BluetoothRobot robot;

	// TODO: remove
	// Strategy used for driving part of milestone 2
	// private MoveToBall mball = new MoveToBall();
	// private MoveToTheBallThread approachThread;

	// Strategy used for driving part of milestone 2
	// private static DribbleBall5 dribbleBall = new DribbleBall5();
	// private DribbleBallThread dribbleThread;

	private WorldState worldState;

	private Thread stratThread;
	private Strategy strat;

	private RobotMover mover;

	public static void main(String[] args) throws IOException {
		// Make the GUI pretty
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Default to main pitch
		PitchConstants pitchConstants = new PitchConstants(0);
		GoalInfo goalInfo = new GoalInfo(pitchConstants);
		WorldState worldState = new WorldState(goalInfo);

		// Default values for the main vision window
		String videoDevice = "/dev/video0";
		int width = 640;
		int height = 480;
		int channel = 0;
		int videoStandard = V4L4JConstants.STANDARD_PAL;
		int compressionQuality = 80;

		try {
			VideoStream vStream = new VideoStream(videoDevice, width, height,
					channel, videoStandard, compressionQuality);

			DistortionFix distortionFix = new DistortionFix(pitchConstants);

			// Create a new Vision object to serve the main vision window
			Vision vision = new Vision(worldState, pitchConstants);

			// Create the Control GUI for threshold setting/etc
			VisionGUI gui = new VisionGUI(width, height, worldState,
					pitchConstants, vStream, distortionFix);

			vStream.addReceiver(distortionFix);
			distortionFix.addReceiver(gui);
			distortionFix.addReceiver(vision);
			vision.addVisionDebugReceiver(gui);
			vision.addWorldStateReceiver(gui);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sets up the communication
		comms = new BluetoothCommunication(DeviceInfo.NXT_NAME,
				DeviceInfo.NXT_MAC_ADDRESS);
		comms.openBluetoothConnection();

		while (!comms.isRobotReady()) {
			// Reduce CPU cost
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		System.out.println("Robot ready!");

		// Sets up robot
		robot = new BluetoothRobot(RobotType.Us);

		// Sets up the GUI
		ControlGUI2 gui = new ControlGUI2(worldState);
		gui.setVisible(true);
	}

	public ControlGUI2(final WorldState worldState) {
		this.worldState = worldState;
		this.mover = new RobotMover(worldState, robot);
		this.mover.start();

		this.setTitle("Group 4 control GUI");
		
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
		gbc_startStopQuitPanel.gridy = 0;
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
		gbc_simpleMoveTestPanel.gridy = 1;
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
		gbc_panel.gridy = 2;
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
		gbc_panel_1.gridy = 3;
		this.getContentPane().add(complexMovePanel, gbc_panel_1);
		complexMovePanel.add(rotateButton);
		complexMovePanel.add(moveButton);
		complexMovePanel.add(moveToButton);
		complexMovePanel.add(rotateAndMoveButton);

		// TODO: remove
		// complexMovePanel.add(dribbleButton);

		this.addWindowListener(new ListenCloseWdw());

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mover.move(100, 100);
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop strategy if it's running
				try {
					Strategy.stop();
					// TODO: this does precisely nothing (hence strategy doesn't
					// immediately stop when this button is clicked) - the
					// strategy thread always terminates almost immediately.
					// Possibly make strategy call MainPlanner.run() instead of
					// creating it in a new thread?
					if (stratThread != null) {
						stratThread.interrupt();
						stratThread.join();
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				mover.stopRobot();
			}
		});

		stratStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Run in a new thread to free up UI while running
				// TODO: remove - this should be in the plannner
				// Offensive attacking = new Offensive(worldState,
				// worldState.ourRobot,worldState.theirRobot, robot);
				// new Thread(attacking).start();

				// Allow restart of strategies after previously killing all
				// strategies
				Strategy.reset();

				strat = new Strategy(worldState, mover);
				Thread stratthr = new Thread(strat);
				stratthr.start();
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

		//
		// dribbleButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		//
		// dribbleThread = new DribbleBallThread();
		// dribbleThread.start();
		// }
		// });

		rotateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int angle = Integer.parseInt(op1field.getText());

				mover.move(angle);
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
				synchronized (mover) {
					try {
						mover.kill();
						mover.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				int[] command = { Commands.QUIT, 0, 0, 0 };
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send QUIT command to robot");
					// e1.printStackTrace();
				} finally {
					System.out.println("Quitting the GUI");
					System.exit(0);
				}
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
			int[] command = { Commands.QUIT, 0, 0, 0 };
			try {
				comms.sendToRobotSimple(command);
			} catch (IOException e1) {
				System.out.println("Could not send command");
				// e1.printStackTrace();
			} finally {
				mover.kill();
				System.out.println("Quit...");
				System.exit(0);
			}
		}
	}

	// TODO: remove
	// class DribbleBallThread extends Thread {
	//
	// public void run() {
	//
	// try {
	// dribbleBall.dribbleBall(worldState, robot);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }
}

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
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import strategy.planning.Commands;
import strategy.planning.DribbleBall;
import strategy.planning.MoveToBall2;
import vision.DistortionFix;
import vision.PitchConstants;
import vision.VideoStream;
import vision.Vision;
import vision.WorldState;
import vision.gui.VisionGUI;
import world.state.RobotController;
import world.state.RobotType;
import au.edu.jcu.v4l4j.V4L4JConstants;

import communication.BluetoothCommunication;
import communication.DeviceInfo;

@SuppressWarnings("serial")
public class ControlGUI2 extends JFrame {
	// GUI elements

	private final JFrame frame = new JFrame("Group 4 control GUI");

	private final JPanel startStopQuitPanel = new JPanel();
	private final JPanel simpleMoveTestPanel = new JPanel();
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	// General control buttons
	private final JButton start = new JButton("Start");
	private final JButton quit = new JButton("Quit");
	private final JButton stop = new JButton("Stop");
	// Basic movement
	private final JButton forward = new JButton("Forward");
	private final JButton backward = new JButton("Backward");
	private final JButton left = new JButton("Left");
	private final JButton right = new JButton("Right");
	// Kick
	private final JButton kick = new JButton("Kick");
	// Other movement
	private final JButton rotate = new JButton("Rotate");
	private final JButton move = new JButton("Move");
	private final JButton moveToBall = new JButton("MoveToBall");
	private final JButton dribble = new JButton("Dribble");
	// Communication variables
	public static BluetoothCommunication comms;
	private static RobotController robot;
	// OPcode fields
	private final JLabel op1label = new JLabel("Option 1: ");
	private final JLabel op2label = new JLabel("Option 2: ");
	private final JLabel op3label = new JLabel("Option 3: ");
	private final JTextField op1field = new JTextField();
	private final JTextField op2field = new JTextField();
	private final JTextField op3field = new JTextField();

	// Strategy used for driving part of milestone 2
	private static MoveToBall2 mball = new MoveToBall2();
	private MoveToTheBallThread approachThread;
	
	// Strategy used for driving part of milestone 2
	private static DribbleBall dribbleBall = new DribbleBall();
	private DribbleBallThread dribbleThread;

	public static WorldState worldState = new WorldState();
	public static Vision vision;

	public static void main(String[] args) throws IOException {
		// Make the GUI pretty
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Default to main pitch
		PitchConstants pitchConstants = new PitchConstants(0);

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
            VisionGUI gui = new VisionGUI(width, height, worldState, pitchConstants,
            		vStream, distortionFix);

        	vStream.addReceiver(distortionFix);
    		distortionFix.addReceiver(gui);
    		distortionFix.addReceiver(vision);
            vision.addVisionDebugReceiver(gui);
            vision.addWorldStateReceiver(gui);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sets up the GUI
		ControlGUI2 gui = new ControlGUI2();
		gui.Launch();
		gui.action();

		// Sets up the communication
		comms = new BluetoothCommunication(DeviceInfo.NXT_NAME, DeviceInfo.NXT_MAC_ADDRESS);
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
		;

		// Sets up robot
		robot = new RobotController(RobotType.Us);
		System.out.println("Robot ready!");
		int[] command = new int[] { Commands.TEST, 0, 0, Commands.TEST };
		comms.sendToRobot(command);
		int[] test = new int[4];
		test = comms.receiveFromRobot();
		System.out.println(Arrays.toString(test));
	}

	public ControlGUI2() {
		op1field.setColumns(6);
		op2field.setColumns(6);
		op3field.setColumns(6);

		// Auto-generated GUI code (made more readable)
		GridBagLayout gridBagLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gridBagLayout);

		GridBagConstraints gbc_startStopQuitPanel = new GridBagConstraints();
		gbc_startStopQuitPanel.anchor = GridBagConstraints.NORTH;
		gbc_startStopQuitPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_startStopQuitPanel.insets = new Insets(0, 0, 5, 0);
		gbc_startStopQuitPanel.gridx = 0;
		gbc_startStopQuitPanel.gridy = 0;
		frame.getContentPane().add(startStopQuitPanel, gbc_startStopQuitPanel);
		startStopQuitPanel.add(start);
		startStopQuitPanel.add(stop);
		startStopQuitPanel.add(quit);

		GridBagConstraints gbc_simpleMoveTestPanel = new GridBagConstraints();
		gbc_simpleMoveTestPanel.anchor = GridBagConstraints.NORTH;
		gbc_simpleMoveTestPanel.fill = GridBagConstraints.VERTICAL;
		gbc_simpleMoveTestPanel.insets = new Insets(0, 0, 5, 0);
		gbc_simpleMoveTestPanel.gridx = 0;
		gbc_simpleMoveTestPanel.gridy = 1;
		// gbc_simpleMoveTestPanel.gridwidth = 2;
		frame.getContentPane().add(simpleMoveTestPanel, gbc_simpleMoveTestPanel);
		simpleMoveTestPanel.add(op1label);
		simpleMoveTestPanel.add(op1field);
		simpleMoveTestPanel.add(op2label);
		simpleMoveTestPanel.add(op2field);
		simpleMoveTestPanel.add(op3label);
		simpleMoveTestPanel.add(op3field);

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		frame.getContentPane().add(panel, gbc_panel);
		panel.add(forward);
		panel.add(backward);
		panel.add(left);
		panel.add(right);

		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		frame.getContentPane().add(panel_1, gbc_panel_1);
		panel_1.add(rotate);
		panel_1.add(kick);
		panel_1.add(move);
		panel_1.add(moveToBall);
		panel_1.add(dribble);

		frame.addWindowListener(new ListenCloseWdw());

		// Center the window on startup
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getPreferredSize();
		frame.setLocation((dim.width - frameSize.width) / 2, (dim.height - frameSize.height) / 2);
		frame.setResizable(false);
	}

	public void action() {

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Run in a new thread to free up UI while running
			}
		});

		kick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.kick();
			}

		});

		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());
				robot.move(op1, op2);
			}
		});

		backward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());
				robot.backward(op1, op2);
			}
		});

		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());
				robot.left(op1, op2);
			}
		});

		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());
				robot.right(op1, op2);
			}
		});

		moveToBall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				approachThread = new MoveToTheBallThread();
				approachThread.start();

				/*
				 * try {
				 * Class mtb = Class.forName("strategy.planning." + "MoveToBall");
				 * Strategy s = (Strategy)mtb.newInstance();
				 * s.stop();
				 * } catch (ClassNotFoundException e1) {
				 * e1.printStackTrace();
				 * System.err.println("Class not found.");
				 * } catch (InstantiationException e2) {
				 * e2.printStackTrace();
				 * System.err.println("Class could not be instantiated.");
				 * } catch (IllegalAccessException e3) {
				 * e3.printStackTrace();
				 * } catch (ClassCastException e4) {
				 * System.err.println(
				 * "Class is not an extension of abstract class Strategy.\nAdd \"extends Strategy\" to declaration?");
				 * }
				 */
			}
		});
		
		dribble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				dribbleThread = new DribbleBallThread();
				dribbleThread.start();
			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop the drive thread if it's running
				/* robot.stop(timer); */
				robot.stop();
			}
		});

		// TODO - Should we have timer here as well?
		rotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int angle = Integer.parseInt(op1field.getText());
				robot.rotate(angle);
			}
		});

		move.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(op1field.getText());
				int op2 = Integer.parseInt(op2field.getText());
				int[] command = { Commands.ANGLEMOVE, op1, op2, 0 };
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send MOVE command to robot");
					e1.printStackTrace();
				}
				System.out.println("Moving at an angle...");
			}
		});

		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
	}

	public void Launch() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			int[] command = { Commands.QUIT, 0, 0, 0 };
			try {
				comms.sendToRobot(command);
			} catch (IOException e1) {
				System.out.println("Could not send command");
				// e1.printStackTrace();
			} finally {
				System.out.println("Quit...");
				System.exit(0);
			}
		}
	}

	class MoveToTheBallThread extends Thread {

		public void run() {

			try {
				mball.approach(worldState, robot);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	class DribbleBallThread extends Thread {

		public void run() {

			try {
				dribbleBall.dribbleBall(worldState, robot);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/*
	 * class Stopping extends TimerTask{
	 * 
	 * @Override
	 * public void run() {
	 * int[] command = {Commands.STOP, 0, 0, 0};
	 * try {
	 * comms.sendToRobot(command);
	 * }
	 * catch (IOException e) {
	 * System.out.println("Could not send command");
	 * e.printStackTrace();
	 * }
	 * //r.stop;
	 * System.out.println("Stop...");
	 * }
	 * }
	 */
}

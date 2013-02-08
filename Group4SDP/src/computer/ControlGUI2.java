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
import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JRadioButton;

import au.edu.jcu.v4l4j.V4L4JConstants;

import vision.PitchConstants;
import vision.VideoStream;
import vision.Vision;
import vision.gui.VisionGUI;
import vision.WorldState;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import communication.BluetoothCommunication;
import communication.DeviceInfo;
import strategy.planning.Commands;
import strategy.planning.MoveToBall2;
import strategy.planning.Strategy;
import strategy.movement.StraightLineVision;
import world.state.RobotController;
import world.state.RobotType;
import world.state.World;
import strategy.planning.MoveToBall;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ControlGUI2 extends JFrame {
	Timer timer;
	int seconds = 10;
	
	private static final int SATURATION = 64;
	private static final int BRIGHTNESS = 128;
	private static final int CONTRAST = 64;
	private static final int HUE = 0;
	private static final int CHROMA_GAIN = 0;
	private static final int CHROMA_AGC = 1;

	private final JFrame frame = new JFrame("Control Panel");

	private final JPanel startStopQuitPanel = new JPanel();
	private final JPanel simpleMoveTestPanel = new JPanel();
	private final JPanel actionTestPanel = new JPanel();
	private final JPanel angleMovePanel = new JPanel();

	private final JButton start = new JButton("Start");
	private final JButton kick = new JButton("Kick");
	private final JButton stop = new JButton("Stop");
	private final JButton forward = new JButton("Forward");
	private final JButton backward = new JButton("Backward");
	private final JButton left = new JButton("Left");
	private final JButton right = new JButton("Right");
	private final JButton rotate = new JButton("Rotate");
	private final JButton anglemove = new JButton("AngleMove");
	private final JButton quit = new JButton("Quit");
	private final JButton moveToBall = new JButton("MoveToBall");
	private final JRadioButton rdbtnForwards = new JRadioButton("Forwards");
	private final JRadioButton rdbtnBackwards = new JRadioButton("Backwards");
	private final JRadioButton rdbtnLeft = new JRadioButton("Left");
	private final JRadioButton rdbtnRight = new JRadioButton("Right");
	
	// Communication variables
	private static BluetoothCommunication comms;
	private static RobotController robot;

	// Strategy used for driving part of milestone 1
	private static MoveToBall2 mball = new MoveToBall2();
	private DriveThread driveThread;
	private MoveToTheBallThread approachThread;
	private static StraightLineVision strat = new StraightLineVision();
	private final JPanel panel = new JPanel();
	private final JTextField mforward1 = new JTextField();
	private final JTextField mforward2 = new JTextField();
	private final JPanel panel_1 = new JPanel();
	private final JTextField mrotate = new JTextField();
	private final JPanel panel_2 = new JPanel();
	private final JTextField manglemove = new JTextField();
	public static WorldState worldState = new WorldState();
	static public Vision vision;
	
	public static void main(String[] args) throws IOException {
		// Make the GUI pretty
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Set up vision
		//WorldState worldState = new WorldState();

        // Default to main pitch
        PitchConstants pitchConstants = new PitchConstants(0);
        
        // Default values for the main vision window
        String videoDevice = "/dev/video0";
        int width = 640;
        int height = 480;
        int channel = 0;
        int videoStandard = V4L4JConstants.STANDARD_PAL;
        int compressionQuality = 80;
        
        Vision vis =null;
        try {
        	VideoStream vStream = new VideoStream(videoDevice, width, height,
        			channel, videoStandard, compressionQuality);
            
            vStream.setSaturation(SATURATION);
    		vStream.setBrightness(BRIGHTNESS);
    		vStream.setContrast(CONTRAST);
    		vStream.setHue(HUE);
    		vStream.setChromaGain(CHROMA_GAIN);
    		vStream.setChromaAGC(CHROMA_AGC);
    		vStream.updateVideoDeviceSettings();
    		
            // Create a new Vision object to serve the main vision window

    		vision = new Vision(worldState, pitchConstants);
    		vStream.addReceiver(vision);
            
            // Create the Control GUI for threshold setting/etc
            VisionGUI visionGUI = new VisionGUI(width, height, worldState, pitchConstants);
            
            vision.addVisionListener(visionGUI);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

		// Sets up the GUI
		ControlGUI2 gui = new ControlGUI2();
		gui.Launch();
		gui.action();

		// Sets up the communication
		comms = new BluetoothCommunication(DeviceInfo.NXT_NAME, DeviceInfo.NXT_MAC_ADDRESS);
		comms.openBluetoothConnection();

		//Sets up robot
		robot = new RobotController(RobotType.Us);
		robot.setComms(comms);

		while (!comms.isRobotReady()){
			// Reduce CPU cost
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		};

		System.out.println("Robot ready!");
		int [] command = new int [] {Commands.TEST, 0, 0, Commands.TEST};
		comms.sendToRobot(command);
		int[] test = new int[4];
		test = comms.receiveFromRobot();
		System.out.println(Arrays.toString(test));
	} 

	public ControlGUI2() {
		
		
		manglemove.setColumns(10);
		mrotate.setColumns(10);
		mforward2.setColumns(10);
		mforward1.setColumns(10);		
		// Auto-generated GUI code (made more readable)
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{301, 0};
		gridBagLayout.rowHeights = new int[]{33, 33, 0, 0, 33, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
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
		gbc_simpleMoveTestPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_simpleMoveTestPanel.insets = new Insets(0, 0, 5, 0);
		gbc_simpleMoveTestPanel.gridx = 0;
		gbc_simpleMoveTestPanel.gridy = 1;
		frame.getContentPane().add(simpleMoveTestPanel, gbc_simpleMoveTestPanel);
		simpleMoveTestPanel.add(forward);
		
		simpleMoveTestPanel.add(mforward1);
		
		simpleMoveTestPanel.add(mforward2);
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		frame.getContentPane().add(panel, gbc_panel);
		panel.add(backward);
		panel.add(right);
		panel.add(moveToBall);
		panel.add(left);
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		frame.getContentPane().add(panel_1, gbc_panel_1);
		panel_1.add(rotate);
		
		panel_1.add(mrotate);

		GridBagConstraints gbc_actionTestPanel = new GridBagConstraints();
		gbc_actionTestPanel.insets = new Insets(0, 0, 5, 0);
		gbc_actionTestPanel.anchor = GridBagConstraints.NORTH;
		gbc_actionTestPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_actionTestPanel.gridx = 0;
		gbc_actionTestPanel.gridy = 4;
		frame.getContentPane().add(actionTestPanel, gbc_actionTestPanel);
		actionTestPanel.add(kick);
		
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 5;
		frame.getContentPane().add(panel_2, gbc_panel_2);
		
		panel_2.add(manglemove);

		GridBagConstraints gbc_angleMovePanel = new GridBagConstraints();
		gbc_angleMovePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_angleMovePanel.anchor = GridBagConstraints.NORTH;
		gbc_angleMovePanel.gridx = 0;
		gbc_angleMovePanel.gridy = 6;
		frame.getContentPane().add(angleMovePanel, gbc_angleMovePanel);
		GridBagLayout gbl_angleMovePanel = new GridBagLayout();
		gbl_angleMovePanel.columnWidths = new int[] {79, 88, 125};
		gbl_angleMovePanel.rowHeights = new int[] {18, 0};
		gbl_angleMovePanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_angleMovePanel.rowWeights = new double[]{0.0, 0.0};
		angleMovePanel.setLayout(gbl_angleMovePanel);

		GridBagConstraints gbc_rdbtnForwards = new GridBagConstraints();
		gbc_rdbtnForwards.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnForwards.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnForwards.gridx = 0;
		gbc_rdbtnForwards.gridy = 0;
		rdbtnForwards.setSelected(true);
		rdbtnForwards.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (rdbtnForwards.isSelected())
					rdbtnBackwards.setSelected(false);
				else
					rdbtnBackwards.setSelected(true);
			}
		});
		angleMovePanel.add(rdbtnForwards, gbc_rdbtnForwards);

		GridBagConstraints gbc_rdbtnBackwards = new GridBagConstraints();
		gbc_rdbtnBackwards.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnBackwards.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnBackwards.gridx = 0;
		gbc_rdbtnBackwards.gridy = 1;
		rdbtnBackwards.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (rdbtnBackwards.isSelected())
					rdbtnForwards.setSelected(false);
				else
					rdbtnForwards.setSelected(true);
			}
		});
		angleMovePanel.add(rdbtnBackwards, gbc_rdbtnBackwards);

		GridBagConstraints gbc_rdbtnLeft = new GridBagConstraints();
		gbc_rdbtnLeft.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnLeft.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnLeft.gridx = 1;
		gbc_rdbtnLeft.gridy = 0;
		rdbtnLeft.setSelected(true);
		rdbtnLeft.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (rdbtnLeft.isSelected())
					rdbtnRight.setSelected(false);
				else
					rdbtnRight.setSelected(true);
			}
		});
		angleMovePanel.add(rdbtnLeft, gbc_rdbtnLeft);

		GridBagConstraints gbc_rdbtnRight = new GridBagConstraints();
		gbc_rdbtnRight.anchor = GridBagConstraints.WEST;
		gbc_rdbtnRight.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnRight.gridx = 1;
		gbc_rdbtnRight.gridy = 1;
		rdbtnRight.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (rdbtnRight.isSelected())
					rdbtnLeft.setSelected(false);
				else
					rdbtnLeft.setSelected(true);
			}
		});
		angleMovePanel.add(rdbtnRight, gbc_rdbtnRight);

		GridBagConstraints gbc_anglemove = new GridBagConstraints();
		gbc_anglemove.fill = GridBagConstraints.VERTICAL;
		gbc_anglemove.gridheight = 2;
		gbc_anglemove.gridwidth = 2;
		gbc_anglemove.gridx = 2;
		gbc_anglemove.gridy = 0;
		angleMovePanel.add(anglemove, gbc_anglemove);


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
				driveThread = new DriveThread();
				driveThread.start();
			}
		});

		kick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.kick(); 
			} 

		});

		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(mforward1.getText());
				int op2 = Integer.parseInt(mforward2.getText());
				robot.move(op1,op2);
				
				//TODO Timer?
				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		}); 


		backward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(mforward1.getText());
				int op2 = Integer.parseInt(mforward2.getText());
				robot.backward(op1,op2);

				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});

		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(mforward1.getText());
				int op2 = Integer.parseInt(mforward2.getText());
				robot.left(op1,op2);

				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});

		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(mforward1.getText());
				int op2 = Integer.parseInt(mforward2.getText());
				robot.right(op1,op2);

				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});
		
		//TODO - Attach with MoveToBall
		moveToBall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				approachThread = new MoveToTheBallThread();
				approachThread.start();
				
	    		/*try {
					Class mtb = Class.forName("strategy.planning." + "MoveToBall");
					Strategy s = (Strategy)mtb.newInstance();
				    s.stop();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
					System.err.println("Class not found.");
				} catch (InstantiationException e2) {
					e2.printStackTrace();
					System.err.println("Class could not be instantiated.");
				} catch (IllegalAccessException e3) {
					e3.printStackTrace();
				} catch (ClassCastException e4) {
					System.err.println("Class is not an extension of abstract class Strategy.\nAdd \"extends Strategy\" to declaration?");
				}
				*/
			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Stop the drive thread if it's running
				driveThread.halt();
				robot.stop(timer);
				robot.stop();
			}
		});

		//TODO - Should we have timer here as well?
		rotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int angle = Integer.parseInt(mrotate.getText());
				robot.rotate(angle);
			}
		});

		anglemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int op1 = Integer.parseInt(mforward1.getText());
				int op2 = Integer.parseInt(mforward2.getText());
				byte direction = 0;
				if (rdbtnForwards.isSelected())
					direction |= 2;
				if (rdbtnLeft.isSelected())
					direction |= 1;

				int[] command = {Commands.ANGLEMOVE, op1, op2, direction};
				try {
					comms.sendToRobot(command);
				}
				catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Moving at an angle...");
			}
		});

		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {Commands.QUIT, 0, 0, 0};
				try {
					comms.sendToRobot(command);
				}
				catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Quit...");
				System.exit(0);
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
			int[] command = {Commands.QUIT, 0, 0, 0};
			try {
				comms.sendToRobot(command);
			}
			catch (IOException e1) {
				System.out.println("Could not send command");
				e1.printStackTrace();
			}
			System.out.println("Quit...");
			System.exit(0);
		}
	}
	class MoveToTheBallThread extends Thread {
		
		
		public void run(){
			
			try {
				mball.approach(worldState, robot);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	class DriveThread extends Thread {
		private int STEP_DELAY = 150;
		private boolean halted = false;

		public void halt() {
			halted = true;
		}
		@Override
		public void run() {
			halted = false;
			int[] command = {Commands.FORWARDS, 0, 0, 0};
			try {
				comms.sendToRobot(command);
				Thread.sleep(STEP_DELAY);
			}
			catch (IOException e1) {
				System.out.println("Could not send command");
				e1.printStackTrace();
			}
			catch (InterruptedException e1) {
				System.out.println("Drive thread interrupted");
			}
			System.out.println("Attempting to move in a straight line...");

			WorldState worldState = strat.getWorldState();
			int startX = worldState.getBlueX();
			strat.setStart(startX, worldState.getBlueY());
			System.out.println("StartY: " + worldState.getBlueY());
			while (worldState.getBlueX() < 570 && !halted) {
				int[] correction = strat.getCorrection();
				command[1] = correction[0];
				command[2] = correction[1];

				try {
					comms.sendToRobot(command);
					System.out.println("Applying correction: " + Arrays.toString(correction));

					// Wait before updating correction
					Thread.sleep(STEP_DELAY);
				}
				catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				catch(InterruptedException e1) {
					System.out.println("Drive thread interrupted");
				}

				worldState = strat.getWorldState();
			}
			// If the thread has been halted, the stop command was already issued.
			if (!halted) {
				try {
					comms.sendToRobot(new int[]{Commands.STOP, 0, 0, 0});
				}
				catch(IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	class Stopping extends TimerTask{
		@Override
		public void run() {
			int[] command = {Commands.STOP, 0, 0, 0};
			try {
				comms.sendToRobot(command);
			}
			catch (IOException e) {
				System.out.println("Could not send command");
				e.printStackTrace();
			}
			//r.stop;
			timer.cancel();
			System.out.println("Stop...");
		}
	}
}
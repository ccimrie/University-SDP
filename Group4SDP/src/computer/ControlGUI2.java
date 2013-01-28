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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import communication.BluetoothCommunication;
import strategy.planning.Commands;

public class ControlGUI2 extends JFrame {
	Timer timer;
	int seconds =10;
	
	private JFrame frame = new JFrame("Control Panel");

	private JPanel startStopQuitPanel = new JPanel();
	private JPanel simpleMoveTestPanel = new JPanel();
	private JPanel actionTestPanel = new JPanel();

	private JButton start = new JButton("Start");
	private JButton kick = new JButton("Kick");
	private JButton stop = new JButton("Stop");
	private JButton forward = new JButton("Forward");
	private JButton backward = new JButton("Backward");
	private JButton left = new JButton("Left");
	private JButton right = new JButton("Right");
	private JButton rotate = new JButton("Rotate");
	private JButton anglemove = new JButton("AngleMove");
	private JButton quit = new JButton("Quit");
	
	private JTextField angle= new JTextField("0", 3);

	// Communication variables
	private static BluetoothCommunication comms;
	
	public static final String NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
	public static final String NXT_NAME = "4s";

	
	// static Robot r = new Robot();

	public static void main(String[] args) throws IOException {
		// Make the GUI pretty - uses search because of classname differences
		// between JRE 1.6 and 1.7:
		// 1.6: com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel
		// 1.7: javax.swing.plaf.nimbus.NimbusLookAndFeel
		try {
			String look = "";
			LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
		    for (LookAndFeelInfo info : plafs)
		        if (info.getName().contains("Nimbus"))
		            look = info.getClassName();
			UIManager.setLookAndFeel(look);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// Sets up the GUI
		ControlGUI2 gui = new ControlGUI2();
		gui.Launch();
		gui.action();

		// Sets up the communication
		comms = new BluetoothCommunication(NXT_NAME, NXT_MAC_ADDRESS);
		comms.openBluetoothConnection();

		// Tests the communication
		//String tst = "";
		//BufferedReader inn = new BufferedReader(new InputStreamReader(System.in));
		//tst = inn.readLine();
		//int command = Integer.parseInt(tst);
		//byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.parseInt(tst)).array();
		while (!comms.isRobotReady()){
			// Reduce CPU cost
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		};
		System.out.println("Robot ready!");
		int [] command = new int [] {66,0,0,66};
		comms.sendToRobot(command);
		int[] test = new int[4];
		test = comms.receiveFromRobot();
		System.out.println(test.toString());

		// r.startCommunications();
		// r.setConnected(false);
	}

	public ControlGUI2() {		
		// Auto-generated GUI code (made more readable)
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{301, 0};
		gridBagLayout.rowHeights = new int[]{33, 33, 33, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		simpleMoveTestPanel.add(backward);
		simpleMoveTestPanel.add(left);
		simpleMoveTestPanel.add(right);
		
		GridBagConstraints gbc_actionTestPanel = new GridBagConstraints();
		gbc_actionTestPanel.anchor = GridBagConstraints.NORTH;
		gbc_actionTestPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_actionTestPanel.gridx = 0;
		gbc_actionTestPanel.gridy = 2;
		frame.getContentPane().add(actionTestPanel, gbc_actionTestPanel);
		actionTestPanel.add(kick);
		actionTestPanel.add(rotate);
		actionTestPanel.add(anglemove);
		actionTestPanel.add(angle);
		
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
				int[] command = {1,0,0,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Start...");
				// change timer to automatic stop when detecting the wall
				// when vision will be ready
				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), 10 * 1000);
			}
		});

		kick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {4,0,0,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Kick...");
			}
		});

		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {1,100,99,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Moving forward...");
				
				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});
		
		backward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {2,0,0,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Moving backwards...");
				
				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});
		
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {10, 0, 0, 0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Moving leftside...");
				
				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});
		
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {11, 0, 0, 0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Moving rightside...");
				
				timer = new Timer();
				// Stop in 5 seconds
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {3,0,0,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Stop...");
			}
		});
		
		rotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {6,-120,-30,0};//Angle is the sum of option1 + option2
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Rotate...");
			}
		});
		
		anglemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte direction = (byte) Integer.parseInt(angle.getText());				
				
				int[] command = {12,-1,-1,direction};//Angle is the sum of option1 + option2
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Moving at an angle...");
			}
		});
		
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {5,0,0,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
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
			int[] command = {5,0,0,0};
			try {
				comms.sendToRobot(command);
			} catch (IOException e1) {
				System.out.println("Could not send command");
				e1.printStackTrace();
			}
			System.out.println("Quit...");
			System.exit(0);
		}
	}
	
	class Stopping extends TimerTask{
		@Override
		public void run() {
			int[] command = {3,0,0,0};
			try {
				comms.sendToRobot(command);
			} catch (IOException e) {
				System.out.println("Could not send command");
				e.printStackTrace();
			}
			//r.stop;
			timer.cancel();
			System.out.println("Stop...");
		}
	}
}

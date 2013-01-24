package computer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vision.VideoFeed;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import communication.BluetoothCommunication;

public class ControlGUI extends JFrame {
	Timer timer;
	int seconds =5;
	
	private JFrame frame = new JFrame("Control Panel");

	private JPanel startPnl = new JPanel();
	private JPanel kickPnl = new JPanel();
	private JPanel stopPnl = new JPanel();
	private JPanel moveF5sec = new JPanel();
	private JPanel moveB5sec = new JPanel();
	private JPanel moveL5sec = new JPanel();
	private JPanel moveR5sec = new JPanel();

	

	private JButton start = new JButton("Start");
	private JButton kick = new JButton("Kick");
	private JButton stop = new JButton("Stop");
	private JButton forward = new JButton("Forward");
	private JButton backward = new JButton("Backward");
	private JButton leftside = new JButton("Leftside");
	private JButton rightside = new JButton("Rightside");

	// Communication variables
	private static BluetoothCommunication comms;
	
	public static final String NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
	public static final String NXT_NAME = "4s";

	
	// static Robot r = new Robot();

	public static void main(String[] args) throws IOException {
		   new VideoFeed();
		// Sets up the gui
		ControlGUI gui = new ControlGUI();
		gui.Launch();
		gui.action();

		// Setting up the communication
		comms = new BluetoothCommunication(NXT_NAME, NXT_MAC_ADDRESS);
		comms.openBluetoothConnection();

		// Testing the communication
		//String tst = "";
		//BufferedReader inn = new BufferedReader(new InputStreamReader(System.in));
		//tst = inn.readLine();
		//int command = Integer.parseInt(tst);
		//byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.parseInt(tst)).array();
		while (!comms.isRobotReady()){};
		System.out.println("Robot ready!");
		int [] command = new int [] {66,0,0,66};
		comms.sendToRobot(command);
		int[] test = new int[4];
		test = comms.receiveFromRobot();
		System.out.println(test.toString());

		// r.startCommunications();
		// r.setConnected(false);
	}

	public ControlGUI() {
		startPnl.add(start);
		kickPnl.add(kick);
		stopPnl.add(stop);
		moveF5sec.add(forward);
		moveB5sec.add(backward); 
		moveL5sec.add(leftside);
		moveR5sec.add(rightside); 
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(startPnl);
		frame.getContentPane().add(kickPnl);
		frame.getContentPane().add(stop);
		frame.getContentPane().add(moveF5sec);
		frame.getContentPane().add(moveB5sec);
		frame.getContentPane().add(moveL5sec);
		frame.getContentPane().add(moveR5sec);
		
		frame.addWindowListener(new ListenCloseWdw());

	}

	public void action() {
		start.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.out.println("Start...");
				// r.moveForward(60);
				// r.each_wheel_speed(-900, -750);
				// r.accelerateRobot(100);
				// try {
				// Thread.sleep(2*1000);
				// } catch (InterruptedException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				// r.moveBackward(20);

				// r.each_wheel_speed(50, 100);
				// r.moveForwardByDistance(-60, 40);
				// r.accelerateRobot(50);

				// r.rotateRobot(90);

			}

		});

		kick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Kick...");
				int[] command = {4};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// r.kick();

			}
		});

		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("starting moving forward...");
				int[] command = {1};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				timer = new Timer();
			    timer.schedule(new Stopping(), seconds * 1000);
				System.out.println("Moving forward...");
				// r.stop();
			}
		});
		
		backward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {2};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Moving backwards...");
				timer = new Timer();
			    timer.schedule(new Stopping(), seconds * 1000);
				
				// r.stop();
			}
		});
		
		leftside.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {10};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Moving leftside...");
				// r.stop();
				timer = new Timer();
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});
		
		rightside.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {10};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Moving rightside...");
				// r.stop();
				timer = new Timer();
			    timer.schedule(new Stopping(), seconds * 1000);
			}
		});
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] command = {3};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Stop...");
				
			}
		});

	}

	public class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	public void Launch() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	class Stopping extends TimerTask{
		

		@Override
		public void run() {
			int[] command = {3};
			try {
				comms.sendToRobot(command);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Stop...");
			//r.stop;
			timer.cancel();
		}
		
		
	}

}

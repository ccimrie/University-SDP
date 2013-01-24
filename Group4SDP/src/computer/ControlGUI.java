package computer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vision.VideoFeed;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import communication.BluetoothCommunication;

public class ControlGUI extends JFrame {
	private JFrame frame = new JFrame("Control Panel");

	private JPanel startPnl = new JPanel();
	private JPanel kickPnl = new JPanel();
	private JPanel stopPnl = new JPanel();

	private JButton start = new JButton("Start");
	private JButton kick = new JButton("Kick");
	private JButton stop = new JButton("Stop");

	// Communication variables
	private static BluetoothCommunication comms;
	
	public static final String NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
	public static final String NXT_NAME = "4s";

	
	// static Robot r = new Robot();

	public static void main(String[] args) throws IOException {
		  // new VideoFeed();
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
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(startPnl, BorderLayout.WEST);
		frame.getContentPane().add(kickPnl, BorderLayout.CENTER);
		frame.getContentPane().add(stop, BorderLayout.EAST);
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
				// r.kick();

			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Stop...");
				// r.stop();
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

}

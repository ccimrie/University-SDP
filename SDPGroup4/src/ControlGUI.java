import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

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
	private static lejos.pc.comm.NXTComm nxtComm;
	private static NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH, "NXT",
			"00:16:53:08:DC:7F");

	// static Robot r = new Robot();

	public static void main(String[] args) throws IOException {

		// Sets up the gui
		//ControlGUI gui = new ControlGUI();
		//gui.Launch();
		//gui.action();

		// Setting up the communication
		//comms = new BluetoothCommunication(nxtComm, info);
		//comms.openConnection();

		// Testing the communication
		//String tst = "";
		//BufferedReader inn = new BufferedReader(new InputStreamReader(System.in));
		//tst = inn.readLine();
		//comms.sendToRobot(Integer.parseInt(tst));
		System.out.println("tst");

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

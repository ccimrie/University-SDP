package computer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

import communication.BluetoothCommunication;


public class ControlGUI3 extends JFrame {
	Timer timer;
	int seconds =10;

	private JPanel contentPane;
	private JTextField mainMoption1;
	private JTextField mainMoption2;
	private JTextField sidemotoroption1;
	private JTextField sidemotoroption2;
	private JTextField turnLangle;
	private JTextField turnRangle;
	private JTextField turnEangle;
	private JTextField movesideA1;
	private JTextField movesideA2;
	private JTextField extratextField;
	private JTextField extratextField_1;
	private JTextField extratextField_2;
	private JTextField extratextField_3;
	
	private JButton btnMoveForward;
	private JButton btnMoveBackward;
	private JButton btnTurnLeft;
	private JButton btnTurnRight;
	private JButton btnMoveRightside;
	private JButton btnMoveLeftside;
	private JButton btnExtraTurn;
	private JButton btnMoveAngle1;
	private JButton btnMoveAngle2;
	private JButton btnQuit_1;
	private JButton btnKick;
	private JButton btnStop;
	private JButton btnNewButton_1extra;
	private JButton btnNewButton_2extra;
	private JButton btnNewButton_3extra;
	private JButton btnNewButton_4extra;
	
	private JFrame frame = new JFrame("Control Panel");
	
	private static BluetoothCommunication comms;
	
	public static final String NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
	public static final String NXT_NAME = "4s";
	private JPanel panel_3;
	private JPanel panel_4;
	private JPanel panel_5;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		  // new VideoFeed();
				// Sets up the gui
				ControlGUI3 gui = new ControlGUI3();
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

	/**
	 * Create the frame.
	 */
	public ControlGUI3() {
		setTitle("Controls");
		setBounds(new Rectangle(0, 0, 1000, 1000));
		setAlwaysOnTop(true);
		setSize(new Dimension(900, 900));
		setPreferredSize(new Dimension(900, 900));
		setMinimumSize(new Dimension(600, 600));
		setMaximumSize(new Dimension(1000, 1000));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 831, 631);
		contentPane = new JPanel();
		contentPane.setMinimumSize(new Dimension(600, 600));
		contentPane.setMaximumSize(new Dimension(900, 900));
		contentPane.setBounds(new Rectangle(0, 0, 600, 600));
		contentPane.setPreferredSize(new Dimension(600, 600));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[222px]", "[373px]"));
		
		panel_5 = new JPanel();
		contentPane.add(panel_5, "cell 0 0,grow");
		panel_5.setLayout(new MigLayout("", "[222px][25px]", "[373px][26px]"));
		
		JPanel panel_main = new JPanel();
		panel_5.add(panel_main, "cell 0 0,grow");
		panel_main.setSize(new Dimension(350, 350));
		panel_main.setPreferredSize(new Dimension(350, 350));
		panel_main.setMinimumSize(new Dimension(350, 350));
		panel_main.setMaximumSize(new Dimension(350, 350));
		panel_main.setLayout(new MigLayout("", "[349px]", "[50px][50px][242px]"));
		
		JPanel panel_2 = new JPanel();
		panel_main.add(panel_2, "cell 0 0,grow");
		panel_2.setLayout(null);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setBounds(0, 0, 349, 50);
		panel_2.add(horizontalBox_1);
		
		Box verticalBox_3 = Box.createVerticalBox();
		horizontalBox_1.add(verticalBox_3);
		
		btnMoveForward = new JButton("move forward");
		btnMoveForward.setPreferredSize(new Dimension(125, 25));
		verticalBox_3.add(btnMoveForward);
		btnMoveForward.setMinimumSize(new Dimension(125, 25));
		btnMoveForward.setMaximumSize(new Dimension(125, 25));
		
		btnMoveBackward = new JButton("move backward");
		btnMoveBackward.setPreferredSize(new Dimension(125, 25));
		verticalBox_3.add(btnMoveBackward);
		btnMoveBackward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnMoveBackward.setMinimumSize(new Dimension(125, 25));
		btnMoveBackward.setMaximumSize(new Dimension(125, 25));
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalGlue_2.setPreferredSize(new Dimension(5, 0));
		horizontalGlue_2.setSize(new Dimension(5, 0));
		horizontalBox_1.add(horizontalGlue_2);
		
		mainMoption1 = new JTextField();
		mainMoption1.setMinimumSize(new Dimension(15, 22));
		mainMoption1.setMaximumSize(new Dimension(15, 22));
		mainMoption1.setPreferredSize(new Dimension(15, 22));
		mainMoption1.setText("0");
		horizontalBox_1.add(mainMoption1);
		mainMoption1.setColumns(10);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalGlue_3.setSize(new Dimension(5, 0));
		horizontalGlue_3.setPreferredSize(new Dimension(5, 0));
		horizontalBox_1.add(horizontalGlue_3);
		
		mainMoption2 = new JTextField();
		mainMoption2.setMaximumSize(new Dimension(15, 22));
		horizontalBox_1.add(mainMoption2);
		mainMoption2.setText("0");
		mainMoption2.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_main.add(panel_1, "cell 0 1,grow");
		panel_1.setLayout(null);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		horizontalBox_2.setBounds(0, 0, 349, 50);
		panel_1.add(horizontalBox_2);
		
		Box verticalBox_2 = Box.createVerticalBox();
		horizontalBox_2.add(verticalBox_2);
		
		btnMoveLeftside = new JButton("move leftside");
		btnMoveLeftside.setPreferredSize(new Dimension(125, 25));
		verticalBox_2.add(btnMoveLeftside);
		btnMoveLeftside.setMinimumSize(new Dimension(125, 25));
		btnMoveLeftside.setMaximumSize(new Dimension(125, 25));
		
		btnMoveRightside = new JButton("move rightside");
		btnMoveRightside.setPreferredSize(new Dimension(125, 25));
		verticalBox_2.add(btnMoveRightside);
		btnMoveRightside.setMinimumSize(new Dimension(125, 25));
		btnMoveRightside.setMaximumSize(new Dimension(125, 25));
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalGlue.setSize(new Dimension(5, 0));
		horizontalGlue.setPreferredSize(new Dimension(5, 5));
		horizontalGlue.setMinimumSize(new Dimension(5, 0));
		horizontalGlue.setMaximumSize(new Dimension(5, 0));
		horizontalBox_2.add(horizontalGlue);
		
		sidemotoroption1 = new JTextField();
		sidemotoroption1.setPreferredSize(new Dimension(15, 22));
		sidemotoroption1.setMinimumSize(new Dimension(15, 22));
		sidemotoroption1.setMaximumSize(new Dimension(15, 22));
		sidemotoroption1.setText("0");
		horizontalBox_2.add(sidemotoroption1);
		sidemotoroption1.setColumns(10);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalGlue_1.setSize(new Dimension(5, 0));
		horizontalGlue_1.setPreferredSize(new Dimension(5, 0));
		horizontalBox_2.add(horizontalGlue_1);
		
		sidemotoroption2 = new JTextField();
		sidemotoroption2.setMinimumSize(new Dimension(15, 22));
		sidemotoroption2.setMaximumSize(new Dimension(15, 22));
		horizontalBox_2.add(sidemotoroption2);
		sidemotoroption2.setText("0");
		sidemotoroption2.setColumns(10);
		
		JPanel panel = new JPanel();
		panel_main.add(panel, "cell 0 2,grow");
		panel.setLayout(null);
		
		btnTurnLeft = new JButton("turn left");
		btnTurnLeft.setBounds(0, 0, 125, 25);
		panel.add(btnTurnLeft);
		btnTurnLeft.setPreferredSize(new Dimension(125, 25));
		btnTurnLeft.setMinimumSize(new Dimension(125, 25));
		btnTurnLeft.setMaximumSize(new Dimension(125, 25));
		
		turnLangle = new JTextField();
		turnLangle.setBounds(170, 0, 106, 25);
		panel.add(turnLangle);
		turnLangle.setText("0");
		turnLangle.setColumns(10);
		
		btnTurnRight = new JButton("turn right");
		btnTurnRight.setBounds(0, 38, 125, 25);
		panel.add(btnTurnRight);
		btnTurnRight.setPreferredSize(new Dimension(125, 25));
		btnTurnRight.setMinimumSize(new Dimension(125, 25));
		btnTurnRight.setMaximumSize(new Dimension(125, 25));
		
		btnExtraTurn = new JButton("extra turn");
		btnExtraTurn.setBounds(0, 76, 125, 25);
		panel.add(btnExtraTurn);
		btnExtraTurn.setPreferredSize(new Dimension(125, 25));
		btnExtraTurn.setMinimumSize(new Dimension(125, 25));
		btnExtraTurn.setMaximumSize(new Dimension(125, 25));
		
		turnRangle = new JTextField();
		turnRangle.setBounds(170, 39, 106, 25);
		panel.add(turnRangle);
		turnRangle.setText("0");
		turnRangle.setColumns(10);
		
		turnEangle = new JTextField();
		turnEangle.setBounds(170, 77, 106, 25);
		panel.add(turnEangle);
		turnEangle.setText("0");
		turnEangle.setColumns(10);
		
		btnMoveAngle1 = new JButton("move sideway1");
		btnMoveAngle1.setBounds(0, 114, 125, 25);
		panel.add(btnMoveAngle1);
		btnMoveAngle1.setPreferredSize(new Dimension(125, 25));
		btnMoveAngle1.setMinimumSize(new Dimension(125, 25));
		btnMoveAngle1.setMaximumSize(new Dimension(125, 25));
		
		btnMoveAngle2 = new JButton("move sideway2");
		btnMoveAngle2.setBounds(0, 152, 125, 25);
		panel.add(btnMoveAngle2);
		btnMoveAngle2.setPreferredSize(new Dimension(125, 25));
		btnMoveAngle2.setMinimumSize(new Dimension(125, 25));
		btnMoveAngle2.setMaximumSize(new Dimension(125, 25));
		
		movesideA1 = new JTextField();
		movesideA1.setBounds(170, 115, 106, 25);
		panel.add(movesideA1);
		movesideA1.setText("0");
		movesideA1.setColumns(10);
		
		movesideA2 = new JTextField();
		movesideA2.setBounds(170, 153, 106, 25);
		panel.add(movesideA2);
		movesideA2.setText("0");
		movesideA2.setColumns(10);
		
		btnKick = new JButton("kick");
		btnKick.setBounds(0, 198, 230, 44);
		panel.add(btnKick);
		btnKick.setMinimumSize(new Dimension(125, 25));
		btnKick.setMaximumSize(new Dimension(125, 25));
		btnKick.setPreferredSize(new Dimension(125, 25));
		
		btnStop = new JButton("Stop");
		btnStop.setBounds(242, 201, 95, 38);
		panel.add(btnStop);
		
		JLabel lblPlaceForVideo = new JLabel("Place for video");
		panel_5.add(lblPlaceForVideo, "cell 1 0,grow");
		lblPlaceForVideo.setHorizontalTextPosition(SwingConstants.CENTER);
		lblPlaceForVideo.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel_4 = new JPanel();
		panel_5.add(panel_4, "cell 0 1 2 1,grow");
		panel_4.setLayout(new MigLayout("", "[222px][110px][25px]", "[26px]"));
		
		JPanel panel_wasd = new JPanel();
		panel_4.add(panel_wasd, "cell 0 0,grow");
		panel_wasd.setLayout(null);
		
		JButton btnF = new JButton("F");
		btnF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 87){
					int[] command = {1,100,100,0};
					try {
						comms.sendToRobot(command);
					} catch (IOException e1) {
						System.out.println("Could not send command");
						e1.printStackTrace();
					}
					System.out.println("Moving forward...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnF.setBounds(61, 0, 45, 33);
		panel_wasd.add(btnF);
		btnF.setHorizontalTextPosition(SwingConstants.CENTER);
		
		JButton btnL = new JButton("L");
		btnL.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 65){
					int[] command = {10, 0, 0, 0};
					try {
					comms.sendToRobot(command);
					} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
					}
					System.out.println("Moving leftside...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnL.setBounds(4, 50, 45, 33);
		panel_wasd.add(btnL);
		btnL.setHorizontalTextPosition(SwingConstants.CENTER);
		
		JButton btnB = new JButton("B");
		btnB.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 83){
					int[] command = {2,0,0,0};
					try {
						comms.sendToRobot(command);
					} catch (IOException e1) {
						System.out.println("Could not send command");
						e1.printStackTrace();
					}
					System.out.println("Moving forward...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnB.setBounds(61, 50, 45, 33);
		panel_wasd.add(btnB);
		btnB.setHorizontalTextPosition(SwingConstants.CENTER);
		
		JButton btnR = new JButton("R");
		btnR.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 68){
					int[] command = {11, 0, 0, 0};
					try {
					comms.sendToRobot(command);
					} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
					}
					System.out.println("Moving rightside...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnR.setBounds(118, 50, 45, 33);
		panel_wasd.add(btnR);
		btnR.setHorizontalTextPosition(SwingConstants.CENTER);
		
		JButton btnTL = new JButton("<");
		btnTL.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 81){
					int[] command = {6,0,16,0};//Angle is the sum of option1 + option2
					try {
					comms.sendToRobot(command);
					} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
					}
					System.out.println("turning...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnTL.setBounds(4, 0, 45, 33);
		panel_wasd.add(btnTL);
		btnTL.setHorizontalTextPosition(SwingConstants.CENTER);
		
		JButton btnTR = new JButton(">");
		btnTR.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 69){
					int[] command = {6,0,-16,0};//Angle is the sum of option1 + option2
					try {
					comms.sendToRobot(command);
					} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
					}
					System.out.println("Rotate...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnTR.setBounds(118, 0, 45, 33);
		panel_wasd.add(btnTR);
		btnTR.setHorizontalTextPosition(SwingConstants.CENTER);
		
		JButton btnNewButton = new JButton("KKKick!");
		btnNewButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()== 32) {
					int[] command = { 4, 0, 0, 0 };
					try {
						comms.sendToRobot(command);
					} catch (IOException e1) {
						System.out.println("Could not send command");
						e1.printStackTrace();
					}
					System.out.println("Kick...");
				}
			}
		});
		btnNewButton.setBounds(4, 96, 159, 44);
		panel_wasd.add(btnNewButton);
		
		JButton btnU = new JButton("U");
		btnU.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 87){
					int[] command = {1,100,100,0};
					try {
						comms.sendToRobot(command);
					} catch (IOException e1) {
						System.out.println("Could not send command");
						e1.printStackTrace();
					}
					System.out.println("Moving forward...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnU.setBounds(175, 50, 45, 33);
		panel_wasd.add(btnU);
		btnU.setHorizontalTextPosition(SwingConstants.CENTER);
		
		JButton btnD = new JButton("D");
		btnD.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 87){
					int[] command = {1,100,100,0};
					try {
						comms.sendToRobot(command);
					} catch (IOException e1) {
						System.out.println("Could not send command");
						e1.printStackTrace();
					}
					System.out.println("Moving forward...");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
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
		btnD.setBounds(175, 96, 45, 33);
		panel_wasd.add(btnD);
		btnD.setHorizontalTextPosition(SwingConstants.CENTER);
		
		panel_3 = new JPanel();
		panel_4.add(panel_3, "cell 1 0 2 1,grow");
		panel_3.setLayout(new MigLayout("", "[110px][15px][25px][26px][116px][23px][215px]", "[26px][25px][25px][20px][25px][15px][25px]"));
		
		btnQuit_1 = new JButton("Quit!");
		panel_3.add(btnQuit_1, "cell 0 0 3 7,grow");
		
		extratextField = new JTextField();
		panel_3.add(extratextField, "cell 4 0,growx,aligny center");
		extratextField.setText("0");
		extratextField.setColumns(10);
		
		btnNewButton_1extra = new JButton("New button");
		panel_3.add(btnNewButton_1extra, "cell 6 0,alignx left,aligny bottom");
		
		extratextField_1 = new JTextField();
		panel_3.add(extratextField_1, "cell 4 2,grow");
		extratextField_1.setText("0");
		extratextField_1.setColumns(10);
		
		btnNewButton_2extra = new JButton("New button");
		panel_3.add(btnNewButton_2extra, "cell 6 2,alignx left,aligny top");
		
		extratextField_2 = new JTextField();
		panel_3.add(extratextField_2, "cell 4 4,grow");
		extratextField_2.setText("0");
		extratextField_2.setColumns(10);
		
		btnNewButton_3extra = new JButton("New button");
		panel_3.add(btnNewButton_3extra, "cell 6 4,alignx left,aligny top");
		
		extratextField_3 = new JTextField();
		panel_3.add(extratextField_3, "cell 4 6,grow");
		extratextField_3.setText("0");
		extratextField_3.setColumns(10);
		
		btnNewButton_4extra = new JButton("New button");
		panel_3.add(btnNewButton_4extra, "cell 6 6,alignx left,aligny top");
		
		frame.getContentPane().add(panel_5);
		
		
	}
	public void action() {
		

		btnKick.addActionListener(new ActionListener() {
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

		btnMoveForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte op1 = (byte) Integer.parseInt(mainMoption1.getText());
				byte op2 = (byte) Integer.parseInt(mainMoption2.getText());
				int[] command = {1,op1,op2,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Moving forward...");

				//timer = new Timer();
				// Stop in 5 seconds
			    //timer.schedule(new Stopping(), seconds * 1000);
			}
		});

		btnMoveBackward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte op1 = (byte) Integer.parseInt(mainMoption1.getText());
				byte op2 = (byte) Integer.parseInt(mainMoption2.getText());
				int[] command = {2,op1,op2,0};
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

		btnMoveLeftside.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte op1 = (byte) Integer.parseInt(sidemotoroption1.getText());
				byte op2 = (byte) Integer.parseInt(sidemotoroption2.getText());
				int[] command = {10, op1, op2, 0};
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

		btnMoveRightside.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte op1 = (byte) Integer.parseInt(sidemotoroption1.getText());
				byte op2 = (byte) Integer.parseInt(sidemotoroption2.getText());
				int[] command = {11, op1, op2, 0};
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

		btnStop.addActionListener(new ActionListener() {
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

		btnTurnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte op1 = (byte) Integer.parseInt(turnLangle.getText());
				byte op2 = (byte) Integer.parseInt(turnEangle.getText());
				
				int[] command = {6,op1,op2,0};//Angle is the sum of option1 + option2
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Rotate...");
			}
		});
		
		btnTurnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte op1 = (byte) Integer.parseInt(turnRangle.getText());
				byte op2 = (byte) Integer.parseInt(turnEangle.getText());
				
				int[] command = {6,-op1,-op2,0};//Angle is the sum of option1 + option2
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Rotate...");
			}
		});
		
		btnExtraTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte op1 = (byte) Integer.parseInt(turnLangle.getText());
				byte op2 = (byte) Integer.parseInt(turnEangle.getText());
				
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

		btnMoveAngle1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte direction = (byte) Integer.parseInt(movesideA1.getText());				

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
		
		btnMoveAngle2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte direction = (byte) Integer.parseInt(movesideA2.getText());				

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


		btnQuit_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
						

				int[] command = {5,0,0,0};
				try {
					comms.sendToRobot(command);
				} catch (IOException e1) {
					System.out.println("Could not send command");
					e1.printStackTrace();
				}
				System.out.println("Quit...");
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

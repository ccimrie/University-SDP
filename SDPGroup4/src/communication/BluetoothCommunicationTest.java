package communication;

import java.io.IOException;

import communication.BluetoothCommunication;

public class BluetoothCommunicationTest {

	public static final int test_no = 1000;

	public static final String NXT_MAC_ADDRESS = "00:16:53:0A:07:1D";
	public static final String NXT_NAME = "4s";

	private static BluetoothCommunication comms;

	/**
	 * Testing the BluetoothCommunication class by sending number to Brick and
	 * wait for an echo
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Bluetooth Working: ");// + testBTComm());
	}
//Test is broken, needs fixing!
	public static boolean testBTComm() {
		int correct = 0;
		long start_time = System.currentTimeMillis();
		long end_time = start_time;

		comms = new BluetoothCommunication(NXT_NAME, NXT_MAC_ADDRESS);
		try {
			comms.openBluetoothConnection();
		} catch (IOException e) {
			System.out.println("Error Open Connection");
			end_time = System.currentTimeMillis();
			System.out.println("Time: " + (end_time - start_time) + " ms");
			return false;
		}
		end_time = System.currentTimeMillis();
		System.out.println("Establish Connection Time: "
				+ (end_time - start_time) + " ms");

		if (comms.isRobotReady()) {
			start_time = System.currentTimeMillis();
			byte b = 0x00;
			for (int i = 0; i < test_no; i++) {
				// comms.sendToRobot(b); // sendToRobot(byte) is deprecated
				System.out.println("Wait Data from Robot");
				int receive_info = 0;
				try {
					receive_info = comms.receiveByteFromRobot();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int sent_info = 0x000000FF & b;
				if (receive_info != sent_info) {
					System.out.println("Error at " + sent_info + ", "
							+ receive_info);
				} else {
					correct++;
				}

				end_time = System.currentTimeMillis();
				System.out.println("Time: " + (end_time - start_time) + " ms");
				b++;
			}
		}
		System.out.println("Result : " + correct + "/" + test_no);
		comms.closeBluetoothConnection();
		return correct == test_no;
	}
}

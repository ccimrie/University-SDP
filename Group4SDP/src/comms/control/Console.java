package comms.control;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console {
	public static void main(String[] args) {
		Server rc = Server.getInstance();
		rc.connect();
		boolean running = true;
		while (running) {
			System.out.print("$ ");
			
			String input = getInput();
			String[] split = input.split(" ");
			String command = split[0];
			int argc = split.length - 1;
			
			if (command.equals("rquit")) {
				rc.quit();
				running = false;
				rc.disconnect();
			} else if (command.equals("quit")) {
				rc.disconnect();
				running = false;
			} else if (command.equals("kick")) {
				rc.kick();
				
			} else if (command.equals("travel")) {
				if (argc >= 2) {
					rc.travel(Double.parseDouble(split[1]), Double.parseDouble(split[2]));
				} else if (argc == 1) {
					rc.travel(Double.parseDouble(split[1]));
				} else {
					System.out.println("Usage: travel <distance> [speed]");
				}
			} else if (command.equals("forward")){
				rc.forward();
			} else if (command.equals("backward")){
				rc.backward();
			} else if (command.equals("rotate")) {
				if (argc >= 2) {
					rc.rotate(Double.parseDouble(split[1]), Double.parseDouble(split[2]));
				} else if (argc == 1) {
					rc.rotate(Double.parseDouble(split[1]));
				} else {
					System.out.println("Usage: rotate <angle> [speed]");
				}
			} else if (command.equals("arc")) {
				if (argc >= 2) {
					rc.arcForward(Double.parseDouble(split[1]), Double.parseDouble(split[2]));
				} else if (argc == 1) {
					rc.arcForward(Double.parseDouble(split[1]));
				} else {
					System.out.println("Usage: arc <radius> [speed]");					
				}
			} else if (command.equals("accel")) {
				if (argc >= 1) {
					rc.setAcceleration(Double.parseDouble(split[1]));
				} else {
					System.out.println("Usage: accel <acceleration>");
				}
			} else if (command.equals("arcback")) {
				if (argc >= 2) {
					rc.arcBackward(Double.parseDouble(split[1]), Double.parseDouble(split[2]));
				} else if (argc == 1) {
					rc.arcBackward(Double.parseDouble(split[1]));
				} else {
					System.out.println("Usage: arcback <radius> [speed]");
				}
			} else if (command.equals("stop")) {
				rc.stop();
			} else {
				System.out.println("Unknown command \"" + input + "\"");
			}
		}
	}

	public static String getInput() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			return in.readLine();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "";
		}
	}
}


package simulator;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import movement.RobotMover;

/**
 * Provides the functionality of a KeyListener on all windows for the simulator.
 * 
 * Implemented as a workaround for the keyPressed/keyReleased spam on linux -
 * should also work on windows & mac without issues
 * 
 * @author Alex Adams (s1046358)
 */
public class KeyControl implements KeyEventDispatcher {
	private static final int KEY_W = 0;
	private static final int KEY_S = 1;
	private static final int KEY_A = 2;
	private static final int KEY_D = 3;

	private static final long KEY_REFRESH_INTERVAL = 50;
	private static final long KEY_HELD_WAIT_PERIOD = 500;

	private final Timer[] keyHeldTimer;
	private long[] timerStart = { 0, 0, 0, 0 };
	private boolean[] timerRunning = { false, false, false, false };

	private double speedForward = 0.0;
	private double speedRight = 0.0;

	private final RobotMover mover;

	/**
	 * Timer to trigger a stop when the key is released
	 */
	private class KeyTimer extends TimerTask {
		private final int keyIndex;

		public KeyTimer(int keyIndex) {
			this.keyIndex = keyIndex;
		}

		@Override
		public void run() {
			if (Math.abs(timerStart[keyIndex] - System.currentTimeMillis()) < KEY_HELD_WAIT_PERIOD) {
				refreshTimer(keyIndex);
				return;
			}
			timerRunning[keyIndex] = false;

			switch (keyIndex) {
			case KEY_W:
				speedForward -= 100.0;
				break;
			case KEY_S:
				speedForward += 100.0;
				break;
			case KEY_A:
				speedRight += 100.0;
				break;
			case KEY_D:
				speedRight -= 100.0;
				break;
			}

			// Stop the robot and reset variables if the robot is configured to
			// stop moving
			if (Math.abs(speedForward) < 0.01 && Math.abs(speedRight) < 0.01) {
				speedForward = 0.0;
				speedRight = 0.0;
				mover.stopRobot();
			}
			else {
				mover.move(speedRight, speedForward);
			}
		}
	}

	public KeyControl(RobotMover mover) {
		this.mover = mover;
		keyHeldTimer = new Timer[4];
		for (int i = 0; i < 4; ++i)
			keyHeldTimer[i] = new Timer();
	}

	private int getKeyIndex(char key) {
		switch (key) {
		case 'w':
			return KEY_W;
		case 's':
			return KEY_S;
		case 'a':
			return KEY_A;
		case 'd':
			return KEY_D;
		default:
			return -1;
		}
	}

	private void refreshTimer(int index) {
		// Only use timers for forward and back controls
		if (index == KEY_W || index == KEY_S || index == KEY_A
				|| index == KEY_D) {
			// Key held down
			if (timerRunning[index]) {
				keyHeldTimer[index].cancel();
				keyHeldTimer[index] = new Timer();
			}
			// Key just pressed
			else {
				timerStart[index] = System.currentTimeMillis();
			}
			keyHeldTimer[index].schedule(new KeyTimer(index),
					KEY_REFRESH_INTERVAL);
			timerRunning[index] = true;
		}
	}

	private boolean process(char key) {
		boolean linearControl = false;
		int index = getKeyIndex(key);
		switch (key) {
		case 'w':
			// Only queue the movement on the first press
			if (!timerRunning[index]) {
				linearControl = true;
				speedForward += 100.0;
			}
			refreshTimer(index);
			break;
		case 's':
			if (!timerRunning[index]) {
				linearControl = true;
				speedForward -= 100.0;
			}
			refreshTimer(index);
			break;
		case 'a':
			if (!timerRunning[index]) {
				linearControl = true;
				speedRight -= 100.0;
			}
			refreshTimer(index);
			break;
		case 'd':
			if (!timerRunning[index]) {
				linearControl = true;
				speedRight += 100.0;
			}
			refreshTimer(index);
			break;
		case 'q':
			mover.rotate(Math.toRadians(-4));
			try {
				mover.waitForCompletion();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 'e':
			mover.rotate(Math.toRadians(4));
			try {
				mover.waitForCompletion();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 'r':
			mover.kick();
			try {
				mover.waitForCompletion();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			return false;
		}

		if (linearControl) {
			mover.move(speedRight, speedForward);
		}

		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent evt) {
		int eventType = evt.getID();
		switch (eventType) {
		case KeyEvent.KEY_PRESSED:
			char key = evt.getKeyChar();
			if (!process(key))
				return false;
			break;
		default:
			return false;
		}
		return true;
	}
}

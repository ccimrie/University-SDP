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

	private static final long KEY_REFRESH_INTERVAL = 50;
	private static final long KEY_HELD_WAIT_PERIOD = 500;

	private final Timer[] keyHeldTimer;
	private long[] timerStart = { 0, 0 };
	private boolean[] timerRunning = { false, false };

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
			mover.stopRobot();
		}
	}

	public KeyControl(RobotMover mover) {
		this.mover = mover;
		keyHeldTimer = new Timer[5];
		for (int i = 0; i < 5; ++i)
			keyHeldTimer[i] = new Timer();
	}

	private int getKeyIndex(char key) {
		switch (key) {
		case 'w':
			return KEY_W;
		case 's':
			return KEY_S;
		default:
			return -1;
		}
	}

	private void refreshTimer(int index) {
		// Only use timers for forward and back controls
		if (index == KEY_W || index == KEY_S) {
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
		int index = getKeyIndex(key);
		switch (key) {
		case 'w':
			// Only queue the movement on the first press
			if (!timerRunning[index])
				mover.move(0, 100);
			refreshTimer(index);
			break;
		case 's':
			if (!timerRunning[index])
				mover.move(0, -100);
			refreshTimer(index);
			break;
		case 'a':
			mover.rotate(Math.toRadians(-4));
			try {
				mover.waitForCompletion();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 'd':
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

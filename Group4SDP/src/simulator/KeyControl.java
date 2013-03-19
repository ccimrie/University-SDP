package simulator;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import movement.RobotMover;

public class KeyControl implements KeyListener {
	private final RobotMover mover;

	public KeyControl(final RobotMover mover) {
		this.mover = mover;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyChar()) {
		case 's':
			mover.move(0, -100);
			break;
		case 'w':
			mover.move(0, 100);
			break;
		case 'a':
			mover.rotate(-1* Math.toRadians(8));
			break;
		case 'd':
			mover.rotate(Math.toRadians(8));
			break;
		case 'k': 
			mover.kick();
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		try {
			mover.resetQueue();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		mover.stopRobot();
	}

	public void keyTyped(KeyEvent e) {
	}

}

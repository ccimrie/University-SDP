package vision.interfaces;

import vision.WorldState;

public interface WorldStateReceiver {
	public void sendWorldState(WorldState worldState);
}

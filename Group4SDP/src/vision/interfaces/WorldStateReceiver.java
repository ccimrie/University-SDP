package vision.interfaces;

import world.state.WorldState;

/**
 * An interface for classes which receive the world state from vision
 * 
 * @author Alex Adams (s1046358)
 */
public interface WorldStateReceiver {
	public void sendWorldState(WorldState worldState);
}

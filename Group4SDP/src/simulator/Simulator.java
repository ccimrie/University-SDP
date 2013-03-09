package simulator;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jbox2d.testbed.framework.TestList;
import org.jbox2d.testbed.framework.TestbedFrame;
import org.jbox2d.testbed.framework.TestbedMain;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.old.RoboFootball;
import world.state.WorldState;

/**
 * 
 * @author Maithu
 *
 */
public class Simulator {
	private final WorldState worldState;
	private static final Logger log = LoggerFactory.getLogger(TestbedMain.class);

	public Simulator(final WorldState worldState) {
		this.worldState = worldState;
		
		 
		
	}

}

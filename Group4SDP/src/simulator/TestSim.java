package simulator;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jbox2d.dynamics.Body;
import org.jbox2d.testbed.framework.TestList;
import org.jbox2d.testbed.framework.TestbedFrame;
import org.jbox2d.testbed.framework.TestbedMain;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import world.state.Ball;
import world.state.WorldState;

public class TestSim {
	 
	
	

	    public static float x, y, ballx, bally, oppx, oppy, bearing, oppBearing;


	

	    public static void main(String[] args) {
	        try {
	            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	        } catch (Exception e) {
	           
	        }
	        //Instantiate model where all the tests reside.
	        TestbedModel model = new TestbedModel();
	        TestbedPanel panel = new TestPanelJ2D(model);

	        //Instantiate new custom test.
	        Updateworld matchTest = new Updateworld();
	        
	       
	        
	        //Add test to the model
	        model.addCategory("Robots");
	        model.addTest(matchTest);

	        //Add the default tests which were included in JBox2D distro.
	        TestList.populateModel(model);
	        JFrame testbed = new TestbedFrame(model, panel);
	        testbed.setVisible(true);
	        testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	
	    }

}

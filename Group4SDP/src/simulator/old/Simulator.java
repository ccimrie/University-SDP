package simulator.old;

import org.jbox2d.dynamics.Body;
import org.jbox2d.testbed.framework.*;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

//import strategy.movement.TurnToBall;

public class Simulator {

    private static final Logger log = LoggerFactory.getLogger(TestbedMain.class);
    static SimulatorRobot ourRobot, enemyRobot;
    static Body ball;

    public static float x, y, ballx, bally, oppx, oppy, bearing, oppBearing;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            log.warn("Could not set the look and feel to nimbus.  "
                    + "Hopefully you're on a mac so the window isn't ugly as crap.");
        }
        //Instantiate model where all the tests reside.
        TestbedModel model = new TestbedModel();
        TestbedPanel panel = new TestPanelJ2D(model);

        //Instantiate new custom test.
        RoboFootball matchTest = new RoboFootball();
        //Add test to the model
        model.addCategory("Robots");
        model.addTest(matchTest);

        //Add the default tests which were included in JBox2D distro.
        TestList.populateModel(model);
        JFrame testbed = new TestbedFrame(model, panel);
        testbed.setVisible(true);
        testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        while (true) {
            ourRobot = matchTest.ourRobo;
            enemyRobot = matchTest.enemyRobo;
            ball = matchTest.ball;
            if (ourRobot == null) {
                System.out.println("our robot is null");
                System.out.println("");
            } else {
            	// Sorry for breaking this, please fix it :)
                //double angle = TurnToBall.findBearing(ourRobot.robot.getWorldCenter().x, ourRobot.robot.getWorldCenter().y,
                //        ball.getWorldCenter().x, ball.getWorldCenter().y);
                //double turnAngle = TurnToBall.turnAngle(Math.toDegrees(ourRobot.robot.getAngle() + Math.PI / 2), angle);
                //System.out.println("Bearing to ball is: " + angle + " degrees.");
                //System.out.println("Amount to turn is: " + turnAngle + " degrees.");
                //System.out.println("");
                
                try {
                    Thread.sleep(17);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
           
        }
    }
}
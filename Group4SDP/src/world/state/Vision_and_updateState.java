
package world.state;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
import javax.swing.JFrame;
import javax.swing.JLabel;
 
import JavaVision.ControlGUI;
import JavaVision.PitchConstants;
import JavaVision.ThresholdsState;
import JavaVision.Vision;
import JavaVision.WorldState;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.Control;
import au.edu.jcu.v4l4j.DeviceInfo;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.ImageFormat;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.ImageFormatException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;

import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.Control;
import au.edu.jcu.v4l4j.DeviceInfo;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.ImageFormat;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.ImageFormatException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
public class Vision_and_updateState {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		final   WorldState worldState = new WorldState();
	      final ThresholdsState thresholdsState = new ThresholdsState();

	        /* Default to main pitch. */
	        final  PitchConstants pitchConstants = new PitchConstants(0);
	        
	        /* Default values for the main vision window. */
	        final String videoDevice = "/dev/video0";
	       final int width = 640;
	       final int height = 480;
	       final int channel = 0;
	       final int videoStandard = V4L4JConstants.STANDARD_PAL;
	       final int compressionQuality = 80;
		
		
		 Thread thread1 = new Thread() {
             public  void run() {
                     try {
                    Vision thevision = new Vision(videoDevice, width, height, channel, videoStandard,
                                 compressionQuality, worldState, thresholdsState, pitchConstants);
                         ControlGUI thresholdsGUI = new ControlGUI(thresholdsState, worldState, pitchConstants);
                         thresholdsGUI.initGUI();
                    } catch (V4L4JException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    }
                          
                     
             }
     };
     Thread thread2 = new Thread() {
         public  void run() {
                 
                while (1>0){
              System.out.println( worldState.getBallX());}
                 
         }
 };  
 thread1.start();
 thread2.start();
 thread1.join();
 thread2.join();	// TODO Auto-generated method stub

	}

}

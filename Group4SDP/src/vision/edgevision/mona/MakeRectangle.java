package vision.edgevision.mona;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MakeRectangle {
	
	BufferedImage image;
	int width, height;

	JLabel label;
	JFrame frame;
	
	static int ax = 0, ay = 0;
	static int bx = 0, by = 0;
	static int cx = 0, cy = 0;
	static int dx = 0, dy = 0;
	static int ex = 0, ey = 0;
	static int fx = 0, fy = 0;
	
	static double slope;
	
	public static void main(String args[]) {
		MakeRectangle insp = new MakeRectangle();
		insp.loadImage("saved.png");
		insp.makeDisplay();
	}
	
	void loadImage(String name) {
		File file = new File(name);
		try {
			this.image = ImageIO.read(file);
			width = this.image.getWidth();
			height = this.image.getHeight();
			System.out.println("Loaded image of width " + width + " and height " + height + ".");
		} catch (IOException e) {
			System.out.println("Failed to load image!");
		}
	}
	
	
	
	void makeDisplay() {
		frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Image processing");
		frame.setVisible(true);
		frame.getContentPane().add(new JPanel(){
			 public void paintComponent(Graphics g) {
				    super.paintComponent(g);
				    g.drawImage(image, 0, 0, width, height, null);
				    
				    ax = 200; ay = 200;
					bx = 100; by = 300;
					
				    g.setColor(Color.RED);
				    g.drawRect(ax, ay, 3, 3);
				    g.drawRect(bx,  by,  1,  1);
				    
				    // Slope is in (-PI, +PI]
				    if (ax < bx && ay == by) {
				    	slope = 0;
				    } else if ( ax < bx && ay < by) {
				    	// Third quadrant
				    	double d = Math.abs((double)(by - ay) / (bx - ax));
				    	slope = Math.atan(d);
				    } else if ( ax == bx && ax < by) {
				    	slope = Math.PI / 2;
				    } else if ( ax > bx && ay < by) {
				    	// Fourth quadrant
				    	double d = Math.abs((double)(by - ay) / (bx - ax));
				    	slope = Math.PI / 2 + Math.atan(d);
				    } else if ( ax > bx && ay == by) {
				    	slope = Math.PI;
				    } else if ( ax > bx && ay > by) {
				    	// First quadrant
				    	double d = Math.abs((double)(by - ay) / (bx - ax));
				    	slope = - Math.PI + Math.atan(d);
				    } else if ( ax == bx && ay > by) {
				    	slope = - Math.PI / 2;
				    } else if (ax < bx && ay > by) {
				    	// second quadrant
				    	double d = Math.abs((double)(by - ay) / (bx - ax));
				    	slope = - Math.PI / 2 + Math.atan(d);
				    }
				    
				    System.out.println("the angle is " + slope / Math.PI * 180);
				    
				    int robotWidth = 30;
					cx = (int) (ax + robotWidth * Math.sin(slope));
					cy = (int) (ay - robotWidth * Math.cos(slope));
					dx = (int) (bx + robotWidth * Math.sin(slope));
					dy = (int) (by - robotWidth * Math.cos(slope));
				    
				    g.setColor(Color.GREEN);
				    g.drawRect(cx, cy, 1, 1);
				    g.drawRect(dx, dy, 1, 1);
				    
				    ex = (int) (ax - robotWidth * Math.sin(slope));
					ey = (int) (ay + robotWidth * Math.cos(slope));
					fx = (int) (bx - robotWidth * Math.sin(slope));
					fy = (int) (by + robotWidth * Math.cos(slope));
					
				    g.setColor(Color.BLUE);
				    g.drawRect(ex, ey, 1, 1);
				    g.drawRect(fx, fy, 1, 1);
				    System.out.println("A(" + ax + "," + ay + ");  B(" + bx + "," + by + ");  C(" + cx + "," + cy + ");  D(" + dx + "," + dy + ").");
			 }
		});
	}

}

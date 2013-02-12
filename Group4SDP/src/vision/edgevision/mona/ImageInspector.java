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

public class ImageInspector {
	
	BufferedImage image;
	int width, height;
	
	int[][][] colors;
	int[][] diffs;
	int[][] proc;
	int[] robot1;
	int[] robot2;
			
	JLabel label;
	JFrame frame;
	
	int top, bottom, left, right;
	
	int ax = 0, ay = 0, bx = 0, by = 0, cx = 0, cy = 0, dx = 0, dy = 0, ex = 0, ey = 0, fx = 0, fy = 0;
	double slope;
	
	public static void main(String args[]) {
		ImageInspector insp = new ImageInspector();
		insp.loadImage("saved.png");
		insp.initializePitch(395, 110, 45, 595);
		insp.makeColorMatrix();
		insp.getDiffs(200);
		insp.removeNoise();
		
//		if (!insp.findNextRobot(50, 35)) {
//			// find ball and then 2 robots
//		} else {
//			// One robot was found, so do the other
//			if (!insp.findNextRobot(50, 35)) {
//				// ball
//			} else {
//				insp.findNextRobot(50, 35);
//			}
//		}
		System.out.print("final resp: " + insp.findNextRobot(50, 35));
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
	
	void initializePitch(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.right = right;
		this.left = left;
		System.out.print("Set pitch margins.");
	}
	
	void makeColorMatrix() {
		colors = new int[width][height][3]; // colors[X-position/column][Y-position/row][red - 0/green - 1/blue - 2]
		for (int column = 0; column < width; column++)
			for (int row = 0; row < height; row++) {
				Color c = new Color(image.getRGB(column, row));		
				colors[column][row][0] = c.getRed();
				colors[column][row][1] = c.getGreen();
				colors[column][row][2] = c.getBlue();
			}
	}
	
	void getDiffs(int treshold) {
		diffs = new int[width][height];
		for (int row = 0; row < height; row++)
			for (int column = 0; column < width; column++)
				diffs[column][row] = 0;
		
		// On every column, find row variation
		for (int column = left; column <= right; column++) {
			for (int row = bottom; row <= top; row++) {
				int dist = (colors[column][row][0] - colors[column][row + 1][0]) * (colors[column][row][0] - colors[column][row + 1][0]) +
					(colors[column][row][1] - colors[column][row + 1][1]) * (colors[column][row][1] - colors[column][row + 1][1]) +
					(colors[column][row][2] - colors[column][row + 1][2]) * (colors[column][row][2] - colors[column][row + 1][2]);
				if (dist > treshold) {
					diffs[column][row] = 1;
					diffs[column][row + 1] = 1;
				}
			}
		}

		// On every row, find column variations		
		for (int row = bottom; row <= top; row++) {
			for (int column = left; column <= right; column++) {
				int dist = (colors[column][row][0] - colors[column + 1][row][0]) * (colors[column][row][0] - colors[column + 1][row][0]) +
						(colors[column][row][1] - colors[column + 1][row][1]) * (colors[column][row][1] - colors[column + 1][row][1]) +
						(colors[column][row][2] - colors[column + 1][row][2]) * (colors[column][row][2] - colors[column + 1][row][2]);
				if (dist > treshold) {
					diffs[column][row] = 1;
					diffs[column + 1][row] = 1;
				}
			}
		}
		proc = new int[width][height];
		for (int row = 0; row < height; row++)
			for (int column = 0; column < width; column++)
				proc[column][row] = diffs[column][row];
	}
	
	boolean isPoint(int x, int y) {
		if (diffs[x][y] == 1)
			return true;
		else
			return false;
	}
	
	boolean isLine(int x1, int y1, int x2, int y2) {
		int distx = x2 - x1;
		int disty = y2 - y1;
		// Test every 4 pixels
		int segs = (int)(Math.sqrt(distx * distx + disty * disty) / 4);
		int cont = 0;
		for (int i = 1; i <= segs; i++) {
			if ( diffs[(int) (x1 + (float)distx / segs * i)][(int) (y1 + (float)disty / segs * i)] == 1)
				cont++;
		}
		if (cont > (float)segs * 0.8)
			return true;
		else
			return false;
	}
	
	void removeNoise() {
		for (int row = top; row >= bottom; row--)
			for (int column = left; column <= right; column++) {
				int cont = 0;
				for (int i = column - 2; i <= column + 2; i++)
					for (int j = row - 2; j <= row + 2; j++)
						if (diffs[i][j] == 1)
							cont++;
				if (cont < 7)
					diffs[column][row] = 0;
			}
	}
	
	boolean findNextRobot(int robotLength, int robotWidth) {
		// Look for a point starting from the upper left corner (up -> down direction)
		boolean flag = true;
		int auxx, auxy;
		for (int column = left; column <= right; column++)
			for (int row = bottom; row <= top; row++)
				if (isPoint(column, row) && flag) {
					// Find a beginning point
					ax = column;
					ay = row;
					for (int angle = 0; angle < 360; angle++) {
						// Look for angles for which there is a point
						auxx = (int)(column + robotLength * Math.cos(angle));
						auxy = (int)(row + robotLength * Math.sin(angle));
						if ( auxx >= left && auxx <= right && auxy >= bottom && auxy <=top && isPoint(auxx, auxy) && flag) {
							if (isLine(column, row, auxx, auxy) && flag) {
								bx = auxx;
								by = auxy;
								
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
							    
								cx = (int) (ax + robotWidth * Math.sin(slope));
								cy = (int) (ay - robotWidth * Math.cos(slope));
								dx = (int) (bx + robotWidth * Math.sin(slope));
								dy = (int) (by - robotWidth * Math.cos(slope));
							    
							    ex = (int) (ax - robotWidth * Math.sin(slope));
								ey = (int) (ay + robotWidth * Math.cos(slope));
								fx = (int) (bx - robotWidth * Math.sin(slope));
								fy = (int) (by + robotWidth * Math.cos(slope));
								
								// Look for parallel lines
								if (isLine(cx, cy, dx, dy)) {
									System.out.println("Points: " + ax + "  " + ay + "  " + bx + "  " + by + "  " + cx + "  " + cy + "  " + dx + "  " + dy + "  ");
									cleanRectangle(ax, ay, bx, by, cx, cy, dx, dy);
									int[] aux = {ax, ay, bx, by, cx, cy, dx, dy};
									if (robot1 != null)
										robot2 = aux;
									else
										robot1 = aux;
									removeNoise();
									return true;
								}
								if (isLine(ex, ey, fx, fy)) {
									System.out.println("Points: " + ax + "  " + ay + "  " + bx + "  " + by + "  " + cx + "  " + cy + "  " + dx + "  " + dy + "  ");
									cleanRectangle(ax, ay, bx, by, cx, cy, dx, dy);
									int[] aux = {ax, ay, bx, by, cx, cy, dx, dy};
									if (robot1 != null)
										robot2 = aux;
									else
										robot1 = aux;
									removeNoise();
									return true;
								}	
							}
						}
					}
				}
		return false;
	}
	
	void cleanRectangle(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		int begx, begy, endx, endy, distx, disty, seg;
		int k = 5;
//		if (x1 < x2) {
//			x1 -= k; x2 += k;
//		} else {
//			x1 += k; x2 -= k;}
		int distx12 = x2 - x1;
//		if (y1 < y2) {
//			y1 -= k; y2 += k;
//		} else {
//			y1 += k; y2 -= k;}
		int disty12 = y2 - y1;
//		if (x3 < x4) {
//			x3 -= k; x4 += k;
//		} else {
//			x3 += k; x4 -= k;}
		int distx43 = x4 - x3;
//		if (y3 < y4) {
//			y3 -= k; y4 += k;
//		} else {
//			y3 += k; y4 -= k;}
		int disty43 = y4 - y3;
		
		int segs = (int)( (Math.sqrt(distx12 * distx12 + disty12 * disty12) +  Math.sqrt(distx43 * distx43 + disty43 * disty43)) / 2);
		for (int i = 0; i <= segs; i++) {
			cleanLine((int) (x1 + (float)distx12 / segs * i), (int) (y1 + (float)disty12 / segs * i),
					(int) (x3 + (float)distx43 / segs * i), (int) (y3 + (float)disty43 / segs * i));
		}
	}
	
	void cleanLine(int x1, int y1, int x2, int y2) {
		int distx = x2 - x1;
		int disty = y2 - y1;
		int segs = (int)(Math.sqrt(distx * distx + disty * disty));
		for (int i = 1; i <= segs; i++)
			clean((int) (x1 + (float)distx / segs * i), (int) (y1 + (float)disty / segs * i));
	}
	
	void clean(int x, int y) {
		for (int i = x - 4; i <= x + 4; i++)
			for (int j = y - 4; j <= y + 4; j++) {
					diffs[i][j] = 0;
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
				    
				    // Draw margins of pitch.
				    g.setColor(Color.ORANGE);
				    g.drawLine(left, top, right, top);
				    g.drawLine(left, bottom, right, bottom);
				    g.drawLine(left, top, left, bottom);
				    g.drawLine(right, top, right, bottom);
				    
				    // Draw original margins that were detected
				    g.setColor(new Color(.3f, .4f, .5f, .8f));
				    for (int column = 0; column < width; column++)
				    	for (int row = 0; row < height; row++)
							if (diffs[column][row] == 1)
								g.drawRect(column, row, 1, 1);
				    
				    // Draw robot points
				    g.setColor(Color.RED);
				    if (robot1 != null) {
				    	g.drawRect(robot1[0], robot1[1], 3, 3);
				    	g.drawRect(robot1[2], robot1[3], 2, 2);
				    	g.drawRect(robot1[4], robot1[5], 1, 1);
				    	g.drawRect(robot1[6], robot1[7], 1, 1);
				    }
				    
				    // Test
//				    g.setColor(Color.PINK);
//				    g.drawRect(ax, ay, 3, 3);
//				    g.drawRect(bx,  by,  2,  2);
//				    g.setColor(Color.GREEN);
//				    g.drawRect(cx, cy, 1, 1);
//				    g.drawRect(dx, dy, 1, 1);
//				    g.setColor(Color.BLUE);
//				    g.drawRect(ex, ey, 1, 1);
//				    g.drawRect(fx, fy, 1, 1);
			 }
		});
	}

}

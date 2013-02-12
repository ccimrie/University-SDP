package vision.edgevision.mona;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Run {
	// comments are gibberish !!! beware !!!
	static JLabel label;
	static JFrame frame;
	static int width, height;
	static int t, b, l, r;
	
	static int ax, ay, bx, by, gx, gy, hx, hy, ang;
	static boolean found = false;

	public static void main(String args[]) {
		
		// UNCOMENT THIS IF YOU WANT TO USE ANOTHER IMAGE FILE
//		System.out.print("Insert a image name:");
//		Scanner scan = new Scanner(System.in);
//		String fileName = scan.next();
//		if (fileName.equals("a"))
//			fileName = "saved.png";
		File file = new File("saved.png");
		try {
			BufferedImage img = ImageIO.read(file);
			width = img.getWidth();
			height = img.getHeight();
			l = 45;
			r = width - 45;
			t = height - 70;
			b = 100;
			System.out.print("Loaded image of the following dimensions: " + width + ";  " + height);
			processImage(img);
		} catch (IOException e) {
			System.out.print("Failed to load image!");
		}
	}
	
	static void processImage(final BufferedImage img) {
		int[][][] vals = new int[height][width][3];
		final int[][] diffs = new int[height][width];
		final int[][] proc = new int[height][width];
		for (int row = 0; row < img.getHeight(); row++)
			for (int column = 0; column < img.getWidth(); column++)
				diffs[row][column] = 0;
			
		// vals[a][b][c] = pixel found on row a and column b, c = 0 -> red; c = 1 -> green; c = 2 -> blue
		for (int row = 0; row < img.getHeight(); row++) {
			for (int column = 0; column < img.getWidth(); column++) {			
				Color c = new Color(img.getRGB(column, row));		
				vals[row][column][0] = c.getRed();
				vals[row][column][1] = c.getGreen();
				vals[row][column][2] = c.getBlue();
			}
		}
		
		// On every row, find column variations
		for (int row = b; row < t; row++) {
			for (int column = l; column < r; column++) {
				int dist = (int)Math.sqrt(
						Math.pow(vals[row][column][0] - vals[row][column + 1][0], 2) + 
						Math.pow(vals[row][column][1] - vals[row][column + 1][1], 2) + 
						Math.pow(vals[row][column][2] - vals[row][column + 1][2], 2) 
						);
				if (dist > 16) {
					diffs[row][column] = 1;
					diffs[row][column + 1] = 1;
				}
			}
		}
		
		// On every column, find row varitaion
		for (int column = l; column < r; column++) {
			for (int row = b; row < t; row++) {
				int dist = (int)Math.sqrt(
						Math.pow(vals[row][column][0] - vals[row + 1][column][0], 2) + 
						Math.pow(vals[row][column][1] - vals[row + 1][column][1], 2) + 
						Math.pow(vals[row][column][2] - vals[row + 1][column][2], 2) 
						);
				if (dist > 15) {
					diffs[row][column] = 1;
					diffs[row + 1][column] = 1;
				}
			}
		}
		System.out.print("before");
		// Remove outliers
		for (int row = b; row < t; row++) {
			for (int column = l; column < r; column++) {
				if (diffs[row][column] == 1) {
					int k = 0;
					
					if (diffs[row - 2][column - 2] == 1) k++;
					if (diffs[row - 2][column - 1] == 1) k++;
					if (diffs[row - 2][column] == 1) k++;
					if (diffs[row - 2][column + 1] == 1) k++;
					if (diffs[row - 2][column + 2] == 1) k++;
					
					if (diffs[row - 1][column - 2] == 1) k++;
					if (diffs[row - 1][column - 1] == 1) k++;
					if (diffs[row - 1][column] == 1) k++;
					if (diffs[row - 1][column + 1] == 1) k++;
					if (diffs[row - 1][column + 2] == 1) k++;
					
					if (diffs[row][column - 2] == 1) k++;
					if (diffs[row][column - 1] == 1) k++;
					if (diffs[row][column + 1] == 1) k++;
					if (diffs[row][column + 2] == 1) k++;
					
					if (diffs[row + 1][column - 2] == 1) k++;
					if (diffs[row + 1][column - 1] == 1) k++;
					if (diffs[row + 1][column] == 1) k++;
					if (diffs[row + 1][column + 1] == 1) k++;
					if (diffs[row + 1][column + 2] == 1) k++;
					
					if (diffs[row + 2][column - 2] == 1) k++;
					if (diffs[row + 2][column - 1] == 1) k++;
					if (diffs[row + 2][column] == 1) k++;
					if (diffs[row + 2][column + 1] == 1) k++;
					if (diffs[row + 2][column + 2] == 1) k++;

					if (k < 4)
						diffs[row][column] = 0;
				}
			}
		}
		System.out.print("after");
		
		// we keep in proc what still needs to be processed out of diff
		for (int row = 0; row < img.getHeight(); row++)
			for (int column = 0; column < img.getWidth(); column++)
				if (diffs[row][column] == 1)
					proc[row][column] =1;
				else
					proc[row][column] = 0;
		
		// Find a large line -- take points from left to right and look from 5 to 5 angles 
		// to have a point at 10 , at 20 and at 40 away on the same line
		for (int column = l; column < r; column++) {
			for (int row = b; row < t; row++) {
				if (diffs[row][column] == 1 && proc[row][column] == 1) {
					// look in a circle of radius one for existing points at dist 10
					ax = column;
					ay = row;	
					for (int i = 0; i < 360; i ++) {
						if (diffs[(int)(row + 10 * Math.sin(i))][(int)(column + 10 * Math.cos(i))] == 1) {
							// try for other distances
							int kk = 0;
							for (int a = 1; a <= 15; a++)
								if (diffs[(int)(row + (10 + a * 2) * Math.sin(i))][(int)(column + (10 + a * 2) * Math.cos(i))] == 1) kk++;
							
							if (kk >= 12) {
								ang = i;
								bx = (int)(column + 45 * Math.cos(i));
								by = (int)(row + 45 * Math.sin(i));
								i = 370;
								
								// Check if there are points on the two parallel lines
								double d = (float)(ay - by) / (ax - bx);
								int cx = (int) (ax + 30 * Math.cos(d + Math.PI / 2));
								int cy = (int) (ay + 30 * Math.sin(d + Math.PI / 2));
								int dx = (int) (bx + 30 * Math.cos(d + Math.PI / 2));
								int dy = (int) (by + 30 * Math.sin(d + Math.PI / 2));
							    
								int kkkk = 0;
								int distx, disty;
								distx = cx - dx;
								disty = cy - dy;
								for (int a = 1; a < 15; a++) {
									try {
										if (diffs[(int) (dx + (float)distx / 15 * a)][(int) (dy + (float)disty / 15 * a)] == 1) kkkk++;
									} catch (ArrayIndexOutOfBoundsException e){
										System.out.print("error: " + (int) (dx + (float)distx / 15 * a) + "  " + (int) (dy + (float)disty / 15 * a));
									}
								}
								if (kkkk >= 8) {
									gx = cx;
									gy = cy;
									hx = dx;
									hy = dy;
									found  = true;
								}
									
								int ex = (int) (ax - 30 * Math.cos(d + Math.PI / 2));
								int ey = (int) (ay - 30 * Math.sin(d + Math.PI / 2));
								int fx = (int) (bx - 30 * Math.cos(d + Math.PI / 2));
								int fy = (int) (by - 30 * Math.sin(d + Math.PI / 2));
							    
								kkkk = 0;
								distx = ex - fx;
								disty = ey - fy;
								for (int a = 1; a < 15; a++){
									try {
										if (diffs[(int) (ex - (float)distx / 15 * a)][(int) (ey - (float)disty / 15 * a)] == 1) kkkk++;
									} catch (ArrayIndexOutOfBoundsException e){
										System.out.print("error: " + (int) (ex - (float)distx / 15 * a) + "  " + (int) (ey - (float)disty / 15 * a));
									}
								}
								
								if (kkkk >= 8) {
									gx = ex;
									gy = ey;
									hx = fx;
									hy = fy;
									found  = true;
								}
							}
						}
					}
				// if no line was found make all the circle around the point 0 in proc
					if (found == false) {
						for (int n = column - 24; n <= column + 24; n ++)
							for (int m = row- 24; m <= row + 24; m ++)
								proc[m][n] = 0;
					}
						
				}
			}
		}
		
		
		frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Image processing");
		frame.setVisible(true);
		frame.getContentPane().add(new JPanel(){
			 public void paintComponent(Graphics g) {
				    super.paintComponent(g);
				    g.drawImage(img, 0, 0, width, height, null);
				    g.setColor(Color.ORANGE);
				    g.drawLine(l, t, r, t);
				    g.drawLine(l, b, r, b);
				    g.drawLine(l, t, l, b);
				    g.drawLine(r, t, r, b);
				    
//				    g.drawLine(540, 225, 590, 225); // length of robot is 45
				    
				    System.out.println("points: " + ax + "  " + ay +";   " + bx + "   " + by );
				    
				    g.setColor(Color.DARK_GRAY);
				    for (int row = 0; row < img.getHeight(); row++) {
						for (int column = 0; column < img.getWidth(); column++) {
							if (diffs[row][column] != 0){
								g.drawRect(column, row, 1, 1);
							}
								
						}
					}
				    g.setColor(Color.GREEN);
				    g.drawRect(ax, ay, 2, 2);
				    g.drawRect(bx, by, 1, 1);
				    g.drawRect(gx, gy, 1, 1);
				    g.drawRect(hx, hy, 1, 1);
				    
//				    double d = (float)(ay - by) / (ax - bx);
//					int cx = (int) (ax + 30 * Math.cos(d + Math.PI / 2));
//					int cy = (int) (ay + 30 * Math.sin(d + Math.PI / 2));
//					int dx = (int) (bx + 30 * Math.cos(d + Math.PI / 2));
//					int dy = (int) (by + 30 * Math.sin(d + Math.PI / 2));
//				    g.drawRect(cx , cy, 1, 1);
//				    g.drawRect(dx , dy, 1, 1);
//				    
//					int kkkk = 0;
//					int distx, disty;
//					distx = cx - dx;
//					disty = cy - dy;
//					for (int a = 1; a <= 15; a++)
//						g.drawRect((int) (dx + (float)distx / 15 * a), (int) (dy + (float)disty / 15 * a) , 1, 1);
//					
//					int ex = (int) (ax - 30 * Math.cos(d + Math.PI / 2));
//					int ey = (int) (ay - 30 * Math.sin(d + Math.PI / 2));
//					int fx = (int) (bx - 30 * Math.cos(d + Math.PI / 2));
//					int fy = (int) (by - 30 * Math.sin(d + Math.PI / 2));
//					
//					g.drawRect(ex , ey, 1, 1);
//				    g.drawRect(fx , fy, 1, 1);
//				    
//					kkkk = 0;
//					distx = ex - fx;
//					disty = ey - fy;
//					for (int a = 1; a <= 15; a++)
//						g.drawRect((int) (ex - (float)distx / 15 * a), (int) (ey - (float)disty / 15 * a) , 1, 1);
			 }
		});
	}
}

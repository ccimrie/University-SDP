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

public class RunTwo {
	// comments are gibberish !!! beware !!!
	static JLabel label;
	static JFrame frame;
	static int width, height;
	static int t, b, l, r;
	


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
				if (dist > 14) {
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
				    

				    
				    g.setColor(Color.DARK_GRAY);
				    for (int row = 0; row < img.getHeight(); row++) {
						for (int column = 0; column < img.getWidth(); column++) {
							if (diffs[row][column] != 0){
								g.drawRect(column, row, 1, 1);
							}
								
						}
					}
				    g.setColor(Color.GREEN);

			 }
		});
	}
}

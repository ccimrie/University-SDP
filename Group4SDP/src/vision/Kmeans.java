package vision;
import java.util.ArrayList;

public class Kmeans {
	
	public int [] dokmeans(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int [] mean1, int [] mean2){
		int [] mean1old = mean1;
		int [] mean2old = mean2;
		int [] mean1new = {0,0};
		int [] mean2new = {0,0};
		int [] newmeans = new int[4];//The means from the new iteration the ones that we will return.
		while ((mean1old != mean1new)&& (mean2old != mean2new)){
			mean1old = mean1new;
			mean2old = mean2new;
			int [] clusters = getclusters(xpoints, ypoints, mean1, mean2);
			newmeans = getnewmeans(xpoints,ypoints,clusters);
			int [] temp1 = {newmeans[0],newmeans[1]};
			int [] temp2 = {newmeans[2],newmeans[3]};
			mean1new = temp1;
			mean2new = temp2;
		}
		return newmeans;
	}
	
	//Position in the returned arraylist will correspond to the nth point in the original array lists and will be
	//either 1 or 2, depending of the cluster
	public int [] getclusters(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int [] mean1, int [] mean2){
		int pixelnum = xpoints.size();
		int [] ret = new int[pixelnum]; // The cluster assignment of each element.
		for (int i = 0; i<pixelnum; i++){
			int xcurrent = xpoints.get(i);
			int ycurrent = ypoints.get(i);
			double mean1dist = getdistance(xcurrent,ycurrent,mean1);
			double mean2dist = getdistance(xcurrent,ycurrent,mean2);
			if (mean1dist>mean2dist){
				ret[i] = 1;
			}else{
				ret[i] = 2;
			}
		}
		return ret;
	}

	
	public int[] getnewmeans(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int [] clustermembers){
		ArrayList<Integer>mean1membersx = new ArrayList<Integer>();
		ArrayList<Integer>mean1membersy = new ArrayList<Integer>();
		ArrayList<Integer>mean2membersx = new ArrayList<Integer>();
		ArrayList<Integer>mean2membersy = new ArrayList<Integer>();
		int pixelnum = clustermembers.length;
		for (int i = 0; i<pixelnum; i++){
			if (clustermembers[i] == 1){
				mean1membersx.add(xpoints.get(i));
				mean1membersy.add(ypoints.get(i));
			} else {
				mean2membersx.add(xpoints.get(i));
				mean2membersy.add(ypoints.get(i));
			}
		}
		int [] newmean1 = findmeans(mean1membersx, mean1membersy);
		int [] newmean2 = findmeans(mean1membersx, mean1membersy);
		//4 elements, first 2 are the x and y of the first mean, second two - of the second mean.
		int [] ret = {newmean1[0],newmean1[1],newmean2[0],newmean2[1]};
		return ret;
	}
	
	//Get the mean for the given points.
	//means is an array in the format {xcenter, ycenter};
	public int [] findmeans( ArrayList<Integer> xpoints, ArrayList<Integer> ypoints){
		int pixelnum = xpoints.size();

		int xpointssum = 0;
		int ypointssum = 0;
		for (int i = 0; i<pixelnum; i++){
			xpointssum += xpoints.get(i);
			ypointssum += ypoints.get(i);
		}
		int meanx = xpointssum/pixelnum;
		int meany = ypointssum/pixelnum;
		int[]means = {meanx,meany};
		return means;

	}
	
	public int[] getinitialmeans(int xpoints, int ypo){
		return null;
	}

	public double sumsquarederror(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int[] center){
		double sumsqerr = 0.0;
		int pixelnum = xpoints.size();
		for (int i = 0; i<pixelnum; i++){
			sumsqerr += getdistance(xpoints.get(i), ypoints.get(i), center);
		}
		return Math.sqrt(sumsqerr);
	}
	
	// mean[0] = x, mean[1] = y
	public double getdistance(int x, int y, int [] mean){
		double distance =Math.sqrt(Math.pow((mean[0] - x),2) + Math.pow((mean[1] - y),2));
		return distance;
	}
}

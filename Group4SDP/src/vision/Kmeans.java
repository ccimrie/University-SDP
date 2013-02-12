package vision;
import java.util.ArrayList;
import vision.Cluster;

public class Kmeans {
	
	public static final double errortarget = 170.0;
	
	public static int [] dokmeans(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int [] mean1, int [] mean2){
		Cluster iteration; //All the information about this iteration is kept here.
		//We set the initial errors to some number that's not going to interfere with our condition.
		double error1new = 200.0;
		double error2new = 200.0;
		int [] mean1old = mean1;
		int [] mean2old = mean2;
		int [] mean1new = {0,0};
		int [] mean2new = {0,0};
		
		//We iterate until we converge or until we get small enough of an error for our clusters (:
		while ((mean1old != mean1new)&& (mean2old != mean2new) && ((error1new>errortarget) || (error2new>errortarget))){
			mean1old = mean1new;
			mean2old = mean2new;
			iteration = getclusters(xpoints, ypoints, mean1, mean2);
			mean1new = iteration.getmean(1);
			mean2new = iteration.getmean(2);
			error1new = sumsquarederror(iteration.getcluster(1, 'x'), iteration.getcluster(1, 'y'), mean1new);
			error2new = sumsquarederror(iteration.getcluster(2, 'x'), iteration.getcluster(2, 'y'), mean2new);
		}
		int [] newmeans = {mean1new[0], mean1new[1], mean2new[0], mean2new[1]};
		return newmeans;
	}
	
	//Position in the returned arraylist will correspond to the nth point in the original array lists and will be
	//either 1 or 2, depending of the cluster
	public static Cluster getclusters(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int [] mean1, int [] mean2){
		
		//Clusters
		ArrayList<Integer>mean1membersx = new ArrayList<Integer>();
		ArrayList<Integer>mean1membersy = new ArrayList<Integer>();
		ArrayList<Integer>mean2membersx = new ArrayList<Integer>();
		ArrayList<Integer>mean2membersy = new ArrayList<Integer>();
		
		int pixelnum = Math.max(xpoints.size(), ypoints.size());
		for (int i = 0; i<pixelnum; i++){
			int xcurrent = xpoints.get(i);
			int ycurrent = ypoints.get(i);
			double mean1dist = getdistance(xcurrent,ycurrent,mean1);
			double mean2dist = getdistance(xcurrent,ycurrent,mean2);
			//Add the points to the appropriate clusters.
			if (mean1dist>mean2dist){
				mean1membersx.add(xcurrent);
				mean1membersy.add(ycurrent);
			}else{
				mean2membersx.add(xcurrent);
				mean2membersy.add(ycurrent);
			}
		}
		//Get the new means using our clusters.
		int [] newmean1 = findmeans(mean1membersx, mean1membersy);
		int [] newmean2 = findmeans(mean1membersx, mean1membersy);
		Cluster ret = new Cluster(mean1membersx,mean1membersy,mean2membersx,mean2membersy, newmean1, newmean2);
		return ret;
	}

	
	//Get the mean for the given points.
	//means is an array in the format {xcenter, ycenter};
	public static int [] findmeans( ArrayList<Integer> xpoints, ArrayList<Integer> ypoints){
		int pixelnum = Math.max(xpoints.size(), ypoints.size());

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
	
	public static double sumsquarederror(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, int[] center){
		double sumsqerr = 0.0;
		int pixelnum = Math.max(xpoints.size(),ypoints.size());
		for (int i = 0; i<pixelnum; i++){
			sumsqerr += getdistance(xpoints.get(i), ypoints.get(i), center);
		}
		return Math.sqrt(sumsqerr);
	}
	
	// mean[0] = x, mean[1] = y
	public static double getdistance(int x, int y, int [] mean){
		double distance =Math.sqrt(Math.pow((mean[0] - x),2) + Math.pow((mean[1] - y),2));
		return distance;
	}
}

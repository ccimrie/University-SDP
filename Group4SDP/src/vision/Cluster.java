package vision;
import java.util.ArrayList;
public class Cluster {
	private ArrayList<Integer> cluster1membersx = new ArrayList<Integer>();
	private ArrayList<Integer> cluster1membersy = new ArrayList<Integer>();
	private ArrayList<Integer> cluster2membersx = new ArrayList<Integer>();
	private ArrayList<Integer> cluster2membersy = new ArrayList<Integer>();
	private int [] mean1;
	private int [] mean2;
	
	public Cluster(ArrayList<Integer> cluster1membersx, ArrayList<Integer> cluster1membersy, 
			ArrayList<Integer> cluster2membersx, ArrayList<Integer> cluster2membersy,
			int [] mean1, int [] mean2){
		this.cluster1membersx = cluster1membersx;
		this.cluster1membersx = cluster1membersy;
		this.cluster2membersx = cluster2membersx;
		this.cluster2membersy = cluster2membersy;
		this.mean1 = mean1;
		this.mean2 = mean2;
	}


	//set and get the mean.
	
	public int [] getmean(int num){
		if (num == 1){
			return mean1;
		}else{
			return mean2;
		}
	}
	
	public void etmean(int num, int [] mean){
		if (num == 1){
			mean1 = mean;
		}else{
			mean2 = mean;
		}
	}
	
	//Set and get where num could be 1 or 2 and letter x or y;
	
	public ArrayList<Integer> getcluster(int num, char letter){
		if (num == 1){
			if(letter == 'x'){
				return cluster1membersx;
			}else{
				return cluster1membersy;
			}
		}else{
			if(letter == 'x'){
				return cluster2membersx;
			}else{
				return cluster2membersy;
			}	
		}
	}
	
	public void setcluster(int num, char letter,ArrayList<Integer> cluster){
		if (num == 1){
			if(letter == 'x'){
				cluster1membersx = cluster;
			}else{
				cluster1membersy = cluster;
			}
		}else{
			if(letter == 'x'){
				cluster2membersx = cluster;
			}else{
				cluster2membersy = cluster;
			}	
		}
	}
	
}

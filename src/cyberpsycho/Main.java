package cyberpsycho;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	
	
	public static void main(String[] args) throws Exception 
	{
		//System.out.println("Hello world");
		//ArrayList<ArrayList<String>> data =  Data.readData();
		//System.out.println("Hello world");
		
		HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");
		double ex = Data.computeEx("2", 1, 5, strategy);
		
	}
	

}

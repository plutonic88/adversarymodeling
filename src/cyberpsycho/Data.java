package cyberpsycho;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class Data {
	
	
	public enum Headers {
		instance(0),round(1),Subject_ID(2),user_id(3),id_assignedgames(4),id_answers(5),id_endsurveys(6),Mean_Inst(7),N_Inst(8),SD_Inst(9),
		Sum_Inst(10),m1(11),m2(12),m3(13),m4(14),m5(15),m6(16),m7(17),m8(18),m9(19),n1(20),
		n2(21),n3(22),n4(23),n5(24),n6(25),n7(26),n8(27),n9(28),p1(29),p2(30),p3(31),p4(32),p5(33),p6(34),p7(35),
		p8(36),p9(37),gender(38),age(39),country(40),race(41),education(42),income(43),device(44),comment(45),game_type(46),
		pick_def_order(47),game_played(48),random_defender_type(49),max_defender_type(50),game_id(51),total_point(52),user_confirmation(53),defender_points(54),
		created_at_assignedsurveys(55),created_at_answers(56),updated_at_answers(57),created_at_endsurveys(58),
		updated_at_endsurveys(59),id(60),defender_action(61),time_defender_moved(62),attacker_action(63),time_attacker_moved(64),
		attacker_points(65),created_at_date(66),created_at_time(67),updated_at_date(68),updated_at_time(69);
		
		private final int value;
		
		private Headers(int value)
		{
	        this.value = value;
	    }
		
		public int getValue() {
	        return value;
	    }
	}
	
	
	/**
	 * example.add(user_id);
				example.add(game_id);
				example.add(game_instance);
				example.add(game_type);
				example.add(pick_def_order);
				example.add(game_played);
				
				
				example.add(round);
				example.add(defender_action);
				example.add(attacker_action);
				example.add(attacker_points);
				example.add(total_points);
	 * @author anjonsunny
	 *
	 */
	
	public enum Headers_minimum {
		user_id(0), game_id(1), game_instance(2),game_type(3), pick_def_order(4), game_played(5), round(6), defender_action(7), attacker_action(8), attacker_points(9), total_points(10), m1(11),m2(12),m3(13),m4(14),m5(15),m6(16),m7(17),m8(18),m9(19),n1(20),
		n2(21),n3(22),n4(23),n5(24),n6(25),n7(26),n8(27),n9(28),p1(29),p2(30),p3(31),p4(32),p5(33),p6(34),p7(35),
		p8(36),p9(37);
		
		private final int value;
		
		private Headers_minimum(int value)
		{
	        this.value = value;
	    }
		
		public int getValue() {
	        return value;
	    }
	}


	/**
	 * for the MTurk data
	 * @return
	 */
	public static ArrayList<ArrayList<String>> readData()
	{
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

		try {

			Reader in = new FileReader("data/Full_long.csv");
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(Headers.class).parse(in);
			for (CSVRecord record : records) 
			{
				ArrayList<String> example = new ArrayList<String>();
				String user_id = record.get(Headers.user_id);
				//System.out.println(user_id);
				
				
				String game_id = record.get(Headers.game_id);
				String game_instance = record.get(Headers.instance);
				String game_type = record.get(Headers.game_type);
				String pick_def_order = record.get(Headers.pick_def_order);
				String game_played = record.get(Headers.game_played);
				
				
				
				String round = record.get(Headers.round);
				String defender_action = record.get(Headers.defender_action);
				String attacker_action = record.get(Headers.attacker_action);
				String attacker_points = record.get(Headers.attacker_points);
				String total_points = record.get(Headers.total_point);
				
				
				/*ArrayList<String> m = new ArrayList<String>();
				for(int i=11; i<=19; i++)
				{
					m.add(record.get(i));
				}*/
				
				
				/*ArrayList<String> n = new ArrayList<String>();
				for(int i=20; i<=28; i++)
				{
					n.add(record.get(i));
				}*/
				
				
				/*ArrayList<String> p = new ArrayList<String>();
				for(int i=29; i<=37; i++)
				{
					p.add(record.get(i));
				}*/
				
				
				
				
				example.add(user_id);
				example.add(game_id);
				example.add(game_instance);
				example.add(game_type);
				example.add(pick_def_order);
				example.add(game_played);
				
				
				example.add(round);
				example.add(defender_action);
				example.add(attacker_action);
				example.add(attacker_points);
				example.add(total_points);
				
				//ArrayList<String> m = new ArrayList<String>();
				for(int i=11; i<=19; i++)
				{
					example.add(record.get(i));
				}
				
				//ArrayList<String> n = new ArrayList<String>();
				for(int i=20; i<=28; i++)
				{
					//n.add(record.get(i));
					example.add(record.get(i));
				}
				
				//ArrayList<String> p = new ArrayList<String>();
				for(int i=29; i<=37; i++)
				{
					//p.add(record.get(i));
					example.add(record.get(i));
				}
				
				
				
				data.add(example);
				

			}
			return data;

		}
		catch(Exception ex)
		{

		}

		return null;

	}
	
	
	
	/**
	 * read data for tests
	 * @param lambda
	 * @return
	 */
	public static ArrayList<ArrayList<String>> readData(double lambda)
	{
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

		try {

			Reader in = new FileReader("result/lambda"+lambda+".csv");
			Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
			for (CSVRecord record : records) 
			{
				ArrayList<String> example = new ArrayList<String>();
				
				String userid = record.get(0);
			    String action = record.get(1);
			    String reward = record.get(2);
			    
			    example.add(userid);
			    example.add(action);
			    example.add(reward);
			    
			    data.add(example);
				

			}
			return data;

		}
		catch(Exception ex)
		{

		}

		return null;

	}
	
	
	/**
	 * 
	 * @param filename
	 * @return strategy def_seq.att_seq -> (Node->prob)
	 */
	public static HashMap<String, HashMap<String, Double>> readStrategy(String filename)
	{
		HashMap<String, HashMap<String, Double>> strategy = new HashMap<String, HashMap<String, Double>>();
		
		
		HashMap<String , String> nodes = new HashMap<String , String>();
		nodes.put("N0", "0");
		nodes.put("N1", "1");
		nodes.put("N2", "2");
		nodes.put("N3", "3");
		nodes.put("N4", "4");
		nodes.put("PASS", "5");
		
		
		
		
		
		
		
		try{
			FileInputStream fstream = new FileInputStream("data/"+filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			ArrayList<String> unparseddata = new ArrayList<String>();

			
			while ((strLine = br.readLine()) != null)  
			{
				unparseddata.add(strLine);
			}
			
			

			for (int i=0; i<unparseddata.size(); )  
			{
				
				if(i==39614)
				{
					System.out.println("hi");
				}
				
				String def_seq = "";
				String att_seq = "";


				if(unparseddata.get(i).equals(""))
				{
					break;
				}



					while(true)
					{

							String row = unparseddata.get(i);

							if(row.charAt(11) == ']') // empty sequence
							{
								if(row.charAt(0) == 'D')
								{
									def_seq = "";
								}
								else if(row.charAt(0) == 'A')
								{
									att_seq = "";
									i++;
									break;
								}
							}
							else if(row.charAt(10) == '[')
							{
								//parse  action sequence
								int index1 = 11; // starting index for sequence
								
								String subs = row.substring(index1, row.lastIndexOf(']'));
								
								//System.out.println("Action sequence "+ subs);
								
								if(row.charAt(0) == 'D')
								{
									def_seq = subs;
								}
								else if(row.charAt(0) == 'A')
								{
									att_seq = subs;
									i++; // move to next row
									break; // exit from loop
								}

							}
							i++; // move to next row
						
					}
					


					 def_seq = def_seq.replace(" ", "");
					 att_seq = att_seq.replace(" ", "");
					 
					// System.out.println("Def Action sequence "+ def_seq);
					// System.out.println("Att Action sequence "+ att_seq);
					 
					// System.out.println();
					 

					
					 
					HashMap<String, Double> bhv_strat = new HashMap<String, Double>();
					
					while(true)
					{
						if(i==unparseddata.size())
						{
							break;
						}

						// get the current row
						String row = unparseddata.get(i);

						if( (row.equals(""))   || (row.charAt(0) == 'D'))
						{
							break;
						}

						row =  row.replace(" ", "");

						String[] arr = row.split(":");
						
						arr[0] = arr[0].replace("\t", "");


						String action = nodes.get(arr[0].substring(0)) ;

						double prob = Double.parseDouble(arr[1]) ;

						
						//System.out.println(action+":"+prob);

						bhv_strat.put(action, prob);


						i++;
						//System.out.println("i :"+ i);
						//counter++;
					}
					
					
					String def_nodes_seq[] = def_seq.split(",");
					String att_nodes_seq[] = att_seq.split(",");
					
					
					for(int k=0; k<def_nodes_seq.length; k++)
					{
						def_nodes_seq[k] = nodes.get(def_nodes_seq[k]);
						
					}
					
					for(int k=0; k<att_nodes_seq.length; k++)
					{
						att_nodes_seq[k] = nodes.get(att_nodes_seq[k]);
						
					}
					
					def_seq =  String.join(",", def_nodes_seq);
					att_seq =  String.join(",", att_nodes_seq);
					
					
					if(def_seq.equals("null") || att_seq.equals("null"))
					{
						strategy.put("EMPTY"+" "+"EMPTY", bhv_strat);
					}
					else
					{
						strategy.put(def_seq+" "+att_seq, bhv_strat);
					}
					
					
					
					//System.out.println();
				
				
				
			}
			in.close();
		}catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
		
		return strategy;
		
	}
	
private static Instances generateInstance(int iter, int k, double[][] examples) {
		
		
		try {
		
		

		 CSVLoader csvload = new CSVLoader();
		 csvload.setSource(new File("result/realdata"+iter+".csv"));
		 Instances data = csvload.getDataSet();
		 
		 
		 ArffSaver arf = new ArffSaver();
		 arf.setInstances(data);
		 
		 File f = new File("result/newdata"+iter+".arff");
		 
		 if(f.exists())
		 {
			 f.delete();
			 f.createNewFile();
		 }
		 
		 arf.setFile(f);
		 arf.writeBatch();
		
		
		
		
		
		
		
		FileInputStream fstream = new FileInputStream("result/newdata"+iter+".arff");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		// Read all the instances in the file (ARFF, CSV, XRFF, ...)
		 //DataSource source = new DataSource(br);
		 Instances instances = new Instances(br);
		 return instances;
		 // Print header and instances.
		// System.out.println("\nDataset:\n");
		/* System.out.println(instances.toSummaryString());
		 
		 SimpleKMeans model = new SimpleKMeans();
		 model.setNumClusters(10);
		 model.buildClusterer(instances);
		 model.setDistanceFunction(new weka.core.ManhattanDistance());
		 System.out.println(model);
		 //MakeDensityBasedClusterer dc = new MakeDensityBasedClusterer();
		 EM dc = new EM();
		// instances.remove(0); // remove the base
		 dc.setNumClusters(k-1);
		 dc.buildClusterer(instances);
		 System.out.println(dc);*/
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		return null;
	}
	
	
	
	
}

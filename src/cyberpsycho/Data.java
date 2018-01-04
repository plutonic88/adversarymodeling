package cyberpsycho;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class Data {
	
	
	public enum Headers {
		instance,round,Subject_ID,user_id,id_assignedgames,id_answers,id_endsurveys,Mean_Inst,N_Inst,SD_Inst,Sum_Inst,m1,m2,m3,m4,m5,m6,m7,m8,m9,n1,n2,n3,n4,n5,n6,n7,n8,n9,p1,p2,p3,p4,p5,p6,p7,p8,p9,gender,age,country,race,education,income,device,comment,game_type,pick_def_order,game_played,random_defender_type,max_defender_type,game_id,total_point,user_confirmation,defender_points,created_at_assignedsurveys,created_at_answers,updated_at_answers,created_at_endsurveys,updated_at_endsurveys,id,defender_action,time_defender_moved,attacker_action,time_attacker_moved,attacker_points,created_at_date,created_at_time,updated_at_date,updated_at_time
	}


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
					 
					 System.out.println("Def Action sequence "+ def_seq);
					 System.out.println("Att Action sequence "+ att_seq);
					 
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

						
						System.out.println(action+":"+prob);

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
					
					
					
					System.out.println();
				
				
				
			}
			in.close();
		}catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
		
		return strategy;
		
	}
	
	
	
	
}

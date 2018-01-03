package cyberpsycho;

import java.util.ArrayList;
import java.util.HashMap;

public class AdversaryModel {
	
	public static double computeEx(String action, int round, int LIMIT_ROUND, HashMap<String, HashMap<String, Double>> strategy)
	{
		double expectedutility = 0.0;
		
		/**
		 * find the sequences where action was played in round
		 */
		
		ArrayList<String> sequences = new ArrayList<String>();
		
		for(String seq: strategy.keySet())
		{
			String[] defatt = seq.split(" ");
			
			String nodes[] = defatt[1].split(",");
			
			if((nodes.length == (LIMIT_ROUND-1)) && nodes[round].equals(action))
			{
				sequences.add(seq);
				System.out.println("Adding "+ seq);
			}
		}
		
		
		/**
		 * for each of the sequences multiply the probabilities by ensuring that attacker also played the actions to reach the leaf nodes
		 */
		double expected_value = 0;
		for(String seq: sequences)
		{
			String sequence[] = seq.split(" ");
			String defender_seq = sequence[0];
			String attacker_seq = sequence[1];
			System.out.println(defender_seq + " "+attacker_seq +"\n");
			
			
			HashMap<String, Double> last_strat = strategy.get(seq);
			
			for(String lastaction: last_strat.keySet())
			{

				double sequence_prob = last_strat.get(lastaction);
				
				//System.out.println("initial seq prob "+ sequence_prob);
				
				//System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ",  prob "+ sequence_prob);

				// run the loop until last round
				
				for(int r=round+1; r < (LIMIT_ROUND-1); r++)
				{
					int roundindex = r*2;
					
					String tmp_def_seq = defender_seq.substring(0, roundindex-1);
					String tmp_att_seq = attacker_seq.substring(0, roundindex-1);
					
					//System.out.println("tmp defender sequence "+ tmp_def_seq + ", tmp attacker sequence "+tmp_att_seq );
					
					String r_action = defender_seq.substring(roundindex, roundindex+1);
					
					// find prob for r_action
					
					String key = tmp_def_seq + " "+ tmp_att_seq;
					
					if(strategy.containsKey(key))
					{

						HashMap<String, Double> tmp_strat = strategy.get(key);

						double prob = tmp_strat.get(r_action);

						//System.out.println("round "+ r + " action "+ r_action + " prob "+ prob);
						
						sequence_prob *= prob;
						
						//System.out.println("prob " +prob+", new seq prob "+ sequence_prob);
						
					}
					else
					{
						System.out.println("new seq does not exist ");
					}
					
					//System.out.println("hi");
					
					
					
				}
				
				System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ",  prob "+ sequence_prob);
				
				// now use the probability to multiply the reward
				// then add the expected value to the global sum

			}

			
			
			
			
			
			
		}
		
		
		
		
		
		
		
		return expectedutility;
	}

}

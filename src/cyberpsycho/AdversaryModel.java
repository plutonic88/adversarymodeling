package cyberpsycho;

import java.util.ArrayList;
import java.util.HashMap;

import cyberpsycho.Data.Headers_minimum;

public class AdversaryModel {



	/**
	 * 
	 * 
	 * @param action  the action for which the expected payoff to be computed
	 * @param round in which round the action needs to be played
	 * @param LIMIT_ROUND limit for round
	 * @param att_prevseq previously played sequence of actions by the attacker. Do I need the defender's sequence too ? yes I think so
	 * @param reward reward received for playing action in round
	 * @param strategy strategy of defender
	 * @return expected payoff if action is played in round
	 */
	public static double computeEx(String action, int round, int LIMIT_ROUND, String def_prevseq, String att_prevseq, double reward,HashMap<String, HashMap<String, Double>> strategy)
	{
		double expectedutility = 0.0;




		/**
		 * 
		 * 
		 * find the sequences where the previous sequence was played by attacker to reach the information set
		 */

		ArrayList<String> sequences = new ArrayList<String>();

		for(String seq: strategy.keySet())
		{
			String[] defatt = seq.split(" ");

			String nodes[] = defatt[1].split(",");



			if((nodes.length == (LIMIT_ROUND)) )
			{

				if(round>0)
				{
					String tmp_att_prev_seq = defatt[1].substring(0, round*2-1);
					String tmp_def_prev_seq = defatt[0].substring(0, round*2-1);

					String[] att_nodes = defatt[1].split(",");

					//System.out.println("tmp_def_prev_seq "+ tmp_def_prev_seq+"   tmp_att_prev_seq "+ tmp_att_prev_seq);

					//if(att_prevseq.compareTo(tmp_att_prev_seq)==0 && def_prevseq.compareTo(tmp_def_prev_seq) == 0)
					if(att_nodes[round].equals(action))
					{

						sequences.add(seq);
						System.out.println("Adding sequence "+ seq);
					}
				}
				else
				{
					sequences.add(seq);
					System.out.println("Adding sequence "+ seq);
				}
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
			System.out.println("\nSequence : "+defender_seq + "   "+attacker_seq +"*************");

			// last strategy for the sequence
			HashMap<String, Double> last_strat = strategy.get(seq);

			for(String lastaction: last_strat.keySet())
			{

				double sequence_prob = last_strat.get(lastaction);

				//System.out.println("initial seq prob "+ sequence_prob);

				System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ",initial prob "+ sequence_prob);

				// run the loop until last round

				for(int r=round; r < (LIMIT_ROUND); r++)
				{
					int roundindex = (r)*2;

					String tmp_def_seq = "";
					String tmp_att_seq = "";

					if(roundindex>0)
					{
						tmp_def_seq = defender_seq.substring(0, roundindex-1);
						tmp_att_seq = attacker_seq.substring(0, roundindex-1);
					}




					System.out.println("tmp defender sequence "+ tmp_def_seq + ", tmp attacker sequence "+tmp_att_seq );

					String r_action = defender_seq.substring(roundindex, roundindex+1);

					System.out.println("round "+ r + " r_action "+ r_action);

					// find prob for r_action

					if(tmp_def_seq.equals("") || tmp_att_seq.equals(""))
					{
						tmp_def_seq = "EMPTY";
						tmp_att_seq = "EMPTY";
					}

					String key = tmp_def_seq + " "+ tmp_att_seq;



					if(strategy.containsKey(key))
					{

						HashMap<String, Double> tmp_strat = strategy.get(key);

						double prob = tmp_strat.get(r_action);

						System.out.println("round "+ r + " action "+ r_action + " prob "+ prob);

						System.out.println("prev seq prob " +sequence_prob);

						sequence_prob *= prob;

						System.out.println("new seq prob "+ sequence_prob);

					}
					else
					{
						System.out.println("new seq does not exist ");
						System.out.println("prev seq prob " +sequence_prob+",\n new seq prob "+ sequence_prob);
					}

					//System.out.println("hi");



				}

				System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ", final prob "+ sequence_prob);

				//TODO

				// now use the probability to multiply the reward


				// then add the expected value to the global sum

			}

		}





		return expectedutility;
	}


	public static double computeExpectedPayoff(String action, int round, int LIMIT_ROUND, HashMap<String,String> att_sequences, HashMap<String,String> def_sequences, HashMap<String,Integer> rewards,HashMap<String, HashMap<String, Double>> strategy)
	{
		double expectedutility = 0.0;




		/**
		 * 
		 * 
		 * find the sequences where the previous sequence was played by attacker to reach the information set
		 */

		ArrayList<String> sequences = getAllSequences(att_sequences, strategy, LIMIT_ROUND);

		/**
		 * for each of the sequences multiply the probabilities by ensuring that attacker also played the actions to reach the leaf nodes
		 */
		double expected_value = 0;
		for(String seq: sequences)
		{
			String sequence[] = seq.split(" ");
			String defender_seq = sequence[0];
			String attacker_seq = sequence[1];
			
			String attacker_id = getAttackerId(att_sequences, attacker_seq);
			String total_attacker_seq = getTotalAttackerSeq(att_sequences, attacker_seq);
			
			
			System.out.println("\nSequence : "+defender_seq + "   "+attacker_seq +"*************");
			System.out.println("Attacker reward "+ rewards.get(attacker_id));

			// last strategy for the sequence
			HashMap<String, Double> last_strat = strategy.get(seq);

			for(String lastaction: last_strat.keySet())
			{

				double sequence_prob = last_strat.get(lastaction);

				//System.out.println("initial seq prob "+ sequence_prob);

				System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ",initial prob "+ sequence_prob);
				System.out.println("total Attacker sequence "+ total_attacker_seq);

				// run the loop until last round

				for(int r=round; r < (LIMIT_ROUND); r++)
				{
					int roundindex = (r)*2;

					String tmp_def_seq = "";
					String tmp_att_seq = "";

					if(roundindex>0)
					{
						tmp_def_seq = defender_seq.substring(0, roundindex-1);
						tmp_att_seq = attacker_seq.substring(0, roundindex-1);
					}




					System.out.println("tmp defender sequence "+ tmp_def_seq + ", tmp attacker sequence "+tmp_att_seq );

					String r_action = defender_seq.substring(roundindex, roundindex+1);

					System.out.println("round "+ r + " r_action "+ r_action);

					// find prob for r_action

					if(tmp_def_seq.equals("") || tmp_att_seq.equals(""))
					{
						tmp_def_seq = "EMPTY";
						tmp_att_seq = "EMPTY";
					}

					String key = tmp_def_seq + " "+ tmp_att_seq;



					if(strategy.containsKey(key))
					{

						HashMap<String, Double> tmp_strat = strategy.get(key);

						double prob = tmp_strat.get(r_action);

						System.out.println("round "+ r + " action "+ r_action + " prob "+ prob);

						System.out.println("prev seq prob " +sequence_prob);

						sequence_prob *= prob;

						System.out.println("new seq prob "+ sequence_prob);

					}
					else
					{
						System.out.println("new seq does not exist ");
						System.out.println("prev seq prob " +sequence_prob+",\n new seq prob "+ sequence_prob);
					}

					//System.out.println("hi");



				}

				System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ", final prob "+ sequence_prob);

				//TODO

				// now use the probability to multiply the reward


				// then add the expected value to the global sum

			}

		}





		return expectedutility;
	}

	private static String getTotalAttackerSeq(HashMap<String, String> att_sequences, String attacker_seq) {
		
		
		for(String id: att_sequences.keySet())
		{
			String seq = att_sequences.get(id);
			
			
			String tmpseq = seq.substring(0, seq.length()-2);
			
			if(tmpseq.equals(attacker_seq))
			{
				return seq;
			}
			
		}
		
		
		return null;
	}


	private static String getAttackerId(HashMap<String, String> att_sequences, String attacker_seq) {
		
		
		
		for(String id: att_sequences.keySet())
		{
			String seq = att_sequences.get(id);
			
			
			String tmpseq = seq.substring(0, seq.length()-2);
			
			if(tmpseq.equals(attacker_seq))
			{
				return id;
			}
			
		}
		
		
		return null;
	}


	private static ArrayList<String> getAllSequences(HashMap<String, String> att_sequences,
			HashMap<String, HashMap<String, Double>> strategy, int LIMIT_ROUND) {



		ArrayList<String> sequences = new ArrayList<String>();




		
			for(String seq: strategy.keySet())
			{
				String[] defatt = seq.split(" ");

				String nodes[] = defatt[1].split(",");



				if((nodes.length == (LIMIT_ROUND)) )
				{
					for(String a_seq: att_sequences.values())
					{
						String tmpseq = a_seq.substring(0, a_seq.length()-2);
						if(defatt[1].equals(tmpseq))
						{
							sequences.add(seq);
						}
					}

				}
			}
		

		return sequences;

	}


	public static void computeLambda(ArrayList<String> users_refined, HashMap<String, int[][]> att_game_play,
			HashMap<String,int[][]> def_game_play, HashMap<String,int[][]> reward, ArrayList<ArrayList<String>> data_refined, int gameinstance) {


		// use game instance 4


		/**
		 * things to compute
		 */

		// Nija : number of samples in information set ij for action a


		for(int r=0; r<5; r++)
		{
			for(int a=0; a<=5; a++)
			{
				int nija = computeNija(att_game_play, r, a, gameinstance, data_refined);
				System.out.println("r: "+r+", a: "+a+ " --> "+nija);
			}
			System.out.println();
		}
		System.out.println();


		// Nij number of samples for information set ij

		int nij = users_refined.size();




		// Uija : expected utility of action a in information set ij


		//read the strategy of defender
		HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");


		// String action, int round, int LIMIT_ROUND, String def_prevseq, String att_prevseq, double reward, HashMap<String, HashMap<String, Double>> strategy

		int LIMIT_ROUND = 4;
		int rewardij = 10;
		String def_prevseq = "0,1,0,1";
		String att_prevseq = "0,0,1,2";
		String att_action = "2";
		int r = 4;



		//double ex = AdversaryModel.computeEx(att_action,r, LIMIT_ROUND, def_prevseq, att_prevseq, rewardij ,strategy);




		for(int ij= 0; ij<5; ij++)
		{
			/**
			 * for each action compute  Nija*Uija and sum them
			 */
			double sum = 0;

			String[] actions = {"0", "1", "2", "3", "4", "5"};
			for(String a: actions)
			{
				// Nija
				int nija = computeNija(att_game_play, ij, Integer.parseInt(a), gameinstance, data_refined);

				//rewardij = computeReward();

				// sequences where 
				HashMap<String, String> att_sequences = computeAttackerSequences(a, ij, att_game_play, data_refined);
				HashMap<String, String> def_sequences = computeDefenderSequences(a, ij, def_game_play, data_refined);
				
				HashMap<String, Integer> rewards = computeRewards(att_sequences, data_refined);
				


				double uija = computeExpectedPayoff(a, ij, LIMIT_ROUND, att_sequences, def_sequences, rewards ,strategy);

			}
		}




	}

	private static HashMap<String, String> computeDefenderSequences(String a, int ij,
			HashMap<String, int[][]> game_play, ArrayList<ArrayList<String>> data_refined) {
		
		
		int gameinstance = 0;
		HashMap<String, String> sequences = new HashMap<String, String>();

		for(String user_id: game_play.keySet())
		{
			int[][] tmpplay = game_play.get(user_id);



			int deforder = getDefOrder(user_id,data_refined);

			if(deforder==0)
				gameinstance = 3;
			else if(deforder==1)
				gameinstance = 0;

			if( tmpplay[gameinstance][ij] == Integer.parseInt(a))
			{
				String [] nodes = new String[tmpplay[gameinstance].length];

				for(int i=0; i<nodes.length; i++)
				{
					nodes[i] = String.valueOf(tmpplay[gameinstance][i]);
				}
				String seq = String.join(",", nodes);
				sequences.put(user_id, seq);
			}
		}



		return sequences;
		
		
	}


	private static HashMap<String, Integer> computeRewards(HashMap<String, String> att_sequences,
			ArrayList<ArrayList<String>> data_refined) {
		
		
		
		HashMap<String, Integer> rewards = new HashMap<String, Integer>();
		
		int gameinstance = 0;
		
		for(String user: att_sequences.keySet())
		{
			int deforder = getDefOrder(user,data_refined);

			if(deforder==0)
				gameinstance = 3;
			else if(deforder==1)
				gameinstance = 0;
			
			
			
			String att_seq = att_sequences.get(user);
			
			//String defatt [] = attdeff_seq.split(" ");
			
			
			
			
			for(ArrayList<String> example : data_refined)
			{
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				int instance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue())) ;
				int round = Integer.parseInt(example.get(Headers_minimum.round.getValue())) ;
				int points =  Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue())) ;
				
				if(instance==gameinstance && round==5 && tmpuser.equals(user))
				{
					rewards.put(tmpuser, points);
					break;
				}
				
			}
			
			
		}
		
		
		return rewards;
	}


	private static HashMap<String, String> computeAttackerSequences(String a, int ij, HashMap<String, int[][]> game_play, ArrayList<ArrayList<String>> data_refined) {

		int gameinstance = 0;
		HashMap<String, String> sequences = new HashMap<String, String>();

		for(String user_id: game_play.keySet())
		{
			int[][] tmpplay = game_play.get(user_id);



			int deforder = getDefOrder(user_id,data_refined);

			if(deforder==0)
				gameinstance = 3;
			else if(deforder==1)
				gameinstance = 0;

			if( tmpplay[gameinstance][ij] == Integer.parseInt(a))
			{
				String [] nodes = new String[tmpplay[gameinstance].length];

				for(int i=0; i<nodes.length; i++)
				{
					nodes[i] = String.valueOf(tmpplay[gameinstance][i]);
				}
				String seq = String.join(",", nodes);
				sequences.put(user_id, seq);
			}
		}



		return sequences;
	}

	private static int computeNija(HashMap<String, int[][]> game_play, int r, int a, int gameinstance, ArrayList<ArrayList<String>> data_refined) {
		// TODO Auto-generated method stub

		int count = 0;

		for(String user_id: game_play.keySet())
		{
			int[][] tmpplay = game_play.get(user_id);



			int deforder = getDefOrder(user_id,data_refined);

			if(deforder==0)
				gameinstance = 3;
			else if(deforder==1)
				gameinstance = 0;

			if(tmpplay[gameinstance][r] == a)
			{
				count++;
			}
		}

		return count;
	}

	private static int getDefOrder(String user_id, ArrayList<ArrayList<String>> data_refined) {



		for(ArrayList<String> example: data_refined)
		{

			String user = example.get(Headers_minimum.user_id.getValue());
			if(user.equals(user_id))
			{
				int deforder = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
				return deforder;
			}
		}


		return -1;
	}

}

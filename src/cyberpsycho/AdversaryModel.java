package cyberpsycho;

import java.util.ArrayList;
import java.util.HashMap;

import cyberpsycho.Data.Headers_minimum;

public class AdversaryModel {

	
	
	public final static int M_START_INDEX = 11;
	public final static int M_END_INDEX = 19;
	public final static int N_START_INDEX = 20;
	public final static int N_END_INDEX = 28;
	public final static int P_START_INDEX = 29;
	public final static int P_END_INDEX = 37;
	
	public static HashMap<Integer, Integer> scoremap = new HashMap<Integer,Integer>();


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


	/**
	 * consider defender and attacker previous sequences
	 * for attacker consider action a in round
	 * @param action
	 * @param round
	 * @param LIMIT_ROUND
	 * @param att_sequences
	 * @param def_sequences
	 * @param rewards
	 * @param strategy
	 * @return
	 */
	public static double computeExpectedPayoffFullInfo(String action, int round, int LIMIT_ROUND, HashMap<String,String> att_sequences, HashMap<String,String> def_sequences, HashMap<String,Integer> rewards,HashMap<String, HashMap<String, Double>> strategy)
	{
		//double expectedutility = 0.0;




		/**
		 * 
		 * 
		 * find the sequences where the previous sequence was played by attacker to reach the information set
		 */

		double global_expected_value = 0;
		double expected_value = 0;
		for(String user_id: att_sequences.keySet())
		{

			expected_value = 0;
			String total_att_seq = att_sequences.get(user_id);
			String total_def_seq = def_sequences.get(user_id);


			/**
			 *  consider defender and attacker previous sequences
			 * for attacker consider action a in round
			 */
			ArrayList<String> sequences = getFullInfoSequences(total_att_seq, total_def_seq, strategy, LIMIT_ROUND, round, action);

			/**
			 * for each of the sequences multiply the probabilities by ensuring that attacker also played the actions to reach the leaf nodes
			 */

			for(String seq: sequences)
			{
				String sequence[] = seq.split(" ");
				String defender_seq = sequence[0];
				String attacker_seq = sequence[1];

				//String attacker_id = user_id;
				//String total_attacker_seq = getTotalAttackerSeq(att_sequences, attacker_seq);  // what if multiple attacker seq? 


				//System.out.println("\nSequence : "+defender_seq + "   "+attacker_seq +"*************");
				//System.out.println("Attacker reward "+ rewards.get(total_att_seq)); // what if multiple player plays same game and gets different rewards?

				// last strategy for the sequence
				HashMap<String, Double> last_strat = strategy.get(seq);

				for(String lastaction: last_strat.keySet())
				{

					double sequence_prob = last_strat.get(lastaction);

					//System.out.println("initial seq prob "+ sequence_prob);

					//System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ",initial prob "+ sequence_prob);
					//System.out.println("total Attacker sequence "+ total_att_seq);

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




						//System.out.println("tmp defender sequence "+ tmp_def_seq + ", tmp attacker sequence "+tmp_att_seq );

						String r_action = defender_seq.substring(roundindex, roundindex+1);

						//System.out.println("round "+ r + " r_action "+ r_action);

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

							//System.out.println("round "+ r + " action "+ r_action + " prob "+ prob);

							//System.out.println("prev seq prob " +sequence_prob);

							sequence_prob *= prob;

							//System.out.println("new seq prob "+ sequence_prob);

						}
						else
						{
							//System.out.println("new seq does not exist ");
							//System.out.println("prev seq prob " +sequence_prob+",\n new seq prob "+ sequence_prob);
						}

						//System.out.println("hi");



					}

					//System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ", final prob "+ sequence_prob);
					//System.out.println("total att sequence "+ total_att_seq);

					//TODO

					// now use the probability to multiply the reward

					int reward = rewards.get(total_att_seq);

					//System.out.println("total attacker reward "+ reward);

					double expectedreward = reward*sequence_prob;

					//System.out.println("total attacker exp_reward "+ expectedreward);

					// then add the expected value to the global sum

					global_expected_value += expectedreward;

					expected_value += expectedreward;

					//System.out.println("attacker global exp_value "+ global_expected_value/att_sequences.size());

					int k=1;

				}

			}

			//System.out.println("attacker exp_value "+ expected_value);
		}

		//System.out.println("attacker global exp_value "+ global_expected_value/att_sequences.size());

		return global_expected_value/att_sequences.size();
	}
	
	
	public static double computeExpectedPayoffPartialInfo(String action, int round, int LIMIT_ROUND, HashMap<String,String> att_sequences, HashMap<String,String> def_sequences, HashMap<String,Integer> rewards,HashMap<String, HashMap<String, Double>> strategy)
	{
		//double expectedutility = 0.0;




		/**
		 * 
		 * 
		 * find the sequences where the previous sequence was played by attacker to reach the information set
		 */

		double global_expected_value = 0;
		double expected_value = 0;
		for(String user_id: att_sequences.keySet())
		{

			expected_value = 0;
			String total_att_seq = att_sequences.get(user_id);
			//String total_def_seq = def_sequences.get(user_id);


			/**
			 *  consider defender and attacker previous sequences
			 * for attacker consider action a in round
			 */
			ArrayList<String> sequences = getPartialInfoSequences(total_att_seq, strategy, LIMIT_ROUND, round, action);

			/**
			 * for each of the sequences multiply the probabilities by ensuring that attacker also played the actions to reach the leaf nodes
			 */

			for(String seq: sequences)
			{
				String sequence[] = seq.split(" ");
				String defender_seq = sequence[0];
				String attacker_seq = sequence[1];

				//String attacker_id = user_id;
				//String total_attacker_seq = getTotalAttackerSeq(att_sequences, attacker_seq);  // what if multiple attacker seq? 


				//System.out.println("\nSequence : "+defender_seq + "   "+attacker_seq +"*************");
				//System.out.println("Attacker reward "+ rewards.get(total_att_seq)); // what if multiple player plays same game and gets different rewards?

				// last strategy for the sequence
				HashMap<String, Double> last_strat = strategy.get(seq);

				for(String lastaction: last_strat.keySet())
				{

					double sequence_prob = last_strat.get(lastaction);

					//System.out.println("initial seq prob "+ sequence_prob);

					//System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ",initial prob "+ sequence_prob);
					//System.out.println("total Attacker sequence "+ total_att_seq);

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




						//System.out.println("tmp defender sequence "+ tmp_def_seq + ", tmp attacker sequence "+tmp_att_seq );

						String r_action = defender_seq.substring(roundindex, roundindex+1);

						//System.out.println("round "+ r + " r_action "+ r_action);

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

							//System.out.println("round "+ r + " action "+ r_action + " prob "+ prob);

							//System.out.println("prev seq prob " +sequence_prob);

							sequence_prob *= prob;

							//System.out.println("new seq prob "+ sequence_prob);

						}
						else
						{
							//System.out.println("new seq does not exist ");
							//System.out.println("prev seq prob " +sequence_prob+",\n new seq prob "+ sequence_prob);
						}

						//System.out.println("hi");



					}

					//System.out.println("total defender sequence "+ defender_seq + ","+lastaction + ", final prob "+ sequence_prob);
					//System.out.println("total att sequence "+ total_att_seq);

					//TODO

					// now use the probability to multiply the reward

					int reward = rewards.get(total_att_seq);

					//System.out.println("total attacker reward "+ reward);

					double expectedreward = reward*sequence_prob;

					//System.out.println("total attacker exp_reward "+ expectedreward);

					// then add the expected value to the global sum

					global_expected_value += expectedreward;

					expected_value += expectedreward;

					//System.out.println("attacker global exp_value "+ global_expected_value/att_sequences.size());

					int k=1;

				}

			}

			//System.out.println("attacker exp_value "+ expected_value);
		}

		//System.out.println("attacker global exp_value "+ global_expected_value/att_sequences.size());

		return global_expected_value/att_sequences.size();
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
			HashMap<String, HashMap<String, Double>> strategy, int LIMIT_ROUND, int round, String action) {



		ArrayList<String> sequences = new ArrayList<String>();


		for(String att_prev: att_sequences.values())
		{

			String att_afterseq = "";
			//if(round>0)
			{
				att_afterseq = att_prev.substring(round*2, att_prev.length()-2);
			}

			for(String seq: strategy.keySet())
			{


				String[] defatt = seq.split(" ");

				String nodes[] = defatt[1].split(",");


				if((nodes.length == (LIMIT_ROUND)) )
				{

					//if(round>0)
					//{
					String tmp_att_after_seq = defatt[1].substring(round*2);
					//String tmp_def_prev_seq = defatt[0].substring(0, round*2-1);

					String[] att_nodes = defatt[1].split(",");

					//System.out.println("tmp_def_prev_seq "+ tmp_def_prev_seq+"   tmp_att_prev_seq "+ tmp_att_prev_seq);

					if(att_afterseq.compareTo(tmp_att_after_seq)==0 /*&& def_prevseq.compareTo(tmp_def_prev_seq) == 0*/)
						//String action = "1";
						//if(att_nodes[round].equals(action))
					{

						sequences.add(seq);
						System.out.println("Adding sequence "+ seq);
					}
					//}
					/*else
					{
						sequences.add(seq);
						System.out.println("Adding sequence "+ seq);
					}*/
				}
			}
		}


		return sequences;

	}


	

	private static ArrayList<String> getPartialInfoSequences(String total_att_seq,
			 HashMap<String, HashMap<String, Double>> strategy, int LIMIT_ROUND, int round, String action) {



		ArrayList<String> sequences = new ArrayList<String>();


		//	for(String att_prev: att_sequences.values())
		{

			String att_seq = "";
			att_seq = total_att_seq.substring(0, total_att_seq.length()-2);
			//String def_prev_seq = "";
			/*if(round>0)
			{
				*//**
				 * consider defender previous sequences
				 * consider attacker full sequence
				 *//*
				def_prev_seq = total_def_seq.substring(0, round*2-1);
				
			}*/

			for(String seq: strategy.keySet())
			{


				String[] defatt = seq.split(" ");

				String nodes[] = defatt[1].split(",");


				if((nodes.length == (LIMIT_ROUND)) )
				{
					
					
					String tmp_att_seq = defatt[1];
					if(round>0)
					{
						/*.substring(0, round*2+1)*/;
						//String tmp_def_prev_seq = defatt[0].substring(0, round*2-1);
						
						//String[] att_nodes = defatt[1].split(",");

						//System.out.println("tmp_def_prev_seq "+ tmp_def_prev_seq+"   tmp_att_prev_seq "+ tmp_att_prev_seq);

						if(att_seq.compareTo(tmp_att_seq)==0 /*&& def_prev_seq.compareTo(tmp_def_prev_seq) == 0*/)
							//String action = "1";
							//if(att_nodes[round].equals(action))
						{

							sequences.add(seq);
							//System.out.println("Adding sequence "+ seq);
						}
					}
					else
					{
						
						if(att_seq.compareTo(tmp_att_seq)==0)
						{

							sequences.add(seq);
							//System.out.println("Adding sequence "+ seq);
						}
						//sequences.add(seq);
						//System.out.println("Adding sequence "+ seq);
					}
				}
			}
		}


		return sequences;

	}
	
	
	private static ArrayList<String> getFullInfoSequences(String total_att_seq,
			String total_def_seq, HashMap<String, HashMap<String, Double>> strategy, int LIMIT_ROUND, int round, String action) {



		ArrayList<String> sequences = new ArrayList<String>();


		//	for(String att_prev: att_sequences.values())
		{

			String att_seq = "";
			att_seq = total_att_seq.substring(0, total_att_seq.length()-2);
			String def_prev_seq = "";
			if(round>0)
			{
				/**
				 * consider defender previous sequences
				 * consider attacker full sequence
				 */
				def_prev_seq = total_def_seq.substring(0, round*2-1);
				
			}

			for(String seq: strategy.keySet())
			{


				String[] defatt = seq.split(" ");

				String nodes[] = defatt[1].split(",");


				if((nodes.length == (LIMIT_ROUND)) )
				{
					
					
					String tmp_att_seq = defatt[1];
					if(round>0)
					{
						/*.substring(0, round*2+1)*/;
						String tmp_def_prev_seq = defatt[0].substring(0, round*2-1);
						
						//String[] att_nodes = defatt[1].split(",");

						//System.out.println("tmp_def_prev_seq "+ tmp_def_prev_seq+"   tmp_att_prev_seq "+ tmp_att_prev_seq);

						if(att_seq.compareTo(tmp_att_seq)==0 && def_prev_seq.compareTo(tmp_def_prev_seq) == 0)
							//String action = "1";
							//if(att_nodes[round].equals(action))
						{

							sequences.add(seq);
							//System.out.println("Adding sequence "+ seq);
						}
					}
					else
					{
						
						if(att_seq.compareTo(tmp_att_seq)==0)
						{

							sequences.add(seq);
							//System.out.println("Adding sequence "+ seq);
						}
						//sequences.add(seq);
						//System.out.println("Adding sequence "+ seq);
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


		//double lambda = 0; 
		
		HashMap<String, Double> Uija = new HashMap<String, Double>();
		
		HashMap<String, Integer> Nija = new HashMap<String, Integer>();
		
		HashMap<Integer, Double> Ai = new HashMap<Integer, Double>();
		
		int Ni = users_refined.size();
		

		for(int ij= 0; ij<5; ij++)
		{
			/**
			 * for each action compute  Nija*Uija and sum them
			 */
			//System.out.println("************ Round "+ij+" *********");
			double Ai_sum = 0;
			String[] actions = {"0", "1", "2", "3", "4", "5"};
			for(String a: actions)
			{
				// Nija
				int nija = computeNija(att_game_play, ij, Integer.parseInt(a), gameinstance, data_refined);
				// sequences where 
				HashMap<String, String> att_sequences = computeAttackerSequences(a, ij, att_game_play, data_refined);
				HashMap<String, String> def_sequences = computeDefenderSequences(a, ij, def_game_play, att_game_play, data_refined);
				HashMap<String, Integer> rewards = computeTotalRewards(att_sequences, data_refined, ij);
		//		double uija = computeExpectedPayoffPartialInfo(a, ij, LIMIT_ROUND, att_sequences, def_sequences, rewards ,strategy);
				double uija = computeExpectedPayoffFullInfo(a, ij, LIMIT_ROUND, att_sequences, def_sequences, rewards ,strategy);
				String key = ij+","+a;
				Uija.put(key, uija);
				Nija.put(key, nija);
				double tmp = nija*uija;
				Ai_sum += tmp;
				
			}
			Ai.put(ij, Ai_sum);
		}
		
		
		
		double sum_dnom = 0;
		double sum_nom = 0;
		
		for(int ij=0; ij<5; ij++)
		{
			double sum_ij_dnom = 0;
			
			String[] actions = {"0", "1", "2", "3", "4", "5"};
			
			
			
			for(String a: actions)
			{
				// Ai-Ni*Uix
				
				
				String key = ij+","+a;
				double x = Math.log(Ai.get(ij) - Ni*Uija.get(key));
				
				sum_ij_dnom += x;
				
				sum_nom += Uija.get(key);
				
				
				
			}
		}
		
		
		
		double lambda = -(sum_dnom/sum_nom);
		
		System.out.println("************ lambda "+lambda+" *********");
		
		
		
		

	}

	private static HashMap<String, String> computeDefenderSequences(String a, int ij,
			HashMap<String, int[][]> def_game_play, HashMap<String,int[][]> att_game_play, ArrayList<ArrayList<String>> data_refined) {


		int gameinstance = 0;
		HashMap<String, String> sequences = new HashMap<String, String>();

		for(String user_id: def_game_play.keySet())
		{
			int[][] tmpplay = def_game_play.get(user_id);
			int[][] att_play = att_game_play.get(user_id);



			int deforder = getDefOrder(user_id,data_refined);

			if(deforder==0)
				gameinstance = 5;
			else if(deforder==1)
				gameinstance = 2;

			if( att_play[gameinstance][ij] == Integer.parseInt(a))
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


	private static HashMap<String, Integer> computeStepRewards(HashMap<String, String> att_sequences,
			ArrayList<ArrayList<String>> data_refined, int r) {



		HashMap<String, Integer> rewards = new HashMap<String, Integer>();

		int gameinstance = 1;

		for(String user: att_sequences.keySet())
		{
			System.out.println("User "+ user);

			if(user.equals("\"$2y$10$FZoYU9pJKScTeiUF.hmZI.fkxHcB4XylPpADen4Nbx6TMDMPkEnv.\""))
			{
				int x = 1;
			}

			int deforder = getDefOrder(user,data_refined);

			if(deforder==0)
				gameinstance = 4;
			else if(deforder==1)
				gameinstance = 1;



			String att_seq = att_sequences.get(user);






			for(ArrayList<String> example : data_refined)
			{
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				int instance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue())) ;
				int round = Integer.parseInt(example.get(Headers_minimum.round.getValue())) ;
				int points =  Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue())) ;
				int prev_points = 0;

				if(instance==gameinstance && round==(r+1) && tmpuser.equals(user))
				{
					prev_points = points;
				}

				if(instance==gameinstance && round==(r+2) && tmpuser.equals(user))
				{
					System.out.println("points "+ points);
					rewards.put(att_seq, points-prev_points);
					break;
				}

			}


		}


		return rewards;
	}

	private static HashMap<String, Integer> computeTotalRewards(HashMap<String, String> att_sequences,
			ArrayList<ArrayList<String>> data_refined, int r) {



		HashMap<String, Integer> rewards = new HashMap<String, Integer>();

		int gameinstance = 1;

		for(String user: att_sequences.keySet())
		{
			//System.out.println("User "+ user);

			if(user.equals("\"$2y$10$FZoYU9pJKScTeiUF.hmZI.fkxHcB4XylPpADen4Nbx6TMDMPkEnv.\""))
			{
				int x = 1;
			}

			int deforder = getDefOrder(user,data_refined);

			if(deforder==0)
				gameinstance = 6;
			else if(deforder==1)
				gameinstance = 3;



			String att_seq = att_sequences.get(user);






			for(ArrayList<String> example : data_refined)
			{
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				int instance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue())) ;
				int round = Integer.parseInt(example.get(Headers_minimum.round.getValue())) ;
				int points =  Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue())) ;
				int prev_points = 0;



				if(instance==gameinstance && round==5 && tmpuser.equals(user))
				{
					//System.out.println("points "+ points);
					rewards.put(att_seq, points);
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
				gameinstance = 5;
			else if(deforder==1)
				gameinstance = 2;

			if( tmpplay[gameinstance][ij] == Integer.parseInt(a))
			{
				String [] nodes = new String[tmpplay[gameinstance].length];

				for(int i=0; i<nodes.length; i++)
				{
					nodes[i] = String.valueOf(tmpplay[gameinstance][i]);
				}
				String seq = String.join(",", nodes);
				//seq = seq.substring(0,  seq.length()-2);
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

package cyberpsycho;

import java.util.ArrayList;
import java.util.HashMap;

import cyberpsycho.Data.Headers;
import cyberpsycho.Data.Headers_minimum;

public class AdversaryModelExps {


	public static void doDummyTesting()
	{
		// read the data we got from MTurk
		ArrayList<ArrayList<String>> data =  Data.readData();



		// keep the users who played all 6 games
		// that means they have 
		ArrayList<String> users_refined = refineUser(data, -1, 1);
		ArrayList<ArrayList<String>>  data_refined = refineData(data,1);
		System.out.println("Total number of users "+ users_refined.size());


		// list the game play of the users

		HashMap<String, int[][]> att_game_play = buildGamePlay(users_refined, data_refined, 1);
		HashMap<String, int[][]> def_game_play = buildGamePlay(users_refined, data_refined, 0);
		HashMap<String, int[][]> reward = buildGameRewards(users_refined, data_refined);

		printGamePlay(att_game_play);



		AdversaryModel.computeLambda(users_refined, att_game_play, def_game_play,reward, data_refined, 4);
		



		
	}

	private static void printGamePlay(HashMap<String, int[][]> game_play) {


		System.out.println("Game play : ");
		for(String user: game_play.keySet())
		{
			System.out.println("user " + user);
			int[][] play = game_play.get(user);

			for(int ins =0; ins<6; ins++)
			{
				for(int r=0; r<5; r++)
				{
					System.out.print(play[ins][r] + " ");
				}
				System.out.println();
			}
		}

	}

	private static HashMap<String, int[][]> buildGamePlay(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int i) {


		HashMap<String, int[][]> gameplay = new HashMap<String, int[][]>();

		// for every user

		System.out.println("Building game play");
		for(String user_id: users_refined)
		{
			System.out.println("\nuser "+ user_id);
			//int gameinstance = 1;
			//int round = 0;
			int[][] tmpgameplay = new int[6][5];
			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id
				if(user_id.equals(tmpuser))
				{

					if(user_id.equals("\"$2y$10$1eppnuF14Ls9jlRDjIPIOuX4Dg3v.KS6CP.6nl0NZCZuKAnS7Kosm\""))
					{
						//System.out.print("h");
						int g=1;
					}

					int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					String attackeraction = example.get(Headers_minimum.attacker_action.getValue());
					
					if(i==0)
					{
						attackeraction = example.get(Headers_minimum.defender_action.getValue());
					}
					
					System.out.print(attackeraction+" ");

					if(round==4)
					{
						System.out.println();
					}

					if(attackeraction.equals(" ") || attackeraction.equals(""))
					{
						attackeraction = "5";
					}

					int action = Integer.parseInt(attackeraction);
					tmpgameplay[gameinstance-1][round-1] = (action);

				}

			}
			gameplay.put(user_id, tmpgameplay);

		}
		return gameplay;
	}
	
	
	private static HashMap<String, int[][]> buildGameRewards(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined) {


		HashMap<String, int[][]> reward = new HashMap<String, int[][]>();

		// for every user

		System.out.println("Building game play");
		for(String user_id: users_refined)
		{
			System.out.println("\nuser "+ user_id);
			//int gameinstance = 1;
			//int round = 0;
			int[][] tmpgameplay = new int[6][5];
			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id
				if(user_id.equals(tmpuser))
				{

					if(user_id.equals("\"$2y$10$Zss70qaplxmdn5QIxlkM4Oh/4GtT2f0BmWg9ISHZ1OxRvikqrMCOC\""))
					{
						//System.out.print("h");
					}

					int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					String attackeraction = example.get(Headers_minimum.attacker_points.getValue());
					System.out.print(attackeraction+" ");

					if(round==4)
					{
						System.out.println();
					}

					if(attackeraction.equals(" ") || attackeraction.equals(""))
					{
						attackeraction = "5";
					}

					int action = Integer.parseInt(attackeraction);
					tmpgameplay[gameinstance-1][round-1] = (action);

				}

			}
			reward.put(user_id, tmpgameplay);

		}
		return reward;
	}



	private static ArrayList<ArrayList<String>> refineData(ArrayList<ArrayList<String>> data, int game_type) {


		ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;
			if(!users.contains(tmpuser) && gameplayed == 6 && gametype == game_type)
			{
				users.add(example);
			}

		}


		return users;
	}

	private static ArrayList<String> refineUser(ArrayList<ArrayList<String>> data, int def_order, int gametype) {


		ArrayList<String> users = new ArrayList<String>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int tmp_def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue())) ;
			int game_type = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;
			if(!users.contains(tmpuser) && gameplayed == 6 && game_type==gametype)
			{
				if(def_order>=0 && def_order == tmp_def_order)
				{
					users.add(tmpuser);
					System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
				}
				else
				{
					users.add(tmpuser);
					System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
				}
			}

		}


		return users;
	}

}

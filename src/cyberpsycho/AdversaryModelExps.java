package cyberpsycho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cs.Interval.contraction.SecurityGameContraction;
import cs.Interval.contraction.TargetNode;
import cyberpsycho.Data.Headers_minimum;
import games.EmpiricalMatrixGame;
import games.MatrixGame;
import games.MixedStrategy;
import games.OutcomeDistribution;
import games.OutcomeIterator;
import groupingtargets.ClusterTargets;
import kmeans.KmeanClustering;
import kmeans.Weka;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import solvers.QRESolver;
import solvers.SolverUtils;


public class AdversaryModelExps {

	public static Random rand = new Random(5);


	public static void doDummyTesting()
	{
		// read the data we got from MTurk
		ArrayList<ArrayList<String>> data =  Data.readData();



		// keep the users who played all 6 games
		// that means they have 
		ArrayList<String> users_refined = refineUser(data, -1, 1);
		ArrayList<ArrayList<String>>  data_refined = refineData(data,1,users_refined);
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

	private static void printOneStageGamePlay(HashMap<String, Integer> game_play) 
	{


		System.out.println("Game play : ");
		for(String user: game_play.keySet())
		{
			System.out.println("user " + user);
			int play = game_play.get(user);


			System.out.print(play + " ");

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


	private static HashMap<String, Integer> buildOneStageGamePlay(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int i) {


		HashMap<String, Integer> gameplay = new HashMap<String, Integer>();

		// for every user

		//System.out.println("Building game play");
		for(String user_id: users_refined)
		{
			//System.out.println("\nuser "+ user_id);
			//int gameinstance = 1;
			//int round = 0;
			int tmpgameplay = -1;
			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id
				if(user_id.equals(tmpuser))
				{

					/*if(user_id.equals("\"$2y$10$1eppnuF14Ls9jlRDjIPIOuX4Dg3v.KS6CP.6nl0NZCZuKAnS7Kosm\""))
					{
						//System.out.print("h");
						int g=1;
					}*/




					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));

					if(round==1)
					{
						//int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));

						String attackeraction = example.get(Headers_minimum.attacker_action.getValue());

						if(i==0)
						{
							attackeraction = example.get(Headers_minimum.defender_action.getValue());
						}

						//System.out.print(attackeraction+" ");



						if(attackeraction.equals(" ") || attackeraction.equals(""))
						{
							attackeraction = "5";
						}

						int action = Integer.parseInt(attackeraction);
						tmpgameplay = (action);
					}



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



	private static ArrayList<ArrayList<String>> refineData(ArrayList<ArrayList<String>> data, int game_type, ArrayList<String> users_refined) {


		ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;

			/*boolean isuser = examples.contains(tmpuser);
			if(users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type)
			{

			}*/


			String def_order = example.get(Headers_minimum.pick_def_order.getValue());
			int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
			//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
			/*String action = example.get(Headers_minimum.attacker_action.getValue());
			int attackaction = 0;
			if(!action.equals(" "))
			{
				attackaction = Integer.parseInt(action);
			}*/
			if(def_order.equals("0") && (gameinstance>=4) && users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type) // asc, take 4th game instance to 6th
			{

				examples.add(example);
			}
			else if(def_order.equals("1") && (gameinstance<=3) && users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type) // desc, take 1st game instance to 3rd
			{
				examples.add(example);
			}




		}


		return examples;
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

	/**
	 * use a 5x5 game
	 * 
	 * compute mixed strategy for defender
	 * 
	 * use defender's stratgey. generate attacker strategy given defenders strategy using AQRE
	 * @throws Exception 
	 */
	public static void doDummyTest2() throws Exception {


		int nrow = 1;

		int ncol = 5;

		int dmax = 20;



		int ITER = 1;

		int als[] = {2}; //DO + weka + CON target per cluster
		//radius


		int ranges[][] = {{0,2},{3,8},{9, 10}};
		int[] percforranges = {80, 10, 10};

		/*int ranges[][] = {{0,7},{6,8},{8, 10}};
		int[] percforranges = {90, 0, 10};*/



		int blockdim = 2; // block = blockdim x blockdim

		// nrow has to be divisible by block

		int nTargets = nrow*ncol;

		int ncat = 3;
		int[] targetsincat = getTargetsInCats(nTargets, percforranges);
		double[][] density=SecurityGameContraction.generateRandomDensityV2(ncat, ITER, ranges, nTargets, targetsincat);


		HashMap<Integer, ArrayList<TargetNode>> alltargets = new HashMap<Integer, ArrayList<TargetNode>>();
		HashMap<Integer, HashMap<Integer, TargetNode>> alltargetmaps = new HashMap<Integer, HashMap<Integer, TargetNode>>();
		//HashMap<Integer, ArrayList<Integer>[]> allclus = new HashMap<Integer, ArrayList<Integer>[]>();
		HashMap<Integer, ArrayList<Integer>[]> allclus = new HashMap<Integer, ArrayList<Integer>[]>();
		//double[][] density=SecurityGameContraction.generateRandomDensity( perc, ITER, lstart, lend,  hstart, hend, nTargets, false);

		//double[][] density = new double[ITER][nTargets];



		for(int iter = 0; iter<ITER; iter++)
		{
			ArrayList<TargetNode> targets = new ArrayList<TargetNode>();  //createGraph();
			HashMap<Integer, TargetNode> targetmaps = new HashMap<Integer, TargetNode>();
			ClusterTargets.buildcsvGraphExp(nrow,ncol,density,targets, iter );
			//SecurityGameContraction.assignRandomDensityZeroSum(density, gamedata, targets, iter);
			//SecurityGameContraction.buildGraph(nrow, ncol, gamedata, targets);
			//SecurityGameContraction.assignRandomDensityZeroSum(density, gamedata, targets, iter);
			alltargets.put(iter, targets);
			for(TargetNode t : targets)
			{
				targetmaps.put(t.getTargetid(), t);

			}
			alltargetmaps.put(iter, targetmaps);

			ClusterTargets.buildFile(nrow,ncol,density,targets, iter );

			int g=0;



		}


	}

	private static int[] getTargetsInCats(int nTargets, int[] percforcats) {


		int x[] = new int[percforcats.length];


		int sum = 0;

		for(int i=0; i<percforcats.length; i++)
		{
			x[i] = (int)Math.floor(nTargets*(percforcats[i]/100.00));
			sum += x[i];
		}




		if(sum<nTargets)
		{

			int max = Integer.MIN_VALUE;
			int index= 0;
			for(int i=0; i<percforcats.length; i++)
			{
				if(max>percforcats[i])
				{
					max = percforcats[i];
					index = i;
				}
			}

			x[index] += (nTargets-sum);

		}



		return x;
	}


	public static int randInt(int min, int max) {

		// Usually this should be a field rather than a method variable so
		// that it is not re-seeded every call.


		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static void getLambdaOneStageGame() throws MatlabConnectionException, MatlabInvocationException
	{
		int[] actions = {3,3};
		MatrixGame gm = new MatrixGame(2,actions);
		OutcomeIterator itr = gm.iterator();
		while(itr.hasNext())
		{
			int[] outcome = itr.next();
			double payoff = randInt(5, 10);
			gm.setPayoff(outcome, 0, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 0 "+payoff);
			payoff = randInt(5, 10);
			gm.setPayoff(outcome, 1, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 1 "+payoff);

		}

		MixedStrategy[] gamestrategy = new MixedStrategy[2];

		QRESolver qre = new QRESolver(100);
		EmpiricalMatrixGame emg = new EmpiricalMatrixGame(gm);
		qre.setDecisionMode(QRESolver.DecisionMode.RAW);
		for(int i=0; i< gm.getNumPlayers(); i++ )
		{
			gamestrategy[i] = qre.solveGame(emg, i);
		}

		System.out.println("s");

		//gamestrategy = RegretLearner.solveGame(gm);



		// parse data

		double[] lambdas = {0.08/*,.08, .1, .5, 1, 2*/};
		for(double lambda: lambdas)
		{

			ArrayList<ArrayList<String>> data = Data.readData(lambda);

			HashMap<Integer, Integer> ni = computeNi(data);

			HashMap<Integer, Double> ui = computeUi(data);

			int n=300;

			double A=0;
			for(int action=1; action <=3; action++)
			{
				A += (ui.get(action)*ni.get(action));
			}

			double B = A/n;

			double[] coeffs = new double[3];

			for(int i=0; i<3; i++)
			{
				coeffs[i] = B-ui.get(i+1);
			}



			//int x=0;

			double estimatedlambda = estimateLambda(ni, n, ui);

			System.out.println("Estimated lambda "+ estimatedlambda);


		}

	}

	private static double estimateLambda(HashMap<Integer, Integer> ni, int n, HashMap<Integer, Double> ui) throws MatlabConnectionException, MatlabInvocationException
	{

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();

		//Set a variable, add to it, retrieve it, and print the result

		proxy.eval("syms lambda");

		proxy.setVariable("n",300);
		// set ni variables
		String n_i [] = new String[ni.size()];
		for(int i=1; i<=3; i++)
		{
			n_i[i-1] = "n"+i;
			proxy.setVariable(n_i[i-1], ni.get(i));

		}

		// set ui variables
		String u_i [] = new String[ui.size()];
		for(int i=1; i<=3; i++)
		{
			u_i[i-1] = "u"+i;
			proxy.setVariable(u_i[i-1], ui.get(i));

		}


		//Eq1 = 0 == (exp(lambda*U1) + exp(lambda*U2) + exp(lambda*U3)) * ((N1*U1)+(N2*U2)+(N3*U3)) - N*(exp(lambda*U1)*(U1) + exp(lambda*U2)*(U2) + exp(lambda*U2)*(U2));
		// build the equation string
		//Eq1 = 0 ==(exp( lambda *u1)+exp( lambda *u2)+exp( lambda *u3))*((n1*u1)+(n2*u2)+(n3*u3))-n*(exp( lambda *u1)*u1+exp( lambda *u2)*u2+exp( lambda *u3)*u3)


		String eqn = buildEqnString(ui, ni, u_i, n_i);

		System.out.println("\n"+eqn);

		proxy.eval(eqn);
		proxy.eval("symlambda = solve(Eq1, lambda)");

		proxy.eval("dlambda = double(symlambda)");

		double result = ((double[]) proxy.getVariable("dlambda"))[0];
		System.out.println("\n dlambda: " + result);

		//Disconnect the proxy from MATLAB
		proxy.disconnect();


		return 0.0;

	}


	private static double estimateFlipItLambda(HashMap<Integer, Integer> ni, int n, HashMap<Integer, Double> ui) throws MatlabConnectionException, MatlabInvocationException
	{

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();

		//Set a variable, add to it, retrieve it, and print the result

		proxy.eval("syms lambda");

		proxy.setVariable("n",n);
		// set ni variables
		String n_i [] = new String[ni.size()];
		for(int i=0; i<ni.size(); i++)
		{
			n_i[i] = "n"+i;
			proxy.setVariable(n_i[i], ni.get(i));
			System.out.println(n_i[i]+"="+ni.get(i) );

		}

		// set ui variables
		String u_i [] = new String[ui.size()];
		for(int i=0; i<ui.size(); i++)
		{
			u_i[i] = "u"+i;
			proxy.setVariable(u_i[i], ui.get(i));
			System.out.println(u_i[i]+"="+ui.get(i));

		}


		//Eq1 = 0 == (exp(lambda*U1) + exp(lambda*U2) + exp(lambda*U3)) * ((N1*U1)+(N2*U2)+(N3*U3)) - N*(exp(lambda*U1)*(U1) + exp(lambda*U2)*(U2) + exp(lambda*U2)*(U2));
		// build the equation string
		//Eq1 = 0 ==(exp( lambda *u1)+exp( lambda *u2)+exp( lambda *u3))*((n1*u1)+(n2*u2)+(n3*u3))-n*(exp( lambda *u1)*u1+exp( lambda *u2)*u2+exp( lambda *u3)*u3)


		String eqn = buildFlipItEqnString(ui, ni, u_i, n_i);

		System.out.println("\n"+eqn);

		proxy.eval(eqn);
		proxy.eval("symlambda = solve(Eq1, lambda)");

		proxy.eval("dlambda = double(symlambda)");

		double result = ((double[]) proxy.getVariable("dlambda"))[0];
		System.out.println("\nlambda: " + result);

		//Disconnect the proxy from MATLAB
		proxy.disconnect();


		return result;

	}


	private static double estimateFlipItLambdaV2(HashMap<Integer, Integer> ni, int n, HashMap<Integer, Double> ui, MatlabProxy proxy) throws MatlabConnectionException, MatlabInvocationException
	{



		//Set a variable, add to it, retrieve it, and print the result

		proxy.eval("syms lambda");

		proxy.setVariable("n",n);
		// set ni variables
		String n_i [] = new String[ni.size()];
		for(int i=0; i<ni.size(); i++)
		{
			n_i[i] = "n"+i;
			proxy.setVariable(n_i[i], ni.get(i));

		}

		// set ui variables
		String u_i [] = new String[ui.size()];
		for(int i=0; i<ui.size(); i++)
		{
			u_i[i] = "u"+i;
			proxy.setVariable(u_i[i], ui.get(i));

		}


		//Eq1 = 0 == (exp(lambda*U1) + exp(lambda*U2) + exp(lambda*U3)) * ((N1*U1)+(N2*U2)+(N3*U3)) - N*(exp(lambda*U1)*(U1) + exp(lambda*U2)*(U2) + exp(lambda*U2)*(U2));
		// build the equation string
		//Eq1 = 0 ==(exp( lambda *u1)+exp( lambda *u2)+exp( lambda *u3))*((n1*u1)+(n2*u2)+(n3*u3))-n*(exp( lambda *u1)*u1+exp( lambda *u2)*u2+exp( lambda *u3)*u3)


		String eqn = buildFlipItEqnString(ui, ni, u_i, n_i);

		System.out.println("\n"+eqn);

		proxy.eval(eqn);
		proxy.eval("symlambda = solve(Eq1, lambda)");

		proxy.eval("dlambda = double(symlambda)");

		double result = ((double[]) proxy.getVariable("dlambda"))[0];
		System.out.println("\nlambda: " + result);




		return result;

	}

	private static String buildEqnString(HashMap<Integer,Double> ui, HashMap<Integer,Integer> ni, String[] u_i, String[] n_i) {

		String eqn = "Eq1 = 0 ==";

		// build first loop
		String e1 = "";
		for(int i=1; i<=ui.size(); i++)
		{
			e1 = e1 + "exp( lambda *"+u_i[i-1]+ ")";

			if(i != ui.size())
			{
				e1 = e1 + "+";
			}
		}

		e1 = "(" + e1 + ")";



		String e2 = "";
		for(int i=1; i<=ui.size(); i++)
		{
			e2 = e2 + "("+n_i[i-1]+"*"+ u_i[i-1]+")";

			if(i != ui.size())
			{
				e2 = e2 + "+";
			}
		}

		e2 = "(" + e2 + ")";


		eqn = eqn + e1 + "*" + e2;


		String e3 = "";
		for(int i=1; i<=ui.size(); i++)
		{
			e3 = e3 + "exp( lambda *"+u_i[i-1]+ ")*"+u_i[i-1];

			if(i != ui.size())
			{
				e3 = e3 + "+";
			}
		}

		e3 = "(" + e3 + ")";

		eqn = eqn + "-" + "n" + "*" + e3;

		return eqn;
	}


	private static String buildFlipItEqnString(HashMap<Integer,Double> ui, HashMap<Integer,Integer> ni, String[] u_i, String[] n_i) {

		String eqn = "Eq1 = 0 ==";

		// build first loop
		String e1 = "";
		for(int i=0; i<ui.size(); i++)
		{
			e1 = e1 + "exp( lambda *"+u_i[i]+ ")";

			if(i != ui.size()-1)
			{
				e1 = e1 + "+";
			}
		}

		e1 = "(" + e1 + ")";



		String e2 = "";
		for(int i=0; i<ui.size(); i++)
		{
			e2 = e2 + "("+n_i[i]+"*"+ u_i[i]+")";

			if(i != ui.size()-1)
			{
				e2 = e2 + "+";
			}
		}

		e2 = "(" + e2 + ")";


		eqn = eqn + e1 + "*" + e2;


		String e3 = "";
		for(int i=0; i<ui.size(); i++)
		{
			e3 = e3 + "exp( lambda *"+u_i[i]+ ")*"+u_i[i];

			if(i != ui.size()-1)
			{
				e3 = e3 + "+";
			}
		}

		e3 = "(" + e3 + ")";

		eqn = eqn + "-" + "n" + "*" + e3;

		return eqn;
	}


	private static HashMap<Integer, Integer> computeNi(ArrayList<ArrayList<String>> data) {


		HashMap<Integer, Integer> ni = new HashMap<Integer, Integer>();

		int[] count = new int[3];



		for(ArrayList<String> ex: data)
		{
			String action = ex.get(1);
			int a = Integer.parseInt(action);

			count[a-1]++;
		}
		for(int i=0; i<3; i++)
		{
			ni.put(i+1, count[i]);
		}

		return ni;

	}


	private static HashMap<Integer, Double> computeUi(ArrayList<ArrayList<String>> data) {


		HashMap<Integer, Double> ui = new HashMap<Integer, Double>();

		int[] count = {0,0,0};
		Double[] uis = {0.0,0.0,0.0};



		for(ArrayList<String> ex: data)
		{
			String action = ex.get(1);
			int a = Integer.parseInt(action);

			String us = ex.get(2);
			double u = Double.parseDouble(us);

			uis[a-1] += u;
			count[a-1]++;
		}




		for(int i=0; i<3; i++)
		{
			uis[i] /= count[i];

			ui.put(i+1, uis[i]);
		}

		return ui;

	}



	public static void generateOneStageGameData() {

		int[] actions = {3,3};
		MatrixGame gm = new MatrixGame(2,actions);
		OutcomeIterator itr = gm.iterator();
		while(itr.hasNext())
		{
			int[] outcome = itr.next();
			double payoff = randInt(5, 10);
			gm.setPayoff(outcome, 0, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 0 "+payoff);
			payoff = randInt(5, 10);
			gm.setPayoff(outcome, 1, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 1 "+payoff);

		}

		MixedStrategy[] gamestrategy = new MixedStrategy[2];

		QRESolver qre = new QRESolver(100);
		EmpiricalMatrixGame emg = new EmpiricalMatrixGame(gm);
		qre.setDecisionMode(QRESolver.DecisionMode.RAW);
		for(int i=0; i< gm.getNumPlayers(); i++ )
		{
			gamestrategy[i] = qre.solveGame(emg, i);
		}

		System.out.println("s");

		//gamestrategy = RegretLearner.solveGame(gm);

		System.out.println("h");


		int ITER = 300;


		double[] lambdas = {0.02,.08, .1, .5, 1, 2};
		for(double lambda: lambdas)
		{

			try 
			{

				// create a file using different lambda

				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("result/lambda"+lambda+".csv"),true));
				//PrintWriter pw = new PrintWriter(new FileOutputStream(new File("/Users/fake/Documents/workspace/IntervalSGAbstraction/"+"result.csv"),true));
				for(int i=0; i<ITER; i++)
				{

					// use defender strategy to play defender action
					int defaction = getDefenderMove(gm, gamestrategy[0]);
					//System.out.println("lambda "+lambda+", iter "+ i + ", defaction "+ defaction);
					// use defender strategy to find attacker action using AQRE for different lambda
					double attprobs[] = getAttackerProbs(gm,gamestrategy, lambda);
					int attaction = getAttackerMove(attprobs);
					if(attaction==-1)
					{
						int f=1;
					}
					int[] outcome = {defaction, attaction};
					double attpayoff = gm.getPayoff(outcome, 1);
					double defpayoff = gm.getPayoff(outcome, 0);
					for(int a=0; a<gamestrategy[1].getNumActions(); a++)
					{
						gamestrategy[1].setProb(a+1, attprobs[a]);
					}

					double[] exppayoffs = getExp(gamestrategy, gm);


					double exp = getExp(gamestrategy, gm, 1, attaction);


					System.out.println("lambda "+lambda+", iter "+ i + ", attaction "+ attaction + ", attpayoff "+ exp + ", defpayoff "+ defpayoff+", defexp "+ exppayoffs[0] + ", attexp "+ exppayoffs[1]);
					pw.append(i +","+attaction+ ","+exp +"\n");
				}
				pw.close();

			}
			catch(Exception e)
			{

			}


			//lambda += 1;
		}




	}

	private static int getAttackerMove(double[] probs) {
		int action = -1;
		Random random = new Random();

		//double[] probs = mixedStrategy.getProbs();
		double probsum = 0.0;
		double r = random.nextDouble();
		for(int j=0; j<(probs.length); j++)
		{
			probsum += probs[j];
			if(r<probsum)
			{
				action = j;
				break;
			}
		}




		return action+1;
	}

	private static double[] getAttackerProbs(MatrixGame gm, MixedStrategy mixedStrategy[], double lambda) {

		//int attaction = -1;

		// find probability of making every move

		double probs[] = new double[gm.getNumActions(1)];

		for(int action=0; action<probs.length; action++)
		{
			probs[action] = getProbAQRE(action, gm, mixedStrategy, lambda);
		}

		return probs;




	}

	private static double getProbAQRE(int action, MatrixGame gm, MixedStrategy mixedStrategy[], double lambda) {


		double prob = 0.0;

		double[] logit = new double[gm.getNumActions(1)];





		double logitsum  = 0;

		for(int a=0; a<logit.length; a++)
		{
			double ux = getExp(mixedStrategy, gm, 1, a+1);

			//System.out.println("action "+ (a+1) + ", expected payoff "+ ux);

			logit[a] = Math.exp(ux*lambda);

			//System.out.println("action "+ (a+1) + ", logit  "+ logit[a]);
			logitsum += logit[a];

		}

		prob = logit[action]/logitsum;



		return prob;
	}

	private static double getExp(MixedStrategy[] mixedStrategy, MatrixGame gm, int player, int action) {


		List<MixedStrategy> strategylist = new ArrayList<MixedStrategy>();

		for(int i=0; i<mixedStrategy[1].getNumActions(); i++)
		{
			if((i+1) == action)
			{
				mixedStrategy[1].setProb(action, 1);
			}
			else
			{
				mixedStrategy[1].setProb(i+1, 0.0);
			}
		}


		for(int i=0; i<mixedStrategy.length; i++)
		{
			strategylist.add(mixedStrategy[i]);
		}
		OutcomeDistribution origdistribution = new OutcomeDistribution(strategylist);
		double[]  originalpayoff = SolverUtils.computeOutcomePayoffs(gm, origdistribution);

		return originalpayoff[player];

	}


	private static double[] getExp(MixedStrategy[] mixedStrategy, MatrixGame gm) {


		List<MixedStrategy> strategylist = new ArrayList<MixedStrategy>();



		for(int i=0; i<mixedStrategy.length; i++)
		{
			strategylist.add(mixedStrategy[i]);
		}
		OutcomeDistribution origdistribution = new OutcomeDistribution(strategylist);
		double[]  originalpayoff = SolverUtils.computeOutcomePayoffs(gm, origdistribution);

		return originalpayoff;

	}

	private static int getDefenderMove(MatrixGame gm, MixedStrategy mixedStrategy) 
	{


		int action = 0;
		Random random = new Random();

		double[] probs = mixedStrategy.getProbs();
		double probsum = 0.0;
		double r = random.nextDouble();
		for(int j=0; j<(probs.length-1); j++)
		{
			probsum += probs[j+1];
			if(r<probsum)
			{
				action = j+1;
				break;
			}
		}

		return action;

	}

	public static void getLambdaOneStageFlipIt() throws MatlabConnectionException, MatlabInvocationException 
	{



		// read the data we got from MTurk
		ArrayList<ArrayList<String>> data =  Data.readData();



		// keep the users who played all 6 games
		// that means they have 
		ArrayList<String> users_refined = refineUser(data, -1, 1);
		System.out.println("NUmber of users found "+ users_refined.size());


		/**
		 * 1. high score
		 * 2. Low score
		 * 
		 * 3. Rank depending on personality
		 * 3a. remove those for whom corresponding type score is not the maximum
		 * 3b. Keep High variation
		 * 3c. Remove high variation
		 * 4. cluster depending on personality score
		 * 5. cluster depending on frequency
		 * 6. cluster depending on play in each round
		 */

		int personality = 	-1;
		int user_refine_type = 1;



		ArrayList<String> users_refined_type = refineUsers(users_refined, data, user_refine_type, personality);
		//ArrayList<String> users_lowscore = refineUsers(users_refined, data, 0, personality);


		ArrayList<ArrayList<String>>  data_refined = refineData(data,1,users_refined_type);
		System.out.println("Total number of high score users "+ users_refined_type.size());


		// list the game play of the users

		//users_refined = users_refined_type;

		// keep 50

		int remove = 0;

		int keep = users_refined.size() - remove;

		// keep 0 to 50

		int keepstart = 121;
		int keepend = 154;

		users_refined.clear();

		double sumscore = 0;

		double sum_mscore =0;
		double sum_nscore = 0;
		double sum_pscore = 0;


		for(int i=keepstart; i<keepend; i++)
		{
			users_refined.add(users_refined_type.get(i));

			String tmpusr = users_refined_type.get(i);

			sumscore += getUserScore(tmpusr, data_refined);

			sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
			sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
			sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


			System.out.println("kept user "+ tmpusr);
		}

		sumscore /= users_refined.size();
		sum_mscore /= users_refined.size();
		sum_nscore /= users_refined.size();
		sum_pscore /= users_refined.size();



		HashMap<String, Integer> att_game_play = buildOneStageGamePlay(users_refined, data_refined, 1);
		//HashMap<String, int[][]> def_game_play = buildGamePlay(users_refined, data_refined, 0);
		//HashMap<String, int[][]> reward = buildGameRewards(users_refined, data_refined);

		printOneStageGamePlay(att_game_play);

		HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");
		String key = "EMPTY EMPTY"; 
		HashMap<String, Double> probs = strategy.get(key);

		//AdversaryModel.computeLambda(users_refined, att_game_play, def_game_play,reward, data_refined, 4);

		HashMap<Integer, Double > ui = attExpPayoffs(att_game_play, probs);


		//ArrayList<ArrayList<String>> data = Data.readData(lambda);

		HashMap<Integer, Integer> ni = computeFLipItNi(att_game_play);

		//HashMap<Integer, Double> ui = computeUi(data);

		int n=users_refined.size();

		double A=0;
		for(int action=0; action <6; action++)
		{
			double u = ui.get(action);
			double n_i = ni.get(action);

			A += (u*n_i);
		}

		double B = A/n;

		double[] coeffs = new double[6];

		for(int i=0; i<6; i++)
		{
			coeffs[i] = B-ui.get(i);
		}



		//int x=0;

		double estimatedlambda = estimateFlipItLambda(ni, n, ui);

		System.out.println("Estimated lambda "+ estimatedlambda + ", avg score "+ sumscore);


		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("iter-lambda.csv"),true));
			// gamenumber, subgame, psne, meb,qre
			pw.append(keepstart+"-"+keepend+","+sumscore +","+estimatedlambda+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}




	}




	/**
	 * 
	 * @param users_refined
	 * @param data
	 * @param user_refine_type
	 * @param personality 
	 * @return
	 */
	private static ArrayList<String> refineUsers(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data,
			int user_refine_type, int personality) {



		ArrayList<String> sorted_users = new ArrayList<String>();


		if(user_refine_type==0)// high score
		{
			ArrayList<int[]> new_users = new ArrayList<int[]>();
			int userindex = 0;
			for(String tmpusr: users_refined)
			{

				int score = getUserScore(tmpusr,data);

				System.out.println("User "+ tmpusr + ", score " + score);

				int[] ex = {userindex, score};

				new_users.add(ex);
				userindex++;
			}

			// sort the users
			System.out.println("Sorting the users");

			int[][] srted_users = sortUsersDesc(new_users);



			for(int i=0; i<srted_users.length; i++)
			{
				sorted_users.add(users_refined.get(srted_users[i][0]));
				System.out.println("User "+ users_refined.get(srted_users[i][0]) + ", score "+ srted_users[i][1]);
			}
		}
		else if(user_refine_type==1) // low score
		{
			ArrayList<int[]> new_users = new ArrayList<int[]>();
			int userindex = 0;
			for(String tmpusr: users_refined)
			{

				int score = getUserScore(tmpusr,data);

				System.out.println("User "+ tmpusr + ", score " + score);

				int[] ex = {userindex, score};

				new_users.add(ex);
				userindex++;
			}

			// sort the users
			System.out.println("Sorting the users");

			int[][] srted_users = sortUsersAsc(new_users);



			for(int i=0; i<srted_users.length; i++)
			{
				sorted_users.add(users_refined.get(srted_users[i][0]));
				System.out.println("User "+ users_refined.get(srted_users[i][0]) + ", score "+ srted_users[i][1]);
			}
		}
		else if(personality >= 0) // mach 
		{
			ArrayList<double[]> new_users = new ArrayList<double[]>();
			int userindex = 0;
			for(String tmpusr: users_refined)
			{

				double mscore = getPersonalityScore(tmpusr,data, 0);
				double nscore = getPersonalityScore(tmpusr,data, 1);
				double pscore = getPersonalityScore(tmpusr,data, 2);

				System.out.println("user "+ tmpusr + " m: "+ mscore + ", n: "+ nscore + ", p: "+ pscore);


				double maxp = (( (mscore>=nscore)?mscore:nscore)>=pscore)?(((mscore>=nscore)?mscore:nscore)):pscore;

				if(maxp==mscore && personality==0)
				{
					System.out.println("User "+ tmpusr + ", max mscore " + mscore);

					double[] ex = {userindex, mscore};

					new_users.add(ex);
					userindex++;
				}
				else if(maxp==nscore && personality==1)
				{
					System.out.println("User "+ tmpusr + ", max nscore " + nscore);

					double[] ex = {userindex, nscore};

					new_users.add(ex);
					userindex++;
				}
				else if(maxp==pscore && personality==2)
				{
					System.out.println("User "+ tmpusr + ", max pscore " + pscore);

					double[] ex = {userindex, pscore};

					new_users.add(ex);
					userindex++;
				}


			}

			// sort the users
			System.out.println("Sorting the users");

			double[][] srted_users = sortUsersAscD(new_users);



			for(int i=0; i<srted_users.length; i++)
			{
				sorted_users.add(users_refined.get((int)srted_users[i][0]));
				System.out.println("User "+ users_refined.get((int)srted_users[i][0]) + ", score "+ srted_users[i][1]);
			}
		}


		return sorted_users;
	}




	private static double getPersonalityScore(String tmpusr, ArrayList<ArrayList<String>> data, int personality) {

		int start =-1;
		int end = -1;

		if(personality==0)
		{
			start = AdversaryModel.M_START_INDEX;
			end = AdversaryModel.M_END_INDEX;
		}
		else if(personality==1)
		{
			start = AdversaryModel.N_START_INDEX;
			end = AdversaryModel.N_END_INDEX;
		}
		else if(personality == 2)
		{
			start = AdversaryModel.P_START_INDEX;
			end = AdversaryModel.P_END_INDEX;
		}


		for(ArrayList<String> example: data)
		{
			if(example.get(Headers_minimum.user_id.getValue()).equals(tmpusr))
			{
				double sum = 0;
				int count = 0;
				for(int i=start; i<=end; i++)
				{
					String s = example.get(i);
					if(!s.equals(" "))
					{


						int choice = Integer.parseInt(s);
						int score = AdversaryModel.scoremap.get(choice);
						if(((i-10)==11) || ((i-10)==15) || ((i-10)==17) || ((i-10)==20) || ((i-10)==25))
						{
							score = choice;
						}
						count++;
						sum += score;
					}
				}
				sum = sum/count;
				return sum;

			}

		}

		return -1;
	}

	public static int[][] sortUsersDesc(ArrayList<int[]> users) 
	{

		int[][] srted = new int[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		int[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] < srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}

	public static double[][] sortUsersDescD(ArrayList<double[]> users) 
	{

		double[][] srted = new double[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		double[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] < srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}

	public static int[][] sortUsersAsc(ArrayList<int[]> users) 
	{

		int[][] srted = new int[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		int[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] > srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}

	public static double[][] sortUsersAscD(ArrayList<double[]> users) 
	{

		double[][] srted = new double[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		double[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] > srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}




	private static int getUserScore(String tmpusr, ArrayList<ArrayList<String>> data) {

		for(ArrayList<String> example: data)
		{
			if(example.get(Headers_minimum.user_id.getValue()).equals(tmpusr))
				return Integer.parseInt(example.get(Headers_minimum.total_points.getValue()));
		}

		return -1;
	}

	private static HashMap<Integer, Integer> computeFLipItNi(HashMap<String,Integer> att_game_play) {


		HashMap<Integer, Integer> ni = new HashMap<Integer, Integer>();

		int[] count = new int[6];



		for(String ex: att_game_play.keySet())
		{
			int a = att_game_play.get(ex);
			//int a = Integer.parseInt(action);

			count[a]++;
		}
		for(int i=0; i<6; i++)
		{
			ni.put(i, count[i]);
		}

		return ni;



	}

	private static HashMap<Integer, Double> attExpPayoffs(HashMap<String, Integer> att_game_play,
			HashMap<String, Double> probs) {


		HashMap<Integer, Double> ui = new HashMap<Integer, Double>();

		int[] count = {0,0,0,0,0,0};
		Double[] uis = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

		int[][] target = new int[6][4];


		/*target[0][0] = 0;
		target[0][1] = -2;
		target[0][2] = 2;
		target[0][3] = 0;

		target[1][0] = 0;
		target[1][1] = -8;
		target[1][2] = 8;
		target[1][3] = 0;

		target[2][0] = 0;
		target[2][1] = -2;
		target[2][2] = 2;
		target[2][3] = 0;

		target[3][0] = 0;
		target[3][1] = -4;
		target[3][2] = -4;
		target[3][3] = 0;

		target[4][0] = 0;
		target[4][1] = -5;
		target[4][2] = 5;
		target[4][3] = 0;


		target[5][0] = 0;
		target[5][1] = 0;
		target[5][2] = 0;
		target[5][3] = 0;


		 */


		target[0][0] = 0;
		target[0][1] = -10;
		target[0][2] = 2;
		target[0][3] = 0;

		target[1][0] = 0;
		target[1][1] = -10;
		target[1][2] = 8;
		target[1][3] = 0;

		target[2][0] = 0;
		target[2][1] = -2;
		target[2][2] = 2;
		target[2][3] = 0;

		target[3][0] = 0;
		target[3][1] = -4;
		target[3][2] = 0;
		target[3][3] = 0;

		target[4][0] = 0;
		target[4][1] = -10;
		target[4][2] = 5;
		target[4][3] = 0;


		target[5][0] = 0;
		target[5][1] = 0;
		target[5][2] = 0;
		target[5][3] = 0;









		for(String ex: att_game_play.keySet())
		{
			int a = att_game_play.get(ex);



			String sa = a+"";
			double cov = 0;
			if(probs.containsKey(sa))
			{
				cov = probs.get(sa);
			}

			double u = cov*target[a][3] + (1-cov)*target[a][2];


			uis[a] += u;
			count[a]++;
		}




		for(int i=0; i<6; i++)
		{
			if(count[i] != 0)
			{
				uis[i] /= count[i];
			}

			ui.put(i, uis[i]);
		}

		return ui;


	}

	public static void computeLambdaExps() throws Exception {

		// create the data
		/**
		 * 1. load user data
		 * 2. prepare data for clustering 2d data (normalize the data)
		 * 3. cluster
		 * 4. For each cluster compute lambda
		 * 
		 */
		int k= 3; // how many clusters you want
		int numberofnodes = 6;

		ArrayList<ArrayList<String>> data =  Data.readData();

		ArrayList<String> users_refined = refineUser(data, -1, 1);

		ArrayList<ArrayList<String>>  data_refined = refineData(data,1, users_refined);

		//double[][] examples = prepareExamplesDTScorePoints(data_refined, users_refined);
		//double[][] examples = prepareExamplesNodeCostPoint(data_refined, users_refined);
		double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);


		/*double[][] dummydata = new double[6][4];

		dummydata[0][0] = 1.0;
		dummydata[0][1] = 2.0;
		dummydata[0][2] = 3.0;
		dummydata[0][3] = 5.0;

		dummydata[1][0] = 1.0;
		dummydata[1][1] = 2.0;
		dummydata[1][2] = 3.0;
		dummydata[1][3] = 5.0;



		dummydata[2][0] = 7.0;
		dummydata[2][1] = 8.0;
		dummydata[2][2] = 9.0;
		dummydata[2][3] = 10.0;

		dummydata[3][0] = 7.0;
		dummydata[3][1] = 8.0;
		dummydata[3][2] = 9.0;
		dummydata[3][3] = 10.0;


		dummydata[4][0] = 1.0;
		dummydata[4][1] = 3.0;
		dummydata[4][2] = 2.0;
		dummydata[4][3] = 1.0;

		dummydata[5][0] = 1.0;
		dummydata[5][1] = 3.0;
		dummydata[5][2] = 2.0;
		dummydata[5][3] = 1.0;*/


		//List<Integer>[] clusters = Weka.clusterUsers(k,normalizedexamples);



		List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);


		/**
		 * next use weka to cluster
		 */

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		/*MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		 */

		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

			pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}

		for(int cluster=0; cluster<k; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);

			int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes);


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}


			HashMap<String, Integer> att_game_play = buildOneStageGamePlay(users_groups, data_refined, 1);
			//HashMap<String, int[][]> def_game_play = buildGamePlay(users_refined, data_refined, 0);
			//HashMap<String, int[][]> reward = buildGameRewards(users_refined, data_refined);

			//printOneStageGamePlay(att_game_play);

			HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");
			String key = "EMPTY EMPTY"; 
			HashMap<String, Double> probs = strategy.get(key);

			//AdversaryModel.computeLambda(users_refined, att_game_play, def_game_play,reward, data_refined, 4);

			HashMap<Integer, Double > ui = attExpPayoffs(att_game_play, probs);


			//ArrayList<ArrayList<String>> data = Data.readData(lambda);

			HashMap<Integer, Integer> ni = computeFLipItNi(att_game_play);

			//HashMap<Integer, Double> ui = computeUi(data);

			int n=users_groups.size();

			double A=0;
			for(int action=0; action <6; action++)
			{
				double u = ui.get(action);
				double n_i = ni.get(action);

				A += (u*n_i);
			}

			double B = A/n;

			double[] coeffs = new double[6];

			for(int i=0; i<6; i++)
			{
				coeffs[i] = B-ui.get(i);
			}



			//int x=0;

			double estimatedlambda = estimateFlipItLambda(ni, n, ui);

			//Disconnect the proxy from MATLAB
			//proxy.disconnect();



			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);

				sumscore += getUserScore(tmpusr, data_refined);

				sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore /= users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambda);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}

		}

		// for each of the user groups compute lambda



	}

	private static double[][] prepareFrquencey(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int numberofnodes) {

		double [][] examples = new double[users_refined.size()][7]; // frequency , total points

		int exampleindex = 0;
		for(String user_id: users_refined)
		{
			
			int count[] = new int[numberofnodes];
			
			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					String def_order = tmpexample.get(Headers_minimum.pick_def_order.getValue());
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					if(def_order.equals("0") && (gameinstance>=4)) // asc, take 4th game instance to 6th
					{
						count[attackaction]++;

					}
					else if(def_order.equals("1") && (gameinstance<=3)) // desc, take 1st game instance to 3rd
					{
						count[attackaction]++;
					}

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop
			
			int findex =0;
			for(int c: count)
			{
				examples[exampleindex][findex++] = c;
			}
			examples[exampleindex][findex] = getUserScore(user_id, data_refined);;
			exampleindex++;
		}
		return examples;
	}

	private static int[] getAttackFrequency(ArrayList<String> users_groups, ArrayList<ArrayList<String>> data_refined, int numberofnodes) {

		int count[] = new int[numberofnodes];

		for(String user_id: users_groups)
		{
			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					String def_order = tmpexample.get(Headers_minimum.pick_def_order.getValue());
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					if(def_order.equals("0") && (gameinstance>=4)) // asc, take 4th game instance to 6th
					{
						count[attackaction]++;

					}
					else if(def_order.equals("1") && (gameinstance<=3)) // desc, take 1st game instance to 3rd
					{
						count[attackaction]++;
					}

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop
		}



		return count;

	}

	private static double[][] prepareExamplesNodeCostPoint(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined) {



		int[][] target = new int[6][2];


		target[0][0] = 10;
		target[0][1] = 8;


		target[1][0] = 10;
		target[1][1] = 2;

		target[2][0] = 4;
		target[2][1] = 2;


		target[3][0] = 4;
		target[3][1] = 8;

		target[4][0] = 10;
		target[4][1] = 5;



		target[5][0] = 0;
		target[5][1] = 0;




		//3 features per round, 3 games, 5 round per game , 45 features
		double examples[][] = new double[users_refined.size()][45]; // nodevalue,cost,pointforaction,


		int exampleindex = 0;
		for(String user_id: users_refined)
		{
			// get all the actions in every round and poins

			System.out.println("User : "+ user_id);
			int featureindex = 0;

			//examples[exampleindex][0] = exampleindex;

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					String def_order = tmpexample.get(Headers_minimum.pick_def_order.getValue());
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}


					int nodevalue = target[attackaction][0];
					int nodecost = target[attackaction][1];
					int attackerpoints = Integer.parseInt(tmpexample.get(Headers_minimum.attacker_points.getValue()));





					if(def_order.equals("0") && (gameinstance>=4)) // asc, take 4th game instance to 6th
					{
						System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-4;
						//featureindex = round + (featuregameinstance*5);
						System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 




					}
					else if(def_order.equals("1") && (gameinstance<=3)) // desc, take 1st game instance to 3rd
					{
						System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-1;
						//int featureindex = round + (featuregameinstance*5);
						System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 
					}

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop


			exampleindex++;
		}

		return examples;

	}

	private static ArrayList<String> getUserGroup(List<Integer> list, ArrayList<String> users_refined) {


		ArrayList<String> users = new ArrayList<String>();

		for(double index: list)
		{
			users.add(users_refined.get((int)index));
		}

		return users;


	}

	private static void printClusters(List<Double>[] clusters) {


		System.out.println();
		for(int i=0; i<clusters.length; i++)
		{
			System.out.print("cluster "+i + ": ");
			for(Double x: clusters[i])
			{
				System.out.print(x.intValue()+", ");
			}
			System.out.println();
		}




	}

	private static void printClustersInt(List<Integer>[] clusters) {


		System.out.println();
		for(int i=0; i<clusters.length; i++)
		{
			System.out.print("cluster "+i + ": ");
			for(Integer x: clusters[i])
			{
				System.out.print(x.intValue()+", ");
			}
			System.out.println();
		}




	}

	private static double[][] normalizeData(double[][] examples) {


		double[][] normalizedExamples = new double[examples.length][examples[0].length];

		for(int feature=0; feature<examples[0].length; feature++)
		{
			double dataLowHigh[] = getDataLow(examples, feature);
			double normalizedLowHigh[] = {0.0, 10};
			//System.out.println("Feature  "+ feature + ", low "+ dataLowHigh[0] + ", high "+ dataLowHigh[1]);

			for(int row=0; row<examples.length; row++)
			{
				double normx = normalize(examples[row][feature], dataLowHigh[0], dataLowHigh[1], normalizedLowHigh[0], normalizedLowHigh[1]);
				normalizedExamples[row][feature] = normx;
				System.out.println("Feature  "+ feature + ", low "+ dataLowHigh[0] + ", high "+ dataLowHigh[1] + ", value "+examples[row][feature]+ ", normval "+ normx);
			}

		}

		return normalizedExamples;

	}

	private static double[] getDataLow(double[][] examples, int feature) {

		double[] lowhigh = {Double.MAX_VALUE, Double.MIN_VALUE};

		for(int row=0; row<examples.length; row++)
		{
			if(examples[row][feature]<lowhigh[0])
			{
				lowhigh[0] = examples[row][feature];
			}
			else if(examples[row][feature]>lowhigh[1])
			{
				lowhigh[1] = examples[row][feature];
			}
		}
		return lowhigh;
	}

	/**
	 * 
	 * @param x
	 * @param dataLow
	 * @param dataHigh
	 * @param normalizedLow
	 * @param normalizedHigh
	 * @return
	 */
	public static double normalize(double x, double dataLow, double dataHigh, double normalizedLow, double normalizedHigh ) 
	{
		return ((x - dataLow) 
				/ (dataHigh - dataLow))
				* (normalizedHigh - normalizedLow) + normalizedLow;
	}




	private static void printData(ArrayList<String> users_refined, double[][] examples) {

		for(int i=0; i<examples.length; i++)
		{
			//System.out.print("id_"+ i + "="+i+"" );
			for(int j=0; j<examples[i].length; j++)
			{
				System.out.print(" f_"+j+"= "+ examples[i][j]);
			}
			System.out.println();
		}

	}

	/**
	 * prepare data for clustering using DT score and points
	 * @param data_refined
	 * @param users_refined
	 * @return
	 */
	private static double[][] prepareExamplesDTScorePoints(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined) {

		double[][] examples = new double[users_refined.size()][4];

		/**
		 * for each user compute DT scores 
		 */

		int userindex = 0;
		for(String usr_id: users_refined)
		{
			double mscore = getPersonalityScore(usr_id, data_refined, 0);
			double nscore = getPersonalityScore(usr_id, data_refined, 1);
			double pscore = getPersonalityScore(usr_id, data_refined, 2);
			double totalpoints = getUserScore(usr_id, data_refined);

			examples[userindex][0] = mscore;
			examples[userindex][1] = nscore;
			examples[userindex][2] = pscore;
			examples[userindex][3] = totalpoints;
			userindex++;
		}
		return examples;
	}

}

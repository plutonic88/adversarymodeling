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
import ega.games.MatrixGame;
import ega.games.MixedStrategy;
import ega.games.OutcomeDistribution;
import ega.games.OutcomeIterator;
import ega.solvers.RegretLearner;
import ega.solvers.SolverUtils;
import groupingtargets.ClusterTargets;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class AdversaryModelExps {

	public static Random rand = new Random(5);


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
	
	public static void getLambda() throws MatlabConnectionException, MatlabInvocationException
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

		/*QRESolver qre = new QRESolver(100);
		EmpiricalMatrixGame emg = new EmpiricalMatrixGame(gm);
		qre.setDecisionMode(QRESolver.DecisionMode.RAW);
		for(int i=0; i< gm.getNumPlayers(); i++ )
		{
			gamestrategy[i] = qre.solveGame(emg, i);
		}

		System.out.println("s");*/

		gamestrategy = RegretLearner.solveGame(gm);
		
		
		
		// parse data
		
		double[] lambdas = {.5 };
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
			
			
			
			int x=0;
			
			double estlambda = estimateLambda(ni, n, ui);
			
			
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
	    
	    System.out.println(eqn);
	    
	    proxy.eval(eqn);
	    proxy.eval("solve(Eq1, lambda)");
	    
	    double result = ((double[]) proxy.getVariable("lambda"))[0];
	    System.out.println("Result: " + result);

	    //Disconnect the proxy from MATLAB
	    proxy.disconnect();
	    
	   
	    return 0.0;
		
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
	
	

	public static void doDummyTest3() {

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

		/*QRESolver qre = new QRESolver(100);
		EmpiricalMatrixGame emg = new EmpiricalMatrixGame(gm);
		qre.setDecisionMode(QRESolver.DecisionMode.RAW);
		for(int i=0; i< gm.getNumPlayers(); i++ )
		{
			gamestrategy[i] = qre.solveGame(emg, i);
		}

		System.out.println("s");*/

		gamestrategy = RegretLearner.solveGame(gm);

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

}

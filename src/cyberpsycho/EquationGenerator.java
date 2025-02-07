package cyberpsycho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cyberpsycho.Data;

public class EquationGenerator {

	public static int treenodecount =0;





	public static void main(String[] args) throws Exception 
	{
		int DEPTH_LIMIT = 4;
		int naction = 6;

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTree(DEPTH_LIMIT, naction, noderewards);
		System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		System.out.println();
		printTree(root, naction);
		HashMap<String, ArrayList<DNode>> I = prepareInformationSets(root, DEPTH_LIMIT, naction);
		printInfoSet(I);

		HashMap<String, InfoSet> ISets = prepareInfoSet(I);

		printISets(ISets);

		//assignRewardToLeafNodes(ISets, root);

		//varify it
		//expUtility( 1, root, ISets, "d2_p0_1");


		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, "x(3)", "d3_p1_3_0");


		// need strategy for sequence
		HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");
		String key = "EMPTY EMPTY"; 
		HashMap<String, Double> probs = strategy.get(key);


		printMatLabCodeQRETest(ISets, root, naction);



		printMatLabCodeQRE(ISets, root, naction);

		//printMatLabCodeMLE(ISets, root);
		
		
		//TODO write code for loglikelihood, so that we can plug anything
		/**
		 * 1. create sequence and corresponding frequency
		 * 2. Use searching method for finding lambda with max ll
		 * 3. Inside ll computation function use frequency and the solution from a method
		 */


	}

	private static void printMatLabCodeQRE(HashMap<String, InfoSet> iSets, DNode root, int naction) {


		String[] keyset = new String[iSets.size()];
		int indx=0;

		for(String is: iSets.keySet())
		{
			keyset[indx++] = is;
		}

		Arrays.sort(keyset);


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}
		
		



		// print equations for every action in every information set
		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, I, "x(3)", "d3_p1_3_0");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			// find the player
			System.out.println("\n");

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_prob.keySet())
			{

				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);



				String[] x = qre_prob.split("_");
				int action = Integer.parseInt(qre_prob.split("_")[3]);


				if(!doneprobs.contains(qre_prob))
				{

					if(iset.player==0)
					{
						// for likelihood func
						//System.out.println("response_"+iset.qre_prob.get(nodeid) + " = " + iset.qre_prob.get(nodeid));

						//TODO
						// for equilibrium, need to adjust other values too
						String eqn = generateEqnInInformationSetQRE(iSets, iset.id, action , root, qre_var, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");
					}
					else if(iset.player==1)
					{
						String eqn = generateEqnInInformationSetQRE(iSets, iset.id, action , root, qre_var, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");

					}
					doneprobs.add(qre_prob);

				}

			}
		}

		/*

		System.out.println();
		String err ="";
		HashMap<String, String> seterror = new HashMap<String, String>();
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			err = "err_"+iset.id+" = ";
			int index = 0;
			ArrayList<String> done = new ArrayList<String>();
			for(String qre_var: iset.qre_var.values())
			{
				if(!done.contains(qre_var))
				{
					if(index==0)
					{
						err +=  qre_var;
					}
					else
					{
						err += " + " + qre_var;
					}
					done.add(qre_var);
				}
				index++;
			}
			System.out.println(err +" - 1 ;");
			seterror.put(iset.id, err +" - 1 ;");
		}
		System.out.println();




		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{
				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println("belief_error_"+qre_prob +" = "+ qre_prob +" - response_"+qre_prob +";");
					doneprobs.add(qre_prob);
				}
			}
		}

*/

		System.out.println();

		System.out.print("belief_error_vector = [ ");


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("err_"+iset.id + "; ");
		}



		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			// print all linear errors




			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);


				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("belief_error_"+qre_prob);



					System.out.print("; ");

					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.println("]; "+"\n");


/*


		// create linear constraint using Aeq b


		int infcount = 0;

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			System.out.print("A"+infcount+" = [ ");

			int innercount = 0;

			for(String isetname1: keyset)
			{
				InfoSet iset1 = iSets.get(isetname1);


				if(iset.id.equals(iset1.id))
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("1 ");
						if(innercount<(iSets.size()*naction-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}
				else
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("0 ");
						if(innercount<((iSets.size()*naction)-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}




			}
			System.out.println(" ]; ");
			infcount++;

		}


		int count = 0;

		System.out.print("Aeq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("A"+count);
			count++;
			if(count<iSets.size())
			{
				System.out.print(";");
			}
		}
		System.out.println("]; "+"\n");



		count = 0;

		System.out.print("beq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("1");
			count++;
			if(count<iSets.size())
			{
				System.out.print("; ");
			}
		}
		System.out.println("]; ");






		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.println("\n");

		System.out.print("lb = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		System.out.print("ub = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("1, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");
*/

		System.out.print("x0 = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.166;");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		HashMap<String, String> container = new HashMap<String, String>();



		for(String isetname: keyset)
		{


			InfoSet iset = iSets.get(isetname);

			if(iset.player==1 && iset.depth>0)
			{

				/*
			ArrayList<String> doneprobs = new ArrayList<String>();
				 */
				// find the history of any node

				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node.parent;
				
				int pl = 1;

				while(tmp != null && tmp.nodeid != 0)
				{
					
					if(pl==0)
					{
						histp0 += (tmp.prevaction+1);
					}
					else
					{
						histp1 += (tmp.prevaction+1);
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );
				
				
				String key = histp0 +"0"+histp1;
				String value = iset.qre_var.get(child.nodeid);
				container.put(key, value);

				int z=1;
			}
			
			


			/*for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.5;");
					doneprobs.add(qre_prob);
				}



			}*/
		}
		
		String keyst = "{";
		String valuest = "{";
		
		
		
		String[] contkey = new String[container.keySet().size()];
		int ind = 0;
		
		for(String key: container.keySet())
		{
			contkey[ind++] = key;
		}
		
		Arrays.sort(contkey);
		
		
		int index = 0;
		
		
		
		for(String key: contkey)
		{
			String value = container.get(key);
			
			String tmpval = value.substring(2, value.length()-1);
			
			
			valuest += "\'"+tmpval +"\'";;
			keyst += "\'"+key +"\'";
			if(index<container.keySet().size()-1)
			{
				keyst += ", ";
				valuest += ", ";
			}
			
			index ++;
		}
		keyst += "}";
		valuest += "}";
		
		
		
		
		
		
		System.out.println("M = containers.Map(" + keyst +","+ valuest + ");" );










	}



	private static void printMatLabCodeQRETest(HashMap<String, InfoSet> iSets, DNode root, int naction) {


		String[] keyset = new String[iSets.size()];
		int indx=0;

		for(String is: iSets.keySet())
		{
			keyset[indx++] = is;
		}

		Arrays.sort(keyset);


		/*for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}*/



		// print equations for every action in every information set
		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, I, "x(3)", "d3_p1_3_0");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			// find the player
			System.out.println("\n");

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_prob.keySet())
			{

				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				double qre_var_val = iset.qre_var_val.get(nodeid);



				String[] x = qre_prob.split("_");
				int action = Integer.parseInt(qre_prob.split("_")[3]);


				if(!doneprobs.contains(qre_prob))
				{

					if(iset.player==0)
					{
						// for likelihood func
						//System.out.println("response_"+iset.qre_prob.get(nodeid) + " = " + iset.qre_prob.get(nodeid));

						//TODO
						// for equilibrium, need to adjust other values too
						double eqn = generateEqnInInformationSetQRE_V(iSets, iset.id, action , root, qre_var_val, qre_prob);
						System.out.println(iset.id+", action "+action+", "+ eqn +";");
						int c=1;

					}
					else if(iset.player==1)
					{
						double eqn = generateEqnInInformationSetQRE_V(iSets, iset.id, action , root, qre_var_val, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");

					}
					doneprobs.add(qre_prob);

				}

			}
		}



		/*System.out.println();
		String err ="";
		HashMap<String, String> seterror = new HashMap<String, String>();
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			err = "err_"+iset.id+" = ";
			int index = 0;
			ArrayList<String> done = new ArrayList<String>();
			for(String qre_var: iset.qre_var.values())
			{
				if(!done.contains(qre_var))
				{
					if(index==0)
					{
						err +=  qre_var;
					}
					else
					{
						err += " + " + qre_var;
					}
					done.add(qre_var);
				}
				index++;
			}
			System.out.println(err +" - 1 ;");
			seterror.put(iset.id, err +" - 1 ;");
		}
		System.out.println();
		 */



		/*for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{
				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println("belief_error_"+qre_prob +" = "+ qre_prob +" - response_"+qre_prob +";");
					doneprobs.add(qre_prob);
				}
			}
		}*/



		/*System.out.println();

		System.out.print("belief_error_vector = [ ");


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("err_"+iset.id + "; ");
		}



		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			// print all linear errors




			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);


				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("belief_error_"+qre_prob);



					System.out.print("; ");

					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.println("]; "+"\n");
		 */




		// create linear constraint using Aeq b


		/*int infcount = 0;

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			System.out.print("A"+infcount+" = [ ");

			int innercount = 0;

			for(String isetname1: keyset)
			{
				InfoSet iset1 = iSets.get(isetname1);


				if(iset.id.equals(iset1.id))
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("1 ");
						if(innercount<(iSets.size()*naction-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}
				else
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("0 ");
						if(innercount<((iSets.size()*naction)-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}




			}
			System.out.println(" ]; ");
			infcount++;

		}
		 */

		/*int count = 0;

		System.out.print("Aeq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("A"+count);
			count++;
			if(count<iSets.size())
			{
				System.out.print(";");
			}
		}
		System.out.println("]; "+"\n");



		count = 0;

		System.out.print("beq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("1");
			count++;
			if(count<iSets.size())
			{
				System.out.print("; ");
			}
		}
		System.out.println("]; ");

		 */




		/*for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}*/

		/*System.out.println("\n");

		System.out.print("lb = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		System.out.print("ub = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("1, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		System.out.print("x0 = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.5;");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");
		 */









	}

	private static HashMap<Integer, Integer[]> createNodeRewards(int naction) {


		HashMap<Integer, Integer[]> values = new HashMap<Integer, Integer[]>();

		Integer [] v = {10, 8};
		values.put(0, v);

		Integer[] v1 = {10, 2};
		values.put(1, v1);

		Integer [] v2 = {4, 2};
		values.put(2, v2);

		Integer[] v3 = {4, 8};
		values.put(3, v3);


		Integer [] v4 = {10, 5};
		values.put(4, v4);

		Integer[] v5 = {0, 0};
		values.put(5, v5);





		return values;
	}

	private static String generateEqnInInformationSet(HashMap<String, InfoSet> iSets, String infosetname, int action, DNode root, String variable, String prob) {


		// get the information set
		// get the variable for that action
		// get the probability for the action
		// u(action') - u(actoin)



		InfoSet infoset = iSets.get(infosetname);

		int naction = infoset.nodes.get(0).child.size();

		HashMap<Integer, String> exputilities = new HashMap<Integer, String>();

		//System.out.println("\nInfoset name "+ infosetname);
		for(int i =0; i<naction ; i++)
		{
			String expu_action = expUtility(i, root, iSets, infosetname);

			//System.out.println("Action "+ i + ", expected utility "+ expu_action);

			exputilities.put(i, expu_action);

		}


		String eqn = "";
		for(int i =0; i<naction ; i++)
		{



			String expterm = "";

			if(i!=action)
			{
				expterm = "exp(lambda*("+ "("+exputilities.get(i) +") - " +  "(" + exputilities.get(action) + ")" + "))"; 
			}
			else
			{
				expterm = "1"; 
			}

			if(i!=0)
			{
				eqn =  eqn +" + "+expterm;
			}
			else
			{
				eqn =  eqn + expterm;
			}
		}


		eqn = "response_"+prob +" = "+  variable +"*("+ eqn + ") - 1" ;

		//System.out.println("Eqn for variable "+ variable + ": "+ eqn);


		return eqn;











	}


	private static String generateEqnInInformationSetQRE(HashMap<String, InfoSet> iSets, String infosetname, int action, DNode root, String variable, String prob) {


		// get the information set
		// get the variable for that action
		// get the probability for the action
		// u(action') - u(actoin)



		InfoSet infoset = iSets.get(infosetname);

		int naction = infoset.nodes.get(0).child.size();

		HashMap<Integer, String> exputilities = new HashMap<Integer, String>();

		//System.out.println("\nInfoset name "+ infosetname);
		for(int i =0; i<naction ; i++)
		{
			String expu_action = expUtilityQRE(i, root, iSets, infosetname);

			//System.out.println("Action "+ i + ", expected utility "+ expu_action);

			exputilities.put(i, expu_action);

		}


		String eqn = "";
		for(int i =0; i<naction ; i++)
		{



			String expterm = "";

			if(i!=action)
			{
				expterm = "exp(lambda*("+ "("+exputilities.get(i) +") - " +  "(" + exputilities.get(action) + ")" + "))"; 
			}
			else
			{
				expterm = "1"; 
			}

			if(i!=0)
			{
				eqn =  eqn +" + "+expterm;
			}
			else
			{
				eqn =  eqn + expterm;
			}
		}


		eqn = "response_"+prob +" = "+  variable +"*("+ eqn + ") - 1" ;

		//System.out.println("Eqn for variable "+ variable + ": "+ eqn);


		return eqn;











	}



	private static double generateEqnInInformationSetQRE_V(HashMap<String, InfoSet> iSets, String infosetname, int action, DNode root, double variable, String prob) {


		// get the information set
		// get the variable for that action
		// get the probability for the action
		// u(action') - u(actoin)


		int lambda = 2;



		InfoSet infoset = iSets.get(infosetname);

		int naction = infoset.nodes.get(0).child.size();

		HashMap<Integer, Double> exputilities = new HashMap<Integer, Double>();

		//System.out.println("\nInfoset name "+ infosetname);
		for(int i =0; i<naction ; i++)
		{
			double expu_action = expUtilityQRE_V(i, root, iSets, infosetname);

			//System.out.println("Action "+ i + ", expected utility "+ expu_action);

			exputilities.put(i, expu_action);

		}


		double eqn = 0.0;


		for(int i =0; i<naction ; i++)
		{


			double expterm = 1;


			if(i!=action)
			{
				//expterm = "exp(lambda*("+ "("+exputilities.get(i) +") - " +  "(" + exputilities.get(action) + ")" + "))"; 

				expterm = Math.exp(lambda* (exputilities.get(i) - exputilities.get(action)));

			}
			else
			{
				expterm = 1; 
			}

			if(i!=0)
			{
				eqn =  eqn +expterm;
			}
			else
			{
				eqn =  eqn + expterm;
			}
		}


		eqn =  variable*eqn - 1 ;

		//System.out.println("Eqn for variable "+ variable + ": "+ eqn);


		return eqn;


	}




	private static void printMatLabCodeMLE(HashMap<String,InfoSet> iSets, DNode root) {


		// print initialization


		String[] keyset = new String[iSets.size()];
		int indx=0;

		for(String is: iSets.keySet())
		{
			keyset[indx++] = is;
		}

		Arrays.sort(keyset);


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}



		// print equations for every action in every information set
		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, I, "x(3)", "d3_p1_3_0");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			// find the player
			System.out.println("\n");

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_prob.keySet())
			{

				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);



				String[] x = qre_prob.split("_");
				int action = Integer.parseInt(qre_prob.split("_")[3]);


				if(!doneprobs.contains(qre_prob))
				{

					if(iset.player==0)
					{
						// for likelihood func
						System.out.println("response_"+iset.qre_prob.get(nodeid) + " = " + iset.qre_prob.get(nodeid)+";");

						//TODO
						// for equilibrium, need to adjust other values too
						//String eqn = generateEqnInInformationSet(iSets, iset.id, action , root, qre_var, qre_prob);
						//System.out.println("response_"+qre_prob + " = " + eqn);
					}
					else if(iset.player==1)
					{
						String eqn = generateEqnInInformationSet(iSets, iset.id, action , root, qre_var, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");

					}
					doneprobs.add(qre_prob);

				}

			}




		}




	}

	public static void printISets(HashMap<String,InfoSet> iSets) 
	{

		for(InfoSet iset: iSets.values())
		{
			System.out.println("\nISet : "+ iset.id + ", player "+ iset.player + ", depth "+ iset.depth);
			System.out.print("nodes : ");
			for(DNode node: iset.nodes)
			{
				System.out.print(node.nodeid+" ");
			}
			System.out.println();
			for(Integer nodeid: iset.qre_prob.keySet())
			{
				System.out.println(nodeid + " prob:  "+ iset.qre_prob.get(nodeid));
			}

			for(Integer nodeid: iset.qre_var.keySet())
			{
				System.out.println(nodeid + " var :  "+ iset.qre_var.get(nodeid));
			}

		}

	}

	public static HashMap<String, InfoSet> prepareInfoSet(HashMap<String, ArrayList<DNode>> I) {


		HashMap<String, InfoSet> isets = new HashMap<String, InfoSet>();

		String[] keyset = new String[I.keySet().size()];


		int index = 0;
		for(String is: I.keySet())
		{
			keyset[index++] = is;
		}

		Arrays.sort(keyset);


		for(String is: keyset)
		{
			//System.out.println(is);
			InfoSet infset = makeObj(is, I.get(is));
			isets.put(is ,infset);
		}


		return isets;
	}

	private static InfoSet makeObj(String is, ArrayList<DNode> nodes) {

		int player = nodes.get(0).player;
		int depth = nodes.get(0).depth;

		InfoSet obj = new InfoSet(player, depth, is);


		for(DNode n: nodes)
		{
			n.infoset = is;
			obj.nodes.add(n);
		}



		if(player==0)
		{

			for(DNode node: nodes)
			{
				for(DNode child: node.child.values())
				{
					obj.qre_prob.put(child.nodeid, is+"_"+child.prevaction);
					obj.qre_var.put(child.nodeid, "x("+InfoSet.varcount+")");

					//obj.qre_prob_val.put(child.nodeid, 0.5);
					//obj.qre_var_val.put(child.nodeid, 0.5);



					InfoSet.varcount++;
				}
			}
		}
		else if(player==1)
		{

			for(int action=0; action<nodes.get(0).child.size(); action++)
			{
				for(DNode node: nodes)
				{
					for(DNode child: node.child.values())
					{
						if(child.prevaction == action)
						{
							obj.qre_prob.put(child.nodeid, is+"_"+child.prevaction);
							obj.qre_var.put(child.nodeid, "x("+InfoSet.varcount+")");

							//obj.qre_prob_val.put(child.nodeid, 0.5);
							//obj.qre_var_val.put(child.nodeid, 0.5);
						}

					}
				}
				InfoSet.varcount++;
			}
		}



		return obj;
	}

	private static String expUtility(int action, DNode root, HashMap<String,InfoSet> iSets, String infosetname)
	{
		String exputility = "";
		String probtoreach = "";
		String continuationvalue = "";
		int nodecount = 0;
		for(DNode node: iSets.get(infosetname).nodes)
		{
			// for node find the probability to reach the information set
			probtoreach = getProbToReachInfoNode(node, root, iSets, infosetname);

			ArrayList<Integer> seqofactions = getSequenceOfActions(node, root, iSets, infosetname);

			if(probtoreach.equals(""))
			{
				probtoreach = "1";
			}

			seqofactions.add(action);

			//System.out.println("\nNode "+ node.nodeid + ", Infoset "+ node.infoset + ", prob to reach : "+ probtoreach);


			// consider prob of playing action 1
			// play the continuation game
			DNode nextroot = node.child.get(action);
			continuationvalue = playContinuationGame(nextroot, "", iSets, seqofactions, "");
			nodecount++;

			/*System.out.println("\nInfoset "+ infosetname);
			System.out.println("In node "+ node.nodeid);
			System.out.println("Prob to reach infoset "+probtoreach);
			System.out.println("Continuation value "+continuationvalue);*/


			exputility += probtoreach +"*("+continuationvalue + ")";
			if(nodecount< iSets.get(infosetname).nodes.size())
			{
				exputility += "+";
			}
		}



		//System.out.println("\nEXP : "+exputility+"\n");

		return exputility;


	}


	private static String expUtilityQRE(int action, DNode root, HashMap<String,InfoSet> iSets, String infosetname)
	{
		String exputility = "";
		String probtoreach = "";
		String continuationvalue = "";
		int nodecount = 0;
		int player = iSets.get(infosetname).player;
		for(DNode node: iSets.get(infosetname).nodes)
		{
			// for node find the probability to reach the information set
			probtoreach = getProbToReachInfoNodeQRE(node, root, iSets, infosetname);

			ArrayList<Integer> seqofactions = getSequenceOfActionsQRE(node, root, iSets, infosetname);

			if(probtoreach.equals(""))
			{
				probtoreach = "1";
			}

			seqofactions.add(action);

			//System.out.println("\nNode "+ node.nodeid + ", Infoset "+ node.infoset + ", prob to reach : "+ probtoreach);


			// consider prob of playing action 1
			// play the continuation game
			DNode nextroot = node.child.get(action);
			continuationvalue = playContinuationGameQRE(nextroot, "", iSets, seqofactions, "", player);
			nodecount++;

			/*System.out.println("\nInfoset "+ infosetname);
			System.out.println("In node "+ node.nodeid);
			System.out.println("Prob to reach infoset "+probtoreach);
			System.out.println("Continuation value "+continuationvalue);*/


			exputility += probtoreach +"*("+continuationvalue + ")";
			if(nodecount< iSets.get(infosetname).nodes.size())
			{
				exputility += "+";
			}
		}



		//System.out.println("\nEXP : "+exputility+"\n");

		return exputility;


	}


	private static double expUtilityQRE_V(int action, DNode root, HashMap<String,InfoSet> iSets, String infosetname)
	{
		double exputility = 0.0;
		double probtoreach = 1;
		double continuationvalue = 1;
		int nodecount = 0;
		int player = iSets.get(infosetname).player;
		for(DNode node: iSets.get(infosetname).nodes)
		{
			// for node find the probability to reach the information set
			probtoreach = getProbToReachInfoNodeQRE_V(node, root, iSets, infosetname);

			ArrayList<Integer> seqofactions = getSequenceOfActionsQRE(node, root, iSets, infosetname);

			/*if(probtoreach.equals(""))
			{
				probtoreach = 1;
			}*/

			seqofactions.add(action);

			//System.out.println("\nNode "+ node.nodeid + ", Infoset "+ node.infoset + ", prob to reach : "+ probtoreach);


			// consider prob of playing action 1
			// play the continuation game
			DNode nextroot = node.child.get(action);
			continuationvalue = playContinuationGameQRE_V(nextroot, "", iSets, seqofactions, "", player);
			nodecount++;

			/*System.out.println("\nInfoset "+ infosetname);
			System.out.println("In node "+ node.nodeid);
			System.out.println("Prob to reach infoset "+probtoreach);
			System.out.println("Continuation value "+continuationvalue);*/


			exputility += probtoreach *(continuationvalue );
			if(nodecount< iSets.get(infosetname).nodes.size())
			{
				//exputility += "+";
			}
		}



		//System.out.println("\nEXP : "+exputility+"\n");

		return exputility;


	}

	private static ArrayList<Integer> getSequenceOfActions(DNode node, DNode root, HashMap<String, InfoSet> iSets,
			String infosetname) {


		DNode tempnode = node;

		String prb = "";

		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + tempnode.parent.infoset + "_"+ tempnode.prevaction;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}




		return revseq;
	}

	private static ArrayList<Integer> getSequenceOfActionsQRE(DNode node, DNode root, HashMap<String, InfoSet> iSets,
			String infosetname) {


		DNode tempnode = node;

		String prb = "";

		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}




		return revseq;
	}


	private static String playContinuationGame(DNode nextroot, String conplay, HashMap<String,InfoSet> iSets, ArrayList<Integer> seqofactions, String godeep) {



		if(nextroot.leaf)
		{
			//compute reward for sequence
			return nextroot.attacker_reward+"";
		}

		// find the informations set

		String infsetname = nextroot.infoset;
		// find the variable associated with the node
		InfoSet iset = iSets.get(infsetname);

		String tmpplay = "";
		int childcount = 0;
		for(DNode child: nextroot.child.values())
		{

			String prob = "";
			if(nextroot.player==0)
			{
				prob = iset.qre_prob.get(child.nodeid); 
			}
			else
			{
				prob = iset.qre_var.get(child.nodeid); 
			}

			String val = playContinuationGame(child, conplay, iSets, seqofactions, godeep + " "+ child.prevaction);
			childcount++;

			tmpplay += prob +"*("+ val +")";

			if(childcount<nextroot.child.size())
			{
				tmpplay += "+";
			}
		}
		conplay = tmpplay;
		return conplay;
	}


	private static String playContinuationGameQRE(DNode nextroot, String conplay, HashMap<String,InfoSet> iSets, ArrayList<Integer> seqofactions, String godeep, int player) {



		if(nextroot.leaf)
		{
			//compute reward for sequence
			if(player==0)
			{
				return nextroot.defender_reward+"";
			}
			return nextroot.attacker_reward+"";


		}

		// find the informations set

		String infsetname = nextroot.infoset;
		// find the variable associated with the node
		InfoSet iset = iSets.get(infsetname);

		String tmpplay = "";
		int childcount = 0;
		for(DNode child: nextroot.child.values())
		{

			String prob = "";
			if(nextroot.player==0)
			{
				prob = iset.qre_var.get(child.nodeid); 
			}
			else
			{
				prob = iset.qre_var.get(child.nodeid); 
			}

			String val = playContinuationGameQRE(child, conplay, iSets, seqofactions, godeep + " "+ child.prevaction, player);
			childcount++;

			tmpplay += prob +"*("+ val +")";

			if(childcount<nextroot.child.size())
			{
				tmpplay += "+";
			}
		}
		conplay = tmpplay;
		return conplay;
	}



	private static double playContinuationGameQRE_V(DNode nextroot, String conplay, HashMap<String,InfoSet> iSets, ArrayList<Integer> seqofactions, String godeep, int player) {



		if(nextroot.leaf)
		{
			//compute reward for sequence
			if(player==0)
			{
				return nextroot.defender_reward;
			}
			return nextroot.attacker_reward;


		}

		// find the informations set

		String infsetname = nextroot.infoset;
		// find the variable associated with the node
		InfoSet iset = iSets.get(infsetname);

		double tmpplay = 0;;
		int childcount = 0;
		for(DNode child: nextroot.child.values())
		{

			double prob = 1;
			if(nextroot.player==0)
			{
				prob = iset.qre_var_val.get(child.nodeid); 
			}
			else
			{
				prob = iset.qre_var_val.get(child.nodeid); 
			}

			double val = playContinuationGameQRE_V(child, conplay, iSets, seqofactions, godeep + " "+ child.prevaction, player);
			childcount++;

			tmpplay += prob *( val );

			if(childcount<nextroot.child.size())
			{
				//tmpplay += "+";
			}
		}
		//conplay = tmpplay;
		return tmpplay;
	}

	private static InfoSet getInfoSet(String infsetname, ArrayList<InfoSet> iSets) {


		for(InfoSet is: iSets)
		{
			if(is.id.equals(infsetname))
			{
				return is;
			}
		}

		return null;
	}

	private static String getProbToReachInfoNode(DNode node, DNode root, HashMap<String,InfoSet> iSets, String infosetname) {


		DNode tempnode = node;

		String prb = "";




		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + tempnode.parent.infoset + "_"+ tempnode.prevaction;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}
			tempnode = tempnode.parent;
		}


		return prb;
	}


	private static String getProbToReachInfoNodeQRE(DNode node, DNode root, HashMap<String,InfoSet> iSets, String infosetname) {


		DNode tempnode = node;

		String prb = "";






		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}
			tempnode = tempnode.parent;
		}


		return prb;
	}

	private static double getProbToReachInfoNodeQRE_V(DNode node, DNode root, HashMap<String,InfoSet> iSets, String infosetname) {


		DNode tempnode = node;

		double prb = 1;






		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb * infset.qre_var_val.get(tempnode.nodeid);;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb * infset.qre_var_val.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				//prb += "*";
			}
			tempnode = tempnode.parent;
		}


		return prb;
	}


	public static void printInfoSet(HashMap<String, ArrayList<DNode>> I) 
	{
		System.out.println();
		for(String infset: I.keySet())
		{
			System.out.print("Information set "+ infset +": ");
			for(DNode n: I.get(infset))
			{
				System.out.print(n.nodeid+" ");
			}
			System.out.println();

		}


	}

	public static HashMap<String, ArrayList<DNode>> prepareInformationSets(DNode root, int DEPTH_LIMIT, int naction) {


		HashMap<String, ArrayList<DNode>> I = new HashMap<String, ArrayList<DNode>>();

		int player = 1;

		System.out.println();
		for(int depth=0; depth<DEPTH_LIMIT; depth++)
		{
			player ^= 1;
			ArrayList<DNode> depthnodes = getNodes(depth, root);
			System.out.print("Depth "+ depth + " nodes ");
			for(DNode n: depthnodes)
			{
				System.out.print(n.nodeid +" ");
			}
			System.out.println();

			if(player==0)
			{
				int countI = 0;
				for(DNode n: depthnodes)
				{
					String key = "d"+depth+"_p"+player + "_" + countI;
					ArrayList<DNode> ns = new ArrayList<DNode>();
					n.infoset = key;
					ns.add(n);
					I.put(key, ns);
					countI++;
				}
			}
			else if(player==1)
			{
				// collect nodes who has same parent

				int prevdepth = depth-1;

				ArrayList<DNode> prevdepthnodes = getNodes(prevdepth, root);

				int countI =0;
				for(DNode parentnode: prevdepthnodes)
				{
					ArrayList<DNode> ns = new ArrayList<DNode>();
					String key = "d"+depth+"_p"+player + "_" + countI;
					for(DNode n: depthnodes)
					{
						if(n.parent.nodeid == parentnode.nodeid)
						{
							n.infoset = key;
							ns.add(n);
						}
					}

					I.put(key, ns);
					countI++;


				}


			}
		}

		return I;


	}

	private static ArrayList<DNode> getNodes(int depth, DNode root) {


		ArrayList<DNode> nodes = new ArrayList<DNode>();
		collectNodes(depth, 0, root, nodes);
		return nodes;
	}

	private static void collectNodes(int depth, int curdepth, DNode node, ArrayList<DNode> nodes) {


		if(depth==curdepth)
		{
			nodes.add(node);
			return;
		}

		for(int action = 0; action<node.child.size(); action++)
		{
			collectNodes(depth, curdepth+1, node.child.get(action), nodes);
		}

	}

	private static void printTree(DNode node, int naction) {


		if(node.child.get(0)==null)
			return;

		for(int action =0; action<naction; action++)
		{
			DNode c = node.child.get(action);
			System.out.println("Node id "+ c.nodeid + ", parent : "+
					node.nodeid + ", player "+ c.player +
					", leaf "+ c.leaf + ", prevaction "+ c.prevaction);
			printTree(node.child.get(action), naction);
		}

	}

	private static DNode createGameTree(int DEPTH_LIMIT, int naction, HashMap<Integer,Integer[]> noderewards) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount++;
		genTree(0, naction, DEPTH_LIMIT, root, noderewards);
		return root;

	}

	private static void genTree(int depth, int naction, int DEPTH_LIMIT, DNode node, HashMap<Integer,Integer[]> noderewards) 
	{

		if(depth==DEPTH_LIMIT)
		{
			int defreward = computeDefenderReward(node, noderewards);
			int reward = computeAttackerReward(node, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			return;
		}

		for(int action =0; action<naction; action++)
		{
			DNode child = new DNode(treenodecount, depth+1, node.player^1);
			treenodecount++;
			child.parent = node;
			child.prevaction = action;
			node.child.put(action, child);
			genTree(depth+1, naction, DEPTH_LIMIT, child, noderewards);


		}

	}

	private static int computeAttackerReward(DNode node, HashMap<Integer,Integer[]> noderewards) {

		DNode tempnode = node;


		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}

		int reward = computeAttackerReward(revseq, noderewards);
		return reward;
	}

	private static int computeDefenderReward(DNode node, HashMap<Integer,Integer[]> noderewards) {

		DNode tempnode = node;


		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}

		int reward = computeDefenderReward(revseq, noderewards);
		return reward;
	}

	private static int computeAttackerReward(ArrayList<Integer> seq, HashMap<Integer, Integer[]> noderewards) {





		int[] controllers = new int[noderewards.size()];

		int attpoints = 0;
		int defpoints = 0;

		/*//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 */
		//System.out.println();
		for(int i= 0; i<(seq.size()/2); i++)
		{

			int defaction = seq.get(2*i);
			int attaction = seq.get(2*i+1);


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action
			attpoints -= attcost;
			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
				controllers[attaction] = 1;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((controllers[j] != controllers[attaction]) && (controllers[j]==1))
				{
					int attreward = noderewards.get(attaction)[0];
					attpoints += attreward;
				}
			}
		}
		System.out.print( attpoints+", ");

		return attpoints;
	}


	private static int computeDefenderReward(ArrayList<Integer> seq, HashMap<Integer, Integer[]> noderewards) {





		int[] controllers = new int[noderewards.size()];

		int attpoints = 0;
		int defpoints = 0;

		/*//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 */
		//System.out.println();
		for(int i= 0; i<(seq.size()/2); i++)
		{

			int defaction = seq.get(2*i);
			int attaction = seq.get(2*i+1);


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action
			attpoints -= attcost;
			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
				controllers[attaction] = 1;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((controllers[j] != controllers[attaction]) && (controllers[j]==1))
				{
					int attreward = noderewards.get(attaction)[0];
					attpoints += attreward;
				}
			}
		}
		System.out.print( defpoints+", ");

		return defpoints;
	}

	public static DNode buildGameTree(int DEPTH_LIMIT, int naction) {
		
		
		

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTree(DEPTH_LIMIT, naction, noderewards);
		System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		System.out.println();
		printTree(root, naction);
		
		
		return root;
		
	}

	
	
	
	public static void computeAttackerBestResponse(HashMap<String, InfoSet> isets, HashMap<String, int[]> attackfrequency, int naction,
			HashMap<String,HashMap<String,Double>> defstrategy, DNode root, int depthlimit, HashMap<Integer,ArrayList<String>> depthinfoset, double lambda) throws Exception 
	{
		
		
		String[] keyset = new String[isets.size()];
		int indx=0;

		for(String is: isets.keySet())
		{
			keyset[indx++] = is;
		}
		Arrays.sort(keyset);
		
		
		
		
		/**
		 * 1. for all information set in a depth compute attacker best response
		 * 2. 
		 * 
		 */
		
		for(int depth=depthlimit-1; depth>0; depth -=2)
		{
			System.out.println("depth "+ depth);
			
			
			for(String isetname: depthinfoset.get(depth))
			{
				System.out.println("iset : "+ isetname);
				InfoSet iset = isets.get(isetname);
				
				/**
				 * now compute the sequence to reach the information set of defender
				 */
				
				/**
				 * find the information set of defender that led to iset
				 * 
				 * find a parent node pnode
				 * 
				 * find the infoset of the pnode 
				 */
				
				DNode parentnode = iset.nodes.get(0).parent;
				
				
				
				String parentisetname = parentnode.infoset;
				
				System.out.println("parent iset "+ parentisetname);
				
				
				InfoSet parentiset = isets.get(parentisetname);
				
				
				/**
				 * now find the sequence that led to parentiset
				 */
				
				//System.out.println(parentisetname+ " probs : ");
				for(Integer a: parentiset.qre_prob_val.keySet())
				{
					System.out.println(a+" : "+ parentiset.qre_prob_val.get(a));
				}
				
				
				/**
				 * now compute expected utiilty for attacker for playing every action using reward for the child nodes
				 */
				
				/**
				 * for every action of attacker in every node compute the expected utility
				 */
				
				HashMap<Integer, Double> attackerexputility = new HashMap<Integer, Double>();
				double sumexputility = 0; // sum of exponent of utility
				
				for(int action=0; action<naction; action++)
				{
					
					double sumexpval = 0;
					
					System.out.println("action "+ action);
					
					for(DNode infnode: iset.nodes)
					{
						
						/**
						 * get the defender prob
						 */
						
						System.out.println("Node  "+ infnode.nodeid);
						
						double defprob = parentiset.qre_prob_val.get(infnode.prevaction);
						
						System.out.println("def prob  "+ defprob);
						/**
						 * get attacker utility
						 */
						// get the child node for playing action
						
						DNode child = infnode.child.get(action);
						
						System.out.println("child node  "+ child.nodeid);
						
						
						double attutility = child.attacker_reward;
						
						
						System.out.println("attutility  "+ attutility);
						
						
						double tmpattackexputility = defprob*attutility;
						
						
						System.out.println("tmpattackexputility  "+ tmpattackexputility);
						
						sumexpval += tmpattackexputility;
						
						System.out.println("sumexpval  "+ sumexpval);
						
						
					}
					
					sumexputility += Math.exp(lambda*sumexpval);
					
					System.out.println("sumexputility  "+ sumexputility);
					
					attackerexputility.put(action, sumexpval);
					
				}
				
				
				/**
				 * now compute the Q-BR
				 */
				
				HashMap<Integer, Double> qbr = new HashMap<Integer, Double>();
				
				
				double sumqbr = 0.0;
				
				for(int action=0; action<naction; action++)
				{
					double tmpqbr = Math.exp(lambda*attackerexputility.get(action)) /sumexputility;
					sumqbr += tmpqbr;
					
					System.out.println("action  "+ action + " , qbr "+ tmpqbr);
					qbr.put(action, tmpqbr);
				}
				
				System.out.println("sum qbr  "+ Math.round(sumqbr * 100.0) / 100.0);
				
				
				/**
				 * update the attacker strategy
				 */
				
				for(int action=0; action<naction; action++)
				{
					iset.qre_prob_val.put(action, qbr.get(action));
					
				}
				
				
				
				/**
				 * now propagate the expected values to upwards
				 * 
				 * 
				 * 1. For every node of attacker compute the expected utility and update the node's atatcker utility
				 * 2. for defender information set compute the expected utlity for attacker
				 * 
				 */
				System.out.println("iset "+ iset.id);
				for(DNode node: iset.nodes)
				{
					double exp = 0;
					for(int action=0; action<naction; action++)
					{
						DNode child = node.child.get(action);
						
						double attut = iset.qre_prob_val.get(action)*child.attacker_reward;
						exp += attut;
					}
					System.out.println("node "+ node.nodeid + ", att_reward " + exp);
					node.attacker_reward = exp;
				}
				
				
				
				for(DNode node: parentiset.nodes)
				{
					double exp = 0;
					for(int action=0; action<naction; action++)
					{
						System.out.print("action "+ action + ", ");

						DNode child = node.child.get(action);

						System.out.println("child node "+ child.nodeid + ", att_reward " + child.attacker_reward);
					}
				}
				
				
				
				
				// now update the reward for the defender's infoset's node's attacker reward
				System.out.println("updating parent info set "+ parentiset.id+" node with attacker reward");
				for(DNode node: parentiset.nodes)
				{
					double exp = 0;
					for(int action=0; action<naction; action++)
					{
						System.out.println("action "+ action);
						
						DNode child = node.child.get(action);
						
						System.out.println("child node "+ child.nodeid + ", att_reward " + child.attacker_reward);
						
						
						System.out.println("parentiset.qre_prob_val "+ parentiset.qre_prob_val.get(action));
						
						double attut = parentiset.qre_prob_val.get(action)*child.attacker_reward;
						
						System.out.println("attut "+ attut);
						exp += attut;
						System.out.println("exp "+ exp);
					}
					System.out.println("node "+ node.nodeid + ", att_reward " + exp);
					node.attacker_reward = exp;
				}
				System.out.println();
				//int p=1;
				
					
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*HashMap<String, String> container = new HashMap<String, String>();
		for(String isetname: keyset)
		{


			InfoSet iset = isets.get(isetname);
			
			if(iset.player==1 && iset.depth>0)
			{

				
			ArrayList<String> doneprobs = new ArrayList<String>();
				 
				// find the history of any node

				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node.parent;
				
				int pl = 1;

				while(tmp != null && tmp.nodeid != 0)
				{
					
					if(pl==0)
					{
						histp0 += (tmp.prevaction+1);
					}
					else
					{
						histp1 += (tmp.prevaction+1);
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );
				
				
				String key = histp0 +"0"+histp1;
				String value = iset.qre_var.get(child.nodeid);
				container.put(key, value);

				int z=1;
			}
			
			


			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.5;");
					doneprobs.add(qre_prob);
				}



			}
		}*/
		
		
	}

	public static void updateTreeWithDefStartegy(HashMap<String, InfoSet> isets, DNode root, HashMap<String, HashMap<String, Double>> strategy, int naction) 
	{
		
		String[] keyset = new String[isets.size()];
		int indx=0;

		for(String is: isets.keySet())
		{
			keyset[indx++] = is;
		}
		Arrays.sort(keyset);
		
		
		
		for(String isetname: keyset)
		{
			
			InfoSet iset = isets.get(isetname);
			// for every info set
			/**
			 * 1. find the history of attackere and defender
			 */
			if(iset.player==0)
			{

				
				// find the history of any node
				System.out.println("infoset "+ isetname);
				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node;
				
				int pl = 1; // player of previous node.parent

				while(tmp != null && tmp.nodeid != 0)
				{
					
					if(pl==0)
					{
						histp0  = (tmp.prevaction)+"," + histp0;
					}
					else
					{
						histp1 = (tmp.prevaction)+"," + histp1;
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );
				
				
				
				
				
				String key = histp0 +" "+histp1;
				//String value = iset.qre_var.get(child.nodeid);
				
				if(key.equals(" "))
				{
					key = "EMPTY EMPTY";
				}
				else
				{
					histp0 = histp0.substring(0, histp0.length()-1);
					histp1 = histp1.substring(0, histp1.length()-1);
					key = histp0 +" "+histp1;
					
				}
				
				
				
				
				
				HashMap<String, Double> defstrat = new HashMap<String, Double>();
				
				if(strategy.containsKey(key))
				{
					defstrat = strategy.get(key);
				}
				else
				{
					for(int i=0; i<naction; i++)
					{
						if(i==0)
							defstrat.put(String.valueOf(i), 1.0);
						else
							defstrat.put(String.valueOf(i), 0.0);
							
					}
				}
				
				
				
				for(DNode ch : iset.nodes) // here we assume that defender information sets have only one node
				{
					
					
					for(int ac=0; ac<naction; ac++)
					{
						String action = ac+"";
						double prob = 0;
						if(defstrat.containsKey(action))
						{
							prob = defstrat.get(action);
							
						}
						iset.qre_prob_val.put(ac, prob);
						System.out.println("Setting prob "+ prob + " for action "+ action);
						
					}
					int z=1;
				}


				
			}
			
			
		}
		
	}

	public static HashMap<String, Double[]> prepareAttackerStrategy(HashMap<Integer, ArrayList<String>> depthinfoset, HashMap<String, InfoSet> isets, int naction) throws Exception 
	{
		
		
		HashMap<String, Double[]> attstrat = new HashMap<String, Double[]>();
		
		
		for(ArrayList<String> depthisets: depthinfoset.values())
		{
			
			
			
			for(String isetname: depthisets)
			{
				
				Double strat [] = new Double[naction];
				InfoSet iset = isets.get(isetname);
				
				// get the history
				
				//System.out.println("infoset "+ isetname);
				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node.parent;
				
				int pl = 1;

				while(tmp != null && tmp.nodeid != 0)
				{
					
					if(pl==0)
					{
						histp0 = (tmp.prevaction)+"," + histp0;
					}
					else
					{
						histp1 = (tmp.prevaction)+"," + histp1;
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				//System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );
				
				
				
				
				
				String key = histp0 +" "+histp1;
				//String value = iset.qre_var.get(child.nodeid);
				
				if(key.equals(" ") || histp1.equals(""))
				{
					key = "EMPTY EMPTY";
				}
				else
				{
					histp0 = histp0.substring(0, histp0.length()-1);
					histp1 = histp1.substring(0, histp1.length()-1);
					key = histp0 +" "+histp1;
					
				}
				
				
				//System.out.println("key " + key );
				
				double sum = 0.0;
				
				
				
				System.out.println("infoset "+ iset.id + ", seq "+ key + ", att_strat : ");
				for(int a=0; a<naction; a++)
				{
					double prob = iset.qre_prob_val.get(a);
					strat[a] = prob;
					System.out.println("action " + a + ", prob " + prob);
					sum += prob;
				}
				
				
				//System.out.println("prob sum " + Math.round(sum * 100.0) / 100.0);
				
				attstrat.put(key, strat);
				
				
				
				
				if(sum<(1.0-0.0001))
				{
					throw new Exception("Attacker strategy prob sum "+ sum);
				}
				
				
				
				
			}
		}
		
		return attstrat;
		
	}
	
	


}


class InfoSet{

	ArrayList<DNode> nodes = new ArrayList<DNode>();


	HashMap<Integer, String> qre_prob = new HashMap<Integer, String>();
	HashMap<Integer, String> qre_var = new HashMap<Integer, String>();

	HashMap<Integer, Double> qre_prob_val = new HashMap<Integer, Double>();
	HashMap<Integer, Double> qre_var_val = new HashMap<Integer, Double>();




	int player;
	int depth;
	String id;
	public static int varcount = 1;

	public InfoSet(int player, int depth, String id) 
	{
		super();
		this.player = player;
		this.depth = depth;
		this.id = id;
	}






}


class DNode {
	public int nodeid;
	public int depth;
	public int player;
	public DNode parent;
	public boolean leaf;
	public int defender_reward;
	public double attacker_reward;
	int prevaction = -1;
	String infoset;

	public HashMap<Integer, DNode> child = new HashMap<Integer, DNode>();

	
	public DNode() {
		super();
		
	}

	public DNode(int nodeid, int depth, int player) {
		super();
		this.nodeid = nodeid;
		this.depth = depth;
		this.player = player;
		this.attacker_reward = nodeid;
	}

	public DNode(DNode node) {
		super();
		this.nodeid = node.nodeid;
		this.depth = node.depth;
		this.player = node.player;
		this.parent = node.parent;

		for(Integer action: node.child.keySet())
		{
			this.child.put(action, node.child.get(action));
		}
	}





}

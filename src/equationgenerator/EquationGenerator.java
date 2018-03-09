package equationgenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class EquationGenerator {

	public static int treenodecount =0;


	public static void main(String[] args) throws Exception 
	{
		int DEPTH_LIMIT = 4;
		int naction = 2;
		DNode root = createGameTree(DEPTH_LIMIT, naction);
		System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		System.out.println();
		printTree(root, naction);
		HashMap<String, ArrayList<DNode>> I = prepareInformationSets(root, DEPTH_LIMIT, naction);
		printInfoSet(I);

		HashMap<String, InfoSet> ISets = prepareInfoSet(I);

		printISets(ISets);
		
		assignRewardToLeafNodes(ISets, root);

		//varify it
		expUtility(I.get("d3_p1_3"), 0, root, ISets, "d3_p1_3");
		
		
		printMatLabCode(ISets, root);


	}

	private static void assignRewardToLeafNodes(HashMap<String,InfoSet> iSets, DNode root) {
		// TODO Auto-generated method stub
		
	}

	private static void printMatLabCode(HashMap<String,InfoSet> iSets, DNode root) {
		
		
		// print initialization
		
		
		for(InfoSet iset: iSets.values())
		{
			for(Integer nodeid: iset.qre_var.keySet())
			{
				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);
				
				System.out.println(qre_prob +" = "+ qre_var);
			}
		}
		
		
	}

	private static void printISets(HashMap<String,InfoSet> iSets) 
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

	private static HashMap<String, InfoSet> prepareInfoSet(HashMap<String, ArrayList<DNode>> I) {


		HashMap<String, InfoSet> isets = new HashMap<String, InfoSet>();

		for(String is: I.keySet())
		{
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
						}
						
					}
				}
				InfoSet.varcount++;
			}
		}



		return obj;
	}

	private static void expUtility(ArrayList<DNode> Infoset, int action, DNode root, HashMap<String,InfoSet> iSets, String infosetname)
	{
		String exputility = "";
		String probtoreach = "";
		String continuationvalue = "";
		int nodecount = 0;
		for(DNode node: Infoset)
		{
			// for node find the probability to reach the information set
			probtoreach = getProbToReachInfoNode(node, root, iSets, infosetname);
			
			if(probtoreach.equals(""))
			{
				probtoreach = "1";
			}
			
			//System.out.println("\nNode "+ node.nodeid + ", Infoset "+ node.infoset + ", prob to reach : "+ probtoreach);
			
			
			// consider prob of playing action 1
			// play the continuation game
			DNode nextroot = node.child.get(action);
			continuationvalue = playContinuationGame(nextroot, "", iSets);
			nodecount++;
			
			System.out.println("\nInfoset "+ infosetname);
			System.out.println("In node "+ node.nodeid);
			System.out.println("Prob to reach infoset "+probtoreach);
			System.out.println("Continuation value "+continuationvalue);
			
			
			exputility += probtoreach +"*("+continuationvalue + ")";
			if(nodecount< Infoset.size())
			{
				exputility += "+";
			}
		}
		
		
		
		System.out.println("\nEXP : "+exputility+"\n");


	}

	private static String playContinuationGame(DNode nextroot, String conplay, HashMap<String,InfoSet> iSets) {
		
		
		
		if(nextroot.leaf)
			return nextroot.attacker_reward+"";
		
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
			
			String val = playContinuationGame(child, conplay, iSets);
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

	private static void printInfoSet(HashMap<String, ArrayList<DNode>> I) 
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

	private static HashMap<String, ArrayList<DNode>> prepareInformationSets(DNode root, int DEPTH_LIMIT, int naction) {


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

	private static DNode createGameTree(int DEPTH_LIMIT, int naction) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount++;
		genTree(0, naction, DEPTH_LIMIT, root);
		return root;

	}

	private static void genTree(int depth, int naction, int DEPTH_LIMIT, DNode node) 
	{

		if(depth==DEPTH_LIMIT)
		{
			node.leaf = true;
			return;
		}

		for(int action =0; action<naction; action++)
		{
			DNode child = new DNode(treenodecount, depth, node.player^1);
			treenodecount++;
			child.parent = node;
			child.prevaction = action;
			node.child.put(action, child);
			genTree(depth+1, naction, DEPTH_LIMIT, child);


		}

	}


}


class InfoSet{

	ArrayList<DNode> nodes = new ArrayList<DNode>();
	HashMap<Integer, String> qre_prob = new HashMap<Integer, String>();
	HashMap<Integer, String> qre_var = new HashMap<Integer, String>();
	
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
	public int attacker_reward;
	int prevaction = -1;
	String infoset;

	public HashMap<Integer, DNode> child = new HashMap<Integer, DNode>();


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

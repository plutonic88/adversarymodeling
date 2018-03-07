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
					String key = depth+"-"+player + "-" + countI;
					ArrayList<DNode> ns = new ArrayList<DNode>();
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
					
					for(DNode n: depthnodes)
					{
						if(n.parent.nodeid == parentnode.nodeid)
						{
							ns.add(n);
						}
					}
					String key = depth+"-"+player + "-" + countI;
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


class DNode {
	public int nodeid;
	public int depth;
	public int player;
	public DNode parent;
	public boolean leaf;
	public int defender_reward;
	public int attacker_reward;
	int prevaction = -1;

	public HashMap<Integer, DNode> child = new HashMap<Integer, DNode>();


	public DNode(int nodeid, int depth, int player) {
		super();
		this.nodeid = nodeid;
		this.depth = depth;
		this.player = player;
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

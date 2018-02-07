package kmeans;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import weka.clusterers.EM;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class Weka {

	public static List<Integer>[] clusterUsers(int k, double[][] normalizedexamples) throws Exception {


		Instances instances = generateInstance(normalizedexamples);
		Instances newinstance = new Instances(instances);
		EM dc = new EM();
		ArrayList<Integer>[] clusters = clusterWithWeka(newinstance, k, dc);
		return clusters;

	}


	private static ArrayList<Integer>[] clusterWithWeka(Instances newinstance, int k, EM dc) throws Exception 
	{


		ArrayList<Integer>[] clusters = (ArrayList<Integer>[])new ArrayList[k];

		for(int i=0; i<k; i++)
		{
			clusters[i] = new ArrayList<Integer>();
		}
		dc.setNumClusters(k); // 0 for base
		dc.buildClusterer(newinstance);
		System.out.println(dc);
		for(int i=0; i<newinstance.size(); i++)
		{
			System.out.println("instance  "+i +", cluster "+ dc.clusterInstance(newinstance.get(i))+1);
			int clusterid = dc.clusterInstance(newinstance.get(i));
			int tid = (int)newinstance.get(i).value(0);
			clusters[clusterid].add(tid);
		}
		return clusters;
	}


	private static Instances generateInstance(double[][] examples) {


		try {




			File f = new File("examples"+".csv");

			if(f.exists())
			{
				f.delete();
				f.createNewFile();
			}


			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("examples.csv"),true));

			String feature_header = "Id,";
			
			for(int i=0; i<examples[0].length; i++)
			{
				feature_header += "f"+i;
				if(i<(examples[0].length-1))
				{
					feature_header += ",";
				}
			}
			
			feature_header += "\n";
			
			pw.append(feature_header);
			pw.close();
			
			pw = new PrintWriter(new FileOutputStream(new File("examples.csv"),true));
			
			for(int i=0; i<examples.length; i++)
			{
				pw.append(i+",");
				for(int j=0; j<examples[i].length; j++)
				{
					pw.append(examples[i][j]+"");
					if(j<(examples[0].length-1))
					{
						pw.append(",");
					}
				}
				pw.append("\n");
			}
			
			
			pw.close();
			
			CSVLoader csvload = new CSVLoader();
			csvload.setSource(new File("examples.csv"));
			Instances data = csvload.getDataSet();


			ArffSaver arf = new ArffSaver();
			arf.setInstances(data);

			f = new File("examples"+".arff");

			if(f.exists())
			{
				f.delete();
				f.createNewFile();
			}

			arf.setFile(f);
			arf.writeBatch();

			FileInputStream fstream = new FileInputStream("examples"+".arff");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// Read all the instances in the file (ARFF, CSV, XRFF, ...)
			//DataSource source = new DataSource(br);
			Instances instances = new Instances(br);
			return instances;

		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

		return null;
	}

}

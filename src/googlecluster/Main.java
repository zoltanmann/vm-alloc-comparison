package googlecluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Main {

	public static String sourcefolder = "C:\\googleclusterdata\\";
	public static String destinationfolder = "C:\\workloads\\google\\";
	
	public static void test()
	{
		List<LinkedList<Integer>> cucc = new LinkedList<LinkedList<Integer>>();

		Random rand = new Random();
		
		for(int k=0; k<100; k++)
		{
			LinkedList<Integer> l = new LinkedList<Integer>();
			for(int i=0; i<100; i++)
			{
				int j = rand.nextInt() % 101;
				l.add(j);
			}
			cucc.add(l);
		}
		
		try {
			FileWriter fw = new FileWriter("mofotest.txt");
			
			DumperOptions options = new DumperOptions();
		    options.setWidth(500);
			Yaml yaml = new Yaml(options);
			yaml.dump(cucc, fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getExtension(String filename)
	{
		int i = filename.lastIndexOf('.');
		return filename.substring(i+1);
	}
	
	public static void readPMdata()
	{
		File inputFolder = new File(sourcefolder + "machine_events\\");
		File[] files = inputFolder.listFiles();

		for (File f:files)
		{
			if (getExtension(f.getName()).equals("gz"))
			{
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));
				
					String line = br.readLine();
					while(line!=null)
					{
						String[] fields = line.split(",");
						line = br.readLine();
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static HashMap<String,GoogleTask> getTaskdata()
	{
		return getTaskdata(true);
	}
	
	public static HashMap<String,GoogleTask> getTaskdata(boolean cpuSampled)
	{
		File inputFolder = new File(sourcefolder + "task_events\\");
		File[] files = inputFolder.listFiles();

		HashMap<String,GoogleTask> tasks = new HashMap<String,GoogleTask>();
		
		for (File f:files)
		{
			System.out.println(f.getName());
			if (getExtension(f.getName()).equals("gz"))
			{
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));

					String line = br.readLine();
					while(line!=null)
					{
						String[] fields = line.split(",",-1);
						
						if (!fields[9].equals("") && !fields[10].equals("") && !fields[11].equals(""))
						{
							String ID = fields[2] + "_" + fields[3];
							
							if (!tasks.containsKey(ID))
							{
								long ts = Long.parseLong(fields[0])/1000;
								
								double rc = Double.parseDouble(fields[9]); 
								double rr = Double.parseDouble(fields[10]);
								double rd = Double.parseDouble(fields[11]);
								tasks.put(ID, new GoogleTask(ID,ts,rc,rr,rd));
							}
							else
							{
								
							}
						}
						line = br.readLine();
					}
					
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		int cpuFieldIndex;
		if (cpuSampled)
		{
			cpuFieldIndex = 19;
		}
		else
		{
			cpuFieldIndex = 5;
		}
		
		inputFolder = new File(sourcefolder + "task_usage\\");
		files = inputFolder.listFiles();
		
		for (File f:files)
		{
			System.out.println(f.getName());
			if (getExtension(f.getName()).equals("gz"))
			{
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));

					String line = br.readLine();
					while(line!=null)
					{
						String[] fields = line.split(",",-1);
						
						if (!fields[cpuFieldIndex].equals("") && !fields[10].equals("") && !fields[11].equals(""))
						{
							String ID = fields[2] + "_" + fields[3];
							
							if (!tasks.containsKey(ID))
							{
								
							}
							else
							{
								GoogleTask task = tasks.get(ID);
								task.usedCPU.add((int) (Double.parseDouble(fields[cpuFieldIndex])/task.requiredCPU*100));
								task.usedRAM.add((int) (Double.parseDouble(fields[7])/task.requiredRAM*100));
								task.usedDisk.add((int) (Double.parseDouble(fields[12])/task.requiredDisk*100));
								task.complete = true;
							}
						}
						line = br.readLine();
					}
					
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return tasks;
		
	}

	public static void writeTaskData(HashMap<String,GoogleTask> tasks)
	{
		writeTaskData(tasks, 100, 100, 100);//edit 1000 1000 1000
	}
	
	public static void writeTaskData(HashMap<String,GoogleTask> tasks, int maxCpu, int maxRam, int maxDisk)
	{
		try {
			long currentmillis = System.currentTimeMillis();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationfolder+currentmillis+"_gtrace_cpuusage"+".txt")));
			FileWriter fw = new FileWriter(destinationfolder+currentmillis+"_gtrace_vms"+".txt");
			
			LinkedList<HashMap<String,HashMap<String,Object>>> vms = new LinkedList<HashMap<String,HashMap<String,Object>>>();
			
			
			int ID=0;
			for(GoogleTask task:tasks.values())
			{
				if (task.complete && task.usedCPU.size()>=100)
				{
					HashMap<String,HashMap<String,Object>> vm = new HashMap<String,HashMap<String,Object>>();
					HashMap<String,Object> vmdata = new HashMap<String,Object>();
					
					LinkedList<Integer> ids = new LinkedList<Integer>();
					ids.add(ID);
					vmdata.put("ID", ids);
					vmdata.put("StartTime", task.StartTime);
					vmdata.put("Cores", 1);
					LinkedList<Integer> mipss = new LinkedList<Integer>();
					mipss.add((int)(task.requiredCPU*maxCpu));
					vmdata.put("MIPS", mipss);
					vmdata.put("RAM", (int)(task.requiredRAM*maxRam));
					vmdata.put("DiskCapacity", (int)(task.requiredDisk*maxDisk));
					vmdata.put("Num", 1);
					
					bw.write(String.valueOf(ID));bw.newLine();
					bw.write(String.valueOf(300000));bw.newLine();
					int num = task.usedCPU.size();
					if (num<288) num = 288;
					bw.write(String.valueOf(num));bw.newLine();
					
					vmdata.put("EndTime", task.StartTime + (num - 1) * 300000);
					vm.put("VM",vmdata);
					vms.add(vm);
					
					for(int i:task.usedCPU)
					{
						bw.write(String.valueOf(i));bw.newLine();
					}
					if (task.usedCPU.size() < 288)
					{
						for (int i  = task.usedCPU.size() + 1; i<=288; i++)
						{
							bw.write(String.valueOf(0));bw.newLine();
						}
					}
					
					ID++;if (ID == 1052) break;
				}
			}
			
			DumperOptions options = new DumperOptions();
		    options.setWidth(500);
			Yaml yaml = new Yaml(options);
			yaml.dump(vms, fw);
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		writeTaskData(getTaskdata(false));
		/*for(String s:"sadsad,adsad,,sdasd,aa,a,,,a".split(","))
			System.out.println(s);*/
	}

}

package bitbrains;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Main {
	
	public static String sourcefolder = "C:\\bitbrains\\fastStorage\\2013-8\\";
	public static String destinationfolder = "C:\\workloads\\bitbrains\\";
	
	public static String getExtension(String filename)
	{
		int i = filename.lastIndexOf('.');
		return filename.substring(i+1);
	}
	
	public static HashMap<String,BBTask> getTaskdata()
	{
		File inputFolder = new File(sourcefolder);
		File[] files = inputFolder.listFiles();

		HashMap<String,BBTask> tasks = new HashMap<String,BBTask>();
		
		int id=0;
		
		for (File f:files)
		{
			System.out.println(f.getName());
			if (getExtension(f.getName()).equals("csv"))
			{
				String ID = f.getName().split("\\.x")[0];
				BBTask task = null;
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
					
					boolean submitted = false;

					String line = br.readLine();
					line = br.readLine();
					while(line!=null)
					{
						String[] fields = line.split(";",-1);
						
						if (!fields[1].trim().equals("0") && !submitted)
						{
							submitted = true;
							long ts = (Long.parseLong(fields[0].trim())-1372629804L)*1000;
							int c = Integer.parseInt(fields[1].trim());
							double rc = Double.parseDouble(fields[2].trim()); 
							double rr = Double.parseDouble(fields[5].trim())/1000;
							task = new BBTask(ID,ts,c,rc,rr);
						}
						if (submitted)
						{
							task.usedCPU.add((int)(Double.parseDouble(fields[4].trim())));
							task.usedRAM.add((int)(Double.parseDouble(fields[6].trim())/Double.parseDouble(fields[5].trim())*100));							
						}
						line = br.readLine();
					}
					
					tasks.put(ID, task);
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
	
	public static void writeTaskData(HashMap<String,BBTask> tasks)
	{
		try {
			long currentmillis = System.currentTimeMillis();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationfolder+currentmillis+"_bbtrace_cpuusage"+".txt")));
			FileWriter fw = new FileWriter(destinationfolder+currentmillis+"_bbtrace_vms"+".txt");
			
			LinkedList<HashMap<String,HashMap<String,Object>>> vms = new LinkedList<HashMap<String,HashMap<String,Object>>>();
			
			
			int ID=0;
			for(BBTask task:tasks.values())
			{
				HashMap<String,HashMap<String,Object>> vm = new HashMap<String,HashMap<String,Object>>();
				HashMap<String,Object> vmdata = new HashMap<String,Object>();
				
				LinkedList<Integer> ids = new LinkedList<Integer>();
				ids.add(ID);
				vmdata.put("ID", ids);
				vmdata.put("StartTime", task.StartTime);
				vmdata.put("Cores", 1);
				LinkedList<Integer> mipss = new LinkedList<Integer>();
				mipss.add((int)(task.requiredCPU));
				vmdata.put("MIPS", mipss);
				vmdata.put("RAM", (int)(task.requiredRAM));
				vmdata.put("DiskCapacity", 0);
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
					int v = i * 10;
					if (v>100) v = 100;
					bw.write(String.valueOf(v));bw.newLine();
				}
				if (task.usedCPU.size() < 288)
				{
					for (int i  = task.usedCPU.size() + 1; i<=288; i++)
					{
						bw.write(String.valueOf(0));bw.newLine();
					}
				}
				
				ID++;
				if (ID == 1052) break;
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
		writeTaskData(getTaskdata());
	}

}

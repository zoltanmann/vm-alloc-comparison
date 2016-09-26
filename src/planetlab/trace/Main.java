package planetlab.trace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class Main {

	public static String sourcefolder = "C:\\cloudsim-3.0.3\\examples\\workload\\planetlab\\20110303\\";
	public static String destinationfolder = "C:\\workloads\\planetlab\\";
	
	public static void convert()
	{
			File inputFolder = new File(sourcefolder);
			File[] files = inputFolder.listFiles();
			
			long currentmillis = System.currentTimeMillis();
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationfolder+currentmillis+"_planetlab_cpuusage"+".txt")));	
				
				int ID=0;
				
				for (File f:files)
				{
					System.out.println(f.getName());
			
					//try {
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
					
						boolean submitted = false;
	
						bw.write(String.valueOf(ID));bw.newLine();
						bw.write(String.valueOf(300000));bw.newLine();
						bw.write(String.valueOf(288));bw.newLine();	// accidentally this was set to 289 earlier
						
						String line = br.readLine();
						while(line!=null)
						{
							bw.write(line);bw.newLine();
							line = br.readLine();
						}
						
						ID++;
						br.close();
				/*	} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				
				}

				bw.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	}
	
	public static void main(String[] args) {
		convert();
	}

}

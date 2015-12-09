package org.cloudbus.cloudsim.examples.power.custom;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ReadLog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String[] workload = new String[]{"controlplanetlabconfig.txt", "constant10.txt", "constant50.txt", "constant100.txt", "periodic.txt", "markov.txt", "bitbrains.txt",
				"googlecluster.txt", "physicalmachine1200.txt", "physicalmachinehetero.txt", "physicalmachinehomo.txt",
				"powermodelhetero.txt", "powermodelhomo1.txt", "powermodelhomo3.txt", "virtualmachinebigger.txt", "virtualmachinesmaller.txt"};

		String[] vmAllocationPolicy = new String[] {"thr", "dvfs", "lago", "chowdm", "chowds", "guazzone", "perc", "abs", "calavecchia"};
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("logcsv.txt")));
			
			/*bw.write("adatok");
			for (int i = 0; i < workload.length; i++) {
				bw.write(',');bw.write(workload[i]);
			}
			bw.newLine();*/
			
			
		    BufferedReader br = new BufferedReader(new FileReader(new File("ralmostfull.txt")));
		    
		    
		    
		    for (int i = 0; i < workload.length; i++) {
		    	
		    	
		    	
			    String[][] everything = new String[24][];
			    for (int j = 0; j < everything.length; j++) {
					everything[j] = new String[vmAllocationPolicy.length + 1];
					for (int j2 = 0; j2 < everything[j].length; j2++) {
						everything[j][j2] = "N/A";
					}
				}
			    
			    everything[0][0] = br.readLine();
			    
				for (int j = 0; j < vmAllocationPolicy.length; j++) {
					everything[0][j + 1] = br.readLine();
					
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					
					for (int j2 = 0; j2 < 23; j2++) {
						if (everything[0][j+1].equals("dvfs") && j2==15) break;
						String[] splitted = br.readLine().split(":");
						everything[j2+1][0] = splitted[0];
						//System.out.println(splitted[0]);
						splitted = splitted[1].trim().split(" ");
						everything[j2+1][j+1] = splitted[0];
					}
					
					br.readLine();
					br.readLine();
				}
				
				for (int j = 0; j < everything.length; j++) {
					for (int j2 = 0; j2 < everything[j].length; j2++) {System.out.println(j+" "+j2);
						bw.write(everything[j][j2]);bw.write("\t");
					}
					bw.newLine();
				}
				bw.newLine();
			}
		    
		    br.close();
		    bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

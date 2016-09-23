package org.cloudbus.cloudsim.examples.power.custom;

import java.io.IOException;

/**
 * A simulation of a heterogeneous power aware data center that only applied DVFS, but no dynamic
 * optimization of the VM allocation. The adjustment of the hosts' power consumption according to
 * their CPU utilization is happening in the PowerDatacenter class.
 * 
 * This example uses a real PlanetLab workload: 20110303.
 * 
 * The remaining configuration parameters are in the Constants and PlanetLabConstants classes.
 * 
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class RunEverything {

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		boolean enableOutput = true;
		boolean outputToFile = false;
		String inputFile = "testconfig\\all";
		String outputFolder = "output";
		
		String[] workload = new String[]{"controlplanetlabconfig.txt", "constant10.txt", "constant50.txt", "constant100.txt", "periodic.txt", "markov.txt", "bitbrains.txt",
										"googlecluster.txt", "physicalmachine400.txt", "physicalmachine1200.txt", "physicalmachinehetero.txt", "physicalmachinehomo.txt",
										"powermodelhetero.txt", "powermodelhomo1.txt", "powermodelhomo3.txt", "virtualmachinebigger.txt", "virtualmachinesmaller.txt"};
		
		for (int i = 13; i < workload.length; i++) {
			
			System.out.println(workload[i]);
			
			//String workload = "controlplanetlabconfig.txt"; // PlanetLab workload
			String[] vmAllocationPolicy = new String[] {/*"thr", "dvfs", "lago", "chowdm", "chowds", "guazzone", "perc", "abs",*/ "calavecchia"};
			//String vmAllocationPolicy = "thr"; // DVFS policy without VM migrations
			String vmSelectionPolicy = "mc";
			String parameter = "0.8";
	
			for (int j = 0; j < vmAllocationPolicy.length; j++) {
				
				System.out.println(vmAllocationPolicy[j]);
				
				new CustomRunner(
						enableOutput,
						outputToFile,
						inputFile,
						outputFolder,
						workload[i],
						vmAllocationPolicy[j],
						vmSelectionPolicy,
						parameter);
				
				System.out.println();
			}
		}
	}

}

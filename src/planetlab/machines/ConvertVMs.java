package planetlab.machines;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class ConvertVMs {

	public final static int VM_TYPES	= 4;
	public final static int[] VM_MIPS	= { 2500, 2000, 1000, 500 };
	public final static int[] VM_PES	= { 1, 1, 1, 1 };
	public final static int[] VM_RAM	= { 870,  1740, 1740, 613 };
	public final static int VM_BW		= 100000; // 100 Mbit/s
	public final static int VM_SIZE		= 2500; // 2.5 GB
	
	public static String sourcefolder = "C:\\cloudsim-3.0.3\\examples\\workload\\planetlab\\20110303\\";
	public static String destinationfolder = "C:\\workloads\\planetlab\\";
	
	public static void main(String[] args) {
		
		File inputFolder = new File(sourcefolder);
		File[] files = inputFolder.listFiles();
		int cloudletNum = files.length;
		
		List<Map<String,Object>> vms = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < cloudletNum; i++) {
			int vmType = i / (int) Math.ceil((double) cloudletNum / VM_TYPES);
			
			Map<String,Object> vm = new HashMap<String,Object>();
			vm.put("ID", new int[]{i});
			vm.put("Cores", VM_PES[vmType]);
			vm.put("CpuCapacity", new int[]{VM_MIPS[vmType]});
			vm.put("RAM", VM_RAM[vmType]);
			vm.put("BandWidth", VM_BW);
			vm.put("DiskCapacity", VM_SIZE);
			
			Map<String,Object> vmwrap = new HashMap<String,Object>();
			vmwrap.put("VM", vm);
			
			vms.add(vmwrap);	//grouping VMs of the same type together is not necessary here, they don't take up too much space to begin with
		}
		
		//bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationfolder+currentmillis+"_planetlab_cpuusage"+".txt")));
		long currentmillis = System.currentTimeMillis();
		
		Yaml yaml = new Yaml();
		OutputStreamWriter writer;
		try {

			writer = new OutputStreamWriter(new FileOutputStream(destinationfolder+currentmillis+"_vms"+".txt"));
	    	yaml.dump(vms, writer);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

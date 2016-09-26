package planetlab.machines;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ConvertHosts {


	public final static int NUMBER_OF_HOSTS = 800;
	public final static int HOST_TYPES	 = 2;
	public final static int[] HOST_MIPS	 = { 1860, 2660 };
	public final static int[] HOST_PES	 = { 2, 2 };
	public final static int[] HOST_RAM	 = { 4096, 4096 };
	public final static int HOST_BW		 = 1000000; // 1 Gbit/s
	public final static int HOST_STORAGE = 1000000; // 1 GB
	public final static double[][] HOST_POWER = {{86, 89.4, 92.6, 96, 99.5, 102, 106, 108, 112, 114, 117},
												{93.7, 97, 101, 105, 110, 116, 121, 125, 129, 133, 135}};
	
	public static String destinationfolder = "C:\\workloads\\planetlab\\";
	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> hosts = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < NUMBER_OF_HOSTS; i++) {
			int hostType = i % HOST_TYPES;

			Map<String,Object> host = new HashMap<String,Object>();
			host.put("ID", new int[]{i});
			host.put("Cores", HOST_PES[hostType]);
			host.put("CpuCapacity", new int[]{HOST_MIPS[hostType]});
			host.put("RAM", HOST_RAM[hostType]);
			host.put("BandWidth", HOST_BW);
			host.put("DiskCapacity", HOST_STORAGE);
			host.put("PowerModel", HOST_POWER[hostType]);
			
			Map<String,Object> hostwrap = new HashMap<String,Object>();
			hostwrap.put("Host", host);
			
			hosts.add(hostwrap);	//grouping VMs of the same type together is not necessary here, they don't take up too much space to begin with
		}
		
		//bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationfolder+currentmillis+"_planetlab_cpuusage"+".txt")));
		long currentmillis = System.currentTimeMillis();
		

	    DumperOptions options = new DumperOptions();
	    options.setWidth(500);	//maybe it would be a good idea to turn off both the automatic anchor and reference of YAML
		Yaml yaml = new Yaml(options);
		OutputStreamWriter writer;
		try {

			writer = new OutputStreamWriter(new FileOutputStream(destinationfolder+currentmillis+"_hosts"+".txt"));
	    	yaml.dump(hosts, writer);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

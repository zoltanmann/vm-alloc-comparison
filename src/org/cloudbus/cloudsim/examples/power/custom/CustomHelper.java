package org.cloudbus.cloudsim.examples.power.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.yaml.snakeyaml.Yaml;

public class CustomHelper{
	
	public static List<Vm> createVmList(int brokerId, String inputFileName) {
		List<Vm> vmlist = new ArrayList<Vm>();
		InputStream input;
	
		try {
			input = new FileInputStream(new File(inputFileName));
		
		    Yaml yaml = new Yaml();
		    List<Map<String,Object>> vms = (List<Map<String,Object>>)yaml.load(input);
	
		
			for(Map<String,Object> vmwrap: vms)
			{	
				Map<String,Object> vm = (Map<String,Object>)vmwrap.get("VM");
				
				int ID = ((ArrayList<Integer>)vm.get("ID")).get(0);
			    int VM_MIPS = ((ArrayList<Integer>)vm.get("CpuCapacity")).get(0);
				int VM_PES = (int)vm.get("Cores");
				int VM_RAM = (int)vm.get("RAM");
				int VM_BW = (int)vm.get("BandWidth");
				int VM_SIZE = (int)vm.get("DiskCapacity");
				
				vmlist.add(new PowerVm(
						ID,
						brokerId,
						VM_MIPS,
						VM_PES,
						VM_RAM,
						VM_BW,
						VM_SIZE,
						1,
						"Xen",
						new CloudletSchedulerDynamicWorkload(VM_MIPS, VM_PES),
						Constants.SCHEDULING_INTERVAL));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vmlist;
	}

	public static List<PowerHost> createHostList(String inputFileName) {
	
		List<PowerHost> hostList = new ArrayList<PowerHost>();
	    InputStream input;
		try {
			input = new FileInputStream(new File(inputFileName));
		    Yaml yaml = new Yaml();
		    List<Map<String,Object>> hosts = (List<Map<String,Object>>)yaml.load(input);
	
			//int NUMBER_OF_HOSTS = hosts.size();
			
			for(Map<String,Object> hostwrap: hosts)
			{	
				Map<String,Object> host = (Map<String,Object>)hostwrap.get("Host");
				
				int ID = ((ArrayList<Integer>)host.get("ID")).get(0);
			    int HOST_MIPS = ((ArrayList<Integer>)host.get("CpuCapacity")).get(0);
				int HOST_PES = (int)host.get("Cores");
				int HOST_RAM = (int)host.get("RAM");
				int HOST_BW = (int)host.get("BandWidth");
				int HOST_STORAGE = (int)host.get("DiskCapacity");
				Double[] HOST_POWER = ((ArrayList<Double>)host.get("PowerModel")).toArray(new Double[0]);	//ezt komolyan így kell?
				
				List<Pe> peList = new ArrayList<Pe>();
				for (int j = 0; j < HOST_PES; j++) {
					peList.add(new Pe(j, new PeProvisionerSimple(HOST_MIPS)));
				}
	
				hostList.add(new PowerHostUtilizationHistory(
						ID,
						new RamProvisionerSimple(HOST_RAM),
						new BwProvisionerSimple(HOST_BW),
						HOST_STORAGE,
						peList,
						new VmSchedulerTimeSharedOverSubscription(peList),
						new CustomPowerModel(HOST_POWER)));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostList;
	}

	public static List<Cloudlet> createCloudletListCustom(int brokerId, String cpuInputFileName)
			throws FileNotFoundException {
		return createCloudletListCustom(brokerId, cpuInputFileName, null);
	}
	
	public static List<Cloudlet> createCloudletListCustom(int brokerId, String cpuInputFileName, String ramInputFileName)
			throws FileNotFoundException {
		List<Cloudlet> list = new ArrayList<Cloudlet>();

		long fileSize = 300;
		long outputSize = 300;
		UtilizationModel utilizationModelNull = new UtilizationModelNull();

		BufferedReader cpuInput = new BufferedReader(new FileReader(cpuInputFileName));
		BufferedReader ramInput = null;
		String line = "";
		if (ramInputFileName != null)
			ramInput = new BufferedReader(new FileReader(ramInputFileName));
		
		try {
			while((line = cpuInput.readLine()) != null) {
				Cloudlet cloudlet = null;
				int cloudletId = Integer.parseInt(line);
				int cpuSampleinterval = Integer.parseInt(cpuInput.readLine())/1000;
				int cpuSamplenum = Integer.parseInt(cpuInput.readLine());
				double[] cpuData = new double[cpuSamplenum];
				for (int i = 0; i < cpuSamplenum; i++) {
					cpuData[i] = Integer.parseInt(cpuInput.readLine());
				}
				UtilizationModel ramUtilization;
				if (ramInputFileName != null)
				{
					ramInput.readLine();
					int ramSampleinterval = Integer.parseInt(ramInput.readLine())/1000;
					int ramSamplenum = Integer.parseInt(ramInput.readLine());
					double[] ramData = new double[ramSamplenum];
					for (int i = 0; i < ramSamplenum; i++) {
						cpuData[i] = Integer.parseInt(ramInput.readLine());
					}
					ramUtilization = 
							new UtilizationModelCustomInMemory(
							ramData,
							ramSampleinterval);
				}
				else ramUtilization = utilizationModelNull;
	
				try {
					cloudlet = new Cloudlet(
							cloudletId,
							CustomConstants.CLOUDLET_LENGTH,
							Constants.CLOUDLET_PES,
							fileSize,
							outputSize,
							new UtilizationModelCustomInMemory(
									cpuData,
									cpuSampleinterval), ramUtilization, utilizationModelNull);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
				cloudlet.setUserId(brokerId);
				cloudlet.setVmId(cloudletId);
				list.add(cloudlet);
			}
			
			cpuInput.close();
			if (ramInput != null) ramInput.close();
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

}

package org.cloudbus.cloudsim.examples.power.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.Helper;
import org.cloudbus.cloudsim.examples.power.RunnerAbstract;
import org.cloudbus.cloudsim.examples.power.custom.CustomConstants;
import org.cloudbus.cloudsim.examples.power.custom.CustomHelper;
import org.yaml.snakeyaml.Yaml;

public class CustomRunner extends RunnerAbstract {

	public CustomRunner(
			boolean enableOutput,
			boolean outputToFile,
			String inputFile,
			String outputFolder,
			String workload,
			String vmAllocationPolicy,
			String vmSelectionPolicy,
			String parameter) {
		super(
				enableOutput,
				outputToFile,
				inputFile,
				outputFolder,
				workload,
				vmAllocationPolicy,
				vmSelectionPolicy,
				parameter);
	}

	@Override
	protected void init(String configFile) {
		try {
			CloudSim.init(1, Calendar.getInstance(), false);

			broker = Helper.createBroker();
			int brokerId = broker.getId();

			InputStream input = new FileInputStream(new File(configFile));
		    Yaml yaml = new Yaml();
		    Map<String,String> filenames = (Map<String,String>)yaml.load(input);
	
			cloudletList = CustomHelper.createCloudletListCustom(brokerId, filenames.get("workload"));
			vmList = CustomHelper.createVmList(brokerId, filenames.get("vms"));
			hostList = CustomHelper.createHostList(filenames.get("hosts"));
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}
	}

}

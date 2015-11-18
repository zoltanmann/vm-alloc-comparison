package chowdhury;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.lists.PowerVmList;

public class SWFDVP extends PowerVmAllocationPolicyMigrationStaticThreshold {

	public SWFDVP(List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double parameter) {
		super(hostList, vmSelectionPolicy, parameter);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected List<Map<String, Object>> getNewVmPlacement(
			List<? extends Vm> vmsToMigrate,
			Set<? extends Host> excludedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		
		PowerVmList.sortByCpuUtilization(vmsToMigrate);
		//Collections.reverse(vmsToMigrate);
		
		//vmsToMigrate = Lists.
		for (Vm vm : vmsToMigrate) {
			PowerHost allocatedHost = findHostForVm(vm, excludedHosts);
			if (allocatedHost != null) {
				allocatedHost.vmCreate(vm);
				Log.printLine("VM #" + vm.getId() + " allocated to host #" + allocatedHost.getId());

				Map<String, Object> migrate = new HashMap<String, Object>();
				migrate.put("vm", vm);
				migrate.put("host", allocatedHost);
				migrationMap.add(migrate);
			}
		}
		return migrationMap;
	}


	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		double maxPower = Double.MIN_VALUE;
		PowerHost allocatedHost = null;
		PowerHost secondHost = null;
		
		for (PowerHost host : this.<PowerHost> getHostList()) {
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {
				if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				}	//ez most akkor végül is kell?

				try {
					double powerAfterAllocation = getPowerAfterAllocation(host, vm);
					if (powerAfterAllocation != -1) {
						double powerDiff = powerAfterAllocation - host.getPower();
						if (powerDiff > maxPower) {
							maxPower = powerDiff;
							if (allocatedHost!=null) secondHost = allocatedHost;
							allocatedHost = host;
						}
					}
				} catch (Exception e) {
				}
			}
		}
		return allocatedHost;
	}

}

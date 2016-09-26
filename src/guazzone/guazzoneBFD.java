package guazzone;

import java.util.Collections;
import java.util.Comparator;
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

public class guazzoneBFD extends PowerVmAllocationPolicyMigrationStaticThreshold{
	public guazzoneBFD(List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double parameter) {
		super(hostList, vmSelectionPolicy, parameter);
		// TODO Auto-generated constructor stub
	}

	public Comparator<Vm> VmComparator = new Comparator<Vm>()
	{
		@Override
		public int compare(Vm a, Vm b) throws ClassCastException {
			Double aUtilization = a.getCurrentRequestedTotalMips();
			Double bUtilization = b.getCurrentRequestedTotalMips();
			return bUtilization.compareTo(aUtilization);
		}
	};
	
	public Comparator<PowerHost> HostComparator = new Comparator<PowerHost>()
	{
		@Override
		public int compare(PowerHost a, PowerHost b) throws ClassCastException {
			Integer aUtilization = (a.getUtilizationOfCpu()==0)?0:1;	//its name is misleading, but this is actually the correct method to use
			Integer bUtilization = (b.getUtilizationOfCpu()==0)?0:1;	//we know this because the  getSwitchedOffHosts method of CloudSim calls this too
			int cUtilization = bUtilization.compareTo(aUtilization);	//descending
			
			if (cUtilization!=0) return cUtilization;
			
			Integer aTotal = a.getTotalMips();
			Integer bTotal = b.getTotalMips();
			int cTotal = bTotal.compareTo(aTotal);	//descending
			
			if (cTotal!=0) return cTotal;
			
			Double aIdle = a.getPowerModel().getPower(0);	//idle power consumption
			Double bIdle = b.getPowerModel().getPower(0);
			int cIdle = aIdle.compareTo(bIdle);	//ascending
			
			return cIdle;
		}
	};
	
	protected List<Map<String, Object>> getNewVmPlacement(
			List<? extends Vm> vmsToMigrate,
			Set<? extends Host> excludedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		
		Collections.sort(vmsToMigrate, VmComparator);	// sort VMs with custom comparator, otherwise it would be the same as in the superclass
		
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
		List<PowerHost> lph = this.<PowerHost> getHostList();
		Collections.sort(lph, HostComparator);	//sort the hosts too, then do a simple ffd
		for (PowerHost host : lph) {
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {
				if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				}

				return host;
			}
		}
		return null;
	}

}

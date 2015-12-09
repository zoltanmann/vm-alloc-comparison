package shi;

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


public class AbsoluteCapacity extends PowerVmAllocationPolicyMigrationStaticThreshold{
	
	public AbsoluteCapacity(List<? extends Host> hostList,
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
			Integer aMips = a.getTotalMips() ;
			Integer bMips = b.getTotalMips();
			int capacity = bMips.compareTo(aMips);	//csökkenõ
			
			return capacity;
		}
	};
	
	protected List<Map<String, Object>> getNewVmPlacement(
			List<? extends Vm> vmsToMigrate,
			Set<? extends Host> excludedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		
		Collections.sort(vmsToMigrate, VmComparator);	// most így rendezzük a vm-eket, egyébként ugyanaz lenne mint az õsosztályban
		
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
		Collections.sort(lph, HostComparator);	//rendezzük a hostokat is, aztán sima ffd
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

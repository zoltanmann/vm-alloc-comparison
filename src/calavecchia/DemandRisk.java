package calavecchia;

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

public class DemandRisk extends PowerVmAllocationPolicyMigrationStaticThreshold{
	
	public DemandRisk(List<? extends Host> hostList,
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
	
	
	double UDScore(double A, double B, double delta, PowerHost h) //unsatisfied demand
	{
		return (h.getUtilizationOfCpu() - delta) / delta;
	}
	
	double DRScore(double A, double B, double delta, PowerHost h, Vm V)
	{
		
		return A*UDScore(A,B,delta,h)+B*UDScore(A,B,delta,h);
	}
	
	/*public Comparator<PowerHost> HostComparator = new Comparator<PowerHost>()
	{
		@Override
		public int compare(PowerHost a, PowerHost b) throws ClassCastException {
			Integer aMips = a.getTotalMips() ;
			Integer bMips = b.getTotalMips();
			int capacity = bMips.compareTo(aMips);	//descending
			
			return capacity;
		}
	};*/
	
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
		 double minDRScore = Double.MAX_VALUE;
         PowerHost allocatedHost = null;

         for (PowerHost host : this.<PowerHost> getHostList()) {
                 if (excludedHosts.contains(host)) {
                         continue;
                 }
                 if (host.isSuitableForVm(vm)) {
                         if (host.getUtilizationOfCpuMips() != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
                                 continue;
                         }

                         try {
                                 double powerAfterAllocation = getPowerAfterAllocation(host, vm);
                                 if (powerAfterAllocation != -1) {
                                         double score = DRScore(10,1,0.9,host,vm);
                                         if (score < minDRScore) {
                                        	 	minDRScore = score;
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

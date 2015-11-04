package lago;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;

public class LagoAllocator extends PowerVmAllocationPolicyMigrationAbstract{

	public LagoAllocator(List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy) {
		super(hostList, vmSelectionPolicy);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean isHostOverUtilized(PowerHost host) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
	    double bestEnergyEfficiency = Double.MIN_VALUE;
	    PowerHost allocatedHost = null;

	    for (PowerHost host : this.<PowerHost>getHostList()) {
	      if (host.isSuitableForVm(vm)) {
	        double utilization = getMaxUtilizationAfterAllocation(host, vm);
	        if ((!vm.isBeingInstantiated()) && utilization > getUtilizationThreshold() || (vm.isBeingInstantiated() && utilization > 1.0)) {
	          continue;
	        }
	        
	        try {
	          double powerAfterAllocation = getPowerAfterAllocation(host, vm);
	          if (powerAfterAllocation != -1) {
	            // Host can be used

	            // Calculates the power quality
	            double energyEfficiency = getHostEnergyEfficiency(host);
	            if (energyEfficiency > bestEnergyEfficiency) {
	              bestEnergyEfficiency = energyEfficiency;
	              allocatedHost = host;
	            } else if (energyEfficiency == bestEnergyEfficiency) {
	              // Tie break

	              // The lesser power consumption with the VMs instantiated at datacenter will be chosen
	              double power_vm_allocated_at_host = allocatedHost.getPower() + getPowerAfterAllocation(host, vm);
	              double power_vm_allocated_at_allocatedHost = host.getPower() + getPowerAfterAllocation(allocatedHost, vm);
	              if (power_vm_allocated_at_host < power_vm_allocated_at_allocatedHost) {
	                allocatedHost = host;
	              } else if (power_vm_allocated_at_host == power_vm_allocated_at_allocatedHost) {
	                // Calculates the best using MIPS / total MIPS to minimized vm
	                // migration
	                if (host.getUtilizationOfCpu() > allocatedHost.getUtilizationOfCpu()) {
	                  allocatedHost = host;
	                } else if (host.getUtilizationOfCpu() == allocatedHost.getUtilizationOfCpu()) {
	                  // The best processor will be allocated
	                  if (host.getTotalMips() > allocatedHost.getTotalMips()) {
	                    allocatedHost = host;
	                  }
	                }
	              }
	            }
	          }
	        } catch (Exception e) {
	        }
	      }
	    }

	    return allocatedHost;
	}

	private double getUtilizationThreshold() {
		return 0.0;
	}
	
	public static double getHostEnergyEfficiency(PowerHost host) {
	    return (host.getTotalMips() / host.getMaxPower());
	  }
}

package org.cloudbus.cloudsim.examples.power.custom;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

public class CustomPowerModel extends PowerModelSpecPower {

	private Double[] power;
	
	public CustomPowerModel(Double[] hOST_POWER)
	{
		power = hOST_POWER;
	}
	
	@Override
	public double getPower(double utilization) throws IllegalArgumentException {
		if (utilization < 0) utilization = 0;
		if (utilization > 1) utilization = 1;
		
		if (utilization % 0.1 == 0) {
			return getPowerData((int) (utilization * 10));
		}
		int utilization1 = (int) Math.floor(utilization * 10);
		int utilization2 = (int) Math.ceil(utilization * 10);
		double power1 = getPowerData(utilization1);
		double power2 = getPowerData(utilization2);
		double delta = (power2 - power1) / 10;
		double power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;
		return power;
	}
	
	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}

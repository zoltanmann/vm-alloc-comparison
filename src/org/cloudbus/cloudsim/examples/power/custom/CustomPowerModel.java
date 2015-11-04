package org.cloudbus.cloudsim.examples.power.custom;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

public class CustomPowerModel extends PowerModelSpecPower {

	private Double[] power;
	
	public CustomPowerModel(Double[] hOST_POWER)
	{
		power = hOST_POWER;
	}
	
	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}

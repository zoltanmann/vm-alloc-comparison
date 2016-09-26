package org.cloudbus.cloudsim.examples.power.custom;

import java.io.IOException;

import org.cloudbus.cloudsim.UtilizationModel;

public class UtilizationModelCustomInMemory implements UtilizationModel {
	/** The scheduling interval. */
	private double schedulingInterval;

	private final double[] data; 
	
	/**
	 * Instantiates a new utilization model PlanetLab with variable data samples.
	 * 
	 * @param inputPath the input path
	 * @param dataSamples number of samples in the file
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UtilizationModelCustomInMemory(double[] data, double schedulingInterval)
			throws NumberFormatException,
			IOException {
		setSchedulingInterval(schedulingInterval);
		this.data = new double[data.length + 1];
		int n = data.length;
		for (int i = 0; i < n; i++) {
			this.data[i] = data[i] / 100.0;
		}
		data[n - 1] = data[n - 2];	//the last sample in the PlanetLab example is duplicated
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.power.UtilizationModel#getUtilization(double)
	 */
	@Override
	public double getUtilization(double time) {
		//if (time > getSchedulingInterval() * (data.length - 1)) return 0;	//the last sample in the PlanetLab example is duplicated
		if (time % getSchedulingInterval() == 0) {
			return data[(int) time / (int) getSchedulingInterval()];
		}
		int time1 = (int) Math.floor(time / getSchedulingInterval());
		int time2 = (int) Math.ceil(time / getSchedulingInterval());
		double utilization1 = data[time1];
		double utilization2 = data[time2];
		double delta = (utilization2 - utilization1) / ((time2 - time1) * getSchedulingInterval());
		double utilization = utilization1 + delta * (time - time1 * getSchedulingInterval());
		return utilization;

	}

	/**
	 * Sets the scheduling interval.
	 * 
	 * @param schedulingInterval the new scheduling interval
	 */
	public void setSchedulingInterval(double schedulingInterval) {
		this.schedulingInterval = schedulingInterval;
	}

	/**
	 * Gets the scheduling interval.
	 * 
	 * @return the scheduling interval
	 */
	public double getSchedulingInterval() {
		return schedulingInterval;
	}

}

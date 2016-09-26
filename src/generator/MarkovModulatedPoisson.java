package generator;

import java.util.Random;

import org.apache.commons.math3.distribution.PoissonDistribution;

public class MarkovModulatedPoisson {

	PoissonDistribution[] pd = new PoissonDistribution[2];
	public double[] P = new double[2];
	
	public static Random rand;
	
	int state = 0;
	
	
	public MarkovModulatedPoisson(double lambda0, double lambda1, double P01, double P10, long seed)
	{
		rand = new Random(seed);
		pd[0] = new PoissonDistribution(lambda0);
		pd[1] = new PoissonDistribution(lambda1);
		P[0] = P01;
		P[1] = P10;
	}
	
	public MarkovModulatedPoisson(double lambda0, double lambda1, double P01, double P10)
	{
		rand = new Random();
		pd[0] = new PoissonDistribution(lambda0);
		pd[1] = new PoissonDistribution(lambda1);
		P[0] = P01;
		P[1] = P10;
	}
	
	public int[] GenerateUtilizationData(int samplenum)
	{
		int[] data = new int[samplenum];
	
		for (int i = 0; i < samplenum; i++) {
			data[i] +=pd [state].sample();
			if (data[i] > 100)
			{
				if (i < samplenum - 1)
				{
					data[i + 1] += data[i] - 100;
				}
				data[i] = 100;
			}
			state = RandomStep(state);
		}
		
		return data;
	}
	
	public int RandomStep(int from)
	{
		double r = rand.nextDouble();
		if (r<=P[state]) return 1-state;
		else return state;
	}
	
	public static void main(String[] args) {
		MarkovModulatedPoisson mmp = new MarkovModulatedPoisson(60,11,0.07,0.01);
		int[] data = mmp.GenerateUtilizationData(288);
		for (int i = 0; i < data.length; i++) {
			System.out.println(data[i]);
		}
	}

}

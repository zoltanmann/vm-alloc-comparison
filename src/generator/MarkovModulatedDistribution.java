package generator;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;

public class MarkovModulatedDistribution {

	AbstractRealDistribution[] rd = new AbstractRealDistribution[2];
	public double[] P = new double[2];

	public static Random rand;
	
	int state = 0;
	
	
	public MarkovModulatedDistribution(AbstractRealDistribution dist0, AbstractRealDistribution dist1, double P01, double P10, long seed)
	{
		rand = new Random(seed);
		rd[0] = dist0;
		rd[1] = dist1;
		P[0] = P01;
		P[1] = P10;
	}
	
	public MarkovModulatedDistribution(AbstractRealDistribution dist0, AbstractRealDistribution dist1, double P01, double P10)
	{
		rand = new Random();
		rd[0] = dist0;
		rd[1] = dist1;
		P[0] = P01;
		P[1] = P10;
	}
	
	public int[] GenerateUtilizationData(int samplenum)
	{
		int[] data = new int[samplenum];
	
		for (int i = 0; i < samplenum; i++) {
			data[i] +=rd [state].sample();
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
		MarkovModulatedDistribution mmd = new MarkovModulatedDistribution(new LogNormalDistribution(1,1),new LogNormalDistribution(2,1),0.07,0.01);
		MarkovModulatedPoisson mmp = new MarkovModulatedPoisson(60,11,0.07,0.01);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("markov_cpuusage.txt")));
			for(int i = 0; i<1052; i++)
			{
				bw.write(String.valueOf(i));bw.newLine();
				bw.write(String.valueOf(300000));bw.newLine();
				bw.write(String.valueOf(288));bw.newLine();
				int[] data;
				if (i%2==0) data = mmd.GenerateUtilizationData(288);
				else data = mmp.GenerateUtilizationData(288);
				for (int j = 0; j < data.length; j++) {
					bw.write(String.valueOf(data[j]));bw.newLine();
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	

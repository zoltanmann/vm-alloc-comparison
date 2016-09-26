package generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Markov100 {

	public static Random rand;
	
	public int state = 0;
	
	public long[] unigramfreq = new long[101];
	public long[][] bigramfreq = new long[101][101];
	
	public Markov100()
	{
		rand = new Random();
	}
	
	public Markov100(long seed)
	{
		rand = new Random(seed);
	}
	
	public void LoadData(String inputFileName)
	{
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(inputFileName));
		
			String line;
	
			int prev = -1;
			
			while((line = input.readLine()) != null) {
				int cloudletId = Integer.parseInt(line);
				int sampleinterval = Integer.parseInt(input.readLine());
				int samplenum = Integer.parseInt(input.readLine());
				for (int i = 0; i < samplenum; i++) {
					int p = Integer.parseInt(input.readLine());
					unigramfreq[p]++;
					if (prev!=-1)
					{
						bigramfreq[prev][p]++;
					}
					prev=p;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int RandomStep(int from)
	{
		int sum = 0;
		for (int i = 0; i < 101; i++) {
			sum += bigramfreq[from][i];
		}
		int r = rand.nextInt(sum + 1);
		int to = 0;
		while(r>0)
		{
			r -= bigramfreq[from][to];
			to++;
		}
			
		return to;
	}
	
	public int[] GenerateUtilizationData(int samplenum)
	{
		int[] data = new int[samplenum];
		
		state = rand.nextInt(101);
				
		for (int i = 0; i < samplenum; i++) {
			data[i] = state;
			state = RandomStep(state);
		}
		
		return data;
	}
	
	public static void main(String[] args) {
		
		Markov100 mk100 = new Markov100();
		mk100.LoadData("C:\\workloads\\planetlab\\1427963857376_planetlab_cpuusage.txt");
		int[] data = mk100.GenerateUtilizationData(288);
		for (int i = 0; i < data.length; i++) {
			System.out.println(data[i]);
		}
	}

}

package bitbrains;

import java.util.ArrayList;
	
class BBTask {
		public String ID;
		public long  StartTime;
		public long  EndTime;
		public int cores; 
		public double requiredCPU;
		public double requiredRAM;
		
		public ArrayList<Integer> usedCPU;
		public ArrayList<Integer> usedRAM;
		
		public BBTask(String id, long ts, int c, double rc, double rr)
		{
			ID=id;
			StartTime = ts;
			cores = c;
			requiredCPU = rc;
			requiredRAM = rr;
			
			usedCPU = new ArrayList<Integer>(); 
			usedRAM = new ArrayList<Integer>(); 
		}
}

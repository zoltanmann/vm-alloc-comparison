package trace;

import java.util.ArrayList;

class GoogleTask
{
	public boolean complete = false;
	public String ID;
	public long  TSubmit;
	public double requiredCPU;
	public double requiredRAM;
	public double requiredDisk;
	
	public ArrayList<Integer> usedCPU;
	public ArrayList<Integer> usedRAM;
	public ArrayList<Integer> usedDisk;
	
	public GoogleTask(String id, long ts, double rc, double rr, double rd)
	{
		ID=id;
		TSubmit = ts;
		requiredCPU = rc;
		requiredRAM = rr;
		requiredDisk = rd; 
		
		usedCPU = new ArrayList<Integer>(); 
		usedRAM = new ArrayList<Integer>(); 
		usedDisk = new ArrayList<Integer>(); 
	}
}

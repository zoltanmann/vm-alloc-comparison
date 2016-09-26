package generator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class Main {

// implemented operators
public static final char[] operators = {'+', '-', '*', '/', '^', 's', 'p', 'm', 'c'};

// seal character, see sealexpression for usage
public static final char seal = '#';

public static Random rand = new Random();

// all elements of array1 equal to seals
public static boolean allseal(char[] array1)
{
	for(char c: array1)
	{
		if (c!=seal) return false;
	}
	return true;
}

// replaces all parenthesized subexpressions of inputc with the seal character
public static char[] sealexpression(char[] inputc)
{
	char[] array1 = new char[inputc.length];
	int level = 0;
	for (int i=0;i<inputc.length;i++)
	{
		if (inputc[i]=='(')
		{
			level++;
		}
		
		if (level>0)
		{
			array1[i]=seal;
		}
		else
		{
			array1[i]=inputc[i];
		}
		
		if (inputc[i]==')')
		{
			level--;
		}
	}
	return array1;
}

//	parser
public static double evalfunc(String input, long T) {
	if (input.equals("T"))
	{
		return T;
	}
	else if (input.equals("R"))
	{
		return rand.nextDouble();
	}
	else if (isNumber(input))
	{
		return Double.parseDouble(input);
	}
	else
	{
		char[] inputc = input.toCharArray();
		char[] array1 = sealexpression(inputc);
		
		// if the whole expression is in parenthesis, we strip the outermost pair
		if (allseal(array1)) return evalfunc(input.substring(1,input.length()-1),T);
		
		int i = -1;
		boolean gotroot = false;
		// position of the root/pivot operator, by which we'll split into subexpressions
		int rootpos = -1;
		
		while(!gotroot && i<operators.length)
		{
			i++;
			
			int j = input.length() - 1;
			
			while(j>=0 && array1[j]!=operators[i])
			{
				j--;
			}
			
			if (j>=0)
			{
				gotroot = true;
				rootpos = j;
			}
		}
		
		if (i>=operators.length)
		{
			return Double.NaN;
		}
		else
		{
			// split the expression by the chosen operator, then recursively evaluate the subexpressions
			String arg0 = input.substring(0,rootpos);
			String arg1 = input.substring(rootpos + 1,input.length());
			
			switch(operators[i])
			{
				case '^':
				{
					return Math.pow(evalfunc(arg0,T), evalfunc(arg1,T));
				}
				case '*':
				{
					return evalfunc(arg0,T)*evalfunc(arg1,T);
				}
				case '/':
				{
					return evalfunc(arg0,T)/evalfunc(arg1,T);
				}
				case '+':
				{
					return evalfunc(arg0,T)+evalfunc(arg1,T);
				}
				case '-':
				{
					return evalfunc(arg0,T)-evalfunc(arg1,T);
				}
				case 's':
				{
					return Math.sin(evalfunc(arg1,T));
				}
				case 'p':
				{
					String[] sargs = new String(sealexpression(arg1.substring(1,arg1.length()-1).toCharArray())).split(",");
					if (sargs.length!=3) return Double.NaN;
					String[] args = new String[3];
					int acc = 1;
					for(int j = 0; j < 3; j++)
					{
						args[j] = arg1.substring(acc,acc+sargs[j].length());	
						acc += (args[j].length() + 1);
					}
					double start = evalfunc(args[1],T);
					double end =  evalfunc(args[2],T);
					long period = Math.round(end-start+1);
					return evalfunc(args[0],T%period);
				}
				case 'm':
				{
					//String[] args = arg1.substring(1,arg1.length()-1).split(",");
					String[] sargs = new String(sealexpression(arg1.substring(1,arg1.length()-1).toCharArray())).split(",");
					if (sargs.length!=3) return Double.NaN;
					String[] args = new String[3];
					int acc = 1;
					for(int j = 0; j < 3; j++)
					{
						args[j] = arg1.substring(acc,acc+sargs[j].length());	
						acc += (args[j].length() + 1);
					}
					long width =  Math.round(evalfunc(args[2],T));
					if ((T/width)%2==0)
					{
						return evalfunc(args[0],T);
					}
					else
					{
						return evalfunc(args[1],T);
					}
				}
				case 'c':
				{
					String[] sargs = new String(sealexpression(arg1.substring(1,arg1.length()-1).toCharArray())).split(",");
					
					if (sargs.length!=6) return Double.NaN;
					String[] args = new String[6];
					int acc = 1;
					for(int j = 0; j < 6; j++)
					{
						args[j] = arg1.substring(acc,acc+sargs[j].length());
						acc += (args[j].length() + 1);
					}
					long start1 = Math.round(evalfunc(args[2],T));
					long end1 =  Math.round(evalfunc(args[3],T));
					long length1 = end1-start1+1;
					long start2 = Math.round(evalfunc(args[4],T));
					long end2 =  Math.round(evalfunc(args[5],T));
					long length2 = end2-start2+1;
					
					if (T<length1)
					{
						return evalfunc(args[0], start1 + T);
					}
					else if(T<length1+length2)
					{
						return evalfunc(args[1], start2 + T-length1);
					}
					else
					{
						return 0;
					}
				}
			}
		}
		
		return Double.NaN;
	}
}

public static boolean isNumber(String n) {
	  try  
	  {
		  Double.parseDouble(n);  
	  }  
	  catch(NumberFormatException nfe)  
	  {
		  return false;  
	  }  
	  return true;
}

	public static void main(String[] args) {
		/*for (int i = 0; i < 100; i++) {
			//System.out.println(Math.round(evalfunc("T*5+30.3+c(10*s(T/10),c((0-4)*T,50,0,25,0,25),0,50,0,50)",i)));
			System.out.println(Math.round(evalfunc("c(T,T*T/30,0,30,0,40)",i)));
		}*/
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("periodic_cpuusage.txt")));
			for(int i = 0; i<1052; i++)
			{
				bw.write(String.valueOf(i));bw.newLine();
				bw.write(String.valueOf(300000));bw.newLine();
				bw.write(String.valueOf(288));bw.newLine();
				for (int j = 0; j < 9; j++) {
					for (int k = 0; k < 32; k++) {
						bw.write(String.valueOf(k*k*100/625));bw.newLine();
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

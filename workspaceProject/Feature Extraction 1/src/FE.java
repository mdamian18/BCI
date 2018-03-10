import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class feature_extraction{
	static int NUMBER_OF_TRIALS = 40;
	static int NUMBER_OF_RUNS = 9;
	static int NUMBER_OF_SAMPLES = 986780;
	static int NUMBER_OF_CHANNELS = 3;
	static int NUMBER_OF_FILTERS = 2;
	static int[] startTimeTrain = new int[NUMBER_OF_TRIALS * NUMBER_OF_RUNS/2];
	static int[] startTimeTest = new int[NUMBER_OF_TRIALS * NUMBER_OF_RUNS/2];
	static int[] classLabel = new int[NUMBER_OF_TRIALS * NUMBER_OF_RUNS/2];
	static double[][] signalData = new double[NUMBER_OF_SAMPLES][NUMBER_OF_CHANNELS];
	static double[][] filteredSignal = new double[NUMBER_OF_SAMPLES][NUMBER_OF_CHANNELS];
	static int filterOrder = 2;
	static double[] featureVector = new double[NUMBER_OF_CHANNELS*NUMBER_OF_FILTERS];
	
	public static void main (String args[]){
		
		Scanner read1, read2, read3;
		int no = NUMBER_OF_RUNS * NUMBER_OF_TRIALS, m=0, n=0, l=0;
		try {
				read1 = new Scanner (new File("src/k3b_HDR_TRIG.txt"));
				read2 = new Scanner (new File("src/k3b_HDR_Classlabel.txt"));
				
			
			for(int i=0; i<no; i++)
			{
			        if(read2.hasNextInt())
			        {
			            classLabel[n] = read2.nextInt();
			            n++;
			            read2.nextLine();
			            if(read1.hasNextInt())
				        {
				            startTimeTrain[m] = read1.nextInt();
				            m++;
				        }
			        }
			        else
			        {
			        	read2.nextLine();
			        	if(read1.hasNextInt())
				        {
				            startTimeTest[l] = read1.nextInt();
				            l++;
				        }
			        }
			}
			read1.close();
			read2.close();
			
			read3 = new Scanner (new File("src/k3b_s.txt"));
			int p=0;
			for(int i=0; i<NUMBER_OF_SAMPLES; i++)
			{
				for(int j=0; j<60; j++)
				{
					if(read3.hasNextDouble())
					{
						//select only relevant channels
						if(j==28 || j==31 || j==34)
						{
							signalData[i][p] = read3.nextDouble();
							p++;						
						}
						else
						{
							read3.nextDouble();
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.print("Error while reading inputs");
		}
		System.out.print(m +" "+ l);
		System.out.print(signalData[105750][3]);
		
		//filters parameters
		double[] b;
		double[] a;
	
		//filter Mu waves
		//bandpass filter (9 Hz to 11 Hz)
		//filter parameters
		b = new double[] {0.000609854718715838,	0.0,	-0.00121970943743168,	0.0,	0.000609854718715838};
		a = new double[] {1.0,	-3.80670951689333,	5.55280661514110,	-3.67374242998861,	0.931381682126903}; 
		filterSignal(b, a, 2);
		writeToFile("BandPassMu.txt");
		
		//square signal amplitudes
		rectification();
		writeToFile("PowerMu.txt");
		
		//lowpass filter ( < 40 Hz)
		//filter parameters
		b = new double[] {0.145323883877042,	0.290647767754085,	0.145323883877042};
		a = new double[] {1.0,	-0.671029090774096,	0.252324626282266}; 
		filterSignal(b, a, 1);
		writeToFile("LowPassMu.txt");
		
		//extract signal mean for each channel
		extractMean();
		
			
		//filter Beta waves
		//bandpass filter (14 Hz to 30 Hz)
		//filter parameters
		b = new double[] {0.0312389236796864,	0.0,	-0.0624778473593728,	0.0,	0.0312389236796864};
		a = new double[] {1.0,	-2.98893524665783,	3.71040234732454,	-2.23592995894801,	0.566486004657471}; 
		filterSignal(b, a, 2);
		writeToFile("BandPassBeta.txt");
			
		//square signal amplitudes
		rectification();
		writeToFile("PowerBeta.txt");
			
		//lowpass filter ( < 40 Hz)
		//filter parameters
		b = new double[] {0.145323883877042,	0.290647767754085,	0.145323883877042};
		a = new double[] {1.0,	-0.671029090774096,	0.252324626282266}; 
		filterSignal(b, a, 1);
		writeToFile("LowPassBeta.txt");
			
		//extract signal mean for each channel
		extractMean();

	}
	
	static void filterSignal(double[] b, double[] a, int n)
	{
		for(int c=0; c<NUMBER_OF_CHANNELS; c++)
		{
			for(int j=0; j<NUMBER_OF_SAMPLES; j++)
			{
				for(int k=0; k<n*filterOrder+1; k++)
				{
					if(j>=k){
						double input = signalData[j-k][c];
						if(input!=input){
							input = 0.0;
						}
						filteredSignal[j][c] += b[k]*input;
						if(k>0)
						{
							filteredSignal[j][c] -= a[k]*filteredSignal[j-k][c];
						}
					}
				}
			}
		}
	}
	
	static void rectification()
	{
		for(int q=0; q<NUMBER_OF_CHANNELS; q++)
		{
			for(int s=0; s<NUMBER_OF_SAMPLES; s++)
			{
				filteredSignal[s][q] = filteredSignal[s][q] * filteredSignal[s][q];
			}
		}
	}
	
	static void extractMean()
	{
		int s;
		for(int t=0; t<NUMBER_OF_TRIALS*NUMBER_OF_RUNS; t++)
		{
			for(int c=0; c<NUMBER_OF_CHANNELS; c++)
			{
				//ignore first 3 seconds of each trial
				s = startTimeTrain[t]+250*3;
				while(filteredSignal[s][c]!=0.0){
					//take the mean
					
				}
			}	
		}
	}
	
	static void writeToFile(String fileName)
	{
		BufferedWriter write = null;
		try{
			File output = new File (fileName);
			write = new BufferedWriter(new FileWriter(output));
			for(int i=0; i<NUMBER_OF_SAMPLES; i++){
				for(int j=0; j<NUMBER_OF_CHANNELS; j++){
					write.write(filteredSignal[i][j]+" ");
				}
				write.write("\n");
			}
			
		}catch (IOException e){
			System.out.print("Error while writing to file");
		}finally{
			try {
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
}
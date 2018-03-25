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
	static double[][] bandPower;
	static int filterOrder = 2;
	static double[][] featureVectorTrain = new double[NUMBER_OF_TRIALS * NUMBER_OF_RUNS/2][NUMBER_OF_CHANNELS*NUMBER_OF_FILTERS];
	static double[][] featureVectorTest = new double[NUMBER_OF_TRIALS * NUMBER_OF_RUNS/2][NUMBER_OF_CHANNELS*NUMBER_OF_FILTERS];
	
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
			int p, j;
			for(int i=0; i<NUMBER_OF_SAMPLES; i++)
			{
				j=1;
				while(j<36)
				{
					if(read3.hasNextDouble())
					{
						//select only relevant channels
						if(j==28 || j==31 || j==34)
						{
							p=(j-28)/3;
							signalData[i][p] = read3.nextDouble();						
						}
						else
						{
							read3.nextDouble();
						}
					}
					j++;
				}
				read3.nextLine();
			}
		} catch (FileNotFoundException e) {
			System.out.print("Error while reading inputs");
		}
		System.out.print(m +" "+ l);
		System.out.print(signalData[105750][2]);
		
		//filters parameters
		double[] b;
		double[] a;
	
		//filter Mu waves
		//bandpass filter (9 Hz to 11 Hz)
		//filter parameters
		b = new double[] {0.000609854718715838,	0.0,	-0.00121970943743168,	0.0,	0.000609854718715838};
		a = new double[] {1.0,	-3.80670951689333,	5.55280661514110,	-3.67374242998861,	0.931381682126903}; 
		filteredSignal = filterSignal(signalData, b, a, 5);
		writeToFile("BandPassMu.txt", filteredSignal);
		System.out.println("Mu bandpass");
		
		//square signal amplitudes
		rectification();
		writeToFile("PowerMu.txt", filteredSignal);
		System.out.println("Mu rectification");
		
		//lowpass filter ( < 40 Hz)
		//filter parameters
		b = new double[] {2.13961520381389e-05,	0.000106980760190695,	0.000213961520381389,	0.000213961520381389,	0.000106980760190695,	2.13961520381389e-05};
		a = new double[] {1.0,	-4.18730004786440,	7.06972275279247,	-6.00995814818733,	2.57042930252410,	-0.442209182399620}; 
		bandPower = filterSignal(filteredSignal, b, a, 6);
		writeToFile("LowPassMu.txt", bandPower);
		System.out.println("Mu lowpass");
		
		//extract signal mean for each channel
		extractMean(0);
		System.out.println("Mu mean");
		
			
		//filter Beta waves
		//bandpass filter (14 Hz to 30 Hz)
		//filter parameters
		b = new double[] {0.0312389236796864,	0.0,	-0.0624778473593728,	0.0,	0.0312389236796864};
		a = new double[] {1.0,	-2.98893524665783,	3.71040234732454,	-2.23592995894801,	0.566486004657471}; 
		filteredSignal = filterSignal(signalData, b, a, 5);
		writeToFile("BandPassBeta.txt", filteredSignal);
		System.out.println("Beta bandpass");
			
		//square signal amplitudes
		rectification();
		writeToFile("PowerBeta.txt", filteredSignal);
		System.out.println("Beta rectification");
			
		//lowpass filter ( < 40 Hz)
		//filter parameters
		b = new double[] {2.13961520381389e-05,	0.000106980760190695,	0.000213961520381389,	0.000213961520381389,	0.000106980760190695,	2.13961520381389e-05};
		a = new double[] {1.0,	-4.18730004786440,	7.06972275279247,	-6.00995814818733,	2.57042930252410,	-0.442209182399620}; 
		bandPower = filterSignal(filteredSignal, b, a, 6);
		writeToFile("LowPassBeta.txt", bandPower);
		System.out.println("Beta lowpass");
			
		//extract signal mean for each channel
		extractMean(3);
		System.out.println("Beta mean");
		
		double min1=99999999,min2=9999999,max1=-1,max2=-1;
		//write feature values to file
		BufferedWriter write1 = null, write2=null;
		try{
			File output1 = new File ("training_features.txt");
			File output2 = new File ("testing_features.txt");
			write1 = new BufferedWriter(new FileWriter(output1));
			write2 = new BufferedWriter(new FileWriter(output2));
			for(int i=0; i<NUMBER_OF_TRIALS*NUMBER_OF_RUNS/2; i++)
			{
				for(int j=0; j<NUMBER_OF_CHANNELS*NUMBER_OF_FILTERS; j++)
				{
					if(featureVectorTrain[i][j]>max1)
						max1 = featureVectorTrain[i][j];
					if(featureVectorTrain[i][j]<min1)
						min1 = featureVectorTrain[i][j];
					if(featureVectorTest[i][j]>max2)
						max2 = featureVectorTest[i][j];
					if(featureVectorTest[i][j]<min2)
						min2 = featureVectorTest[i][j];
					write1.write(featureVectorTrain[i][j]+" ");
					write2.write(featureVectorTest[i][j]+" ");
				}
				write1.write("\n");
				write2.write("\n");
			}
			
		}catch (IOException e){
			System.out.print("Error while writing to file");
		}finally{
			try {
				write1.close();
				write2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.print(min1 + " " + max1 + " " + min2 + " " + max2);

	}
	
	static double[][] filterSignal(double[][] inputSignal, double[] b, double[] a, int n)
	{
		double[][] newSignal = new double[NUMBER_OF_SAMPLES][NUMBER_OF_CHANNELS];
		for(int c=0; c<NUMBER_OF_CHANNELS; c++)
		{
			for(int j=0; j<NUMBER_OF_SAMPLES; j++)
			{
				for(int k=0; k<n; k++)
				{
					if(j>=k){
						double input = inputSignal[j-k][c];
						if(input!=input){
							input = 0.0;
							//filteredSignal[j-k][c] = 0.0;
						}
						newSignal[j][c] += b[k]*input;
						if(k>0)
						{
							newSignal[j][c] -= a[k]*newSignal[j-k][c];
						}
					}
				}
			}
		}
		return newSignal;
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
	
	static void extractMean(int l)
	{
		int s, u, v, w;
		for(int t=0; t<NUMBER_OF_TRIALS*NUMBER_OF_RUNS/2; t++)
		{
			for(int c=0; c<NUMBER_OF_CHANNELS; c++)
			{
				//ignore first 3 seconds of each trial
				s = startTimeTrain[t]+250*3;
				v = startTimeTrain[t]+250*7;
				while(s<v)
				{
					//take the mean
					//sum += filteredSignal[s][c];
					featureVectorTrain[t][c+l] += bandPower[s][c]; 
					//System.out.println(bandPower[s][c]);
					s++;
				}
				featureVectorTrain[t][c+l] *= 1.0/250.0;
				//System.out.println(featureVectorTrain[t][c+l]);
				
				u = startTimeTest[t]+250*3;
				w = startTimeTest[t]+250*7;
				while(u<w)
				{
					//sum += filteredSignal[u][c];
					featureVectorTest[t][c+l] += bandPower[u][c];
					u++;
				}
				featureVectorTest[t][c+l] *= 1.0/250.0;
			}	
		}
	}
	
	static void writeToFile(String fileName, double[][] signal)
	{
		BufferedWriter write = null;
		try{
			File output = new File (fileName);
			write = new BufferedWriter(new FileWriter(output));
			for(int i=0; i<NUMBER_OF_SAMPLES; i++){
				for(int j=0; j<NUMBER_OF_CHANNELS; j++){
					write.write(signal[i][j]+" ");
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
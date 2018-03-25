import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

class MLP
{
	static int MAX_ITER = 200000;
	static double LEARNING_RATE = 0.45;
	static double MOMENTUM = 0.1;
	static int NUM_TRAINING_INSTANCES = 180;
	static int NUM_TESTING_INSTANCES = 180;
	static int NUM_INPUT_NEURONS = 6;
	static int NUM_HIDDEN_NEURONS = 9;
	static int NUM_OUTPUT_NEURONS = 4;
	static int theta = 0; 
	static double[][] inputs;
	static double[] hidden = new double [NUM_HIDDEN_NEURONS];
	static double[][] outputs = new double [NUM_TRAINING_INSTANCES][NUM_OUTPUT_NEURONS];
	static double[][] actualOutputs = new double [NUM_TRAINING_INSTANCES][NUM_OUTPUT_NEURONS];
	//arrays of sums
	static double[] hiddenSum = new double [NUM_HIDDEN_NEURONS];
	static double[] outputSum = new double [NUM_OUTPUT_NEURONS];
	//array with output errors
	//static double[] outputError = new double [NUM_OUTPUT_NEURONS];
	//array for node deltas
	static double[] hiddenDelta = new double [NUM_HIDDEN_NEURONS];
	static double[] outputDelta = new double [NUM_OUTPUT_NEURONS];
	//matrices for weights
	static double[][] input_to_hidden_weights = new double[NUM_INPUT_NEURONS+1][NUM_HIDDEN_NEURONS];
	static double[][] hidden_to_output_weights = new double[NUM_HIDDEN_NEURONS+1][NUM_OUTPUT_NEURONS];
	//matrices for gradients
	static double[][] input_to_hidden_gradient = new double[NUM_INPUT_NEURONS+1][NUM_HIDDEN_NEURONS];
	static double[][] hidden_to_output_gradient = new double[NUM_HIDDEN_NEURONS+1][NUM_OUTPUT_NEURONS];
	//matrices for weights adjustments
	static double[][] input_to_hidden_weights_adjust = new double[NUM_INPUT_NEURONS+1][NUM_HIDDEN_NEURONS];
	static double[][] hidden_to_output_weights_adjust = new double[NUM_HIDDEN_NEURONS+1][NUM_OUTPUT_NEURONS];
	static double min;
	static double max;
	static int accuracy; 
	
	public static void main(String args[]){                       
		
		//double output;

		//initialise the weights to random numbers between 0 and 1
		for (int i=0; i<=NUM_INPUT_NEURONS; i++)
			for (int j=0; j<NUM_HIDDEN_NEURONS; j++)
				input_to_hidden_weights[i][j] = Math.random()*2 -1;
		
		for (int i=0; i<=NUM_HIDDEN_NEURONS; i++)
			for (int j=0; j<NUM_OUTPUT_NEURONS; j++)
				hidden_to_output_weights[i][j] = Math.random()*2 -1;
		
		//matrix for training inputs
		inputs = new double [NUM_TRAINING_INSTANCES][NUM_INPUT_NEURONS];
		
		//get the training values for the input neurons
		getInputValues(0);
		
		//matrix for training outputs
		actualOutputs = new double [NUM_TRAINING_INSTANCES][NUM_OUTPUT_NEURONS];
		
		//get the training values of the actual outputs
		getActualOutputValues(0);	
		
		//train the network
		train();
		
		//matrix for testing inputs
		inputs = new double [NUM_TESTING_INSTANCES][NUM_INPUT_NEURONS];
		
		//get the testing values for the input neurons
		getInputValues(1);
		
		//matrix for testing outputs
		actualOutputs = new double [NUM_TESTING_INSTANCES][NUM_OUTPUT_NEURONS];
		
		//get the testing values of the actual outputs
		getActualOutputValues(1);
		
		//test the performance of the network
		test();
		
	}//end main  
	
	static void getInputValues(int t) {
		// t=0 means training mode and t=1 means testing mode
		
		//read from file
		int col = NUM_INPUT_NEURONS;
		int row;
		if (t==0)
		{
			row = NUM_TRAINING_INSTANCES;
			min = 5.585991368644054;
			max = 266.796790653213;
		}
		
		else{
			row = NUM_TESTING_INSTANCES;
			min = 5.942390525581951;
			max = 270.8214043599919;
		}
		Scanner read;
		try {
			if (t==0)
				read = new Scanner (new File("training_features.txt"));
			else 
				read = new Scanner (new File("testing_features.txt"));
			for(int i = 0; i < row; i++)
			{
			    for(int j = 0; j < col; j++)
			    {
			        if(read.hasNextDouble())
			        {
			            inputs[i][j] = read.nextDouble();
			            inputs[i][j] -= min;
			            inputs[i][j] /= max;
			            inputs[i][j] = inputs[i][j]*2-1;
			            
			        }
			    }
			}
			read.close();
		} catch (FileNotFoundException e) {
			System.out.print("Error while reading inputs");
		}
		
		//random inputs
//		Random rand = new Random();
//		double min = 0.0, max = 1.0;
//		for(int i = 0; i < col; i++)
//		{
//		    for(int j = 0; j < row; j++)
//		    {
//		            inputs[j][i] = min + (max - min) * rand.nextDouble();
//		    }
//		}
		
	}
	
	static void getActualOutputValues (int t){
		// t=0 means training mode and t=1 means testing mode
		
		//read from file
		int row = NUM_TRAINING_INSTANCES;
		int col = NUM_OUTPUT_NEURONS;
//		int row = NUM_OUTPUT_NEURONS;
//		int col;
//		if (t==0)
//			col = NUM_TRAINING_INSTANCES;
//		else
//			col = NUM_TESTING_INSTANCES;
		Scanner read;
		try {
			if (t==0)
				read = new Scanner (new File("src/output.txt"));
			else
				read = new Scanner (new File("src/testoutputs.txt"));
			for(int i = 0; i < row; i++)
			{
			    for(int j = 0; j < col; j++)
			    {
			        if(read.hasNextDouble())
			        {
			            actualOutputs[i][j] = read.nextDouble();
			        }
			    }
			}
			read.close();
		} catch (FileNotFoundException e) {
			System.out.print("Error while reading outputs");
		}
		
		//calculate actual outputs for a known function
		//Sphere function
		/*double sum;
		for (int k=0; k<col; k++){
			sum = 0.0;
			for (int i=0; i<NUM_INPUT_NEURONS; i++){
				sum += Math.pow(inputs[i][k], 2.0);
			}
			actualOutputs[0][k] = sum/2.0;
		}*/
		
		//implement function
	}
	
	static void train(){
		
		double outputError, globalError = 0.0;
		int ins, iteration;
		double weightDifference;
		
		iteration = 0;
		do {
			iteration++;
			globalError = 0.0;
			//loop through all instances (complete one epoch)
			for (ins = 0; ins < NUM_TRAINING_INSTANCES; ins++) {
				
				//calculate values of hidden neurons
				for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
					hidden[h] = calculateHiddenValue(h, ins);
				}
				
				//calculate values of output neurons
				for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
					outputs[ins][o] = calculateOutputValue(o);
				}
						
				//calculate output deltas
				for (int e=0; e<NUM_OUTPUT_NEURONS; e++){
					outputError = outputs[ins][e] - actualOutputs[ins][e];
					outputDelta[e] = -outputError * sigmoidDerivative(outputSum[e]);
					globalError += outputError * outputError;
				}
				
				//calculate hidden deltas
				for (int f=0; f<NUM_HIDDEN_NEURONS; f++){
					hiddenDelta[f] = sigmoidDerivative(hiddenSum[f]) * calculateSum(f);
				}
				
				//calculating gradients of weights
				for (int h=0; h<=NUM_HIDDEN_NEURONS; h++){
					for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
						if(h != NUM_HIDDEN_NEURONS)
							hidden_to_output_gradient[h][o] = outputDelta[o] * hidden[h];
						else
							hidden_to_output_gradient[h][o] = outputDelta[o];
					}
				}
				for (int i=0; i<=NUM_INPUT_NEURONS; i++){
					for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
						if(i != NUM_INPUT_NEURONS)
							input_to_hidden_gradient[i][h] = hiddenDelta[h] * inputs[ins][i];
						else
							input_to_hidden_gradient[i][h] = hiddenDelta[h];
					}
				}

				//update weights
				for (int h=0; h<=NUM_HIDDEN_NEURONS; h++){
					for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
						weightDifference = LEARNING_RATE * hidden_to_output_gradient[h][o] + MOMENTUM * hidden_to_output_weights_adjust[h][o];
						hidden_to_output_weights_adjust[h][o] = weightDifference;
						hidden_to_output_weights[h][o] += weightDifference;
					}
				}
				for (int i=0; i<=NUM_INPUT_NEURONS; i++){
					for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
						weightDifference = LEARNING_RATE * input_to_hidden_gradient[i][h] + MOMENTUM * input_to_hidden_weights_adjust[i][h];
						input_to_hidden_weights_adjust[i][h] = weightDifference;
						input_to_hidden_weights[i][h] += weightDifference;
					}
				}
			}
			
			if (iteration%100==0)
				System.out.print(globalError + " " + iteration + "\n");

		} while (globalError > 0.0001 && iteration<=MAX_ITER);
		
	}
	
	static void test(){
		double delta;
		double m=0.0;
		int cl=0;
		for (int ins=0; ins<NUM_TESTING_INSTANCES; ins++){
		
			//calculate values of hidden neurons
			for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
				hidden[h] = calculateHiddenValue(h, ins);
			}
			
			//calculate values of output neurons
			for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
				m=0.0;
				outputs[ins][o] = calculateOutputValue(o);
				//values of output neurons
				
				//calculate maximum value 
				for(int i=0; i<NUM_OUTPUT_NEURONS; i++)
				{
					if(outputs[ins][i]>m)
					{
						m = outputs[ins][i];
						cl = i+1;
					}
				}
				
				System.out.print(actualOutputs[ins][o] + " " /*+ outputs[ins][o] + " "*/);
				//determine to which class does the output belong
//				delta = Math.abs(1.0 - outputs[o][ins]);
//				if (delta > 0.5)
//					System.out.print("Class 0");
//				else
//					System.out.print("Class 1");
//				
			}
			if(actualOutputs[ins][cl-1]==1.0)
				accuracy++;
			System.out.print(cl);
			System.out.println();
		}
		System.out.print(accuracy);
		
		//implement a way of testing the performance of the network
	}
	
	static double calculateHiddenValue (int n, int ins){
		double sum = 0.0;
		for (int d=0; d<NUM_INPUT_NEURONS; d++)
			sum += inputs[ins][d]*input_to_hidden_weights[d][n];
		//add the weight of the bias to the sum;
		sum += input_to_hidden_weights[NUM_INPUT_NEURONS][n];
		hiddenSum[n] = sum;
		return (1.0/(1+Math.pow(Math.E,-sum)));
	}

	static double calculateOutputValue (int t){
		double sum = 0.0;
		for (int d=0; d<NUM_HIDDEN_NEURONS; d++)
			sum += hidden[d]*hidden_to_output_weights[d][t];
		//add the weight of the bias to the sum;
		sum += hidden_to_output_weights[NUM_HIDDEN_NEURONS][t];
		outputSum[t] = sum;
		return (1.0/(1+Math.pow(Math.E,-sum)));
	}
	
	static double sigmoidDerivative (double s){
		double f = 1.0/(1+Math.pow(Math.E, -s));
		return (f*(1-f));
	}
	
	static double calculateSum (int i){
		double total = 0.0;
		for (int j=0; j<NUM_OUTPUT_NEURONS; j++){
			total += hidden_to_output_weights[i][j] * outputDelta[j];
		}
		return total;
	}
	
}
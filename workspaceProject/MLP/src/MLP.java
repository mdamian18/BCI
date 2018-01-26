import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

class MLP
{
	static int MAX_ITER = 10000;
	static double LEARNING_RATE = 0.7;
	static double MOMENTUM = 0.05;
	static int NUM_TRAINING_INSTANCES = 4;
	static int NUM_TESTING_INSTANCES = 4;
	static int NUM_INPUT_NEURONS = 2;
	static int NUM_HIDDEN_NEURONS = 5;
	static int NUM_OUTPUT_NEURONS = 1;
	static int theta = 0; 
	static double[][] inputs = new double [NUM_INPUT_NEURONS][NUM_TRAINING_INSTANCES];
	static double[] hidden = new double [NUM_HIDDEN_NEURONS];
	static double[][] outputs = new double [NUM_OUTPUT_NEURONS][NUM_TRAINING_INSTANCES];
	static double[][] actualOutputs = new double [NUM_OUTPUT_NEURONS][NUM_TRAINING_INSTANCES];
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
	
	public static void main(String args[]){                       
		
		//double output;

		//initialise the weights to random numbers between 0 and 1
		for (int i=0; i<=NUM_INPUT_NEURONS; i++)
			for (int j=0; j<NUM_HIDDEN_NEURONS; j++)
				input_to_hidden_weights[i][j] = Math.random()*2 -1;
		
		for (int i=0; i<=NUM_HIDDEN_NEURONS; i++)
			for (int j=0; j<NUM_OUTPUT_NEURONS; j++)
				hidden_to_output_weights[i][j] = Math.random()*2 -1;
		
		//get the training values for the input neurons
		getInputValues(0);
		
		//get the training values of the actual outputs
		getActualOutputValues(0);	
		
		//train the network
		train();
		
		//get the testing values for the input neurons
		getInputValues(1);
		
		//get the testing values of the actual outputs
		getActualOutputValues(1);
		
		//test the performance of the network
		test();
		
	}//end main  
	
	static void getInputValues(int t) {
		// t=0 means training mode and t=1 means testing mode
		
		//read from file
		int row = NUM_INPUT_NEURONS;
		int col;
		if (t==0)
			col = NUM_TRAINING_INSTANCES;
		else
			col = NUM_TESTING_INSTANCES;
		Scanner read;
		try {
			if (t==0)
				read = new Scanner (new File("src/input.txt"));
			else 
				read = new Scanner (new File("src/testinputs.txt"));
			for(int i = 0; i < col; i++)
			{
			    for(int j = 0; j < row; j++)
			    {
			        if(read.hasNextDouble())
			        {
			            inputs[j][i] = read.nextDouble();
			        }
			    }
			}
			read.close();
		} catch (FileNotFoundException e) {
			System.out.print("Error while reading inputs");
		}
		
		//random inputs
		/*Random rand = new Random();
		double randomValue = 0.0, min = 0.0, max = 1000.0;
		for (int i=0; i<NUM_INPUT_NEURONS; i++)
			randomValue = min + (max - min) * rand.nextDouble();
		for(int i = 0; i < col; i++)
		{
		    for(int j = 0; j < row; j++)
		    {
		            inputs[j][i] = randomValue;
		    }
		}*/
	}
	
	static void getActualOutputValues (int t){
		// t=0 means training mode and t=1 means testing mode
		
		//read from file
		int row = NUM_OUTPUT_NEURONS;
		int col;
		if (t==0)
			col = NUM_TRAINING_INSTANCES;
		else
			col = NUM_TESTING_INSTANCES;
		Scanner read;
		try {
			if (t==0)
				read = new Scanner (new File("src/output.txt"));
			else
				read = new Scanner (new File("src/testoutputs.txt"));
			for(int i = 0; i < col; i++)
			{
			    for(int j = 0; j < row; j++)
			    {
			        if(read.hasNextDouble())
			        {
			            actualOutputs[j][i] = read.nextDouble();
			        }
			    }
			}
			read.close();
		} catch (FileNotFoundException e) {
			System.out.print("Error while reading outputs");
		}
		
		//calculate actual outputs for a known function
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
					outputs[o][ins] = calculateOutputValue(o);
				}
						
				//calculate output deltas
				for (int e=0; e<NUM_OUTPUT_NEURONS; e++){
					outputError = outputs[e][ins] - actualOutputs[e][ins];
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
							input_to_hidden_gradient[i][h] = hiddenDelta[h] * inputs[i][ins];
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

		} while (globalError > 0.001 && iteration<=MAX_ITER);
		
	}
	
	static void test(){
		
		for (int ins=0; ins<NUM_TESTING_INSTANCES; ins++){
		
			//calculate values of hidden neurons
			for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
				hidden[h] = calculateHiddenValue(h, ins);
			}
			
			//calculate values of output neurons
			for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
				outputs[o][ins] = calculateOutputValue(o);
				System.out.println();
				System.out.print(outputs[o][ins]);
			}
		}
		
		//implement a way of testing the performance of the network
	}
	
	static double calculateHiddenValue (int n, int ins){
		double sum = 0.0;
		for (int d=0; d<NUM_INPUT_NEURONS; d++)
			sum += inputs[d][ins]*input_to_hidden_weights[d][n];
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
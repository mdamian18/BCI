import java.io.File;
import java.io.FileNotFoundException;
import java.lang.*;
import java.util.Scanner;

class MLP
{
	static int MAX_ITER = 10000;
	static double LEARNING_RATE = 0.7;
	static double MOMENTUM = 0.05;
	static int NUM_INSTANCES = 4;
	static int NUM_INPUT_NEURONS = 2;
	static int NUM_HIDDEN_NEURONS = 5;
	static int NUM_OUTPUT_NEURONS = 1;
	static int theta = 0; 
	static double[][] inputs = new double [NUM_INPUT_NEURONS][NUM_INSTANCES];
	static double[] hidden = new double [NUM_HIDDEN_NEURONS];
	static double[] outputs = new double [NUM_OUTPUT_NEURONS];
	static double[][] actualOutputs = new double [NUM_OUTPUT_NEURONS][NUM_INSTANCES];
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
			
		double outputError, globalError = 0.0;
		int ins, iteration;
		double weightDifference;
		//double output;

		//initialise the weights to random numbers between 0 and 1
		for (int i=0; i<=NUM_INPUT_NEURONS; i++)
			for (int j=0; j<NUM_HIDDEN_NEURONS; j++)
				input_to_hidden_weights[i][j] = Math.random()*2 -1;
		
		for (int i=0; i<=NUM_HIDDEN_NEURONS; i++)
			for (int j=0; j<NUM_OUTPUT_NEURONS; j++)
				hidden_to_output_weights[i][j] = Math.random()*2 -1;

		iteration = 0;
		do {
			iteration++;
			globalError = 0.0;
			//loop through all instances (complete one epoch)
			for (ins = 0; ins < NUM_INSTANCES; ins++) {
				//get values for the input neurons
				getValues();
				//System.out.print(inputs[0]+" "+inputs[1]+" ");
				//calculate values of hidden neurons
				for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
					hidden[h] = calculateHiddenValue(h, ins);
				}
				//calculate values of output neurons
				for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
					outputs[o] = calculateOutputValue(o);
				}
				//System.out.print(outputs[0]+" ");
				//get the values of the actual outputs
				getActualOutputValues();			
				//calculate output deltas
				for (int e=0; e<NUM_OUTPUT_NEURONS; e++){
					outputError = outputs[e] - actualOutputs[e][ins];
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
//				for (int i=0; i<=NUM_INPUT_NEURONS; i++)
//					for (int j=0; j<NUM_HIDDEN_NEURONS; j++)
//						if (iteration%100==0)
//							//System.out.print("input" + input_to_hidden_weights[i][j]+" ");
				
//				for (int i=0; i<=NUM_HIDDEN_NEURONS; i++)
//					for (int j=0; j<NUM_OUTPUT_NEURONS; j++)
//						if (iteration%100==0)
//							//System.out.print(" hidden" +hidden_to_output_weights[i][j]+" ");
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
			
			if (iteration%100==0){
			System.out.print(globalError);
			System.out.print(" ");
			System.out.print(iteration);
			System.out.println();
			}
		} while (globalError > 0.001 && iteration<=MAX_ITER);
		inputs[0][0] = 0.0;
		inputs[1][0] = 1.0;
		//System.out.print(inputs[0]+" "+inputs[1]+" ");
		//calculate values of hidden neurons
		for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
			hidden[h] = calculateHiddenValue(h, 0);
		}
		//calculate values of output neurons
		for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
			outputs[o] = calculateOutputValue(o);
			System.out.println();
			System.out.print(outputs[0]);
		}
	

	}//end main  
	
	static void getValues() {
		//read from file
		int row = NUM_INPUT_NEURONS;
		int col = NUM_INSTANCES;
		Scanner read;
		try {
			read = new Scanner (new File("src/input.txt"));
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
		
		
		/*Random rand = new Random();
		for (int i=0; i<NUM_INPUT_NEURONS-1; i++)
			inputs[i] = rand.nextInt(1000); */
//		if (ins%4==0){
//			inputs[0] = 1.0;
//			inputs[1] = 1.0;
//		}
//		if (ins%4==1){
//			inputs[0] = 1.0;
//			inputs[1] = 0.0;
//		}
//		if (ins%4==2){
//			inputs[0] = 0.0;
//			inputs[1] = 1.0;
//		}
//		if (ins%4==3){
//			inputs[0] = 0.0;
//			inputs[1] = 0.0;
//		}
		
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
	
	static void getActualOutputValues (){
		//read from console
		//read from file
		int row = NUM_OUTPUT_NEURONS;
		int col = NUM_INSTANCES;
		Scanner read;
		try {
			read = new Scanner (new File("src/output.txt"));
			for(int i = 0; i < col; ++i)
			{
			    for(int j = 0; j < row; ++j)
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
		//initialize array  manually
//		if (ins%4==0){
//			actualOutputs[0] = 0.0;
//		}
//		if (ins%4==1){
//			actualOutputs[0] = 1.0;
//		}
//		if (ins%4==2){
//			actualOutputs[0] = 1.0;
//		}
//		if (ins%4==3){
//			actualOutputs[0] = 0.0;
//		}
	
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
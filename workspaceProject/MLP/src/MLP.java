import java.util.*;
import java.io.*;
import java.text.*;
import java.math.*;

class MLP
{
	static int MAX_ITER = 1000;
	static double LEARNING_RATE = 0.1; 
	static int NUM_INSTANCES = 10;
	static int NUM_INPUT_NEURONS = 3;
	static int NUM_HIDDEN_NEURONS = 3;
	static int NUM_OUTPUT_NEURONS = 1;
	static int theta = 0; 
	static double[] inputs = new double [NUM_INPUT_NEURONS];
	static double[] hidden = new double [NUM_HIDDEN_NEURONS];
	static double[] outputs = new double [NUM_OUTPUT_NEURONS];
	static double[] actualOutputs = new double [NUM_OUTPUT_NEURONS];
	//arrays of sums
	static double[] hiddenSum = new double [NUM_HIDDEN_NEURONS];
	static double[] outputSum = new double [NUM_OUTPUT_NEURONS];
	//array with output errors
	//static double[] outputError = new double [NUM_OUTPUT_NEURONS];
	//array for node deltas
	static double[] hiddenDelta = new double [NUM_HIDDEN_NEURONS];
	static double[] outputDelta = new double [NUM_OUTPUT_NEURONS];
	//create the matrices for weights
	static double[][] input_to_hidden_weights = new double[NUM_INPUT_NEURONS+1][NUM_HIDDEN_NEURONS];
	static double[][] hidden_to_output_weights = new double[NUM_HIDDEN_NEURONS+1][NUM_OUTPUT_NEURONS];
	
	public static void main(String args[]){                       
		
//		//100 random points
//		for(int i = 0; i < NUM_INSTANCES; i++){
//			x[i] = randomNumber(0 , 1000);
//			y[i] = randomNumber(0 , 1000); 
//			z[i] = randomNumber(0 , 1000);        
//			outputs[i] = 1.0;         
//			System.out.println(x[i]+"\t"+y[i]+"\t"+z[i]+"\t"+outputs[i]);
//		}

		
		double outputError, globalError;
		int ins, iteration;
		double output;

		//initialise the weights to random numbers between 0 and 1
		for (int i=0; i<=NUM_INPUT_NEURONS; i++)
			for (int j=0; j<=NUM_HIDDEN_NEURONS; j++)
				input_to_hidden_weights[i][j] = randomNumber(0,1);
		
		for (int i=0; i<=NUM_HIDDEN_NEURONS; i++)
			for (int j=0; j<=NUM_OUTPUT_NEURONS; j++)
				hidden_to_output_weights[i][j] = randomNumber(0,1);

		iteration = 0;
		do {
			iteration++;
			globalError = 0;
			//loop through all instances (complete one epoch)
			for (ins = 0; ins < NUM_INSTANCES; ins++) {
				//get values for the input neurons
				getValues();
				//calculate values of hidden neurons
				for (int h=0; h<NUM_HIDDEN_NEURONS; h++){
					hidden[h] = calculateHiddenValue(h);
				}
				//calculate values of output neurons
				for (int o=0; o<NUM_OUTPUT_NEURONS; o++){
					outputs[o] = calculateOutputValue(o);
				}
				//get the values of the actual outputs
				getActualOutputValues();			
				//calculate output deltas
				for (int e=0; e<NUM_OUTPUT_NEURONS; e++){
					outputError = outputs[e] - actualOutputs[e];
					outputDelta[e] = outputError * sigmoidDerivative(outputSum[e]);
					//calculate hidden deltas starting from output node e
					for (int f=0; f<NUM_HIDDEN_NEURONS; f++){
						hiddenDelta[f] = sigmoidDerivative(hiddenSum[f]) * weightsSum(hidden_to_output[][e], ) * outputDelta[e];
					}
				}
				
				
				outputError = outputs[0] - actualOutputs[0];
				//update weights and bias
				weights[0] += LEARNING_RATE * localError * x[p];
				weights[1] += LEARNING_RATE * localError * y[p];
				//weights[2] += LEARNING_RATE * localError * z[p];
				weights[2] += LEARNING_RATE * localError;
				//summation of squared error (error value for all instances)
				globalError += (outputError*outputError);
			}

			/* Root Mean Squared Error */
			System.out.println("Iteration "+iteration+" : RMSE = "+Math.sqrt(globalError/NUM_INSTANCES));
		} while (globalError != 0 && iteration<=MAX_ITER);

		System.out.println("\n=======\nDecision boundary equation:");
		System.out.println(weights[0] +"*x + "+weights[1]+"*y +  "+/*weights[2]+"*z + "+*/weights[2]+" = 0");

		//generate 10 new random points and check their classes
		//notice the range of -10 and 10 means the new point could be of class 1 or 0
		//-10 to 10 covers all the ranges we used in generating the 50 classes of 1's and 0's above
		//for(int j = 0; j < 10; j++){
			//double x1 = randomNumber(-10 , 10);
			//double y1 = randomNumber(-10 , 10);   
			//double z1 = randomNumber(-10 , 10); 
		

			//output = calculateOutput(theta,weights, x1, y1/*, z1*/);
			//System.out.println("\n=======\nNew Random Point:");
			//System.out.println("x = "+x1+",y = "+y1/*+ ",z = "+z1*/);
			//System.out.println("class = "+output);
		
	}//end main  
	
	static void getValues() {
		for (int i=0; i<NUM_INPUT_NEURONS; i++)
			inputs[i] = randomNumber(0,1000); 
	}
	
	static double calculateHiddenValue (int n){
		double sum = 0.0;
		for (int d=0; d<NUM_INPUT_NEURONS; d++)
			sum += inputs[d]*input_to_hidden_weights[d][n];
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
		//initialize array  manually
		actualOutputs[0] = 1.0;
	}
	
	static double sigmoidDerivative (double s){
		double f = 1.0/(1+Math.pow(Math.E, -s));
		return (f*(1-f));
	}
	
	static double weightsSum (double [] a, int len){
		for (int i=0; i<len; i++)
	}
	/**
	 * returns a random double value within a given range
	 * @param min the minimum value of the required range (int)
	 * @param max the maximum value of the required range (int)
	 * @return a random double value between min and max
	 */ 
	public static double randomNumber(int min , int max) {
		DecimalFormat df = new DecimalFormat("#.####");
		double d = min + Math.random() * (max - min);
		String s = df.format(d);
		double x = Double.parseDouble(s);
		return x;
	}

	/**
	 * returns either 1 or 0 using a threshold function
	 * theta is 0range
	 * @param theta an integer value for the threshold
	 * @param weights[] the array of weights
	 * @param x the x input value
	 * @param y the y input value
	 * @param z the z input value
	 * @return 1 or 0
	 */ 
	static double calculateOutput(int theta, double weights[], double x, double y, double z)
	{
		double sum = x * weights[0] + y * weights[1] + z * weights[2] + weights[2];
		return (1.0/(1+Math.pow(Math.E,-sum)));
	}
	
	

}
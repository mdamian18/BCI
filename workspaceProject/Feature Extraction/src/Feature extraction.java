import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class feature_extraction{
	static int NUMBER_OF_TRIALS = 60;
	static int NUMBER_OF_CLASSES = 6;
	static int[] startTime = new int[NUMBER_OF_TRIALS * NUMBER_OF_CLASSES];
	public static void main (String args[]){
		
		Scanner read1, read2;
		int k = NUMBER_OF_CLASSES * NUMBER_OF_TRIALS;
		try {
				read1 = new Scanner (new File("src/k3b_HDR_TRIG.txt"));
				read2 = new Scanner (new File("src/k3b_HDR_Classlabel.txt"));
			
			for(int i = 0; i < k; i++)
			{
			        if(read1.hasNextInt())
			        {
			            startTime[i] = read1.nextInt();
			        }
			        if(read2.hasNextInt())
			        {
			            startTime[i] = read2.nextInt();
			        }
			}
			read1.close();
			read2.close();
		} catch (FileNotFoundException e) {
			System.out.print("Error while reading inputs");
		}
	}
}
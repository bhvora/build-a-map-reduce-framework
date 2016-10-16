import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mapper.Mapper;
import reducer.Reducer;
import slave.Client;
import slave.Job;
import utils.Context;

/*
 * Cluster analysis is a map reduce program to produce average ticket prices for each airline in year 2015.
 */

public class ClusterAnalysis {

	/*
	 * Mapper class which takes the input file and feeds output key value pair to the Reducer R
	 * Here the output Key is carrier:month pair and value is the average ticket price
	 */
	public static class ClusterAnalysisMapper extends Mapper{
		@Override
		public void map(Object key, Object value, Context context) {
			super.map(key, value, context);
			String line = value.toString();
			line = line.replaceAll("\"", "");
			line = line.replaceAll(", ", "#");
			String[] airlinedata = line.split(",");
			int year = 0;
			String month = null;
			String carrier = null;
			String avgPrice = null;
			Boolean emptyRow = true; 

			// check for the year to be not empty
			try{
				year = Integer.valueOf(airlinedata[0]);		
			} catch (NumberFormatException e){
				emptyRow = false;
			}

			// outputting the key,value pair for correct input data for year 2015
			if(year == 2013 && airlinedata.length == 110 && emptyRow) { 
				month = airlinedata[2];
				carrier = airlinedata[6];
				avgPrice = airlinedata[109];
				String returnKey = carrier.trim() + ":"+ month.trim();
				String returnValue = avgPrice;
				context.write(returnKey, returnValue);
			} 
		}
	}

	/*
	 * Reducer class which takes the input key and value from Mappers and calculate the overall average of ticket price
	 * for each airline in year 2015.
	 */
	public static class ClusterAnalysisReducer extends Reducer {
		@Override
		public void reduce(String key, FileInputStream fis, Context context) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			float sum = 0;
			int counter = 0;
			String value = "";
			ArrayList<Integer> iterable =new ArrayList<Integer>(); ;
			try {
				while ((value = reader.readLine()) != null) {
					sum = sum + Float.parseFloat(value);
					counter++;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			float average = (float)(sum/counter);
			context.write(key, counter + " : " + String.valueOf(average));
		}
	}


	/**
	 * Method to instantiate the job and all the parameters to process the word count program.
	 **/
	
	public static void main(String args[]) throws IOException{
		
		/**
		 * If arguments length is greater than 2.
		 */
		if (args.length > 2) {
			String jarName="ClusterAnalysis.jar";
			String mapperClass="'" + ClusterAnalysisMapper.class.getName() + "'";
			String reducerClass="'" + ClusterAnalysisReducer.class.getName() + "'";
			String inputPath=args[0];
			String outputPath=args[1];
			String mode= args[2];
			
			Job job = new Job();
			job.setMapperClass(ClusterAnalysisMapper.class);
			job.setReducerClass(ClusterAnalysisReducer.class);
			job.setInputPath(args[0]);
			job.setOutputPath(args[1]);
			job.setJarName(jarName);
			
			
			System.out.println(jarName);
			System.out.println(mapperClass);
			System.out.println(reducerClass);
			System.out.println(inputPath);
			System.out.println(outputPath);
			System.out.println(mode);
			
			job.submit(jarName, mapperClass, reducerClass, inputPath, outputPath, mode);
		}
		//run the client directly if arguments contains only the master IP
		else if (args.length == 1) {
			new Client(args[0]).start();
		}
	}
}


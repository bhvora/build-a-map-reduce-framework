package jobstart;
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


public class WordCount {

	/**
	 * Word Count Mapper
	 */
	public static class WordCountMapper extends Mapper {

		int counter = 0;

		@Override
		public void map(Object key, Object value, Context context) {
			super.map(key, value, context);
			String data[]=((String)value).trim().split(" ");
			for(int i=0;i<data.length;i++){
				String filteredData=data[i].replaceAll("[^\\p{L}\\p{Nd}]+", " ");

				String array[]=filteredData.split(" ");
				for(int j=0;j<array.length;j++) {
				context.write(array[j].toLowerCase(), 1+"");
				}
			}
		}
	}

	/**
	 * Word Count Reducer
	 **/
	public static class WordCountReducer extends Reducer {

		@Override
		public void reduce(Object key, FileInputStream fis, Context context) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String read = "";
			int count = 0;
			try {
				while ((read = reader.readLine()) != null) {
					count++	;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			context.write(key, count + "");
		}
	}

	/**
	 * Method to instantiate the job and all the parameters to process the word count program.
	 **/
	
	public static void main(String args[]) throws IOException{
		
		/**
		 * If arguments length is greater than 2, run the Process Builder script.
		 */
		if (args.length > 2) {
			String jarName="WordCount.jar";
			String mapperClass="'" + WordCountMapper.class.getName() + "'";
			String reducerClass="'" + WordCountReducer.class.getName() + "'";
			String inputPath=args[0];
			String outputPath=args[1];
			String mode= args[2];
			
			Job job = new Job();
			job.setMapperClass(WordCountMapper.class);
			job.setReducerClass(WordCountReducer.class);
			job.setInputPath(args[0]);
			job.setOutputPath(args[1]);
			job.setJarName(jarName);
		
			
			job.submit(jarName, mapperClass, reducerClass, inputPath, outputPath, mode);
		}
		/**
		 * If areguments length is 1 then start the client directly in pseudo
		 */
		else if (args.length == 1) {
			new Client(args[0]).start();
		}
	}
}
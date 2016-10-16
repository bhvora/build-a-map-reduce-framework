import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import mapper.Mapper;
import reducer.Reducer;
import slave.Client;
import slave.Job;
import utils.Context;

public class WordMedian {

	public static class WordMedianMapper extends Mapper {

		int counter = 0;

		@Override
		public void map(Object key, Object value, Context context) {
			super.map(key, value, context);
			String data[]=((String)value).trim().split(" ");
			for(int i=0;i<data.length;i++){
				String line=data[i].replaceAll("[^\\p{L}\\p{Nd}]+", " ");

				String array[]=line.split(" ");
				for(int j=0;j<array.length;j++){
					context.write(array[j], "1");}
			}
		}
	}

	public static class WordMedianReducer extends Reducer {

		@Override
		public void reduce(String key, FileInputStream fis, Context context) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String read = "";
			ArrayList<Integer> iterable =new ArrayList<Integer>(); ;
			try {
				while ((read = reader.readLine()) != null) {
					iterable.add(Integer.parseInt(read));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String value = iterable.size() + "";
			context.write(key, value);
		}

	}

	/*private static double readAndFindMedian(String path, int medianIndex1,
			int medianIndex2) throws IOException {
		File file = new File(outputPath);
		BufferedReader br = null;
		try {
			int num = 0;
			for(File f : file.listFiles()){
				String line = "";
				long totalWords = 0;
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(line);

					// grab length
					String currLen = st.nextToken();

					// grab count
					String lengthFreq = st.nextToken();

					int prevNum = num;
					num += Integer.parseInt(lengthFreq);

					if (medianIndex2 >= prevNum && medianIndex1 <= num) {
						System.out.println("The median is: " + currLen);
						br.close();
						return Double.parseDouble(currLen);
					} else if (medianIndex2 >= prevNum && medianIndex1 < num) {
						String nextCurrLen = st.nextToken();
						double theMedian = (Integer.parseInt(currLen) + Integer
								.parseInt(nextCurrLen)) / 2.0;
						System.out.println("The median is: " + theMedian);
						br.close();
						return theMedian;
					}
				}
			} 
		}finally {
			br.close();
		}
		// error, no median found
		return -1;
	} */

	/**
	 * Method to instantiate the job and all the parameters to process the word count program.
	 **/

	public static void main(String args[]) throws IOException{

		/**
		 * If arguments length is greater than 2.
		 */
		if (args.length > 2) {
			String jarName="WordMedian.jar";
			String mapperClass="'" +  WordMedianMapper.class.getName() + "'";
			String reducerClass="'" + WordMedianReducer.class.getName() + "'";
			String inputPath=args[0];
			String outputPath=args[1];
			String mode= args[2];

			Job job = new Job();
			job.setMapperClass(WordMedianMapper.class);
			job.setReducerClass(WordMedianReducer.class);
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
			/*if(server.isJobDone){
			Thread.sleep(1000);
			File file = new File(outputPath);
			//System.out.println(file.getPath());
			long totalWords = 0;
			for(File f : file.listFiles()){
				//System.out.println("path to the file: "+f.getName());
				BufferedReader br = null;
				String line = "";
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					String[] data = line.split("\t");
					String value = data[1];
					totalWords += Long.parseLong(value);
				}   			 				
			}
			int medianIndex1 = (int) Math.ceil((totalWords / 2.0));
			int medianIndex2 = (int) Math.floor((totalWords / 2.0));
			double median = readAndFindMedian(outputpath, medianIndex1, medianIndex2);
			System.out.println("Main median : " + median);
			System.exit(0);
		}*/
		}
		//run the client directly if arguments contains only the master IP
		else if (args.length == 1) {
			new Client(args[0]).start();
		}
	}
}

package mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import utils.Constants;
import serverHandling.MapperDescription;
import slave.Client;
import split.Split;
import split.SplitData;
import utils.Context;

/**
 * Class to process the map tasks.
 *
 */
public class MapperReader implements Serializable {

	private static final long serialVersionUID = 1L;
	static RandomAccessFile randomFile;
	static public MapperDescription description;
	static Client client;
	static Context context;
	static Method method;
	static Object instance = null;
	static boolean isLocal = true;

	
	/**
	 * Method to set the mapper data using the mapper description and create a new
	 * instance of the mapper using JAVA reflection.
	 * @param des
	 * @param cli
	 */
	public static void setData(MapperDescription des, Client cli) {
		description = des;
		client = cli;
		context = new Context();
		context.object = Constants.MAPPER;
		context.client = client;
		try {
			Class mapClass=Class.forName(description.Mapperclass);
			method = mapClass.getDeclaredMethod("map", Object.class, Object.class, Context.class);
			method.setAccessible(true);
			//Java reflection
			instance = mapClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			}
		readSplitBySplit();
	}

	/**
	 * Method to copy input files from S3 bucket to EC2 instances' local file system.
	 * Also prints the output in console and error files respectively.
	 * @param fileName
	 */
	public static void copyFile(String fileName){
		try{
			new ProcessBuilder("mkdir", Constants.INPUT_FOLDER).start();
			
			fileName=fileName.replace(":", ":/");
			ProcessBuilder builder=new ProcessBuilder("aws", "s3", "cp" ,fileName, Constants.INPUT_FOLDER + "/");
			 File outputFile = new File("console.txt");
			 File outputFile2 = new File("error.txt");
			 
			 builder.redirectOutput(outputFile);
			 builder.redirectError(outputFile2);
			 
			 Process process=builder.start();
			 int errCode = process.waitFor();
			 System.out.println("Error: "+(errCode==0?"No":"Yes"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to read each split one by one and send the line to reducer as soon
	 * as the mapper reads it and also write the mapper data all at once from slave to master.
	 */
	private static void readSplitBySplit() {
		Split split = description.split;
		ArrayList<SplitData> list = split.splitData;
		
		for (int i = 0; i < list.size(); i++) {
			SplitData data = list.get(i);
			if (!isLocal) 
				copyFile(data.toRead.getPath());
			//System.out.println(data.toRead);
		}
		
		File files[]=(new File(Constants.INPUT_FOLDER)).listFiles();
		
		for(int i=0;i<files.length;i++) {
			SplitData data = list.get(i);
			long start = checkPointer(files[i], data.startPointer);
			if (data.isGZ) {
				readGz(files[i]);
			}
			else {
				read(files[i], start, data.endPointer);
			}
			client.writeToServer(Constants.REDUCER_INCOM);
			context.writeDataInBulk();
			//files[i].delete();
		}
		client.writeToServer(Constants.MAPPER_DONE);
	}

	

	/**
	 * Method to manage the random access file pointer to avoid any
	 * incomplete reading of a line.
	 * @param fileName
	 * @param fileStart
	 * @return pointer
	 */
	private static long checkPointer(File fileName, long fileStart) {
		long pointer = 0;
		try {
			randomFile = new RandomAccessFile(fileName, "r");
			String nextLine = randomFile.readLine();
			boolean check = false;
			if (fileStart != 0) {
				randomFile.seek(fileStart - 1);
				char character = randomFile.readChar();
				if (character == '\n') {
					randomFile.seek(fileStart);
					pointer = fileStart;
					check = true;
				} else {
					randomFile.seek(fileStart + nextLine.length());
					pointer = fileStart + nextLine.length();
				}
			}
		} catch (Exception e) {
		}
		return pointer;
	}

	/**
	 * Method to read the text file based on file start and end pointer passed
	 * as arguments and invoke the map method of Mapper for each line read.
	 * @param fileName
	 * @param fileStart
	 * @param end
	 */
	private static void read(File fileName, long fileStart, long end) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			reader.skip(fileStart);
			String read = "";
			while (fileStart < end &&(read = reader.readLine()) != null) {
				// invoke map method of the mapper.
				method.invoke(instance, new Object[] { null, read, context });
				fileStart += read.length();
			}
			reader.close();

		} catch (Exception e) {
		}
	}
	

	/**
	 * Method to read the zip file based on file start and end pointer passed
	 * as arguments and invoke the map method of Mapper for each line read.
	 * @param fileName
	 * @param fileStart
	 * @param end
	 */
	private static void readGz(File file) {
		try {
			GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
			String read = "";
			while ((read = reader.readLine()) != null) {
				// invoke map method of the mapper.
				method.invoke(instance, new Object[] { null, read, context });
			}
			reader.close();
		} catch (Exception e) {
		}
	}
}

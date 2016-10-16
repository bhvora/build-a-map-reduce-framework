package master;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import serverHandling.Server;
import parameters.Constants;
import split.Split;
import split.SplitData;


public class Job {

	static long totalSize = 0;
	static boolean isLocal = true;
	static int slaves = 1;
	static int fileRead = 0;
	public static ArrayList<Split> slaveSplits = new ArrayList<Split>();
	Server server=null;
	static String inputPath="";

	 /**
	  * Method to start the server.
	  */
	public void submit() {
		server=new Server();
	}
	
	/**
	 * Method to start reading the input file 
	 */
	public void start() {
		startReadingFile();
		
	}
	
	public void setMapperClass(String Mapper){
		server.MapperClass=Mapper;
	}
	
	public void setReducer(String Reducer){
		server.ReducerClass=Reducer;
	}

	
	public void setInputPath(String path) throws IOException {
		inputPath=path;
	}

	public void setOutputPath(String path){
		server.output=path;
	}
	
	/**
	 * Method to start reading the file from the input folder
	 */
	public void startReadingFile(){
		String extension = FilenameUtils.getExtension(inputPath);
		if (extension.equalsIgnoreCase("")) {
			readCompleteFolder(inputPath);
		} 
	}
	
	/**
	 * Method to throw the error for not setting the mapper and reducer class.
	 */
	public void checkError(){
		if(server==null){
			try {
				throw new Exception("Please set mapper and reducer class");
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Method to get all the files from input folder of S3 bucket using AWS CLI
	 * @param s3Path
	 * @return
	 */
	public static File[] getFiles(String s3Path) {
		try{
			//new ProcessBuilder("mkdir",Constants.TEMP_DATA).start().waitFor();
			new File(Constants.TEMP_DATA).mkdir();
			new ProcessBuilder("aws", "s3" , "sync",s3Path,Constants.TEMP_DATA).start().waitFor();
		}catch(Exception e){
		}
		return new File(Constants.TEMP_DATA).listFiles();
	}
	
	/**
	 * Method to read the files from input folder based on appropriate file extensions.
	 * If the file is a zip file then it unzips and then reads that file.
	 * It checks if the running mode is local or ec2 based on which it calls the method
	 * to read input files.
	 * @param folder
	 */
	private static void readCompleteFolder(String folder) {
		File inputFolder = new File (folder);
		File[] files = null;
		
		if (isLocal) {
			files = inputFolder.listFiles();
		}
		else {
			files=getFiles(folder);
		}
		getTotalSize(files);
		Split split = new Split();
		int i = 0;
		int sectionSize = (int) (totalSize / slaves);
		fileRead = -1;
		while (i < files.length) {
			File file = files[i];
			if (file.getName().endsWith(Constants.DS_STORE)) {
				i++;
				continue;
			}
			split = getNewSplit(split, sectionSize);
			if (FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase(Constants.GZ)) {
				addFileIfEndsWithGZ(split, file);
				
				i++;
			} else {
				manageSplits(split, file, sectionSize);
				if (fileRead == -1) {
					i++;
				}
			}
		}
		slaveSplits.add(split);
		printSplits();
	}

	/**
	 * Method to print the splits of the input file(s).
	 */
	private static void printSplits() {
		for (int i = 0; i < slaveSplits.size(); i++) {
			Split splits = slaveSplits.get(i);
			for (int j = 0; j < splits.splitData.size(); j++) {
				SplitData data = splits.splitData.get(j);
			}
			System.out.println();
		}
	}

	/**
	 * Method to manage the splits of a single file.
	 * @param split
	 * @param file
	 * @param sectionSize
	 */
	private static void manageSplits(Split split, File file, int sectionSize) {
		
		SplitData data = new SplitData();
		data.toRead = new File(inputPath + Constants.SEPARATOR + file.getName());
		data.startPointer = fileRead + 1;
		data.endPointer = getDataEndPointer(split.totalSizetaken, file.length(), sectionSize);
		split.totalSizetaken += (data.endPointer - data.startPointer);
		split.splitData.add(data);
	}

	/**
	 * Method to 
	 * @param split
	 * @param file
	 */
	private static void addFileIfEndsWithGZ(Split split, File file) {
		SplitData data = new SplitData();
		data.toRead = file;
		data.isGZ = true;
		split.splitData.add(data);
		fileRead = -1;
	}

	/**
	 * Method to get the end point of the data to be read.
	 * @param totalSizetaken
	 * @param fileRead
	 * @param filelength
	 * @param sectionSize
	 * @return
	 */
	private static long getDataEndPointer(long totalSizetaken, long filelength, int sectionSize) {
		long spaceLeft = sectionSize - totalSizetaken;
		if (spaceLeft >= (filelength - fileRead)) {
			fileRead = -1;
			return (filelength - fileRead);
		} else {
			fileRead = (int) spaceLeft;
			return spaceLeft;
		}
	}

	/**
	 * Method to return the new split generated based on section size.
	 * @param split
	 * @param sectionSize
	 * @return
	 */
	private static Split getNewSplit(Split split, int sectionSize) {

		if (split.totalSizetaken >= sectionSize) {
			slaveSplits.add(split);
			split = new Split();
		}
		return split;
	}

	/**
	 * Method to get the total size of the files.
	 * @param files
	 */
	private static void getTotalSize(File[] files) {
		for (File file : files) {
			totalSize += file.length();
		}
	}
	

	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void setInputPath(String[] path) throws IOException {
		for (int i = 0; i < path.length; i++) {
			setInputPath(path[i]);
		}
	}
	
	/**
	 * Main program to run the server.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception{
		Job job=new Job();
		job.submit();
		job.setMapperClass(args[0]);
		job.setReducer(args[1]);
		job.setInputPath(args[2]);
		job.setOutputPath(args[3]);
		if (!args[4].equalsIgnoreCase("local")) {
			isLocal = false;
		}
		
		job.start();
	}
}

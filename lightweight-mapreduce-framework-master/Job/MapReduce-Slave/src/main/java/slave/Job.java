package slave;

import java.io.File;
import java.io.IOException;

public class Job {
	
	 String jarName;
	 String mapper;
	 String reducer;
	 String input;
	 String output;
	 String mode;
	 Client client;
	 
	 /**
	  * Method that submits the job of the client by passing the below arguments. Also calls a script - script.sh which
	  * runs system in local or ec2 based on the passed mode argument.
	  * @param jar
	  * @param mapperClass
	  * @param reducerClass
	  * @param input
	  * @param output
	  * @param mode
	  */
	public void submit(String jar, String mapperClass, String reducerClass, String input, String output, String mode) {
		try{
			new ProcessBuilder("chmod", "777","script.sh").start();
			ProcessBuilder builder =new ProcessBuilder("/bin/bash","script.sh" ,jar,mapperClass,reducerClass,input,output,mode);
			 File outputFile = new File("console.txt");
			 File outputFile2 = new File("error.txt");
			 
			 builder.redirectOutput(outputFile);
			 builder.redirectError(outputFile2);
			Process process=builder.start();
			int errCode = process.waitFor();
		}catch(Exception e){
		}
	} 
	
	public void setInputPath(String path) throws IOException {
		input=path;
	}
	public void setMapperClass(Class Mapper){
		mapper=Mapper.getName();
	}
	
	public void setOutputPath(String op){
		output=op;
	}
	
	public void setJarName(String jar){
		jarName=jar;
	}
	public void setReducerClass(Class Reducer){
		reducer=Reducer.getName();
	}
}

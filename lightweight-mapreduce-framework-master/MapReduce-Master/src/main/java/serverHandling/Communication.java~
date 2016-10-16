package serverHandling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import master.Job;
import parameters.Constants;

public class Communication extends Thread {

	Socket socket;
	long startTime = 0;
	Server server = null;
	int myIndex=-1;
	BufferedWriter writer=null;

	public Communication(Socket socket, Server ser,int index) {
		this.socket = socket;
		server = ser;
		myIndex=index;
		try {
			writer=new BufferedWriter(new FileWriter(new File(server.output + Constants.SEPARATOR + Constants.PART_FILE + myIndex),true));
			System.out.println("Output flushed");
			
		} catch (IOException e) {
		}
	}

	/**
	 * Method to create an input stream for the listener socket (server) and exchange status messages between Clients
	 * and server based on Object type of the reader (inputstream).
	 */
	public void run() {
		try {
			ObjectInputStream reader=new ObjectInputStream(socket.getInputStream());

			while (true) {
				Object object=reader.readObject();

				/**
				 * If status message is CONNECTED, inform slave by building its mapper and reducer.
				 */
				if((object instanceof String) && ((String)object).equals(Constants.CONNECTED)){
					server.writeToSlave(socket,buildMapperDescription());
					server.writeToSlave(socket,buildReducerDescription());
				}

				/**
				 * If the reducer is incoming(ready) inform slave by writing/sending the object on it.
				 */
				if((object instanceof String) && ((String)object).equalsIgnoreCase(Constants.REDUCER_INCOM)){
					object=reader.readObject();
					server.writeToSlave(socket, Constants.REDUCER_INCOM);
					server.writeToSlave(socket, object);
				}

				/**
				 * Inform slave if MAPPER has completed its map tasks.
				 */
				if((object instanceof String) && ((String)object).equalsIgnoreCase(Constants.MAPPER_DONE)){
					server.writeToSlave(socket, object);
				}

				/**
				 * Publish through writer if reducer has completed its reduce tasks.
				 */
				if((object instanceof String) && ((String)object).equalsIgnoreCase(Constants.REDUCER_DONE)){
					writer.write((String)reader.readObject()+ Constants.NEW_LINE);
				}

				//Close the writer and socket.
				if((object instanceof String) && ((String)object).equalsIgnoreCase(Constants.CLOSE)){
					// server.isJobDone = true;
					writer.flush();
					writer.close();
					server.writeToSlave(socket, Constants.CLOSE);
					//system.exit(0);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Method to build the mapper and set its properties in Mapper Description.
	 * @return
	 */
	public MapperDescription buildMapperDescription() {

		MapperDescription description=new MapperDescription();
		try {
			description.Mapperclass=server.MapperClass;
			description.split=Job.slaveSplits.get(myIndex);
			description.key=myIndex;
		}catch (Exception ex) {

		}
		return description;
	}

	/**
	 * Method to build the reducer and set its properties in Reducer Description.
	 * @return
	 */
	public ReducerDescription buildReducerDescription() {

		ReducerDescription description=new ReducerDescription();
		try {
			description.ReducerClass=server.ReducerClass;
		}catch (Exception ex) {
		}
		return description;
	}
}

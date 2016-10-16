package slave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import mapper.MapperReader;

import utils.Constants;

import reducer.ReducerReader;
import serverHandling.DataTransfer;
import serverHandling.MapperDescription;
import serverHandling.ReducerDescription;

/**
 * Client class
 *
 */
public class Client extends Thread {
	public static ArrayList<DataTransfer> mapperData;
	long startTime = 0;
	
	String MASTER_IP_ADDRESS="";
	Socket socket = null;
	ObjectOutputStream stream = null;
	String message;

	// Constructor to initialize the client's properties. 
	public Client(String ip) {
		try {
			MASTER_IP_ADDRESS=ip;
			mapperData = new ArrayList<DataTransfer>();
			
			socket = new Socket();
			socket.connect(new InetSocketAddress(MASTER_IP_ADDRESS,Constants.SERVER_PORT), 10000);
			stream = new ObjectOutputStream(socket.getOutputStream());
			stream.flush();
			writeToServer(Constants.CONNECTED);
		} catch (Exception e) {
		}
	}

	/**
	 * Method to send message to the server(master).
	 * @param message
	 */
	public void writeToServer(Object message) {
		try {
			stream.writeObject(message);
			stream.flush();
		} catch (Exception e) {
		}
	}

	
	/**
	 * Method to start the client thread.
	 */
	public void run() {
		try {
			
			ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
			while (true) {
				
				Object object = stream.readObject();
			
				if (object instanceof MapperDescription) {
					MapperReader.setData(((MapperDescription) object), this);
				}

				if ((object instanceof String) && ((String) object).equalsIgnoreCase(Constants.HEARTBEAT)) {
					System.out.println(Constants.HEARTBEAT);
				}
				
				if (object instanceof ReducerDescription) {
					ReducerReader.setData(((ReducerDescription) object), this);
				}
				
				if ((object instanceof String) && ((String) object).equalsIgnoreCase(Constants.REDUCER_INCOM)) {
					final ArrayList<DataTransfer> transfer = (ArrayList<DataTransfer>) stream.readObject();
					writeTempKeyFile(transfer);
				}
				
				if ((object instanceof String) && ((String) object).equalsIgnoreCase(Constants.MAPPER_DONE)) {
					ReducerReader.executeReducer();
				}
				if((object instanceof String) && ((String)object).equalsIgnoreCase(Constants.CLOSE)){
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to write the data (key value pair) to temp folder where a file is created
	 * for each key containing the values corresponding to that key.
	 * @param data
	 */
	public void writeTempKeyFile(ArrayList<DataTransfer> data) {
		DataTransfer transfer=null;;
		try {
			for (int i = 0; i < data.size(); i++) {
				 transfer= data.get(i);
				if(((String)transfer.key).length()<1){
					continue;
				}
				File dir=new File(Constants.TEMP_FOLDER);
				
				if(!dir.exists()){
					dir.mkdir();
				}
				File file = new File(dir.getPath()+"/" + ((String)transfer.key));
				
				if(!file.exists()){
					file.createNewFile();
				}
				
				FileOutputStream fos = new FileOutputStream(file, true);
				ObjectOutputStream stream = new ObjectOutputStream(fos);
				stream.writeObject(transfer.value + Constants.NEW_LINE);
				stream.flush();
				stream.close();
			}
		} catch (Exception e) {
		}

	}

	/**
	 * Main method to start the client with the server's IP
	 * passed as an argument.
	 * @param arg
	 */
	public static void main(String[] args) {
	}
}

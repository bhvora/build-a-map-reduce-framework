package serverHandling;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import parameters.Constants;

public class Server {

	HashMap<Socket, ObjectOutputStream> slaves;
	HashMap<InetAddress, Communication> slaveThreads;
	
	ServerSocket server;
	boolean serverClose = false;
	static Socket slaveDown = null;
	boolean noHeartBeat = false;
	ObjectOutputStream stream=null;
	public String MapperClass;
	public String ReducerClass;
	public static String output="";
	
	//Constructor to initialize slaves and slave threads.
	public Server() {
		slaves = new HashMap<Socket, ObjectOutputStream>();
		slaveThreads = new HashMap<InetAddress, Communication>();
		initiateServer();
	}

	/**
	 * Method to initialize the server on specified port and call methods
	 * to start accepting connections from clients.
	 */
	public void initiateServer() {
		try {
			server = new ServerSocket(Constants.SERVER_PORT);
			new Thread() {
				public void run() {
					try {
						acceptNewConns();
					} catch (Exception e) {
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to start accepting new connections from several Clients
	 * and store the information of slaves in the Hash map. Also it opens and starts communicating
	 * with the slave.
	 * @throws Exception
	 */
	private void acceptNewConns() throws Exception {
		int i=0;
		while (!serverClose) {
			Socket slave = server.accept();
			
			InetAddress address=slave.getInetAddress();
			Communication communication;
			if((communication=slaveThreads.get(address))!=null) {
				communication=null;
			}
			
			stream=new ObjectOutputStream(slave.getOutputStream());
			
			communication = new Communication(slave,Server.this,i);
			slaveThreads.put(address, communication);
			slaves.put(slave,stream);
			communication.start();
			i++;
		}
	}


	/*

	public static boolean jobCompletion(){

	while(true){
		if(isJobDone){
		return true;
		}
	   
	}
	*/

	/**
	 * Method to write/send the message/stream to slaves.
	 * @param socket
	 * @param message
	 */
	public void writeToSlave(Socket socket, Object message) {
		try {
			ObjectOutputStream streams=slaves.get(socket);
			streams.writeObject(message);
			streams.flush();
		} catch (Exception e) {
		}
	}
}

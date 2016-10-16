package utils;

import utils.Constants;
import serverHandling.DataTransfer;
import slave.Client;

public class Context {

	 public static String object;
	 public Client client;
	 public boolean shallWrite=false;
	
	 /**
	  * Method to write/emit the key value pair
	  * from mapper and reducer.
	  * @param key
	  * @param value
	  */
	 public void write(Object key, Object value) {
		try{
			if (object.equalsIgnoreCase(Constants.MAPPER)) {
				writeMapper(key,value);
			} else if (object.equalsIgnoreCase(Constants.REDUCER)) {
				writeReducer(key,value);
			} else {
				throw new Exception("Irrelevant type of writer");
			}
			}catch(Exception e){
		}
	}

	/**
	 * Method to write/emit the key value of pair from Mapper
	 * @param key
	 * @param value
	 */
	private void writeMapper(Object key, Object value) {
		DataTransfer dt=new DataTransfer(key, value);
		client.mapperData.add(dt);
	}
	
	/**
	 * Method to write the mapper data at once from slave to master.
	 */
	public void writeDataInBulk(){
		client.writeToServer(client.mapperData);
	}
	
	/**
	 * Method to write/emit the key value of pair from Reducer
	 * @param key
	 * @param value
	 */
	private void writeReducer(Object key, Object value) {
		client.writeToServer(Constants.REDUCER_DONE);
		client.writeToServer(key + Constants.TAB + value);
	}
}

package serverHandling;

import java.io.Serializable;

/**
 * Class to generate a data transfer object of Key value pair.
 */
public class DataTransfer implements Serializable {

	private static final long serialVersionUID = 1L;
	public Object key;
	public Object value;
	
	public DataTransfer(Object key, Object value) {
		this.key=key;
		this.value=value;
	}
}

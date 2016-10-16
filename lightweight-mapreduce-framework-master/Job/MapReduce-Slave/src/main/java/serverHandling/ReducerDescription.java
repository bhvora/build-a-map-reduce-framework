package serverHandling;

import java.io.Serializable;

/**
 * Method to set the properties of Reducer.
 *
 */
public class ReducerDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	public String ReducerClass;
	public char delimiter = '\t';
}

package mapper;

import utils.Context;

/**
 * Maps input key/value pairs to a set of intermediate key/value pairs.
 *
 */
public class Mapper {
	
	public Mapper() {
	}
	
	/**
	 * Map method which is called for each key/value pair in the input split.
	 * @param key
	 * @param value
	 * @param context
	 */
	public void map(Object key,Object value,Context context){}
}

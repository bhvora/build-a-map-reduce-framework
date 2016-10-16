package reducer;

import java.io.FileInputStream;

import utils.Context;

/**
 * Class that reduces a set of intermediate values which share a key to a smaller
 * set of values.
 *
 */
public class Reducer {
	
	/**
	 * This method is called once for each key and reduces values for that given key.
	 * @param key
	 * @param value
	 * @param context
	 */
	public void reduce (Object key,FileInputStream values,Context context){}
}

package serverHandling;
import java.io.Serializable;

import split.Split;

/**
 * Class to set the properties of the Mapper.
 */
public class MapperDescription implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String Mapperclass;
	public Split split;
	public char delimiter = '\t';
	public Object key;
}

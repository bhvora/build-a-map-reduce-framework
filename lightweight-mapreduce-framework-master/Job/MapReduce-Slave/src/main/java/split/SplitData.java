package split;

import java.io.File;
import java.io.Serializable;

/**
 * Class SplitData to set the properties of the splits.
 *
 */
public class SplitData implements Serializable {

	private static final long serialVersionUID = 1L;
	public File toRead;
	public long startPointer;
	public long endPointer;
	public boolean isGZ=false;
}

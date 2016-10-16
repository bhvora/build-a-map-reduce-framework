package split;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Split Class
 *
 */
public class Split implements Serializable {

	private static final long serialVersionUID = 1L;
	public ArrayList<SplitData> splitData=new ArrayList<SplitData>();
	public long totalSizetaken=0;
}

package index;

import general.IndexKey;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 2. Grouping the docId into one group 
 *
 */
public class IndexKeyGroupingComparator extends WritableComparator{

	protected IndexKeyGroupingComparator() {
		super(IndexKey.class, true);
	}
	
	@Override
	/**
	 * After mapping, group the docId, send the same docId to same reducer
	 */
	public int compare(@SuppressWarnings("rawtypes") WritableComparable w1, @SuppressWarnings("rawtypes") WritableComparable w2) {
		IndexKey ik1 = (IndexKey)w1;
		IndexKey ik2 = (IndexKey)w2;
		
		int result = ik1.getDocId().compareTo(ik2.getDocId());
		
		return result;
	}

}

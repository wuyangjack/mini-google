package index;

import general.IndexKey;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 1. Secondary Sort (sort first based on docId, then based on wordId)
 *
 */
public class IndexKeyComparator extends WritableComparator{

	protected IndexKeyComparator() {
		super(IndexKey.class, true);
	}
	
	@Override
	/**
	 * Compare two IndexKey, first based on docId (if same based on wordId, then based on position)
	 */
	public int compare(@SuppressWarnings("rawtypes") WritableComparable w1, @SuppressWarnings("rawtypes") WritableComparable w2) {
		IndexKey ik1 = (IndexKey)w1;
		IndexKey ik2 = (IndexKey)w2;
		
		int result = ik1.getDocId().compareTo(ik2.getDocId());
		if(result == 0) {
			result = ik1.getWordId().compareTo(ik2.getWordId());
			if(result == 0)
				result = ik1.getPosition() - ik2.getPosition();
		}
		return result;
	}

}

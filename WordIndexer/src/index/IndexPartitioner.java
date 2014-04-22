package index;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import general.IndexKey;
import general.IndexValue;
import hash.SHA1Partition;

import org.apache.hadoop.mapreduce.Partitioner;

/**
 * 3. Index Partitioner
 *
 */
public class IndexPartitioner extends Partitioner<IndexKey, IndexValue>{
	
	@Override
	/**
	 * Based on index_key docId, we partition same docId to the same worker
	 */
	public int getPartition(IndexKey index_key, IndexValue index_value, int numPartitions) {
//		try {
//			PrintWriter pw = new PrintWriter(new FileWriter("testttttt", true));
//			pw.println(numPartitions);
//			pw.flush();
//			pw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		int hash = index_key.getDocId().hashCode();
//		int partition = hash % numPartitions;
		int partition = SHA1Partition.getWorkerIndex(index_key.getDocId());
		return partition;
	}
}

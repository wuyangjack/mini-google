package hadoopcrawler;

import hash.SHA1Partition;

import java.math.BigInteger;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.io.Text;

/**
 * 3. Index Partitioner
 * @author Chenyang Yu
 *
 */
public class CrawlerPartitioner extends Partitioner<Text, Text>{

	BigInteger MAX_VALUE = new BigInteger("ffffffffffffffffffffffffffffffffffffffff", 16);
	@Override
	/**
	 * Based on index_key docId, we partition same docId to the same worker
	 */
	public int getPartition(Text key, Text value, int numPartitions) {
		System.out.println("WorkerNum: " + numPartitions);
		SHA1Partition.setRange(numPartitions);
		int partition = SHA1Partition.getWorkerIndex(key.toString());
		return partition;
	}
}

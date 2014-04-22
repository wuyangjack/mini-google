package hadoop;

import general.LogClass;
import hash.SHA1Partition;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

public class CrawlerPartitioner1 implements Partitioner<Text, Text>{
	
	@Override
	public void configure(JobConf arg0) {
		System.out.println("Configure");
		
	}

	@Override
	public int getPartition(Text key, Text value, int numPartitions) {
		LogClass.info("Partitioner Class Called");
		System.out.println("WorkerNum: " + numPartitions);
		SHA1Partition.setRange(numPartitions);
		int partition = SHA1Partition.getWorkerIndex(key.toString());
		return partition;
	}

}

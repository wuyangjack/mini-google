package hadoop;

import general.LogClass;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

public class CrawlerDriver1 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		JobConf conf = new JobConf(CrawlerDriver1.class);
		LogClass.init();
		conf.setJobName("crawlerExample");
 
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		
		// 2.2 set partitioner
        conf.setPartitionerClass(CrawlerPartitioner1.class);
        // 2.3 set job mapper
        conf.setMapperClass(CrawlerMapper1.class);
		conf.setReducerClass(CrawlerReducer1.class);
		// 2.4 set the map output and reduce output key and value class
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(Text.class);
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		conf.setNumReduceTasks(2);
 
		JobClient.runJob(conf);

	}

}

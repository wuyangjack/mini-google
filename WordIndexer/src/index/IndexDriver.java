package index;

import java.util.ArrayList;

import general.IndexKey;
import general.IndexOutput;
import general.IndexValue;
import general.StopWords;
import general.WordPreprocessor;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import storage.BerkeleyDB;
import storage.WordDocTFEntry;

/**
 * The job driver to run a map reduce job
 *
 */
public class IndexDriver extends Configured implements Tool {
	
	//for later tuning use
	public static final double tf_factor = 0.5;
	//directly get from the crawler later
	public static final long total_doc_num = 10000;
//	public static final ArrayList<String> workers = new ArrayList<String>();
	public static final String[] workers = {""};
//	public static StopWords stopWords = new StopWords();
	public static BerkeleyDB db = null;
	
	@Override
	public int run(String[] args) throws Exception {
		// 1. get the input file and output folder
		String input, output;
		if (args.length == 2) {
			input = args[0];
			output = args[1];
			
		} else {
			System.err.println("Incorrect number of arguments.  Expected: input output");
			return -1;
		}

		// 2. Initiate the job
		@SuppressWarnings("deprecation")
		Job job = new Job();
		
		// 2.1 set job runner
		job.setJarByClass(IndexDriver.class);
		job.setJobName(this.getClass().getName());
		// 2.2 set input output format
		FileInputFormat.setInputPaths(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		// 2.3 set secondary comparator, grouping comparator, partitioner
		job.setSortComparatorClass(IndexKeyComparator.class);
        job.setGroupingComparatorClass(IndexKeyGroupingComparator.class);
        job.setPartitionerClass(IndexPartitioner.class);
        // 2.4 set job mapper
//		job.setMapperClass(IndexMapper.class);
		job.setMapperClass(ReverseIndexMapper.class);
        //job.setMapperClass(ReverseIndexMapper.class);
		job.setReducerClass(IndexReducer.class);
		// 2.5 set the map output and reduce output key and value class
		job.setMapOutputKeyClass(IndexKey.class);
		job.setMapOutputValueClass(IndexValue.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IndexOutput.class);		
		
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
		
		

			
	}

	public static void main(String[] args) throws Exception {
		BerkeleyDB.setEnvPath("Database");
		db = BerkeleyDB.getInstance();
		db.openDB();
		
		IndexDriver driver = new IndexDriver();
		int exitCode = ToolRunner.run(driver, args);
		
		
		
		for(WordDocTFEntry entry: db.getTFIDF(WordPreprocessor.preprocess("History"))){
			System.out.println("XXXXXXXXXXXXXXXxxxxx: " + entry);
		}
		
		//close Databse
		db.close();
		System.exit(exitCode);
	}
}

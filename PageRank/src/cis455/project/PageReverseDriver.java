package cis455.project;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * PageDangling
 * @author Yang Wu
 *
 */
public class PageReverseDriver extends Configured implements Tool {
		private Job job = null;
		
		@SuppressWarnings("deprecation")
		@Override
		public int run(String[] args) throws Exception {
			String input, output;
			if (args.length == 2) {
				input = args[0];
				output = args[1];
				System.out.println("Reverse instance input | output: " + input + " | " + output);
			} else {
				System.err.println("Incorrect number of arguments.  Expected: input output");
				return -1;
			}
			
			// Job
			Configuration conf = new Configuration();
			job = new Job(conf);
			//job = new Job();
			job.setJarByClass(PageReverseDriver.class);
			job.setJobName(this.getClass().getName());
			
			// Paths
			FileInputFormat.setInputPaths(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			
			// Workers
			job.setMapperClass(PageReverseMapper.class);
			job.setReducerClass(PageReverseReducer.class);
			
			// Formats
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
			boolean success = job.waitForCompletion(true);			
			return success ? 0 : 1;
		}
		
		public long counterReduceOutput() throws IOException {
			Counters counters = job.getCounters();
			Counter counter = counters.findCounter(PageGlobal.Counters.PAGECOUNT);
			return counter.getValue();
		}
		
		public static void main(String[] args) throws Exception {
			PageReverseDriver driver = new PageReverseDriver();
			int exitCode = ToolRunner.run(driver, args);
			System.exit(exitCode);
		}
		
	}
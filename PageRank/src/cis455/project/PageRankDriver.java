package cis455.project;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * PageRank
 * @author Yang Wu
 *
 */
public class PageRankDriver extends Configured implements Tool {
		
		private Job job = null;
	
		@SuppressWarnings("deprecation")
		@Override
		public int run(String[] args) throws Exception {
			final String input, output;
			final double lossCompensate;
			if (args.length == 3) {
				input = args[0];
				output = args[1];
				lossCompensate = Double.parseDouble(args[2]);
				System.out.println("Rank instance input | output | lossCompensate: " + input + " | " + output + " | " + String.valueOf(lossCompensate));
			} else {
				System.err.println("Incorrect number of arguments.  Expected: input output lossCompensate");
				return -1;
			}
			
			// Job
			job = new Job();
			job.setJarByClass(PageRankDriver.class);
			job.setJobName(this.getClass().getName());
			job.getConfiguration().setDouble(PageGlobal.propertyLossCompensate, lossCompensate);

			// Paths
			FileInputFormat.setInputPaths(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			
			// Workers
			job.setMapperClass(PageRankMapper.class);
			job.setReducerClass(PageRankReducer.class);
			
			// Formats
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
			boolean success = job.waitForCompletion(true);
			return success ? 0 : 1;
		}
		
		public static void main(String[] args) throws Exception {
			// Check arguments
			int ret = 1;
			if (args.length != 5) {
				System.err.println("Incorrect number of arguments.  Expected: mode input middle output iterations");
				System.exit(1);
			}
			final String mode = args[0];
			final String input = args[1];
			final String middle = args[2];
			final String output = args[3];
			final int iterations = Integer.parseInt(args[4]);
			System.out.println("Mode: " + mode);
			
			/* Raw crawler output */
			
			// Parse
			final String prepared = middle + "/0_parsed";
			//final String prepared = output;
			PageParseDriver driverParse = new PageParseDriver();
			ret = ToolRunner.run(driverParse, new String[]{input, prepared});
			if (1 == ret) {
				System.err.println("Job terminated at parse phase");
				System.exit(1);
			}
			
			/*
			// Read loss
			try{
                Path pt = new Path(output + "/part-r-00000");
                FileSystem fs = pt.getFileSystem(new Configuration());
                //FileSystem fs = FileSystem.get(new Configuration());
                if (fs.exists(pt) && fs.isFile(pt)) {
                	// Read total loss from the first line
                	BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
	                String line = br.readLine();
	                if (line != null){
	                	System.out.println("###: " + line);
	                }
                } else {
                	System.err.println("not a file!");
                }
			}	catch(Exception e){
				System.err.println("Read test failure." );
        		e.printStackTrace();
				System.exit(1);
        	}
			
			if (mode.equals(PageGlobal.modeEMR)) {
				System.exit(0);
			}
			*/
			
			// Reverse
			final String reversed = middle + "/1_reversed";
			PageReverseDriver driverReverse = new PageReverseDriver();
			ret = ToolRunner.run(driverReverse, new String[]{prepared, reversed});
			if (1 == ret) {
				System.err.println("Job terminated at reverse phase");
				System.exit(1);
			}
			
			// Count
			long pageCount = driverReverse.counterReduceOutput();
			System.out.println("Webpage count: " + String.valueOf(pageCount));
			
			/* Dangling links removed & Initial page rank assigned */
			
			// Iterate
			final String iterated = middle + "/2_iterated";
			String _input = null;
			String _output = null;
			String _sum = null;
			//String _loss = null;
			double rankLossSingle = 0;
			for (int i = 0; i < iterations; i ++) {
				System.out.println("Rank interation: " + String.valueOf(i + 1) + " / " + String.valueOf(iterations));
				
				// Chaining
				if (i == 0) _input = reversed;
				else _input = _output;
				if (i == iterations - 1) _output = iterated;
				else _output = middle + "/2_iterate" + String.valueOf(i);
				//_loss = _output + "_loss";
				_sum = _output + "_sum";
				
				// Calculate loss
				PageSumDriver driverSum = new PageSumDriver();
				ret = ToolRunner.run(driverSum, new String[]{_input, _sum});
				if (1 == ret) {
					System.err.println("Job terminated at sum phase");
					System.exit(1);
				}
								
				// Read loss
				try{
					Path pt = new Path(_sum + "/part-r-00000");
	                FileSystem fs = pt.getFileSystem(new Configuration());
	                if (fs.exists(pt) && fs.isFile(pt)) {
	                	// Read total loss from the first line
	                	double rankLossTotal = 0;
	                	BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
		                String line = br.readLine();
		                if (line != null){
		            		String[] pair = StringDelimeter.split(line, "\t", 2);
		            		rankLossTotal = (double)pageCount - Double.parseDouble(pair[1]);
		                }
		                br.close();
		                rankLossSingle = rankLossTotal / pageCount;
						System.out.println("Loss compensation: " + String.valueOf(rankLossSingle) + " * " + String.valueOf(pageCount));
	                } else {
	                	rankLossSingle = 0;
	                }
	                fs.close();
				}	catch(Exception e){
					System.err.println("Job terminated at reading loss result: " + i);
	        		e.printStackTrace();
					System.exit(1);
	        	}
				
				// Rank single run
				PageRankDriver driverRank = new PageRankDriver();
				ret = ToolRunner.run(driverRank, new String[]{_input, _output, String.valueOf(rankLossSingle)});
				if (1 == ret) {
					System.err.println("Job terminated at rank iteration: " + i);
					System.exit(1);
				}
			}
			
			/* Page rank converged */
			
			// Postprocess
			final String postprocessed = output;
			PagePostprocessDriver driverPostprocess = new PagePostprocessDriver();
			ret = ToolRunner.run(driverPostprocess, new String[]{iterated, postprocessed});
			if (1 == ret) {
				System.err.println("Job terminated at postprocess phase");
				System.exit(1);
			}
			
			/* Index from url to page rank */
			
			// Confirm
			String sum = middle + "/sum";
			PageSumDriver driverSum = new PageSumDriver();
			ret = ToolRunner.run(driverSum, new String[]{iterated, sum});
			if (1 == ret) {
				System.err.println("Job terminated at sum phase");
				System.exit(1);
			}
			
			/* Success */
			System.exit(0);
		}
		
	}
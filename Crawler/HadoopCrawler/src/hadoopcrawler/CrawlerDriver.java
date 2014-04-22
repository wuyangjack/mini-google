package hadoopcrawler;

import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import crawler.WebCrawler;

public class CrawlerDriver extends Configured implements Tool {

	private static final int MAXLEVEL = 2;
	private static final String FILENAME = "/part-r-00000";
	private static final String URLFILEOUT = "/urlfileout";
	private static final int MAXQUEUE = 1000;
	private FileSystem fs;
	
	
	public CrawlerDriver() {
		super();
		try {
			fs = FileSystem.get(new Configuration());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileSystem getFileSystem() {
		return fs;
	}
	
	public void closeFileSystem() throws IOException {
		if(fs != null)
			fs.close();
	}
	
	@Override
	public int run(String[] args) throws Exception {
		// 1. get the input file and output folder
		boolean success = true;

		// 2. Initiate the job
		@SuppressWarnings("deprecation")
		Job job = new Job();
		
		// 2.1 set job runner
		job.setJarByClass(CrawlerDriver.class);
		job.setJobName(this.getClass().getName());
		// 2.2 set partitioner
        //job.setPartitionerClass(CrawlerPartitioner.class);
        // 2.3 set job mapper
        job.setMapperClass(CrawlerMapper.class);
		job.setReducerClass(CrawlerReducer.class);
		// 2.4 set the map output and reduce output key and value class
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		// 2.2 set input output format
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		CrawlerDriver driver = new CrawlerDriver();
		
		int iterationCount = 1;
		int exitCode = 0;
		String input, folder, dir;
		int size, number;
		WebCrawler crawler;
		if (args.length == 4 | args.length == 5) {
			input = args[0];
			folder = args[1];
			dir = args[2];
	    	size = Integer.parseInt(args[3]);
	    	number = args.length == 5 ? Integer.parseInt(args[4]) : Integer.MAX_VALUE;
	    	File f = new File(dir);
	    	if(f.exists()) {
	    		deleteFolder(f);
	    	}
	    	f.mkdir();
	    	f.setExecutable(true);
	    	f.setReadable(true);
	    	f.setWritable(true);
	    	System.out.println(f.getAbsolutePath());
	    	crawler = new WebCrawler(f.getCanonicalPath(), size, number, 1, driver.getFileSystem());
		} else {
			System.err.println("Incorrect number of arguments.  Expected: input output");
			return;
		}
		
		while(iterationCount <= MAXLEVEL) {
			// 1. get the map reduce folder
			if(iterationCount > 1) {
				// folder => folder/urlfileout1
				args[0] = folder + (iterationCount-1) + URLFILEOUT;
			}
			else {
				args[0] = input;
			}
			args[1] = folder + iterationCount;
			exitCode = ToolRunner.run(driver, args);
			// 2. run crawler
			if(exitCode == 0)
				runCrawler(crawler, folder, iterationCount, driver.getFileSystem());
			else
				break;
			iterationCount++;
		}
		args[0] = folder + (iterationCount-1) + URLFILEOUT;
		args[1] = folder + iterationCount;
		exitCode = ToolRunner.run(driver, args);
		// close title, meta, links, body writer
		crawler.closeWriter();
		driver.closeFileSystem();
		System.out.println("Iteration Number: " + (iterationCount - 1));
		System.out.println("Total Number: " + crawler.getCount());
		System.exit(exitCode);
	}

	@SuppressWarnings("deprecation")
	private static void runCrawler(WebCrawler crawler, String folder,
			int iterationCount, FileSystem fs) throws InterruptedException, IOException {
		//1. crawler need to init the queue
		// input: folder/urlfile	output: folder/urlfile
		int read_count = 0;
		String inputFile, outputFile;
		// input: folder/urlfile1/part-r-00000 output: folder/urlfile1/urlfileout
		inputFile = folder + iterationCount + FILENAME;
		outputFile = folder + iterationCount + URLFILEOUT;
		FSDataInputStream file_reader = fs.open(new Path(inputFile));
		FSDataOutputStream file_writer = fs.create(new Path(outputFile), true);
		// set crawler write to outputFile
		crawler.init(file_writer);
		String line = null;
		// begin read file
		while( (line = file_reader.readLine()) != null) {
			String[] args = line.split("\t", 2);
			if(args.length == 2) {
				crawler.initQueue(args[0], Integer.parseInt(args[1]));
				read_count++;
			}
			// 2. if we got MAXQUEUE, run crawler
			if(read_count == MAXQUEUE) {
				crawler.run();
				read_count = 0;
				// need to reinit the queue and writer
				crawler.init(file_writer);
			}
		}
		if(read_count != 0) {
			crawler.run();
			read_count = 0;
		}
		file_reader.close();
		file_writer.flush();
		file_writer.close();
	}
	
	private static void deleteFolder(File file) {
		  if(file.isDirectory()){
	    		if(file.list().length==0){
	    		   file.delete();
	    		}
			    else{
			        File[] filess = file.listFiles();
			        for (File temp : filess) {
			        	 deleteFolder(temp);
			        }
			        if(file.list().length==0){
			           	 file.delete();
			        }
			     }
		    }
		   else{
	    		file.delete();
	    	}
	  }
}


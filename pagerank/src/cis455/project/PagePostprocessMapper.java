package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PagePostprocess
 * @author Yang Wu
 *
 */
public class PagePostprocessMapper extends Mapper<LongWritable, Text, Text, Text> {
		
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// Parse input
		String line = value.toString();
		String[] pair = StringDelimeter.split(line, "\t", 2);
		if (pair.length != 2) {
			System.err.println("line invalid format");
			return;
		}
		
		// Emit rank
		String url = pair[0];
		ValuePage page = new ValuePage();
		try {
			page.deserialize(pair[1]);
		} catch (Exception e) {
			System.err.println("page invalid format");
			e.printStackTrace();
			return;
		}
		context.write(new Text(url), new Text(String.valueOf(page.rank)));
	}
}
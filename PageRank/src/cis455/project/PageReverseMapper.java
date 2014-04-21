package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Page
 * @author Yang Wu
 *
 */
public class PageReverseMapper extends Mapper<LongWritable, Text, Text, Text> {
		
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// Parse input
		String line = value.toString();
		String[] pair = StringDelimeter.split(line, "\t", 2);
		if (pair.length != 2) {
			System.err.println("line invalid format");
			return;
		}
		
		// Reverse inlinks to outlinks
		String url = pair[0];
		if (pair[1].equals(PageGlobal.valueEmptyWebpage)) {
			context.write(new Text(url), new Text(PageGlobal.valueEmptyWebpage));
		} else {
			String[] inlinks = StringDelimeter.split(pair[1], PageGlobal.delimeterRawWebpage);
			for (String inlink : inlinks) {
				context.write(new Text(inlink), new Text(url));
			}
		}
	}
}
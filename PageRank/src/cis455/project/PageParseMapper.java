package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PagePrepare
 * @author Yang Wu
 *
 */
public class PageParseMapper extends Mapper<LongWritable, Text, Text, Text> {
		
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// Parse input
		String line = value.toString();
		String[] pair = StringDelimeter.split(line, "\t", 2);
		if (pair.length != 2) {
			System.err.println("line invalid format");
			return;
		}
		
		// Emit downloaded webpage
		String url = pair[0];
		ValuePage page = new ValuePage(1, StringDelimeter.split(pair[1], PageGlobal.delimeterRawWebpage));
		context.write(new Text(url), new Text(PageGlobal.valueDownloadedWebpage));
		
		// Emit outlinks
		for (String outlink : page.outlinks) {
			context.write(new Text(outlink), new Text(url));
		}
	}
}
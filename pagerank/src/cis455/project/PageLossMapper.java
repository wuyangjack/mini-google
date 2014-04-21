package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PageLoss
 * @author Yang Wu
 *
 */
public class PageLossMapper extends Mapper<LongWritable, Text, Text, Text> {
		
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// Parse the line
		String line = value.toString();
		String[] pair = StringDelimeter.split(line, "\t", 2);
		if (pair.length != 2) {
			System.err.println("line invalid format");
			return;
		}
				
		// Deserialize page
		ValuePage page = new ValuePage();
		try {
			page.deserialize(pair[1]);
		} catch (Exception e) {
			System.err.println("page deserialize failure");
			e.printStackTrace();
			return;
		}
				
		// Emit loss
		if (0 == page.outlinks.length) {
			// Collect rank loss caused by webpages with no outlinks
			double lossSingle = PageGlobal.damping * page.rank;
    		System.out.println("rank loss: " + String.valueOf(lossSingle));
			context.write(new Text(PageGlobal.keyReduceLoss), new Text(String.valueOf(lossSingle)));
		}
	}
}
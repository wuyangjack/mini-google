package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PageRank
 * @author Yang Wu
 *
 */
public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {
		
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
		String url = pair[0];
		ValuePage page = new ValuePage();
		try {
			page.deserialize(pair[1]);
		} catch (Exception e) {
			System.err.println("page deserialize failure");
			e.printStackTrace();
			return;
		}
		
		// Emit page
		context.write(new Text(url), new Text(page.serialize()));
		
		// Emit rank
		double pass = 0;
		if (page.outlinks.length != 0) {
			pass = PageGlobal.damping * page.rank / (page.outlinks.length);
		} else {
			// Collect rank loss caused by webpages with no outlinks
			double lossSingle = PageGlobal.damping * page.rank;
    		System.out.println("rank loss: " + String.valueOf(lossSingle));
		}
		ValueRank rank = new ValueRank(pass);
		String _rank = rank.serialize();
		for (String outlink : page.outlinks) {
			context.write(new Text(outlink), new Text(_rank));
		}
	}
}
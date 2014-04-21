package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PageRank
 * @author Yang Wu
 *
 */
public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
    @Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
    	// Get url
    	String url = key.toString();
    	
    	// Iterate
    	double rank = 0;
    	ValuePage page = null;
    	ValueRank contribution = new ValueRank();
    	for (Text value : values) {
    		String token = value.toString();
    		if (ValuePage.is(token)) {
    			// Recover page
    			try {
    				page = new ValuePage();
					page.deserialize(token);
				} catch (Exception e) {
					System.err.println("page invalid format");
					e.printStackTrace();
					return;
				}
    		} else if (ValueRank.is(token)) {
    			// Add contribution
    			try {
					contribution.deserialize(token);
					rank += contribution.rank;
				} catch (Exception e) {
					System.err.println("contribution invalid format");
					e.printStackTrace();
					return;
				}
    		} else {
    			System.err.println("unexpected token");
				return;
    		}
    	}
    	
    	// Emit
    	if (page == null) {
    		System.err.println("no webpage recovered: " + url);
			return;
    	} else {
    		// Add global distribution
    		rank += (1 - PageGlobal.damping);
    		// Add loss compensation
    		double lossCompensate = context.getConfiguration().getDouble(PageGlobal.propertyLossCompensate, 0);
    		System.out.println("loss compensate: " + lossCompensate);
    		rank += lossCompensate;
    		page.rank = rank;
			context.write(new Text(url), new Text(page.serialize()));
    	}
	}
}
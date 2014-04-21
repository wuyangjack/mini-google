package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PageLoss
 * @author Yang Wu
 *
 */
public class PageSumReducer extends Reducer<Text, Text, Text, Text> {
    @Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

    	// Guaranteed that all ranks are aggregated on a single reducer
    	// Collect all rank
    	double rankTotal = 0;
    	for (Text value : values) {
    		double rankSingle = Double.valueOf(value.toString());
    		rankTotal += rankSingle;
    	}
    	
    	// Emit
    	context.write(key, new Text(String.valueOf(rankTotal)));
	}
}
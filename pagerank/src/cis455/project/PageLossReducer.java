package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PageLoss
 * @author Yang Wu
 *
 */
public class PageLossReducer extends Reducer<Text, Text, Text, Text> {
    @Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

    	// Guaranteed that all losses are aggregated on a single reducer
    	// Collect all loss
    	double lossTotal = 0;
    	for (Text value : values) {
    		double lossSingle = Double.valueOf(value.toString());
    		lossTotal += lossSingle;
    	}
    	
    	// Emit
    	context.write(key, new Text(String.valueOf(lossTotal)));
	}
}
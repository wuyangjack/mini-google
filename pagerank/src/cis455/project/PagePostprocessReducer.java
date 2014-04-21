package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PagePostprocess
 * @author Yang Wu
 *
 */
public class PagePostprocessReducer extends Reducer<Text, Text, Text, Text> {
    @Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
    	int i = 0;
    	for (Text value : values) {
    		i ++;
    		if ( i > 1) {
    			System.err.println("redundant line");
    			return;
    		} else {
        		context.write(key, value);
    		}
    	}
	}
}
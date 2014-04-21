package cis455.project;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PageRank
 * @author Yang Wu
 *
 */
public class PageParseReducer extends Reducer<Text, Text, Text, Text> {
    @Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
    	boolean downloaded = false;
    	StringBuilder sb = new StringBuilder();
    	for (Text value : values) {
    		String token = value.toString();
    		if (token.equals(PageGlobal.valueDownloadedWebpage)) {
    	    	// Check if page downloaded
    			downloaded = true;
    		} else if (token.equals(PageGlobal.valueEmptyWebpage)) {
    			// Emit empty page
        		context.write(key, value);
    		} else {
    	    	// Collect inlinks
    			sb.append(" " + token);
    		}
    	}
    	String inlinks = sb.toString();
    	
    	if (downloaded && inlinks.length() != 0) {
    		// Emit inlinks that goes to downloaded pages
    		// Guranteed that inlinks starts and ends in downloaded pages
    		context.write(key, new Text(inlinks.substring(1)));
    	}
	}
}
package cis455.project;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Page
 * @author Yang Wu
 *
 */
public class PageReverseReducer extends Reducer<Text, Text, Text, Text> {
    @Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
    	
    	ArrayList<String> list = new ArrayList<String>();
    	for (Text value : values) {
    		if (value.toString().equals(PageGlobal.valueDownloadedWebpage)) {
    	    	// Skip
    		} else {
    	    	// Collect outlinks for non-empty page
        		list.add(value.toString());
    		}
    	}
    	String[] outlinks = new String[list.size()];
    	outlinks = list.toArray(outlinks);
    	
    	// Emit non-empty page
    	// All outlinks start in downloaded page and end in downloaded page
    	ValuePage page = new ValuePage(1, outlinks);
    	context.write(key, new Text(page.serialize()));
    	context.getCounter(PageGlobal.Counters.PAGECOUNT).increment(1);
	}
}
package hadoop;

import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.io.*;

import java.io.IOException;
import java.util.Iterator;


public class CrawlerReducer1 extends MapReduceBase
implements Reducer<Text, Text, Text, IntWritable> {

	public void reduce(Text key, Iterator<Text> values,
	        OutputCollector<Text, IntWritable> output, Reporter reporter)
	        throws IOException {

		while(values.hasNext()) {
			  Text value = values.next();
			  System.out.println(value);
			  String[] args = value.toString().split("\t", 2);
			  if(args.length == 2) {
				  Text out_key = new Text(args[0]);
				  IntWritable out_value = new IntWritable(Integer.parseInt(args[1]));
				  output.collect(out_key, out_value);
			  }
		}
	}

}

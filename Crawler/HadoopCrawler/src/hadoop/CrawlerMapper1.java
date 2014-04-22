package hadoop;

import general.UrlNormalizer;

import java.io.IOException;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.io.*;



public class CrawlerMapper1 extends MapReduceBase
implements Mapper<LongWritable, Text, Text, Text>{

    public void map(LongWritable key, Text value,
        OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {

    	String text = value.toString();
    	String[] args = text.split("\t", 2);
  	  	if(args.length == 2) {
	  		  String hostname = UrlNormalizer.getHostName(args[0]);
	  		  if(hostname != null && ! hostname.equals("")) {
	  			  Text host = new Text(hostname);
	  			  output.collect(host, value);
	  		  }
  	  }
    }

}

package hadoopcrawler;

import general.UrlNormalizer;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CrawlerMapper extends Mapper<LongWritable, Text, Text, Text> {

  @Override
  public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
	  String text = value.toString();
	  String[] args = text.split("\t", 2);
	  if(args.length == 2) {
		  String hostname = UrlNormalizer.getHostName(args[0]);
		  if(hostname != null && ! hostname.equals("")) {
			  Text host = new Text(hostname);
			  context.write(host, value);
		  }
	  }
  }
}

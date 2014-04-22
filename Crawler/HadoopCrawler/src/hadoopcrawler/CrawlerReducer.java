package hadoopcrawler;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CrawlerReducer extends Reducer<Text, Text, Text, IntWritable> {

  @Override
  public void reduce(Text key, Iterable<Text> values, Context context)
      throws IOException, InterruptedException {
	  for (Text value : values) {
		  String[] args = value.toString().split("\t", 2);
		  if(args.length == 2) {
			  Text out_key = new Text(args[0]);
			  IntWritable out_value = new IntWritable(Integer.parseInt(args[1]));
			  context.write(out_key, out_value);
		  }
	  }
  }
}
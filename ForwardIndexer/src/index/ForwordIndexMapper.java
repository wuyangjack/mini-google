package index;


import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class ForwordIndexMapper extends Mapper<LongWritable, Text, Text, ForwardMapperValue> {

	private Text mapper_key;
	private ForwardMapperValue mapper_value;
	
	@Override
	/**
	 * Do map function
	 */
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] pair = value.toString().split("\t");
		// if there is information
		if(pair.length == 4) {
			String word = pair[0];
			String doc = pair[1];
			String tfidf = pair[3];
			mapper_key = new Text(doc);
			mapper_value = new ForwardMapperValue(word, Double.parseDouble(tfidf));
			context.write(mapper_key, mapper_value);
		}
	}
	
	
}
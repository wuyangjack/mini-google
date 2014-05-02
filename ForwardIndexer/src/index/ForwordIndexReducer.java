package index;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ForwordIndexReducer extends Reducer<Text, ForwardMapperValue, Text, DoubleWritable>  {

	public void reduce(Text reduce_key, Iterable<ForwardMapperValue> values, Context context) throws IOException,
	InterruptedException {
		double module = 0;
		
		StringBuilder st = new StringBuilder();
		st.append(reduce_key  + " :: ");
		
		for (ForwardMapperValue value : values) {
			double tfidf = value.getTF();
			module += Math.pow(tfidf, 2);
			st.append(value.getwordId());
		}
		module = Math.sqrt(module);
		
		System.out.println(st.toString());
		
		context.write(reduce_key, new DoubleWritable(module));
	}
}

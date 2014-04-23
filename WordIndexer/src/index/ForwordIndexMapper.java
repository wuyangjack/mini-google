package index;

import general.DelimiterTokenizer;
import general.IndexKey;
import general.IndexValue;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class ForwordIndexMapper extends Mapper<LongWritable, Text, IndexKey, IndexValue> {

	private IndexKey index_key;
	private IndexValue index_value;
	private String pattern = "^[a-zA-Z0-9_]*$";
	
	@Override
	/**
	 * Do map function
	 */
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		byte position = 1;
		String[] pair = value.toString().split("\t", 2);
		// if there is information
		if(pair.length == 2) {
			DelimiterTokenizer tokenizer = new DelimiterTokenizer(pair[1]);
			while(tokenizer.hasNext()) {
				String wordId = tokenizer.nextToken();
				if(! wordId.equals("") && wordId.matches(pattern)) {
					// index key = url, word, postion
					index_key = new IndexKey(pair[0], wordId, position);
					// index value = word, position
					index_value = new IndexValue(wordId, position++, 0.01);
					context.write(index_key, index_value);
				}
			}
		}
		
	}
}
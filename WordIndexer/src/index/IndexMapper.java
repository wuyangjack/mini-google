package index;

import general.DelimiterTokenizer;
import general.IndexKey;
import general.IndexValue;
import general.WordPreprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class IndexMapper extends Mapper<LongWritable, Text, IndexKey, IndexValue> {

	private IndexKey index_key;
	private IndexValue index_value;
	private String pattern = "^[a-zA-Z0-9_]*$";
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		int position = 0;
		String[] pair = value.toString().split("\t", 2);
		// if there is information
		if(pair.length == 2) {
			DelimiterTokenizer tokenizer0 = new DelimiterTokenizer(pair[1]);
			Map<String, Integer> freqMap = new HashMap<String, Integer>();
//			ArrayList<String> copy = new ArrayList<String>();
			
			ArrayList<IndexKey> keyList = new ArrayList<IndexKey>();
			while(tokenizer0.hasNext()) {
				String wordId = tokenizer0.nextToken();
				//preprocess the word: to lowercase, excluding stop words, word stemming
//				wordId = WordPreprocessor.preprocess(wordId);
				
				if(! wordId.equals("") && wordId.matches(pattern)) {
					//preprocess the word: to lowercase, excluding stop words, word stemming
					wordId = WordPreprocessor.preprocess(wordId);
					position++;
					if(wordId == null){
						continue;
					}
					keyList.add(new IndexKey(wordId, pair[0], position));
					
					
//					copy.add(wordId);
					if(freqMap.containsKey(wordId)){
						freqMap.put(wordId, freqMap.get(wordId) + 1);
					}
					else{
						freqMap.put(wordId, 1);
					}
				}
			}
			
			int maxFreq = Integer.MAX_VALUE;
			if(!freqMap.isEmpty()) {
				maxFreq = Collections.max(freqMap.values());
			}
				
				
				
//			DelimiterTokenizer tokenizer = new DelimiterTokenizer(pair[1]);
//			while(tokenizer0.hasNext()) {				
//				String wordId = tokenizer.nextToken();
			for(IndexKey oneKey: keyList){
				String word = oneKey.getDocId();
				
				
				
				if(! word.equals("") && word.matches(pattern)) {
//					System.out.println("again: " + wordId);
					// index key = word, url, postion
					index_key = oneKey;
					// index value = url, postion, tf					
					double tf = IndexDriver.tf_factor + (1 - IndexDriver.tf_factor) * freqMap.get(word) / maxFreq;
					index_value = new IndexValue(pair[0], oneKey.getPosition(), tf);
//					System.out.println("I am in reverse index map, value: " + index_value);
					context.write(index_key, index_value);
				}
			}
		}
		
	}
}
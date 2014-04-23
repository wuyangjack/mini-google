package index;

import general.IndexKey;
import general.IndexOutput;
import general.IndexValue;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IndexReducer extends Reducer<IndexKey, IndexValue, Text, IndexOutput> {

	@Override
	/**
	 * Do reducer
	 */
	public void reduce(IndexKey index_key, Iterable<IndexValue> values, Context context) throws IOException,
			InterruptedException {
		String docId = index_key.getDocId();
		Text key = new Text(docId);
		// need wordId and position to record the word information
		String wordId = null;
		double tf = 0;
		ArrayList<Integer> position = new ArrayList<Integer>();
		// index_output to write the output
		IndexOutput index_output;
		// word : position list
		Map<String, List<Integer> > wordMap = new HashMap<String, List<Integer>>();		
		
		ArrayList<IndexValue> copy = new ArrayList<IndexValue>();
		int count = 0;
		String preWord = null;
		for(IndexValue value: values){
			copy.add(new IndexValue(value.getwordId(), value.getposition(), value.getTF()));
			if(!value.getwordId().equals(preWord)){
				count++;
				preWord = value.getwordId();
			}
		}
		double idf = Math.log(((double)IndexDriver.total_doc_num / count)); 
		
		
//		System.out.println("begin");
		
		// Go through all values to get the docId and it corresponding word
		for (IndexValue value : copy) {
//			System.out.println(index_key.getDocId() + "; " + value.toString());
			// the first time get wordId
			if(wordId == null) {
				wordId = value.getwordId();
				position.add(value.getposition());
				tf = value.getTF();
			}
			// need accumulate
			else if(wordId.equals(value.getwordId())) {
				position.add(value.getposition());
			}
			// we got an new word
			else {
				// write the old to file
				index_output = new IndexOutput(wordId, position, tf * idf);
				context.write(key, index_output);
//				if(IndexDriver.db.addTFIDF(docId, wordId, tf * idf, position)){
//					System.out.println("Succedd storing: " + docId + ";" + wordId +";" + tf * idf);
//				}
//				else{
//					System.out.println("Fail to store: "+ wordId + ";" + docId);
//				}
				// save the old to map
				wordMap.put(wordId, position);
				wordId = value.getwordId();
				tf = value.getTF();
				position = new ArrayList<Integer>();
				position.add(value.getposition());
			}
		}
		if(! position.isEmpty()) {
			index_output = new IndexOutput(wordId, position, tf * idf);
			context.write(key, index_output);
//			if(IndexDriver.db.addTFIDF(docId, wordId, tf * idf, position)){
//				System.out.println("Succedd storing: " + docId + ";" + wordId +";" + tf * idf);
//			}
//			else{
//				System.out.println("Fail to store: "+ wordId + ";" + docId);
//			}
			wordMap.put(wordId, position);
		}
//		System.out.println("end");

	}
}

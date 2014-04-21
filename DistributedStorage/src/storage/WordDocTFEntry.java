package storage;

import java.util.ArrayList;
import java.util.Arrays;

public class WordDocTFEntry {

	public String word = null;
	public String doc = null;
	double tfidf = -1;
	ArrayList<Integer> positions = new ArrayList<Integer>();
	
	public WordDocTFEntry(String word, String doc, double tfidf, String positions){
		this.word = word;
		this.doc = doc;
		this.tfidf = tfidf;
		String[] ps = positions.substring(1, positions.length() - 1).split(", ");
		for(int i = 0; i < ps.length; i++){
			this.positions.add(Integer.parseInt(ps[i]));
		}
		
		
	}
	
	public String toString(){
		return this.word + ";;;" + this.doc + ";;;" + tfidf + ";;;" + positions;
	}

}

package general;

import java.util.ArrayList;

public class TFIDFEntry {

	public String word = null;
	public String doc = null;
	double tfidf = -1;
	ArrayList<Integer> positions = new ArrayList<Integer>();
	
	public TFIDFEntry(String word, String doc, double tfidf, String positions){
		this.word = word;
		this.doc = doc;
		this.tfidf = tfidf;
		String[] ps = positions.substring(1, positions.length() - 1).split(", ");
		for(int i = 0; i < ps.length; i++){
			if(!ps[i].equals("")){
				this.positions.add(Integer.parseInt(ps[i]));
			}
		}
	}
	
	public static TFIDFEntry deserialize(String rawData){
		String[] data = rawData.trim().split("\t");
		String word = data[0];
		String doc = data[1];
		double tfidf = Double.parseDouble(data[2]);
		String positions = data[3];
		return new TFIDFEntry(word, doc, tfidf, positions);
	}

	
	public String toString(){
		return this.word + ";;;" + this.doc + ";;;" + tfidf + ";;;" + positions;
	}

}

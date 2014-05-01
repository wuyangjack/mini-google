package cis455.project.storage;

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
	
	public static TFIDFEntry deserialize(String key, String rawData){
		String[] data = rawData.trim().split("\t");
		String doc = data[0];
		String positions = data[1];
		String tf_idf = data[2];
		try{
			if(!(doc.equals("") || positions.equals("") || tf_idf.equals(""))){
				double tfidf = Double.parseDouble(data[2]);
				return new TFIDFEntry(key, doc, tfidf, positions);
			}
			else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}

	
	public String toString(){
		return this.word + ";;;" + this.doc + ";;;" + tfidf + ";;;" + positions;
	}

}

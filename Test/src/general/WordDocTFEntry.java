package general;

public class WordDocTFEntry {

	public String word = null;
	public String doc = null;
	double tfidf = -1;
	
	public WordDocTFEntry(String word, String doc, double tfidf){
		this.word = word;
		this.doc = doc;
		this.tfidf = tfidf;
				
	}
	
	public String toString(){
		return this.word + ";;;" + this.doc + ";;;" + tfidf;
	}

}

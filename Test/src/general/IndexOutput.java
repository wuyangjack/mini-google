package general;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.WritableComparable;

public class IndexOutput implements WritableComparable<IndexOutput>{
	private String wordId;
//	private int wordNum;
	private List<Integer> position;
	private double tfidf;
	
	public IndexOutput() {
		
	}
	
	public IndexOutput(String wordId, List<Integer> position, double tfidf) {
		this.wordId = wordId;
		this.position = position;
		this.tfidf = tfidf;
	}
	
	public void set(String wordId, List<Integer> position, double tfidf) {
//	public void set(String wordId, int wordNum, List<Integer> position) {
		this.wordId = wordId;
		this.position = position;
		this.tfidf = tfidf;
	}
	
	@Override
	//problematic
	public void readFields(DataInput in) throws IOException {
		wordId = in.readUTF();
		for(int i = 0; i < position.size(); i++) 
			position.add(in.readInt());
		tfidf = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(wordId);
		for(int i = 0; i < position.size(); i++) 
			out.writeInt(position.get(i));
		out.writeDouble(tfidf);
	}
	
	@Override
	public int compareTo(IndexOutput ik) {
		int result  = wordId.compareTo(ik.getWordId());
		return result;
	}
	
	@Override
	public String toString() {
		String result = wordId + "\t" + position.toString() + "\t" + tfidf;
//		String result = wordId + "\t" + wordNum + "\t" + position.toString();
		return result;
	}
	
	
	public void setWordId(String wordId) { this.wordId = wordId; }
	
	public String getWordId() { return wordId; }
	
	public void setPosition(List<Integer> position) { this.position = position; }
	
	public List<Integer> getPosition() { return position; }
	
//	public double getTF() { return tf;}
}

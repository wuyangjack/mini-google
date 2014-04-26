package general;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class IndexValue implements WritableComparable<IndexValue>{

	private String wordId;
	private int position;
	private double tf;
	
	public IndexValue() {
		
	}
	
	public IndexValue(String wordId, int position, double tf) {
		this.wordId = wordId;
		this.position = position;
		this.tf = tf;
	}

	public void set(String wordId, int position, double tf) {
		this.wordId = wordId;
		this.position = position;
		this.tf = tf;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		wordId = in.readUTF();
		position = in.readInt();
		tf = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(wordId);
		out.writeInt(position);
		out.writeDouble(tf);
	}
	
	@Override
	public int compareTo(IndexValue iv) {
		int result = wordId.compareTo(iv.getwordId());
		if(result == 0)
			result = position - iv.position;
		return result;
	}
	
	@Override
	public int hashCode() {
		return wordId.hashCode();
	}
	
	@Override
	public String toString() {
		return wordId + ": " + position + " ; " + tf;
	}
	
	public void setwordId(String wordId) { this.wordId = wordId; }
	
	public String getwordId() { return wordId; }
	
	public void setposition(int position) { this.position = position; }
	
	public int getposition() { return position; }
	
	public double getTF(){return tf;}

//	@Override
//	public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * wordId.hashCode() + position;
//        return result;
//      }
}

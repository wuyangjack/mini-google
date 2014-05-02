package index;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class ForwardMapperValue implements WritableComparable<ForwardMapperValue>{

	private String wordId;
	private double tf = 0;
	
	public ForwardMapperValue() {
		
	}
	
	public ForwardMapperValue(String wordId,  double tf) {
		this.wordId = wordId;
		this.tf = tf;
	}

	public void set(String wordId, double tf) {
		this.wordId = wordId;
		this.tf = tf;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		wordId = in.readUTF();
		tf = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(wordId);
		out.writeDouble(tf);
	}
	
	@Override
	public int compareTo(ForwardMapperValue iv) {
		int result = wordId.compareTo(iv.getwordId());
		if(result == 0)
			result = (int)(this.tf - iv.getTF());
		return result;
	}
	
	@Override
	public int hashCode() {
		return wordId.hashCode();
	}
	
	@Override
	public String toString() {
		return wordId + "\t"  + tf;
	}
	
	public void setwordId(String wordId) { this.wordId = wordId; }
	
	public String getwordId() { return wordId; }
	
	public double getTF(){return tf;}

//	@Override
//	public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * wordId.hashCode() + position;
//        return result;
//      }
}

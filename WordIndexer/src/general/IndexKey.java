package general;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class IndexKey implements WritableComparable<IndexKey>{

	private String docId;
	private String wordId;
	private byte position;
	
	public IndexKey() {
		
	}
	
	public IndexKey(String docId, String wordId, byte position) {
		this.docId = docId;
		this.wordId = wordId;
		this.position = position;
	}
	
	public void set(String docId, String wordId, byte position) {
		this.docId = docId;
		this.wordId = wordId;
		this.position = position;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		docId = in.readUTF();
		wordId = in.readUTF();
		position = in.readByte();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(docId);
		out.writeUTF(wordId);
		out.writeByte(position);
	}
	
	@Override
	public int compareTo(IndexKey ik) {
		int result = docId.compareTo(ik.getDocId());
		if(result == 0) {
			result = wordId.compareTo(ik.getWordId());
			if(result == 0)
				result = position - ik.getPosition();
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return docId.hashCode();
	}
	
	public void setDocId(String docId) { this.docId = docId; }
	
	public String getDocId() { return docId; }
	
	public void setWordId(String wordId) { this.wordId = wordId; }
	
	public String getWordId() { return wordId; }
	
	public void setPosition(byte position) { this.position = position; }
	
	public byte getPosition() { return position; }

	
}

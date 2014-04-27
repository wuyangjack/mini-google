package edu.upenn.cis455.mapreduce.master;

public class Status {
	
	private String status;
	private long count;
	private long keysRead;
	private long keysWritten;
	private long time;
	
	public Status(String status, long count, long keysRead, long keysWritten, long time) {
		this.status = status;
		this.count = count;
		this.keysRead = keysRead;
		this.keysWritten = keysWritten;
		this.time = time;
	}
	
	public String getStatus() { return status; }
	
	public long getCount() { return count; }
	
	public long getKeysRead() { return keysRead; }
	
	public long getKeysWritten() { return keysWritten; }
	
	public long getTime() { return time; }
	
	public void setStatus(String status) { this.status = status; }
	
}

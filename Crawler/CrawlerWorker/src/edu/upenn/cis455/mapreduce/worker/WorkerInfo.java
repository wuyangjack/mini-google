package edu.upenn.cis455.mapreduce.worker;

import java.util.concurrent.atomic.AtomicLong;

public class WorkerInfo {

	// initial parameter, the storage folder and the master ip and port
	private String storage;
	private String master_ip_port;
	// worker parameter
	private int port;
	private String status;
	private AtomicLong count;
	private AtomicLong keysRead;
	private AtomicLong keysWritten;
	
	public WorkerInfo(String storage, String master_ip_port, int port,
			String status, long count, long read, long write) {
		this.storage = storage;
		this.master_ip_port = master_ip_port;
		this.port = port;
		this.status = status;
		this.count = new AtomicLong(count);
		keysRead = new AtomicLong(read);
		keysWritten = new AtomicLong(write);
	}

	public String getStorage() { return storage; }

	public String getMasterIpPort() { return master_ip_port; }
	
	public int getPort() { return port; }

	public String getStatus() { return status; }

	public long getCount() { return count.get(); }

	public long getKeysRead() { return keysRead.get(); }

	public long getKeysWritten() { return keysWritten.get(); }

	public void setStatus(String status) { this.status = status; }
	
	public void setCount(long c) { count.set(c); }
	
	public long incrementCount() { return count.incrementAndGet(); }

	public void setKeysRead(long read) { keysRead.set(read); }

	public long incrementKeysRead() { return keysRead.incrementAndGet(); }

	public void setKeysWritten(long write) { keysWritten.set(write); }

	public long incrementKeysWritten() { return keysWritten.incrementAndGet(); }


}
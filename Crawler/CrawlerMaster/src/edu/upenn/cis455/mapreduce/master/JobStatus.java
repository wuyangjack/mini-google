package edu.upenn.cis455.mapreduce.master;

import java.util.Map;

public class JobStatus {

	private String inputdir;
	private int numMaps;
	private int numReducs;
	// workers ip : finish or not
	private Map<String, Boolean> workers;
	// num works
	private int numWorkers;
	// status = 0: means waiting(after map); status = 1: means waiting1(after push); status = 2: means idle (after reduce)
	private int status;
	private int iterativeNum;
	
	
	public JobStatus(String inputdir, int numMaps, int numReducs, Map<String, Boolean> workers, int iterativeNum) {
		this.inputdir = inputdir;
		this.numMaps = numMaps;
		this.numReducs = numReducs;
		this.workers = workers;
		numWorkers = this.workers.size();
		this.iterativeNum = iterativeNum;
		status = 0;
	}
	
	public void incrementIterativeNum() { this.iterativeNum++; }
	
	public int getIterativeNum() { return iterativeNum; }
	
	public String getInputDirectory() { return inputdir; }
	
	public int getNumMappers() { return numMaps; }
	
	public int getNumReducers() { return numReducs; }
	
	public Map<String, Boolean> getWorkers() { return workers; }
	
	public int getNumWorkers() { return numWorkers; }
	
	public int reduceNumWorkers() { numWorkers--; return numWorkers;}
	
	public void setNumWorkers(int num) { this.numWorkers = num; }
	
	public int getStatus() { return status;}

	public void increaseStatus() { status = (status + 1) % 3; }
	
	public void setWorkers(Map<String, Boolean> map) { this.workers = map; }
}

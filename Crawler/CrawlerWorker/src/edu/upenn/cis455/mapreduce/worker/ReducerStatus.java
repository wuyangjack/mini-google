package edu.upenn.cis455.mapreduce.worker;

public class ReducerStatus {

	private String inputdir;
	private int numReds;

	public ReducerStatus(String inputdir, int numReds) {
		this.inputdir = inputdir;
		this.numReds = numReds;
	}
	
	public String getInputDirectory() { return inputdir; }
	
	public int getNumReducers() { return numReds; }
	
}

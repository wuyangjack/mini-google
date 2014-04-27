package edu.upenn.cis455.mapreduce.worker;

import java.util.Map;

public class MapperStatus {

	private String inputdir;
	private int iteration;
	private int numMaps;
	private Map<String, String> workers;
	private int numWorkers;
	
	
	public MapperStatus(String inputdir, int iteration, int numMaps, int numWorkers, Map<String, String> workers) {
		this.inputdir = inputdir;
		this.iteration = iteration;
		this.numMaps = numMaps;
		this.workers = workers;
		this.numWorkers = numWorkers;
	}
	
	public String getInputDirectory() { return inputdir; }
	
	public int getNumMappers() { return numMaps; }
	
	public int getIteration() { return iteration; }
	
	public Map<String, String> getWorkers() { return workers; }
	
	public String getWorkerByName(String name) { return workers.get(name); }
	
	public int getNumWorkers() { return numWorkers; }
	
}

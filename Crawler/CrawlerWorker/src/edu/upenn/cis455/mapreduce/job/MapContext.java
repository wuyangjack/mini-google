package edu.upenn.cis455.mapreduce.job;

import hash.SHA1Partition;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import edu.upenn.cis455.mapreduce.Context;
import edu.upenn.cis455.mapreduce.worker.Info;
import edu.upenn.cis455.mapreduce.worker.LogClass;
import edu.upenn.cis455.mapreduce.worker.MapperStatus;
import edu.upenn.cis455.mapreduce.worker.WorkerInfo;

public class MapContext implements Context{
	
	private MapperStatus mapStatus;
	private String folder;

	public MapContext(WorkerInfo worker_info, MapperStatus mapStatus) {
		this.mapStatus = mapStatus;
		// level/mapoutput
		this.folder = worker_info.getStorage() + mapStatus.getInputDirectory() + mapStatus.getIteration() + Info.MAPOUTPUT;
		LogClass.info("Map output storage: " + this.folder);
		initMap();
	}
	
	private void initMap() {
		SHA1Partition.setRange(mapStatus.getNumWorkers());
	}
	
	public void write(String key, String value) {
			PrintWriter pw = null;
			try {
				// worker1, worker2
				String filename = Info.MAPOUTFILENAME + SHA1Partition.getWorkerIndex(key);
				LogClass.info(key + " map to: " + filename);
				// level/mapoutput 
				pw = new PrintWriter(new FileWriter(folder + filename, true));
				// hostname + "\t" + url
				pw.println(key + "\t" + value);
				pw.flush();
			} catch(Exception e) {
			} finally {
				if(pw != null)
					pw.close();
			}
	}

	public void close() {
	}
	
	public static void main(String[] args) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter("/Users/ChenyangYu/Documents/Upenn/storage//level1/mapoutput/worker0", true));
		pw.println("url" + "\t" + "hostname");
		pw.flush();
		pw.close();
	}
	
}

package edu.upenn.cis455.mapreduce.job;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import edu.upenn.cis455.mapreduce.Context;
import edu.upenn.cis455.mapreduce.worker.Info;

public class ReduceContext implements Context{
	
	private String folder;
	private PrintWriter pw;
	
	public ReduceContext(String folder) throws FileNotFoundException {
		this.folder = folder + "/" + Info.INPUTFILENAME;
		pw = new PrintWriter(new FileOutputStream(this.folder, true));
	}
	
	// hostname: url
	public synchronized void write(String key, String value) {
		pw.println(key + "\t"  + value);
		pw.flush();
	}

	public void close() {
		if(pw != null) {
			pw.flush();
			pw.close();
		}
	}
	
}

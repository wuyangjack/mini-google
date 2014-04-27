package edu.upenn.cis455.mapreduce.worker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.servlet.http.HttpServletRequest;

public class PushdataProcessor {

	private static PushdataProcessor pushdata_processor = null;
	private String folder;
	PrintStream reduce_writer;
	
	private PushdataProcessor(String folder) {
		this.folder = folder;
	}
	
	public synchronized static PushdataProcessor instanceOf(String folder) {
		if(pushdata_processor == null) {
			pushdata_processor = new PushdataProcessor(folder);
			pushdata_processor.initWriter();
		}
		return pushdata_processor;
	}
	
	private void initWriter() {
		try {
			LogClass.info("Write Data to: " + folder + Info.REDUCEINPUTFILE);
			reduce_writer = new PrintStream(new FileOutputStream(folder + Info.REDUCEINPUTFILE, true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		pushdata_processor = null;
		reduce_writer.close();
	}
	
	public void processPushdata(HttpServletRequest request) throws IOException {
		  LogClass.info("Receive push data");
		  BufferedReader input_reader = new BufferedReader(new BufferedReader(new InputStreamReader(request.getInputStream())));
		  String url = null;
		  while((url = input_reader.readLine()) != null) {
			  reduce_writer.println(url);
			  LogClass.info("Url: " + url);
		  }
		  reduce_writer.flush();
	}
}

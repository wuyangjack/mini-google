package cis455.project.storage;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {

	static Logger logger;
	static final String CRLF = "\r\n";
	public static void init() {
		File log = new File("/tmp/storage.log");
		if(!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(Log.class);
	}
	
	public static void info(String s) {
		if(logger == null)
			init();
		logger.info(s);
	}
	
	public static void error(String s) {
		if(logger == null)
			init();
		logger.error(s);
	}

	
}

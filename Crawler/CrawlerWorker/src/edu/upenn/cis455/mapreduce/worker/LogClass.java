package edu.upenn.cis455.mapreduce.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogClass {

	static Logger logger;
	static File fileName;
	static final String CRLF = "\r\n";
	public static void init() {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(LogClass.class);
		fileName = new File("log.log");
	}
	
	public static void info(String s) {
		if(logger == null)
			init();
		logger.info(s);
	}
	
	public static void error(String s) {
		logger.error(s);
	}

	public static String read() throws IOException {
		 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		 String result = "";
		 String request = null;
		 while((request= br.readLine()) != null) {
			 result += request + CRLF;
		 }
		 br.close();
		 return result;
	}
	
	public static void main(String[] args) {
		info("Test");
	}

}

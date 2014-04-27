package cis455.project.query;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import cis455.project.storage.Storage;

public class QueryWorker {
	private static final Logger logger = Logger.getLogger(QueryWorker.class);
	private static String loggerPath = QueryGlobal.logWorker;
	private static Storage storage = null;
	
	public static void initialize(String pathDatabase) {
		// Logging
		logger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
		logger.setAdditivity(false);
		logger.setLevel(Level.INFO);
		try {
			logger.addAppender(new FileAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), loggerPath, true));
		} catch (IOException e) {
			logger.error("cannot open log file");
		}
		
		// Database
		logger.info("connect to database: " + pathDatabase);
		try {
			Storage.setEnvPath(pathDatabase, true);
			storage = Storage.getInstance();
		} catch (Exception e) {
			logger.error("failure open database", e);
		}
		
	}
	
	public static String get(String table, String key) {
		String[] results = storage.get(table, key);
		StringBuilder sb = new StringBuilder();
		for (String result : results) {
			sb.append(QueryGlobal.delimiterNetwork);
			sb.append(result);
		}
		return sb.toString().substring(QueryGlobal.delimiterNetwork.length());
	}
	
	public static void close() {
		logger.info("close database");
		storage.close();
	}
	
}

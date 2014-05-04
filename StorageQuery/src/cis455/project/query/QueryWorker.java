package cis455.project.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import StopAndStemmer.WordPreprocessor;

import cis455.project.search.SearchWorker;
import cis455.project.storage.Storage;
import cis455.project.storage.StorageGlobal;
import cis455.project.storage.TFIDFEntry;

public class QueryWorker{
	public static final Logger logger = Logger.getLogger(QueryWorker.class);
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
			String[] databaseNames = new String[]{StorageGlobal.tablePageRank, StorageGlobal.tableFreqTitle, StorageGlobal.tableModTitle};
			storage = Storage.getInstance(databaseNames);
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

	public static List<TFIDFEntry> getWordweight(String tableName, List<String> words) {
		ArrayList<TFIDFEntry> entryList = new ArrayList<TFIDFEntry>();
		for(String word: words){
			String[] re = storage.get(tableName, word);
			if(re.length == 0){
				logger.info("No entry found");
			}
			else{
				for(int i = 0; i < re.length; i++){
					TFIDFEntry entry = TFIDFEntry.deserialize(word, re[i]);
					entryList.add(entry);
				}
			}
		}
		return entryList;
	}
	
	public static String search(String query) {
		List<String> words = new ArrayList<String>();
		for(String word : query.split("\\s")) {
			String w = WordPreprocessor.preprocess(word);
			if(w != null)
				words.add(w);
		}
		String result = SearchWorker.search(words);
		return result;
	}
	
	public static Map<String, Double> getPagerank(Set<String> urls) {
		HashMap<String, Double> re = new HashMap<String, Double>();
		for(String url : urls){
			String[] arr = storage.get(StorageGlobal.tablePageRank, url);
			if(arr.length > 0){
				re.put(url, Double.parseDouble(arr[0].trim()));
			}
		}
		return re;
	}

	public static Map<String, Double> getDocmodule(String tableName, Set<String> urls) {
		HashMap<String, Double> re = new HashMap<String, Double>();
		for(String url : urls){
			String[] arr = storage.get(StorageGlobal.tableModTitle, url);
			if(arr.length > 0){
				re.put(url, Double.parseDouble(arr[0].trim()));
			}
		}
		return re;
	}
	
}

package cis455.project.query;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import cis455.project.hash.SHA1Partition;
import cis455.project.rank.DocumentInfo;
import cis455.project.rank.DocumentRanker;
import cis455.project.rank.ImageRanker;
import cis455.project.storage.Storage;
import cis455.project.storage.StorageGlobal;

class GetWorkerThread extends Thread {
	private String result = null;
	private String server = null;
	private String table = null;
	private String key = null;
	
	GetWorkerThread(String server, String table, String key) {
		this.server = server;
		this.table = table;
		this.key = key;
	}
	
	public String result() {
		return result;
	}
	
	@Override
	public void run() {
		QueryMaster.logger.info("fetch from worker server: " + server);
		// Get
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = null;
		try {
			url = "http://" + server + "/" + QueryGlobal.pathContextWorker + "/" + QueryGlobal.pathWorker + "?" 
					+ QueryGlobal.paraMode + "=" + QueryGlobal.modeGet + "&"
					+ QueryGlobal.paraTable + "=" + URLEncoder.encode(table, "UTF-8") + "&"
					+ QueryGlobal.paraKey + "=" + URLEncoder.encode(key, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			QueryMaster.logger.error("error encoding query");
			e.printStackTrace();
		}
		QueryMaster.logger.info(url);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (IOException e) {
			QueryMaster.logger.error("error executing GET request");
			e.printStackTrace();
		}
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = null;
		try {
			body = handler.handleResponse(response);
		} catch (IOException e) {
			QueryMaster.logger.error("error reading response");
			e.printStackTrace();
		}
		int code = response.getStatusLine().getStatusCode();
		QueryMaster.logger.info("server response code: " + code);
		result = body;
	}
}

class SearchWorkerThread extends Thread {
	private String result = null;
	private String server = null;
	private String query = null;
	private String mode = null;
	
	SearchWorkerThread(String server, String query, String mode) {
		this.server = server;
		this.query = query;
		this.mode = mode;
	}
	
	public String result() {
		return result;
	}
	
	@Override
	public void run() {
		QueryMaster.logger.info("fetch from worker server: " + server);
		// Get
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = null;
		try {
			url = "http://" + server + "/" + QueryGlobal.pathContextWorker + "/" + QueryGlobal.pathWorker + "?" 
					+ QueryGlobal.paraMode + "=" + mode + "&"
					+ QueryGlobal.paraSearch + "=" + URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			QueryMaster.logger.error("error encoding query");
			e.printStackTrace();
		}
		QueryMaster.logger.info(url);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (IOException e) {
			QueryMaster.logger.error("error executing GET request");
			e.printStackTrace();
		}
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = null;
		try {
			body = handler.handleResponse(response);
		} catch (IOException e) {
			QueryMaster.logger.error("error reading response");
			e.printStackTrace();
		}
		int code = response.getStatusLine().getStatusCode();
		QueryMaster.logger.info("server response code: " + code);
		result = body;
	}
}

public class QueryMaster {
	public static final Logger logger = Logger.getLogger(QueryMaster.class);
	private static String loggerPath = QueryGlobal.logMaster;
	private static String[] servers = null;
	private static Storage storage = null;

	public static void initialize(String[] nodes, String pathDatabase) {
		// Logging
		logger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
		logger.setAdditivity(false);
		logger.setLevel(Level.ERROR);
		try {
			logger.addAppender(new FileAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), loggerPath, true));
		} catch (IOException e) {
			logger.error("cannot open log file");
		}
		
		// Servers
		servers = nodes;
		logger.info("initialize query client, server count: " + servers.length);
		for (String server : servers) {
			logger.info("register server: " + server);
		}
		
		// Hash
		SHA1Partition.setRange(servers.length);
		
		// Storage
		logger.info("connect to database: " + pathDatabase);
		try {
			Storage.setEnvPath(pathDatabase, true);
			storage = Storage.getInstance(StorageGlobal.tableTitle);
		} catch (Exception e) {
			logger.error("failure open database", e);
		}
	}
	
	public static Map<Integer, List<DocumentInfo>> search(String query, String mode) throws IOException {
		logger.info("search query: " + query);
		String[] results = new String[servers.length];
		Thread[] threads = new Thread[servers.length];
		for (int i = 0; i < servers.length; i ++) {
			threads[i] = new SearchWorkerThread(servers[i], query, mode);
			logger.error("start thread #" + i);
			threads[i].start();
		}
		for (int i = 0; i < servers.length; i ++) {
			try {
				logger.error("join with thread #" + i);
				threads[i].join();
			} catch (InterruptedException e) {
				logger.error("join interrupted");
				e.printStackTrace();
			}
			results[i] = ((SearchWorkerThread)threads[i]).result();
		}
		// Rank
		Map<Integer, List<DocumentInfo>> ret;
		if(mode.equals(QueryGlobal.modeSearch)) ret = DocumentRanker.rankDocument(storage, results);
		else if (mode.equals(QueryGlobal.modeImage)) ret = ImageRanker.rankDocument(results);
		else ret = null;
		return ret;
	}
	
	public static String[] get(String table, String key) throws IOException {
		logger.info("get table | key: " + table + " | " +  key);
		String[] ret = new String[servers.length];
		Thread[] threads = new Thread[servers.length];
		for (int i = 0; i < servers.length; i ++) {
			threads[i] = new GetWorkerThread(servers[i], table, key);
			logger.error("start thread #" + i);
			threads[i].start();
		}
		for (int i = 0; i < servers.length; i ++) {
			try {
				logger.error("join with thread #" + i);
				threads[i].join();
			} catch (InterruptedException e) {
				logger.error("join interrupted");
				e.printStackTrace();
			}
			ret[i] = ((GetWorkerThread)threads[i]).result();
		}
		return ret;
	}
}

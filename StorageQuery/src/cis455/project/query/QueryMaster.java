package cis455.project.query;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

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
import cis455.project.storage.Storage;
import cis455.project.storage.StorageGlobal;

public class QueryMaster {
	public static final Logger logger = Logger.getLogger(QueryMaster.class);
	private static String loggerPath = QueryGlobal.logMaster;
	private static String[] servers = null;
	private static Storage storage = null;

	public static void initialize(String[] nodes, String pathDatabase) {
		// Logging
		logger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
		logger.setAdditivity(false);
		logger.setLevel(Level.INFO);
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
	
	public static List<DocumentInfo> search(String query) throws IOException {
		logger.info("search query: " + query);
		String[] ret = new String[servers.length];
		for (int i = 0; i < servers.length; i ++) {
			String server = servers[i];
			logger.info("fetch from worker server: " + server);
			// Get
			CloseableHttpClient httpclient = HttpClients.createDefault();
			String url = "http://" + server + "/" + QueryGlobal.pathContextWorker + "/" + QueryGlobal.pathWorker + "?" + QueryGlobal.paraSearch + "=" + URLEncoder.encode(query, "UTF-8");
			logger.info(url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			int code = response.getStatusLine().getStatusCode();
			logger.info("server response code: " + code);
			ret[i] = body;
		}
		// Process
		List<DocumentInfo> results = DocumentRanker.rankDocument(storage, ret);
		return results;
	}
	
	public static String get(String table, String key) throws IOException {
		// Hash
		int index = SHA1Partition.getWorkerIndex(key);
		String server = servers[index];
		logger.info("query storage: " + table + " | " + key);
		logger.info("fetch from storage server: " + index + " | " + server);
		
		// Get
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = "http://" + server + "/" + QueryGlobal.pathContextWorker + "/" + QueryGlobal.pathWorker + "?" + QueryGlobal.paraTable + "=" + table + "&" + QueryGlobal.paraKey + "=" + key;
		logger.info(url);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpGet);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = handler.handleResponse(response);
		int code = response.getStatusLine().getStatusCode();
		logger.info("server response code: " + code);
		
		// Process
		return body;
	}
}

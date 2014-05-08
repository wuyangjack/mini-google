package cis455.project.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.google.api.services.samples.youtube.cmdline.youtube_cmdline_search_sample.Search;

import cis455.project.amazon.Item;
import cis455.project.amazon.ItemSearchTool;
import cis455.project.youtube.YoutubeItem;

public class UIWorker {
	public static final Logger logger = Logger.getLogger(UIWorker.class);
	private static String loggerPath = "/tmp/UI.log";
	
	static {
		logger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
		logger.setAdditivity(false);
		logger.setLevel(Level.INFO);
		try {
			logger.addAppender(new FileAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), loggerPath, true));
		} catch (IOException e) {
			logger.error("cannot open log file");
		}
	}
	
	
	
	public static String filter(String query) {
		if (query == null) return null;
		query = query.replaceAll("\\W+", " ");
		query = query.toLowerCase();
		query = query.trim();
		if(query.length() == 0){
			return null;
		} else {
			return query;
		}
	}
	
	public static String wikipedia(String query) {
		String[] query_split_list = query.split("\\s"); 
		String wiki_string = query_split_list[0];
	    for(int i = 1; i < query_split_list.length; i++){
	    	wiki_string = wiki_string + "+" + query_split_list[i];
	    }
		wiki_string = "https://www.wikipedia.org/search-redirect.php?family=wikipedia&search=" + wiki_string + "&language=en&go=++%E2%86%92++&go=Go";
		return wiki_string;
	}
	
	public static List<Item> amazon(String query) {
		ItemSearchTool amazon_tool = new ItemSearchTool();
	    amazon_tool.fetch(query);
	    return amazon_tool.getItems();
	}
	
	@SuppressWarnings("static-access")
	public static YoutubeItem youtube(String query) {
		Search youtube_tool = new Search();
		YoutubeItem youtube_result = new YoutubeItem();
		youtube_result.parse(youtube_tool.search(query));
		return youtube_result;
	}

	public static String search(String query) {
		// Get
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = null;
		try {
			url = UIGlobal.searchURL + URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("error encoding query");
			e.printStackTrace();
		}
		logger.info(url);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (IOException e) {
			logger.error("error executing GET request");
			e.printStackTrace();
		}
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = null;
		try {
			body = handler.handleResponse(response);
		} catch (IOException e) {
			logger.error("error reading response");
			e.printStackTrace();
		}
		int code = response.getStatusLine().getStatusCode();
		logger.info("server response code: " + code);
		return body;
	}
}

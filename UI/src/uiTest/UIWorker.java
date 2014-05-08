package uiTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

public class UIWorker {
	public static final Logger logger = Logger.getLogger(UIWorker.class);
	private static String loggerPath = "/tmp/UI.log";
	private static final String searchURL = "http://ec2-54-221-87-56.compute-1.amazonaws.com:8080/master/query?mode=search&query=";
	
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

	public static String search(String query) {
		// Get
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = null;
		try {
			url = searchURL + URLEncoder.encode(query, "UTF-8");
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

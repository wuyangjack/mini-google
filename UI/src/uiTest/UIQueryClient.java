package uiTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
/*
public class UIQueryClient {
	String search() {
		// Get
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = null;
		"http://ec2-54-221-87-56.compute-1.amazonaws.com:8080/master/query?mode=search&query=university+penn"
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
*/
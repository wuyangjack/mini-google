package edu.upenn.cis455.mapreduce.master;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class MasterClient {
	
	public final static String CRLF = "\r\n";
	public final static String RUNREDUCE = "/CrawlerWorker/runreduce";
	public final static String RUNMAP = "/CrawlerWorker/runmap";
	public final static String RUNPUSH = "/CrawlerWorker/runpush";
	public final static String TERMINATE = "/CrawlerWorker/terminate";
	
	public MasterClient() {
		BasicConfigurator.configure();
		Logger.getLogger("org.apache.http").setLevel(org.apache.log4j.Level.OFF);
	}
	
	public void sendReducePost(JobStatus jobStatus) throws ClientProtocolException, IOException {
		// 1. Get the parameter
		// /level + 2
		String inputDir = jobStatus.getInputDirectory();
		// /level
		int numThreads = jobStatus.getNumReducers();
		Map<String, Boolean> map = jobStatus.getWorkers();
		
		// 2. Construct Parameter
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("inputdir", inputDir));
		formparams.add(new BasicNameValuePair("numThreads", String.valueOf(numThreads)));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
		
		// 3. send parameter
		for(String ip_port : map.keySet()) {
			String url = "http://" + ip_port + RUNREDUCE;
			//LogClass.info("Run Reduce: " + url);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			post.setEntity(entity);
			httpclient.execute(post);
			httpclient.close();
		}
	}
	
	public void sendPushPost(JobStatus jobStatus) throws ClientProtocolException, IOException {
		Map<String, Boolean> map = jobStatus.getWorkers();
		for(String ip_port : map.keySet()) {
			String url = "http://" + ip_port + RUNPUSH;
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			httpclient.execute(post);
			httpclient.close();
		}
	}
	
	public void sendTerminate(JobStatus jobStatus) throws URISyntaxException, ClientProtocolException, IOException {
		Map<String, Boolean> map = jobStatus.getWorkers();
		for(String ip_port : map.keySet()) {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			URI uri = new URIBuilder()
	        .setScheme("http")
	        .setHost(ip_port)
	        .setPath(TERMINATE)
	        .build();
			HttpGet httpGet = new HttpGet(uri);
			httpclient.execute(httpGet);
			httpclient.close();
		}
	}
	
	public void sendMapPost(JobStatus jobStatus) throws ClientProtocolException, IOException {
		// 1. Get the parameter
		// /level
		String inputDir = jobStatus.getInputDirectory();
		int iteration = jobStatus.getIterativeNum();
		int numThreads = jobStatus.getNumMappers();
		Map<String, Boolean> map = jobStatus.getWorkers();
		
		// 2. Construct Parameter
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("inputdir", inputDir));
		formparams.add(new BasicNameValuePair("iteration", String.valueOf(iteration)));
		formparams.add(new BasicNameValuePair("numThreads", String.valueOf(numThreads)));
		formparams.add(new BasicNameValuePair("numWorkers", String.valueOf(map.size())));
		int i = 0;
		for(String worker : map.keySet()) {
			formparams.add(new BasicNameValuePair("worker"+i, worker));
			LogClass.info("worker" + i + ": " + worker);
			i++;
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
		
		// 3. send parameter
		for(String ip_port : map.keySet()) {
			String url = "http://" + ip_port + RUNMAP;
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			post.setEntity(entity);
			httpclient.execute(post);
			httpclient.close();
		}
	}
}

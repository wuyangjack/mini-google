package edu.upenn.cis455.mapreduce.worker;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.TimerTask;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import crawler.WebCrawler;

// Periodic Send to workstatus
public class PeriodicSender extends TimerTask {//implements Runnable {
	
	    private CloseableHttpClient httpclient;
		private String ip_port;
		private WorkerInfo worker_info;
		private WebCrawler crawler;
		
		// initial Sender. Master ip, hostname, and port
		PeriodicSender(WorkerInfo worker_info, WebCrawler crawler) throws UnknownHostException {
			this.worker_info = worker_info;
			this.ip_port = worker_info.getMasterIpPort();
			this.crawler = crawler;
		}
		
		public void run() {
			LogClass.info("Ip_Port: " + ip_port);
			try {
				sendNow();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			while(true) {
				try {
					sendNow();
					// 3. every 10 seconds send one time
					Thread.sleep(10000);
				} catch (Exception e) {
					LogClass.info("Periodic Send Error");
					if(httpclient != null) {
						try {
							httpclient.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
			*/
		}
		
		 /**
		   * Send Immedietely to master
		   * @throws IOException
		   */
		  public void sendNow() throws IOException {
				try {
					httpclient = HttpClients.createDefault();
					URI uri = new URIBuilder()
			        .setScheme("http")
			        .setHost(ip_port)
			        .setPath(Info.WORKERSTATUS)
			        .setParameter("port", String.valueOf(worker_info.getPort()))
					.setParameter("status", worker_info.getStatus())
			        .setParameter("count", String.valueOf(crawler.getCount()))
			        .setParameter("keysRead", String.valueOf(worker_info.getKeysRead()))
			        .setParameter("keysWritten", String.valueOf(worker_info.getKeysWritten()))
			        .build();
					HttpGet httpGet = new HttpGet(uri);
					httpclient.execute(httpGet);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					httpclient.close();
				}
		  }
}
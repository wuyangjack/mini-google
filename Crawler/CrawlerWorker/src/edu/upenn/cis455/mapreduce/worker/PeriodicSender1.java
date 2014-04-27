package edu.upenn.cis455.mapreduce.worker;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import crawler.WebCrawler;

public class PeriodicSender1 implements Runnable {
    private Socket s;
	private String ip;
	private String host;
	private int serverport;
	private String url;
	private PrintStream periodic_send = null;
	private WorkerInfo worker_info;
	private WebCrawler crawler;
	
	// initial Sender. Master ip, hostname, and port
	PeriodicSender1(WorkerInfo worker_info, WebCrawler crawler) throws UnknownHostException {
		this.worker_info = worker_info;
		this.crawler = crawler;
		String[] master = worker_info.getMasterIpPort().split(":");
		// ip address, hostname, serverport
		ip = master[0];
		//host = InetAddress.getByName(ip).getHostName();
		host = master[0];
		serverport = Integer.parseInt(master[1]);
		url = "http://" + worker_info.getMasterIpPort() + Info.WORKERSTATUS;
	}
	
	public void run() {
		while(true) {
			try {
				// 1. create socket to master server
				s = new Socket(ip, serverport);
				periodic_send = new PrintStream(s.getOutputStream());
				// 2. construct the request message, its listening port
				String postMessage = "?port=" + String.valueOf(worker_info.getPort()) + "&status=" + worker_info.getStatus() + 
						"&count=" + crawler.getCount() + "&keysRead=" + String.valueOf(worker_info.getKeysRead()) 
						+ "&keysWritten=" + String.valueOf(worker_info.getKeysWritten());
				StringBuffer sb = new StringBuffer();
				sb.append("GET " + url + postMessage + " HTTP/1.1" + Info.CRLF);
				sb.append("Host: " + host + Info.CRLF + Info.CRLF);
				LogClass.info("Worker: " + worker_info.getPort() + "Send periodic message: " + postMessage);
				periodic_send.print(sb.toString());
				periodic_send.flush();
				// 3. every 10 seconds send one time
				Thread.sleep(10000);
			} catch (Exception e) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
				if(s != null)
					try {
						s.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
				e.printStackTrace();
			}
		}
	}
}

package old;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import edu.upenn.cis455.mapreduce.master.JobStatus;


public class WebClient {
	
	public final static String CRLF = "\r\n";
	public final static String RUNREDUCE = "/worker/runreduce";
	public final static String RUNMAP = "/worker/runmap";
	
	
	public void sendReducePost(String job, JobStatus jobStatus) {
		String outputDir = jobStatus.getInputDirectory();
		int numThreads = jobStatus.getNumReducers();
		Map<String, Boolean> map = jobStatus.getWorkers();
		
		
		String postMessage = "job=" + job + "&output=" + outputDir + 
				"&numThreads=" + numThreads;
		String requestHeader = "POST " + RUNREDUCE + " HTTP/1.1" + CRLF + "Content-Type: " + "application/x-www-form-urlencoded" + CRLF
				+ "Content-Length: " + postMessage.length() + CRLF;
		
		// send message
		send(requestHeader, postMessage, map);
	}
	
	public void sendMapPost(String job, JobStatus jobStatus, String seq_num) {
		// get init param
		int numThreads = jobStatus.getNumReducers();
		Map<String, Boolean> map = jobStatus.getWorkers();
		int numWorkers = map.size();
		
		// create post message
		String postMessage = "job=" + job + "&seqnum=" + seq_num +
				"&numThreads=" + numThreads + "&numWorkers=" + numWorkers;
		StringBuffer sb = new StringBuffer();
		sb.append(postMessage);
		int i = 1;
		for(String worker : map.keySet()) {
			sb.append("&worker" + i + "=" + worker);
			i++;
		}
		postMessage = sb.toString();
		
		// create header message
		String requestHeader = "POST " + RUNMAP + " HTTP/1.1" + CRLF  + "Content-Type: " + "application/x-www-form-urlencoded" + CRLF
				+ "Content-Length: " + postMessage.length() + CRLF;
		
		send(requestHeader, postMessage, map);
	}
	
	private void send(String requestHeader, String postMessage, Map<String, Boolean> map) {
		Socket s;
		PrintStream ps;
		// for each worker
		for(String ip_port : map.keySet()) {
			String[] args = ip_port.split(":");
			// Args[0] is ip, Args[1] is port
			try {
				s = new Socket(args[0], Integer.parseInt(args[1]));
				ps = new PrintStream(s.getOutputStream());
				// host
				String host = InetAddress.getByName(args[0]).getHostName();
				// post
				ps.print(requestHeader);
				ps.print("Host: " + host + CRLF + CRLF);
				ps.print(postMessage);
				ps.flush();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

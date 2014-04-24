package general;


import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;


/**
 * A client used to connect to the url and get the document
 */
public class HttpClient {

	public String host = "";
	public InetAddress ip = null;
	public int port;
	public String url;
	public String path;

	/**
	 * Client constructor
	 * @param url The user input url
	 */
	public HttpClient(String url){
		try{
			URL newURL = new URL("http://" + url);
			String host = newURL.getHost();
			int port = newURL.getPort();
			if(port == -1) port = 80;
			ip = InetAddress.getByName(host);

			this.path = newURL.getPath();
			this.host = host;
			
			this.port = port;
			this.url = url;		
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * Post map command to workers
	 * @param body
	 */
	public void postMap(String body){
		try{
			Socket socket = new Socket(ip, port);
			PrintStream out = new PrintStream(socket.getOutputStream());
			out.print(this.postMapReq(body));
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * Request to get the file content.
	 * @param path
	 * @return
	 */
	public String postMapReq(String body){
		String req = "POST " + "/worker/runmap" + "?" + body + " HTTP/1.1";
		req += "\r\n" + "Host: " + host;
		req += "\r\n" + "Content-Length:" + body.getBytes().length;
		req += "\r\n" + "Connection: close";
		req += "\r\n\r\n";
		req += body + "\r\n";
		return req;
	}
	
	/**
	 * Post reduce command to workers
	 * @param body
	 */
	public void postReduce(String body){
		try{
			Socket socket = new Socket(ip, port);
			PrintStream out = new PrintStream(socket.getOutputStream());
			out.print(this.postReduceReq(body));
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Construct reduce command to be sent
	 * @param body
	 * @return
	 */
	public String postReduceReq(String body){
		String req = "POST " + "/worker/runreduce?" + body + " HTTP/1.1";
		req += "\r\n" + "Host: " + host + ":" + port;
		req += "\r\n" + "Content-Length:" + body.getBytes().length;
		req += "\r\n" + "Connection: close";
		req += "\r\n\r\n";
		req += body + "\r\n";
		return req;
	}
	
	/**
	 * Get the report message to master
	 * @param queryString
	 */
	public void reportToMaster(String queryString){
		try{
			Socket socket = new Socket(ip, port);
			PrintStream out = new PrintStream(socket.getOutputStream());
			out.print(reportReq(queryString));	
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Construct the report message
	 * @param queryString
	 * @return
	 */
	public String reportReq(String queryString){
		String req = "GET " + "/master/workerstatus?" + queryString + " HTTP/1.1";
		req += "\r\n" + "Host: " + host;
		req += "\r\n" + "Connection: close";
		req += "\r\n\r\n";
		return req;		
	}
	
	/**
	 * Send push data command to other workers
	 * @param body
	 */
	public void pushdata(String body){
		try{
			Socket socket = new Socket(ip, port);
			PrintStream out = new PrintStream(socket.getOutputStream());
			out.print(pushdataReq(body));
			socket.close();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Construct the push data command
	 * @param body
	 * @return
	 */
	private String pushdataReq(String body){
		String req = "POST " + "/worker/pushdata" + " HTTP/1.1";
		req += "\r\n" + "Host: " + host;
		req += "\r\n" + "Content-Length:" + body.getBytes().length;
		req += "\r\n" + "Connection: close";
		req += "\r\n\r\n";
		req += body + "\r\n";
		return req;
	}
	


}

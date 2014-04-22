package crawler;

import general.LogClass;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import crawldatatype.HeadNode;


/**
 * One WebClient have one socket
 * @author ChenyangYu
 *
 */
public class WebClient {
	
	public final static String CRLF = "\r\n";
	public final static String PATTERN = ".*text/html.*";
	public final static String PATTERN1 = "http/1.[01] 200 ok";
	public final static String PATTERN2 = "http/1.[01] 304 not modified";
	public final static String PATTERN3 = "http/1.[01] 200";
	public final static String PATTERN4 = "http/1.[01] 304";
	
	private Socket s;
	
	/**
	 * Initial request header and also creat the socket
	 * @param urlPath
	 * @param date
	 * @param head
	 * @return
	 * @throws MalformedURLException
	 */
	private String initRequest(String urlPath, String date, String head) throws MalformedURLException {
		URL url;
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException e) {
			url = new URL("http://" + urlPath);
		}
		try {
			// get the host name
			String host = url.getHost().equals("") ? "localhost" : url.getHost();
			// get the port
			int port = url.getPort() != -1 ? url.getPort() : 80;
			
			InetSocketAddress address = new InetSocketAddress(host, port);
			s = new Socket();
			s.connect(address, 2000);
			
			// Creat the request
			String message = head + " " + url.toString() + " HTTP/1.1" + CRLF + 
					"Host: " + host + CRLF +
					"User-Agent: cis455crawler" + CRLF +
					(date == null ? "" : "If-Modified-Since: " + date + CRLF) + CRLF;
			
			LogClass.info("Request Header: " + message);
			return message;
		} catch(Exception e) {
			return "";
		}
	}
	
	/**
	 * Test header by give urlPath and date
	 * @param urlPath
	 * @param date
	 * @return
	 */
	public HeadNode inspectHeader(String urlPath, String date) {
		try {
			String message = initRequest(urlPath, date, "HEAD");
			PrintStream ps = new PrintStream(s.getOutputStream(),true, "UTF-8");
			BufferedReader readRequest = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			// Creat the request
			// Send message to Server
			ps.print(message);
			ps.flush();
			// Get the response
			String response = "";
			int state = 2;
			boolean typeValid = false;
			int message_length = -1;
			String location = null;
			s.setSoTimeout(2000);
			while((response =readRequest.readLine())!=null) {
				LogClass.info("Header Response(head): " + response);
				response = response.toLowerCase().trim();
				if(response.equals(""))
					break;
				// if it is not modified
				else if(response.matches(PATTERN2) || response.matches(PATTERN4)) {
					state = 1;
				}
				// if type is ok
				else if(response.matches(PATTERN1) || response.matches(PATTERN3)) {
					state = 0;
				}
				else if(response.startsWith("content-length")) {
					String len = response.substring(response.indexOf(":")+1).trim();
					message_length = Integer.parseInt(len);
				}
				else if(response.startsWith("content-type")) {
					String type = response.substring(response.indexOf(":")+1).trim();
					if(type.matches(PATTERN))
						typeValid = true;
				}
				else if(response.startsWith("location")) {
					location = response.substring(response.indexOf(":")+1).trim();
				}
				s.setSoTimeout(2000);
			}
			return new HeadNode(state, typeValid, message_length, location);
		} catch(Exception e) {
			LogClass.info("Inspect Header Error");
			return null;
		}
	}
	
	/**
	 * Download the HTML page by given the url
	 * @param urlPath the path of download path
	 * @return the download string, if it is null, means not modified
	 * @throws Exception
	 */
	public String download(String urlPath) throws Exception {
		try {
			String message = initRequest(urlPath, null, "GET");
			PrintStream ps = new PrintStream(s.getOutputStream());
			DataInputStream readRequest = new DataInputStream(s.getInputStream());
			// Creat the request
			// Send message to Server
			ps.print(message);
			ps.flush();
			// Get the response
			String response = "";
			boolean isChunk = false;
			int message_length = 0;
			s.setSoTimeout(2000);
			while((response = read(readRequest))!=null) {
				// Header is over
				response = response.toLowerCase().trim();
				LogClass.info("Header Response(download): " + response);
				if(response.equals(""))
					break;
				// if it is chunked case, we need to handle that
				else if(response.startsWith("transfer-encoding:")) {
					String encoding = response.substring(response.indexOf(":")+1).trim();
					if(encoding.equals("chunked"))
						isChunk = true;
				}
				// if it is not, we need to get the content-length
				else if(response.startsWith("content-length")) {
					String len = response.substring(response.indexOf(":")+1).trim();
					message_length = Integer.parseInt(len);
				}
				s.setSoTimeout(2000);
			}
			response = getMessage(isChunk, message_length, readRequest);
			//LogClass.info("Response Message: " + response);
			return response;
		} catch(Exception e) {
			return "";
		}
	}
	
	private String read(DataInputStream readRequest) throws IOException {
		List<Byte> list = new ArrayList<Byte>();
		byte[] b = new byte[2];
		while(true) {
			b[0] =  readRequest.readByte();
			list.add(b[0]);
			// The line only have \n or have \r\n
			if((list.size() == 1 && b[0] == 0xa) || (list.size() == 2 && b[0] == 0xa && b[1] == 0xd))
				break;
			// The line end with \r\n
			if(list.size() > 2 && b[0] == 0xa && b[1] == 0xd) {
				list.remove(list.size()-1);
				list.remove(list.size()-1);
				break;
			}
			// The line end with \n
			if(list.size() > 2 && b[0] == 0xa) {
				list.remove(list.size() -1);
				break;
			}
			b[1] = b[0];
		}
		byte[] m = new byte[list.size()];
		int k = 0;
		for(Byte bb : list) {
			m[k++] = bb;
		}
		list = null;
		return new String(m);
	}
	
	/**
	 * 
	 * @param isChunk
	 * @param message_length
	 * @param readRequest reader
	 * @return the download string
	 * @throws IOException
	 */
	// Get the Document contents
	private String getMessage(boolean isChunk, int message_length, DataInputStream readRequest) throws IOException {
		String responseMessage = "";
		
		// if it is not chunk
		if(! isChunk) {
			if(message_length != 0) {
				byte[] m = new byte[message_length];
				s.setSoTimeout(1000);
				// get the data
				
				for(int i = 0; i < message_length; i++) {
					m[i] = readRequest.readByte();
					s.setSoTimeout(1000);
				}
				responseMessage = new String(m, "UTF-8");
			}
			else {
				ArrayList<Byte> list = new ArrayList<Byte>();
				byte b;
				try {
					s.setSoTimeout(1000);
					while(true) {
						b = readRequest.readByte();
						list.add(b);
						s.setSoTimeout(1000);
					}
				} catch(Exception e) {
					LogClass.error("EOF error");
				} finally {
					// we continue to read what we have read yet
					byte[] m = new byte[list.size()];
					int k = 0;
					for(Byte bb : list) {
						m[k++] = bb;
					}
					responseMessage = new String(m, "UTF-8");
				}
			}
		}
		// chunked read
		else {
			try {
				message_length = 0;
				ArrayList<Byte> list;
				int f = 0;
				byte[] b = new byte[2];
				while(true) {
					// we don't know the length
					list = new ArrayList<Byte>();
					// we check the two recent byte if it is CRLF, we stop(need to read the length)
					b = new byte[2];
					boolean ff = true;
					s.setSoTimeout(1000);
					while((message_length !=0 || f % 2 == 0)) {
						b[0] =  readRequest.readByte();
						// if it is the length line, and we read ; then ignore the next readings
						if(f%2 == 0 && b[0] == 0x3b)
							ff = false;
						// if it is the length line and we read the CRLF, break
						if(f%2 == 0 && b[0] == 0xa && b[1] == 0xd) {
							if(ff)
								list.remove(list.size()-1);
							break;
						}
						// if we read is data line
						if(f%2 == 1 && message_length <=0) {
							message_length = 0;
							break;
						}
						b[1] = b[0];
						if(ff)
							list.add(b[0]);
						message_length--;
						s.setSoTimeout(1000);
					}
					// if it is the length line, we got the message_length
					if(f % 2 == 0) {
						message_length = getInt(list);
						if(message_length == 0)
							break;
					}
					// else it is the data line
					else {
						// first read the CRLF or LF
						s.setSoTimeout(1000);
						while(readRequest.readByte() != 0xa)
							s.setSoTimeout(1000);
						// Get the String
						byte[] m = new byte[list.size()];
						int k = 0;
						for(Byte bb : list) {
							m[k++] = bb;
						}
						responseMessage += new String(m, "UTF-8");
						message_length = 0;
					}
					f++;
				}
			} catch(Exception e) {
				LogClass.error("Chunked Eof Error");
			} 
		}
		LogClass.info("Message Return");
		return responseMessage == null ? responseMessage : responseMessage.trim();
	}
	
	/**
	 * 
	 * @param list
	 * @return integer of the byte
	 */
	// Transfer a list of byte to integer
	private int getInt(ArrayList<Byte> list) {
		byte[] m = new byte[list.size()];
		int k = 0;
		// First transfer byte list to byte array
		for(Byte bb : list) {
			m[k++] = bb;
		}
		// transfer to string
		String ss = new String(m);
		LogClass.info("Chunk Size: " + ss);
		// use integer parse int 16
		return Integer.parseInt(ss, 16);
	}
}

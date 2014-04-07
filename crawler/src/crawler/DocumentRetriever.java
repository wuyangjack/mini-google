package crawler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

/**
 * Transfer HTML data to Document
 * @author ChenyangYu
 *
 */
public class DocumentRetriever {
	
	// Html DOM
	Document doc;
	// Download Client
	WebClient client;
	// Html Raw data
	String response;
	// Html Path
	String htmlPath;
	
	public DocumentRetriever(String htmlPath) throws Exception {
		this.htmlPath = htmlPath.trim();
		client = new WebClient();
	}
	
	public DocumentRetriever(String htmlPath, WebClient client) throws Exception {
		this.htmlPath = htmlPath.trim();
		this.client = client;
	}
	
	public void setPath(String htmlPath) {
		this.htmlPath = htmlPath.trim();
	}
	
	/**
	 * Download the html file
	 * @return Document of dom tree
	 * @throws Exception
	 */
	public Document retrieve() throws Exception {
		// Need to Add
		response = null;
		response = client.download(htmlPath);
		doc = convertToHTML(response);
		if(doc != null)
			doc.getDocumentElement().normalize();
		return doc;
	}
	
	/**
	 * 
	 * @param htmlString the html raw string
	 * @return Dom
	 */
	public Document convertToHTML(String htmlString){        
        //ByteArrayOutputStream xhtmlByteOutStream = new ByteArrayOutputStream();   
		try {
	        if (htmlString != null && !htmlString.equals("")){
	            // Convert HTML to XHTML using JTidy API
	        	Tidy tidy = new Tidy();
	    	    tidy.setInputEncoding("UTF-8");
	    	    tidy.setOutputEncoding("UTF-8");
	    	    tidy.setWraplen(Integer.MAX_VALUE);
	    	    tidy.setSmartIndent(true);  
	    	    // Don't show message
	    	    tidy.setQuiet(true);
	    	    tidy.setShowWarnings(false);
	    	    tidy.setShowErrors(0);
	    	    // valid html output
	    	    tidy.setMakeClean(true);
	          
	            Document doc = tidy.parseDOM(new ByteArrayInputStream(htmlString.getBytes()), null);
	            return doc;
	        }
		} catch(Exception e) {
			return null;
		}
        return null;
	}
	
	public String getResponse() {
		return response;
	}
	
	public String getRawHTML(String htmlPath) throws MalformedURLException {
		URL url = new URL(htmlPath);
		URLConnection conn;
		InputStream is = null;
		try {
			conn = url.openConnection();
			is = conn.getInputStream();
		} catch (IOException e1) {
			//LogClass.error("getRawHTML IOException");
		}
		byte[] buffer = new byte[1024];
		String result = "";
		int read = -1;
		try {
			while((read = is.read(buffer)) > 0) {
				result += new String(buffer, 0, read);
			}
		} catch (IOException e) {
			//LogClass.error("GetRawHTML IOException");
		}
		return result;
	}
	
	
	public static void main(String[] args) throws Exception {
		//DocumentRetriever dr = new DocumentRetriever("https://www.yahoo.com/");
		//Document doc = dr.retrieve();
		/*
		String nodename = doc.getDocumentElement().getNodeName();
		System.out.println(doc.getDocumentElement().getNodeName());
		String content = dr.getResponse();
		//System.out.println(dr.getResponse());
		//int index = content.indexOf(nodename);
		int index = content.lastIndexOf("?>");
		content = content.substring(index+2);
		System.out.println(content);
		*/
	}
	
}

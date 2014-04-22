package crawldatatype;

import org.jsoup.nodes.Document;

public class CrawlData {
	private String response;
	private Document doc;
	
	public CrawlData(String response, Document doc) {
		this.response = response;
		this.doc = doc;
	}
	
	public String getResponse() { return response; }
	
	public Document getDocument() { return doc; }
}
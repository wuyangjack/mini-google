package storage;

public class HtmlDocument {
	
	private String url;
	private String htmlData;
	private String date;
	
	public HtmlDocument() {
		
	}
	
	public HtmlDocument(String url, String htmlData, String date) {
		this.url = url;
		this.htmlData = htmlData;
		this.date = date;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setHtmlData(String htmlData) {
		this.htmlData = htmlData;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public String getHtmlData() {
		return htmlData;
	}

	public String getDate() {
		return date;
	}
	
}

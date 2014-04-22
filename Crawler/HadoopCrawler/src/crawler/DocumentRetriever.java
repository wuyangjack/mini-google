package crawler;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import crawldatatype.CrawlData;


public class DocumentRetriever {

	private final static String USERAGENT = "cis455crawler"; 
	private final static String NONENGPAGE = "^.*(%[A-Fa-f0-9][A-Fa-f0-9])+.*$";
	
	/**
	 * Download the html file
	 * @return Document of dom tree
	 * @throws Exception
	 */
	public CrawlData retrieve(String contextUrl) throws Exception {
		if(contextUrl.matches(NONENGPAGE))
			return null;
		WebClient client = new WebClient();
		String response = null;
		Document doc = null;
		response = client.download(contextUrl);
		doc = Jsoup.parse(response, contextUrl);
		return new CrawlData(response, doc);
	}
	
	public CrawlData retrieveData(String contextUrl) throws IOException {
		try {
			if(contextUrl.matches(NONENGPAGE))
				return null;
			Connection connection = Jsoup.connect(contextUrl).userAgent(USERAGENT).header("Accept-Language", "en");
			Document doc = connection.get();
			Connection.Response response = connection.response();
			String res = response.body();
			return new CrawlData(res, doc);
		} catch(Exception e) {
			return null;
		}
	}
	
	
	
	public static void main(String[] args) throws Exception{
		CrawlData cd = new DocumentRetriever().retrieve("http://ar.wikipedia.org/wiki/%D8%A8%D9%88%D8%A7%D8%A8%D8%A9:%D9%85%D8%AD%D8%AA%D9%88%D9%8A%D8%A7%D8%AA");
		if(cd == null)
			System.out.println(false);
		else {
			String title = cd.getDocument().title();
			System.out.println("Title: " + title);
		}
	}
}

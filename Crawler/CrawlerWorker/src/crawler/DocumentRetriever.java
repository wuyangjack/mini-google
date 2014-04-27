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
		if(contextUrl.matches(NONENGPAGE) || contextUrl.contains("#"))
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
			if(contextUrl.matches(NONENGPAGE) || contextUrl.contains("#"))
				return null;
			Connection connection = Jsoup.connect(contextUrl)
					.userAgent(USERAGENT).header("Accept-Language", "en").timeout(5*1000);
			Document doc = connection.get();
			Connection.Response response = connection.response();
			String res = response.body();
			return new CrawlData(res, doc);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/*
	public static void main(String[] args) throws Exception{
		BerkerleyDB.setEnvPath("/Users/ChenyangYu/Documents/Upenn/storage1/berkeleydb");
		String url = "http://www.foxnews.com/on-air/personalities/michael-baden/bio/";
		WebClient wc = new WebClient();
		wc.inspectHeader(url, null);
		BerkerleyDB database = BerkerleyDB.getInstance();
		RobotsParser rp = new RobotsParser(database);
		rp.downloadRobot(url);
		RobotText rt = rp.getRobot(url);
		System.out.println(rp.allowUrl(rt, url));
		CrawlData cd = new DocumentRetriever().retrieveData(url);
		if(cd == null)
			System.out.println(false);
		else {
			String title = cd.getDocument().title();
			//String title = "Hadoop Tutorial - YDN";
			System.out.println("Title: " + title);
			for(int i = 0; i < title.length(); i++) 
				System.out.print(title.codePointAt(i) + " ");
			System.out.println();
			System.out.println(WebCrawler.validTitle(title));
		}
	}
	*/
}

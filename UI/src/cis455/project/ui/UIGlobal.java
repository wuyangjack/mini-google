package cis455.project.ui;

public class UIGlobal {
	public static final Integer pageVolume = 10;
	public static final String delimiterUI = "\t";
	public static final String CRLF = "\r\n";
	public static final String urlMasterSearch = "http://ec2-54-221-87-56.compute-1.amazonaws.com:8080/master/query?mode=search&query=";
	public static final String jspIndex = "/index.jsp";
	public static final String jspResult = "/result.jsp";
	public static final String pathSearch = "search";
	public static final String contextUI = "/ui";
	
	public static final String paraPage = "page";
	public static final String paraAmazon = "amazon";
	public static final String paraYoutube = "youtube";
	public static final String paraWiki = "wiki";
	public static final String paraQuery = "query";
	public static final String paraSessionID = "sessionID";
	
	public static final String attrSearchResult = "result";
	public static final String attrTime = "time";
	public static final String initPathDict = "pathDict";
	
	public static String urlSearchSubmit() {
		return pathSearch + "?" + paraPage + "=1&" + paraWiki + "=1";
	}
}

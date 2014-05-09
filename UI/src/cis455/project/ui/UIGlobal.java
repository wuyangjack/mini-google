package cis455.project.ui;

public class UIGlobal {
	public static final Integer pageVolume = 10;
	public static final String delimiterUI = "\t";
	public static final String CRLF = "\r\n";
	public static final String urlMasterSearchWeb = "http://ec2-54-221-87-56.compute-1.amazonaws.com:8080/master/query?mode=search&query=";
	public static final String urlMasterSearchImage = "http://ec2-54-221-87-56.compute-1.amazonaws.com:8080/master/query?mode=image&query=";
	public static final String jspIndex = "/index.jsp";
	public static final String jspResult = "/result.jsp";
	public static final String pathSearch = "search";
	public static final String contextUI = "/ui";
	
	public static final String paraPage = "page";
	public static final String paraAmazon = "amazon";
	public static final String paraYoutube = "youtube";
	public static final String paraSpellCheck = "spellcheck";
	public static final String paraWiki = "wiki";
	public static final String paraQuery = "query";
	public static final String paraSessionID = "sessionID";
	public static final String paraMode = "mode";
	public static final String paraCatagory = "catagory";

	public static final String modeSearchWeb = "web";
	public static final String modeSearchImage = "image";

	public static final String attrSearchResult = "result";
	public static final String attrTime = "time";
	public static final String initPathDict = "pathDict";
	
	
	public static String urlSearchSubmit(String mode) {
		return pathSearch + "?" + paraMode + "=" + mode + "&" + paraPage + "=1&" + paraWiki + "=1";
	}
	
	public static String urlError() {
		return UIGlobal.contextUI + UIGlobal.jspIndex + "?" + paraMode + "=" + modeSearchWeb;
	}
	
	public static String urlShorten(String url){
		int max_len = 50;
		url = url.substring(7);
		if(url.length() <= max_len){
			return url;
		}

		if(url.charAt(url.length() - 1) == '/')
		url = url.substring(0, url.length() - 1);
		int first_slash = url.indexOf('/');
		int last_slash = url.lastIndexOf('/');
		String domain_name = url.substring(0, first_slash);
		String last_piece = url.substring(last_slash);
		String new_url = domain_name + "/..." + last_piece;
		if(new_url.length() > max_len){
			return new_url.substring(0, max_len) + "...";
		}
		return new_url;
	}
}

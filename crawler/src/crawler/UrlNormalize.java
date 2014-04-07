package crawler;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlNormalize {

	// get absolute url of value, the urlPath is the current url we send request
	public static String getAbsoluteUrl(String value, String urlPath) throws MalformedURLException {
		if(value == null || urlPath == null)
			return value;
		String normalUrl = null;
		try {
			URL url = new URL(value);
			return normalizeUrl(url.toString());
		} catch (MalformedURLException e) {
			normalUrl = "http://" + getHostName(urlPath) + getPath(urlPath);
			if(normalUrl.endsWith("/") && value.startsWith("/"))
				normalUrl = normalUrl + value.substring(1);
			else if(normalUrl.endsWith("/") || value.startsWith("/"))
				normalUrl = normalUrl + value;
			else
				normalUrl = normalUrl + "/" + value;
				
		}
		return normalizeUrl(normalUrl);
	}
	
	public static String normalizeUrl(String value) throws MalformedURLException {
		if(value == null)
			return value;
		URL url;
		try {
			url = new URL(value);
		} catch (MalformedURLException e) {
			value = "http://"+ value;
			url = new URL(value);
		}
		String host = url.getHost();
		String path = url.getFile();
		String fragment = url.getRef();
		return "http://" + host + (path.equals("") ? "/" : path) + (fragment == null ? "" : "#" + fragment);
	
	}
	
	public static String getHostName(String urlPath) throws MalformedURLException {
		URL url;
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException e) {
			url = new URL("http://" + urlPath);
		}
		return url.getHost();
	}
	
	public static String getPath(String urlPath) throws MalformedURLException {
		URL url;
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException e) {
			url = new URL("http://" + urlPath);
		}
		return url.getPath();
	}
	
	public static boolean schemaIsHttp(String urlPath) {
		URL url;
		try {
			url = new URL(urlPath);
			return url.getProtocol().equals("http");
		} catch(MalformedURLException e) {
		}
		return false;
	}
	
	public static void main(String[] args) throws MalformedURLException {
		System.out.println(normalizeUrl("http://example.com:80/docs/books/tutorial"
                           + "/index.html?name=networking#DOWNLOADING"));
		System.out.println(normalizeUrl("www.google.com"));
		System.out.println(normalizeUrl("http://www.sina.com/test%20case"));
		System.out.println(normalizeUrl("http://feeds.wired.com/c/35185/f/661470/s/387cbf77/sc/29/l/0l0swired0n0cwiredscience0c20a140c0a30cwired0espace0ephoto0eday0estar0esurvives0esupernova0eblast0c/story01.htm"));
	}

}

package general;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlNormalizer {

	
	public static String getCanonicalUrl(String context, String urlPath) {
		if(context != null) {
			try {
				new URL(context);
			} catch(Exception e) {
				context = "http://" + context;
			}
			return getAbsoluteUrl(context, urlPath);
		}
		else {
			try {
				new URL(urlPath);
			} catch(Exception e) {
				urlPath = "http://" + urlPath;
			}
			return getAbsoluteUrl(context, urlPath);
		}
		
	}
	/**
	 * Get the abosulote url of urlPath
	 * @param context context url
	 * @param urlPath absolute url or url relative to context
	 * @return
	 */
	public static String getAbsoluteUrl(String context, String urlPath) {
		try {
			URL url;
			if(context == null)
				url = new URL(urlPath);
			else
				url = new URL(new URL(context), urlPath);
			// construct protocol, host, port
			String protocol = url.getProtocol().toLowerCase();
            String host = url.getHost().toLowerCase();
            int port = url.getPort();
            if(port == url.getDefaultPort())
            	port = -1;
            // getAbsolute path
			String path = url.getPath().equals("") ? "/" : getAbsolutePath(url.getPath());
			String query = url.getQuery() == null ? "" : "?" + url.getQuery();
			String fragment = url.getRef() == null ? "" : "#" + url.getRef();
			
			URL normal_url = new URL(protocol, host, port, path+query+fragment);
			String result = normal_url.toExternalForm();
			if(result != null)
				result = result.replaceAll("\\s", "");
			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	private static String getAbsolutePath(String path) throws URISyntaxException {
		URI uri = new URI(path);
		
		path = uri.normalize().toString();
		// replace // to /
        int idx = path.indexOf("//");
        while (idx >= 0) {
                path = path.replace("//", "/");
                idx = path.indexOf("//");
        }
        // trim ../ to null
        while (path.startsWith("/../")) {
                path = path.substring(3);
        }
        
        return path;
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
	
	public static String getFile(String urlPath) throws MalformedURLException {
		URL url;
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException e) {
			url = new URL("http://" + urlPath);
		}
		return url.getFile();
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
		System.out.println(getCanonicalUrl(null, "http://example.com:80/docs/books/tutorial"
                           + "/index.html?name=networking#DOWNLOADING"));
		System.out.println(getCanonicalUrl(null, "www.google.com?test"));
		System.out.println(getCanonicalUrl(null, "http://www.sina.com/test%20case"));
		System.out.println(getCanonicalUrl(null, "http://feeds.wired.com/c/35185/f/661470/s/387cbf77/sc/29/l/0l0swired0n0cwiredscience0c20a140c0a30cwired0espace0ephoto0eday0estar0esurvives0esupernova0eblast0c/story01.htm"));
		System.out.println(getFile(getCanonicalUrl(null, "http://example.com:80/docs/books/tutorial"
                           + "/index.html?name=networking#DOWNLOADING")));
	}

}

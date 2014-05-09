package cis455.project.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cis455.project.amazon.Item;
import cis455.project.youtube.YoutubeItem;

public class SearchResult {
	private String[] titles = null;
	private String[] urls = null;
	private Integer count = null;
	private Integer pages = null;
	private Integer pageCurrent = null;
	private Integer indexStart = null;
	private Integer indexEnd = null;
	private Boolean pageValid = null;
	private String query = null;
	private String queryCheck = null;
	private List<Item> amazonItems = null;
	private YoutubeItem youtubeItems = null;
	private String wikipediaUrl = null;
	private String sessionID = null;
	private String mode = null;
	private String time = null;
	
	SearchResult(String mode, String sessionID, String result, String page, String query, String queryCheck, List<Item> amazonItems, YoutubeItem youtubeItems, String wikipediaUrl) {		
		// Parse result
		String[] lines = result.split(UIGlobal.CRLF);
		List<String> titlesList = new ArrayList<String>();
		List<String> urlsList = new ArrayList<String>();
		UIWorker.logger.info("received lines: " + lines.length);
		for (int i = 0; i < lines.length; i ++ ) {
			UIWorker.logger.info(lines[i]);
			String[] tokens = lines[i].split(UIGlobal.delimiterUI, 2);
			//UIWorker.logger.info("tokens: " + tokens.length);
			if(tokens.length == 2 && mode.equals(UIGlobal.modeSearchWeb)) {
				urlsList.add(tokens[0]);
				titlesList.add(tokens[1]);
			} else if (tokens.length == 2 && mode.equals(UIGlobal.modeSearchImage)) {
				urlsList.add(tokens[0]);
				titlesList.add(tokens[0]);
			} else {
				UIWorker.logger.info("discard invalid line: " + lines[i]);
			}
		}
		count = urlsList.size();
		if (count == 0 && mode.equals(UIGlobal.modeSearchWeb)) {
			urlsList.add("https://www.google.com/");
			titlesList.add("Google");
			urlsList.add("https://www.yahoo.com/");
			titlesList.add("Yahoo!");
			urlsList.add("http://www.bing.com/");
			titlesList.add("Bing");
			count = 3;
		}
		if (count == 0 && mode.equals(UIGlobal.modeSearchImage)) {
			urlsList.add("img/google.jpg");
			titlesList.add("Google");
			urlsList.add("img/yahoo.png");
			titlesList.add("Yahoo!");
			urlsList.add("img/bing.jpg");
			titlesList.add("Bing");
			count = 3;
		}
		UIWorker.logger.info("parsed titles/urls: " + count);
		pages = (int) Math.ceil((double)count / (double)UIGlobal.pageVolume);
		titles = new String[count];
		titles = titlesList.toArray(titles);
		urls = new String[count];
		urls = urlsList.toArray(urls);
		
		// Initalize others
		this.mode = mode;
		this.sessionID = sessionID;
		this.query = query;
		this.queryCheck = queryCheck;
		this.amazonItems = amazonItems;
		this.youtubeItems = youtubeItems;
		this.wikipediaUrl = wikipediaUrl;
		this.setPage(page);
	}
	
	public void setPage(String page) {
		int pageInt = Integer.parseInt(page.trim());
		if (pageInt > pages) {
			this.pageValid = false;
			return;
		} else {
			pageCurrent = pageInt;
			indexStart = UIGlobal.pageVolume * (pageCurrent -1);
			indexEnd = indexStart + UIGlobal.pageVolume - 1;
			if (indexStart > count - 1) {
				this.pageValid = false;
				return;
			}
			if (indexEnd > count - 1) indexEnd = count;
			this.pageValid = true;
			return;
		}
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getTime() {
		return time;
	}
	
	public String getMode() {
		return this.mode;
	}
	
	public int getPages() {
		return this.pages;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public String getQuery() {
		return this.query;
	}
	
	public String getQueryCheck() {
		return this.queryCheck;
	}
	
	public int getPageCurrent() {
		return this.pageCurrent;
	}
	
	public String getWikipediaUrl() {
		return this.wikipediaUrl;
	}
	
	public List<Item> getAmazonItems() {
		return this.amazonItems;
	}
	
	public YoutubeItem getYoutubeItems() {
		return this.youtubeItems;
	}
	
	public String[] getPageTitles() {
		if (this.pageValid == false) return new String[0];
		String[] ret = Arrays.copyOfRange(titles, indexStart, indexEnd);
		return ret;
	}
	
	public String[] getPageUrls() {
		if (this.pageValid == false) return new String[0];
		String[] ret = Arrays.copyOfRange(urls, indexStart, indexEnd);
		return ret;
	}
	
	public String getPageUrl(int page, String mode, String queryString, boolean session, boolean wiki, boolean amazon, boolean youtube, boolean spellcheck) {
		if (page > pages || page < 1) {
			return null;
		} else {
			String url = UIGlobal.pathSearch + "?";
			url += UIGlobal.paraMode + "=" + mode;
			try {
				if (queryString != null) url += "&" + UIGlobal.paraQuery + "=" + URLEncoder.encode(queryString, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				UIWorker.logger.error("encode url error", e);
				return null;
			}
			url += "&" + UIGlobal.paraPage + "=" + String.valueOf(page);
			if (amazon) url += "&" + UIGlobal.paraAmazon + "=1";
			if (youtube) url += "&" + UIGlobal.paraYoutube + "=1";
			if (spellcheck) url += "&" + UIGlobal.paraSpellCheck + "=1";
			if (wiki) url += "&" + UIGlobal.paraWiki + "=1";
			else url += "&" + UIGlobal.paraWiki + "=0";
			if (session) url += "&" + UIGlobal.paraSessionID + "=" + sessionID;
			return url;
		}
	}
	
	public String getSearchUrl() {		
		boolean amazon = false, youtube = false, wiki = true, session = false, spellcheck = true;
		if (amazonItems != null) amazon = true;
		if (youtubeItems != null) youtube = true;
		return getPageUrl(1, mode, null, session, wiki, amazon, youtube, spellcheck);
	}
	
	public String getSpellCheckPageUrl() {
		if (this.queryCheck == null) return null;
		boolean amazon = false, youtube = false, wiki = true, session = false, spellcheck = true;
		if (amazonItems != null) amazon = true;
		if (youtubeItems != null) youtube = true;
		return getPageUrl(1, mode, queryCheck, session, wiki, amazon, youtube, spellcheck);
	}
	
	public String getNeighborPageUrl(boolean next) {
		int pageNeighbor = pageCurrent;
		if (next) pageNeighbor = pageCurrent + 1;
		else pageNeighbor = pageCurrent - 1;
		boolean amazon = false, youtube = false, wiki = false, session = true, spellcheck = false;
		if (amazonItems != null) amazon = true;
		if (youtubeItems != null) youtube = true;
		if (queryCheck != null) spellcheck = true;
		if (pageNeighbor == 1) wiki = true;
		return getPageUrl(pageNeighbor, mode, query, session, wiki, amazon, youtube, spellcheck);
	}
}

package cis455.project.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cis455.project.amazon.Item;
import cis455.project.youtube.YoutubeItem;

public class SearchResult {
	public String[] titles = null;
	public String[] urls = null;
	public Integer count = null;
	public Integer pages = null;
	private Integer pageCurrent = null;
	private Integer indexStart = null;
	private Integer indexEnd = null;
	private Boolean pageValid = null;
	private String query = null;
	private List<Item> amazonItems = null;
	private YoutubeItem youtubeItems = null;
	private String wikipediaUrl = null;
	
	SearchResult(String result, String query, List<Item> amazonItems, YoutubeItem youtubeItems, String wikipediaUrl) {
		this.query = query;
		this.amazonItems = amazonItems;
		this.youtubeItems = youtubeItems;
		this.wikipediaUrl = wikipediaUrl;
		
		String[] lines = result.split(UIGlobal.CRLF);
		List<String> titlesList = new ArrayList<String>();
		List<String> urlsList = new ArrayList<String>();
		UIWorker.logger.info("received lines: " + lines.length);
		for (int i = 0; i < lines.length; i ++ ) {
			//UIWorker.logger.info(lines[i]);
			String[] tokens = lines[i].split(UIGlobal.delimiterUI, 2);
			//UIWorker.logger.info("tokens: " + tokens.length);
			if(tokens.length == 2) {
				titlesList.add(tokens[0]);
				urlsList.add(tokens[1]);
			} else {
				UIWorker.logger.info("discard invalid line: " + lines[i]);
			}
		}
		count = urlsList.size();
		UIWorker.logger.info("parsed titles/urls: " + count);
		pages = (int) Math.ceil((double)count / (double)UIGlobal.pageVolume);
		titles = new String[count];
		titles = titlesList.toArray(titles);
		urls = new String[count];
		urls = urlsList.toArray(urls);
	}
	
	public void setPage(int page) {
		if (page > pages) {
			this.pageValid = false;
			return;
		} else {
			pageCurrent = page;
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
	
	public String getNeighborPageUrl(boolean next) {
		int pageNeighbor = pageCurrent;
		if (next) pageNeighbor = pageCurrent ++;
		else pageCurrent --;
		if (pageNeighbor > pages || pageNeighbor < 1) {
			return null;
		} else {
			String url = UIGlobal.pathSearch + "?";
			try {
				url += UIGlobal.paraQuery + "=" + URLEncoder.encode(query, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				UIWorker.logger.error("encode url error", e);
				return null;
			}
			url += "&" + UIGlobal.paraPage + "=" + String.valueOf(pageNeighbor);
			if (this.amazonItems != null) url += "&" + UIGlobal.paraAmazon + "=1";
			if (this.youtubeItems != null) url += "&" + UIGlobal.paraYoutube + "=1";
			if (this.wikipediaUrl != null) url += "&" + UIGlobal.paraWiki + "=1";
			return url;
		}
		
	}
	
}

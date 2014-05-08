package cis455.project.ui;

import java.util.Arrays;

public class SearchResult {
	public String[] titles = null;
	public String[] urls = null;
	public Integer count = null;
	public Integer pages = null;
	private Integer pageCurrent = null;
	private Integer indexStart = null;
	private Integer indexEnd = null;
	
	SearchResult(String result) {
		String[] lines = result.split(UIGlobal.CRLF);
		count = lines.length;
		pages = (int) Math.ceil((double)count / (double)UIGlobal.pageVolume);
		titles = new String[count];
		urls = new String[count];
		UIWorker.logger.info("received links: " + count + "; pages: " + pages);
		for (int i = 0; i < count; i ++ ) {
			UIWorker.logger.info(lines[i]);
			String[] tokens = lines[i].split(UIGlobal.delimiterUI, 2);
			UIWorker.logger.info("tokens: " + tokens.length);
			titles[i] = tokens[0];
			urls[i] = tokens[1];
		}
	}
	
	public boolean setPage(int page) {
		if (page > pages) {
			return false;
		} else {
			pageCurrent = page;
			indexStart = UIGlobal.pageVolume * (pageCurrent -1);
			indexEnd = indexStart + UIGlobal.pageVolume - 1;
			if (indexEnd > count) indexEnd = count;
			return true;
		}
	}
	
	public String[] getPageTitles() {
		String[] ret = Arrays.copyOfRange(titles, indexStart, indexEnd);
		return ret;
	}
	
	public String[] getPageUrls() {
		String[] ret = Arrays.copyOfRange(urls, indexStart, indexEnd);
		return ret;
	}
}

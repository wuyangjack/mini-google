package cis455.project.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResult {
	public String[] titles = null;
	public String[] urls = null;
	public Integer count = null;
	public Integer pages = null;
	private Integer pageCurrent = null;
	private Integer indexStart = null;
	private Integer indexEnd = null;
	private Boolean pageValid = null;
	
	SearchResult(String result) {
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
	
}

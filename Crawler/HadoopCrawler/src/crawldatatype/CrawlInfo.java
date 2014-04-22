package crawldatatype;

public class CrawlInfo {

	CrawlUrl crawl_url;
	long delay;

	public CrawlInfo(CrawlUrl crawl_url, long delay) {
		this.crawl_url = crawl_url;
		this.delay = delay;
	}

	public CrawlUrl getCrawlUrl() { return crawl_url; }

	public long getDelay() { return delay; }

}
